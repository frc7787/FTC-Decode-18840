package org.firstinspires.ftc.teamcode.subsytems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.subsystems.Subsystem
import kotlinx.serialization.json.Json
import java.io.File

object Shooter: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val shooterMotor by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap["shooterMotor"] as DcMotorEx
    }

    // ---------------------------------------------------------------------------------------------
    // Config

    data class Configuration(
        val debug: Boolean = false,
        val motorDirection: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
        val brake: Boolean = true
    ) {
        companion object {
            fun fromJson(): Configuration {
                try {
                    val rawText = File("shooter-config.json").readText()
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

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun preUserInitHook(opMode: Wrapper) {
        shooterMotor.direction = configuration.motorDirection
        if (configuration.brake) shooterMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    override fun preUserLoopHook(opMode: Wrapper) {}

    // ---------------------------------------------------------------------------------------------
    // Commands

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