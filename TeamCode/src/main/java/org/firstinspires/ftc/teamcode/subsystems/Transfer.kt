package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction
import org.firstinspires.ftc.robotcore.external.Telemetry

class Transfer(hardwareMap: HardwareMap) {
    private val servo          = hardwareMap[SERVO_NAME] as Servo
    private val positionSensor = hardwareMap[POSITION_SENSOR_NAME] as AnalogInput

    init {
        servo.direction = SERVO_DIRECTION
        servo.position  = DOWN_POSITION
    }

    val voltage: Double
        get() {
            return positionSensor.voltage
        }

    fun up() {
        servo.position = UP_POSITION
    }

    fun down() {
        servo.position = DOWN_POSITION
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine()
        telemetry.addLine("--- Transfer ---")
        telemetry.addLine("Voltage")
        if (verbose) {
            telemetry.addLine("Direction: $SERVO_DIRECTION")
            telemetry.addLine("Position (From Servo): ${servo.position}")
        }
    }

    private companion object {
        const val UP_POSITION   = 0.25
        const val DOWN_POSITION = 0.01
        const val SERVO_NAME = "transferServo"
        const val POSITION_SENSOR_NAME = "transferPositionSensor"

        val SERVO_DIRECTION = Direction.REVERSE
    }
}