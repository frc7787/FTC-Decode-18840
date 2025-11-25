package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.math.isReal

class Intake(hardwareMap: HardwareMap) {
    private val motor = hardwareMap[MOTOR_NAME] as DcMotorEx

    var power: Double = 0.0
        set(power) {
            require(power.isReal()) {
                "Expected real intake power. Got: $power"
            }
            field = power.coerceIn(MIN_POWER, MAX_POWER)
            motor.power = field
        }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine("---- Intake ----")
        telemetry.addLine("Power: $power")
        if (verbose) {
            telemetry.addLine("Direction: ${motor.direction}")
            telemetry.addLine("Zero Power Behaviour: ${motor.zeroPowerBehavior}")
        }
    }

    private companion object {
        const val MOTOR_NAME = "intakeMotor"

        const val MIN_POWER = -1.0
        const val MAX_POWER = 0.8
    }
}