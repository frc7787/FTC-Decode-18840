package org.firstinspires.ftc.teamcode.subsytems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DigitalChannel
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.commands.groups.Sequential
import dev.frozenmilk.mercurial.subsystems.Subsystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.firstinspires.ftc.teamcode.control.PIDFCoefficients
import org.firstinspires.ftc.teamcode.control.PIDFController
import org.firstinspires.ftc.teamcode.utility.RotationDirection
import java.io.File
import kotlin.math.abs

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

    @Serializable
    data class Configuration(
        val debug: Boolean = false,
        val maxClockwisePower: Double = 1.0,
        val maxCounterClockwisePower: Double = -1.0,
        val homingPower: Double = 0.6,
        val homingDirection: RotationDirection = RotationDirection.CLOCKWISE,
        val minDegrees: Double = 0.0,
        val maxDegrees: Double = 270.0,
        val toleranceDegrees: Double = 0.5,
        val ticksPerDegree: Double = 20.0,
        val pidfCoefficients: PIDFCoefficients = PIDFCoefficients(0.5, 0.0, 0.1, 0.2),
    ) {
        val controller = PIDFController(
            pidfCoefficients,
            toleranceDegrees,
            maxClockwisePower,
            maxCounterClockwisePower
        )

        companion object {
            fun fromJson(): Configuration {
                try {
                    val rawText = File("turret-config.json").readText()
                    return Json.decodeFromString(rawText)
                } catch (_: Exception) {
                    return Configuration()
                }
            }
        }
    }

    var configuration = Configuration.fromJson()

    // ---------------------------------------------------------------------------------------------
    // State

    val degrees: Double
        get() {
            return turretMotor.currentPosition.toDouble() / configuration.ticksPerDegree
        }
        private set

    // ---------------------------------------------------------------------------------------------
    // Hooks

    // ---------------------------------------------------------------------------------------------
    // Commands

    /**
     * Sets the power of the turret. The power will be clipped to be within
     * [Configuration.maxClockwisePower] and [Configuration.maxCounterClockwisePower]
     */
    fun manual(power: Double): Lambda {
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

    /**
     * Moves the turret the the desired angle. The angle will be clipped to be within
     * [Configuration.minDegrees] and [Configuration.maxDegrees].
     */
    fun toAngle(targetDegrees: Double): Lambda {
        val clippedTargetDegrees
            = targetDegrees.coerceIn(configuration.minDegrees, configuration.maxDegrees)

        return Lambda("turret-set-position-$clippedTargetDegrees")
            .setExecute {
                turretMotor.power = configuration.controller.calculate(degrees, clippedTargetDegrees)
            }
            .setEnd {
                turretMotor.power = 0.0
            }
            .setFinish {
                abs(targetDegrees - degrees) < configuration.toleranceDegrees
            }
    }

    /**
     * Resets the position of the turret.
     */
    fun reset(): Lambda {
        return Lambda("turret-reset")
            .setInit {
                turretMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                turretMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            }
            .setInterruptible(false)
    }

    /**
     * Runs the turret calibration sequence, used to determine [Configuration.ticksPerDegree]
     * empirically. Once the calibration sequence is finished writes the value to turret-config.json.
     *
     * @param calibrationPower The power to run the calibration sequence in. Probably shouldn't be
     *                         higher than 0.5
     * @param writeResult Whether to write the result of the calibration to the properties file
     */
    fun calibrate(calibrationPower: Double, writeResult: Boolean = true): Sequential {
        val clippedCalibrationPower = calibrationPower.coerceIn(
            configuration.maxClockwisePower,
            configuration.maxCounterClockwisePower
        )

        return Sequential(
            manual(clippedCalibrationPower)
                .addFinish { rightLimitSwitch.state }
                .addEnd { reset() },
            manual(-clippedCalibrationPower)
                .addFinish { leftLimitSwitch.state }
                .addEnd {
                    val ticksPerDegree = turretMotor.currentPosition / configuration.maxDegrees
                    if (writeResult) {
                        val newConfig = Configuration(ticksPerDegree = ticksPerDegree)
                        val configString = Json.encodeToString(newConfig)
                        File("turret-config.json").writeText(configString)
                    }
                }
        )
    }

    fun home(): Sequential {
        val homingPower = when (configuration.homingDirection) {
            RotationDirection.CLOCKWISE -> configuration.homingPower
            RotationDirection.COUNTERCLOCKWISE -> -configuration.homingPower
        }

        return Sequential(
            manual(homingPower)
                .addFinish {
                    return@addFinish when (configuration.homingDirection) {
                        RotationDirection.COUNTERCLOCKWISE -> leftLimitSwitch.state
                        RotationDirection.CLOCKWISE -> rightLimitSwitch.state
                    }
                }
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