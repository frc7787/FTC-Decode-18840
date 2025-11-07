package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class Intake(hardwareMap: HardwareMap) {
    val motor = hardwareMap[INTAKE_MOTOR_NAME] as DcMotorEx

    init {
        motor.direction = INTAKE_MOTOR_DIRECTION
        motor.zeroPowerBehavior = INTAKE_MOTOR_ZERO_POWER_BEHAVIOR
        motor.mode = RunMode.STOP_AND_RESET_ENCODER
        motor.mode = RunMode.RUN_WITHOUT_ENCODER
    }

    // State

    var power: Double = 0.0
        private set

    val position: Int
        get() {
            return motor.currentPosition
        }

    fun isStopped(): Boolean {
        return motor.velocity == 0.0
    }

    fun update(power: Double) {
        motor.power = power
    }

    fun debug(telemetry: Telemetry) {
        telemetry.addLine()
        telemetry.addLine("--- Intake Debug ---")
        telemetry.addLine("Power: $power")
        telemetry.addLine("Direction: $INTAKE_MOTOR_DIRECTION")
        telemetry.addLine("Zero Power Behavior: $INTAKE_MOTOR_ZERO_POWER_BEHAVIOR")
    }

    private companion object {
        const val INTAKE_MOTOR_NAME = "intakeMotor"
        val INTAKE_MOTOR_DIRECTION = Direction.REVERSE
        val INTAKE_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE
    }
}