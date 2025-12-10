package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.math.isReal

class Intake(hardwareMap: HardwareMap) {
    private val servo = hardwareMap[SERVO_NAME] as CRServo

    var power: Double = 0.0
        set(power) {
            require(power.isReal()) {
                "Expected real intake power. Got: $power"
            }
            field = power.coerceIn(MIN_POWER, MAX_POWER)
            servo.power = power
        }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine("---- Intake ----")
        telemetry.addLine("Power: $power")
        if (verbose) {
            telemetry.addLine("Direction: ${servo.direction}")
        }
    }

    private companion object {
        const val SERVO_NAME = "intakeServo"

        const val MIN_POWER = -1.0
        const val MAX_POWER = 1.0
    }
}