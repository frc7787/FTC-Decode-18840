package org.firstinspires.ftc.teamcode.subsytems

import com.pedropathing.control.PIDFCoefficients
import com.pedropathing.control.PIDFController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DigitalChannel
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.commands.groups.Sequential
import dev.frozenmilk.mercurial.subsystems.Subsystem
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

object Turret: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val turretMotor by subsystemCell {
        val hardwareMap = FeatureRegistrar.activeOpMode.hardwareMap
        hardwareMap["turretMotor"] as DcMotorEx
    }

    private val rightLimitSwitch by subsystemCell {
        val hardwareMap = FeatureRegistrar.activeOpMode.hardwareMap
        hardwareMap["rightLimitSwitch"] as DigitalChannel
    }

    private val leftLimitSwitch by subsystemCell {
        val hardwareMap = FeatureRegistrar.activeOpMode.hardwareMap
        hardwareMap["leftLimitSwitch"] as DigitalChannel
    }

    // ---------------------------------------------------------------------------------------------
    // Config

    var configuration = Configuration()

    data class Configuration(
        val debug: Boolean = false,
        val maxClockwisePower: Double = 1.0,
        val maxCounterClockwisePower: Double = -1.0,
        val minDegrees: Double = 0.0,
        val maxDegrees: Double = 270.0,
        val toleranceDegrees: Double = 0.5,
        val ticksPerDegree: Int = 20,
        val PIDFCoefficients: PIDFCoefficients = PIDFCoefficients(0.5, 0.0, 0.005, 0.2)
    ) {

        fun minPosition(): Int {
            return (minDegrees * ticksPerDegree).roundToInt()
        }

        fun maxPosition(): Int {
            return (maxDegrees * ticksPerDegree).roundToInt()
        }

        fun toleranceTicks(): Int {
            return (toleranceDegrees * ticksPerDegree).roundToInt()
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Control

    private val turretController = PIDFController(configuration.PIDFCoefficients)

    // ---------------------------------------------------------------------------------------------
    // State

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun postUserInitHook(opMode: Wrapper) {}

    override fun preUserLoopHook(opMode: Wrapper) {}

    // ---------------------------------------------------------------------------------------------
    // Commands

    fun power(power: Double): Lambda {
        val clippedPower
            = power.coerceIn(configuration.maxClockwisePower, configuration.maxCounterClockwisePower)
        return Lambda("turret-set-power-$power")
            .setInit {
                turretMotor.power = clippedPower
            }
            .setEnd {
                turretMotor.power = 0.0
            }
            .setFinish {
                false
            }
    }

    fun toPosition(target: Int): Lambda {
        val clippedTargetPosition
            = target.coerceIn(configuration.minPosition(), configuration.maxPosition())

        return Lambda("turret-set-position-$clippedTargetPosition")
            .setExecute {
                val error = (clippedTargetPosition - turretMotor.currentPosition).toDouble()
                turretController.updateFeedForwardInput(error.sign)
                turretController.updateError(error)
                val power = turretController.run().coerceIn(
                    configuration.maxClockwisePower,
                    configuration.maxCounterClockwisePower
                )
                turretMotor.power = power
            }
            .setFinish {
                abs(clippedTargetPosition - turretMotor.currentPosition) < configuration.toleranceDegrees
            }
    }

    fun toPosition(degrees: Double): Lambda {
        return toPosition((degrees * configuration.ticksPerDegree).roundToInt())
    }

    fun reset(): Lambda {
        return Lambda("turret-reset")
            .setInit {
                turretMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                turretMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            }
            .setInterruptible(false)
    }

    fun home(): Sequential {
        return Sequential(
            power(-0.5).addFinish { leftLimitSwitch.state }
                .setInterruptible(false),
            reset()
        )
    }

    // ---------------------------------------------------------------------------------------------
    // Subsystem Boilerplate

    override var dependency: Dependency<*>
            = Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    annotation class Attach

    // ---------------------------------------------------------------------------------------------
}