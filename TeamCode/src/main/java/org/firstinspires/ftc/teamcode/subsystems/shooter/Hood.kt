package org.firstinspires.ftc.teamcode.subsystems.shooter

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo.Direction
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.math.isReal

internal class Hood(hardwareMap: HardwareMap) {
    private val servo = hardwareMap[SERVO_NAME] as ServoImplEx

    init {
        servo.direction = SERVO_DIRECTION
    }

    var angle: Double = 0.0
        set(deg) {
            require(deg.isReal()) {
                "Expected real hood angle. Got: $deg"
            }
            require(deg >= 0.0) {
                "Expected positive hood angle. Got: $deg"
            }
            field = deg.coerceIn(MIN_DEG, MAX_DEG) / DEGREES_PER_SERVO_STEP
            servo.position = field
        }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine("---- Hood ----")
        telemetry.addLine("Position: ${servo.position}")
        telemetry.addLine("Angle (Deg): $angle")
        if (verbose) {
            telemetry.addLine("Direction: ${servo.direction}")
        }
    }

    private companion object {
        const val SERVO_NAME = "hoodServo"
        val SERVO_DIRECTION  = Direction.REVERSE

        const val MIN_DEG = 0.0
        const val MAX_DEG = 45.0

        const val DEGREES_PER_SERVO_STEP = 1.0
    }
}