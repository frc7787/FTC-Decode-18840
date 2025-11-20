package org.firstinspires.ftc.teamcode.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction
import org.firstinspires.ftc.robotcore.external.Telemetry

@Configurable
class Transfer(hardwareMap: HardwareMap) {
    // -------------------------------------------------------
    // Configuration

    private val transferServo = hardwareMap[TRANSFER_SERVO_NAME] as Servo

    companion object {
        private const val TRANSFER_SERVO_NAME = "transferServo"

        @JvmField
        var TRANSFER_SERVO_DIRECTION = Direction.REVERSE
        @JvmField
        var UP_POSITION = 0.57
        @JvmField
        var DOWN_POSITION = 0.50
    }

    // -------------------------------------------------------
    // Construction

    init {
        transferServo.direction = TRANSFER_SERVO_DIRECTION
        transferServo.position = DOWN_POSITION
    }

    // -------------------------------------------------------
    // State

    var state: State = State.DOWN
        private set

    val position: Double
        get() {
            return state.position
        }

    // -------------------------------------------------------
    // Core

    fun update() {
        transferServo.position = position
    }

    fun up() {
        state = State.UP
    }

    fun down() {
        state = State.DOWN
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine()
        telemetry.addLine("--- Transfer ---")
        telemetry.addLine("State: $state")
        telemetry.addLine("Position: $position")
        if (verbose) {
            telemetry.addLine("Direction: $TRANSFER_SERVO_DIRECTION")
        }
    }

    enum class State(val position: Double) {
        UP(UP_POSITION),
        DOWN(DOWN_POSITION),
    }

    // -------------------------------------------------------
}