package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.math.isReal

class Intake(hardwareMap: HardwareMap) {
    // Hardware

    private val motor = hardwareMap[MOTOR_NAME] as DcMotorEx

    // State

    var power: Double = 0.0
        set(power) {
            require(power.isReal()) {
                "Expected real intake power. Got: $power"
            }

            field = power.coerceIn(MIN_POWER, MAX_POWER)
        }

    fun update() {
        motor.power = power
    }

    private companion object {
        const val MOTOR_NAME = "intakeMotor"

        const val MIN_POWER = -1.0
        const val MAX_POWER = 0.8
    }
}