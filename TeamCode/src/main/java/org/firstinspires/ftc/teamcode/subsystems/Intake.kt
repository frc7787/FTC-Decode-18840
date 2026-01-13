package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.util.Constrained
import kotlin.math.abs

class Intake(
    hardwareMap: HardwareMap,
    val configuration: Configuration = Configuration.DEFAULT
): Subsystem {
    private val motor = hardwareMap["intakeMotor"] as DcMotorSimple

    init {
        motor.direction = configuration.direction
    }

    var power: Double = 0.0
        set(power) {
            val constrainedPower = power.coerceIn(configuration.minPower, configuration.maxPower)
            mode = if (abs(constrainedPower) < 0.02) STOPPED else ACTIVE
            field = constrainedPower
        }

    var mode: Mode = STOPPED
        private set

    fun isActive(): Boolean {
        return mode == ACTIVE
    }

    fun isStopped(): Boolean {
        return mode == STOPPED
    }

    override fun update() {
        motor.power = when (mode) {
            STOPPED -> 0.0
            ACTIVE  -> power
        }
    }

    override fun debug(telemetry: Telemetry, verbose: Boolean) {
        telemetry.addLine("---- Intake ----")
        telemetry.addLine("Power: $power")
        if (verbose) {
            telemetry.addLine("Direction: ${motor.direction}")
        }
    }

    enum class Mode {
        ACTIVE,
        STOPPED
    }

    data class Configuration(
        val minPower: Double,
        val maxPower: Double,
        val direction: DcMotorSimple.Direction,
    ) {
        companion object {
            val DEFAULT = Configuration(
                    minPower = -1.0,
                    maxPower = 1.0,
                    direction = FORWARD
                )
        }
    }
}