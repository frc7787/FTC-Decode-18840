package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction
import org.firstinspires.ftc.robotcore.external.Telemetry

class Transfer(hardwareMap: HardwareMap) {
    // -------------------------------------------------------
    // Hardware

    private val servo = hardwareMap[SERVO_NAME] as Servo

    // -------------------------------------------------------
    // Configuration

    private companion object {
        const val MIN_POSITION = 0.49
        const val MAX_POSITION = 0.62

        const val UP_POSITION   = 0.59
        const val DOWN_POSITION = 0.51
        const val SERVO_NAME = "transferServo"

        val SERVO_DIRECTION = Direction.REVERSE
    }

    // -------------------------------------------------------
    // Construction

    init {
        servo.direction = SERVO_DIRECTION
        servo.position = DOWN_POSITION
    }

    // State

    var position: Double = MIN_POSITION
        set(position) {
            field = position.coerceIn(MIN_POSITION, MAX_POSITION)
        }

    // -------------------------------------------------------
    // Core

    fun up() {
        position = UP_POSITION
    }

    fun down() {
        position = DOWN_POSITION
    }

    fun update() {
        servo.position = position
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine()
        telemetry.addLine("--- Transfer ---")
        telemetry.addLine("Position: ${servo.position}")
        if (verbose) {
            telemetry.addLine("Direction: $SERVO_DIRECTION")
        }
    }

    // -------------------------------------------------------
}