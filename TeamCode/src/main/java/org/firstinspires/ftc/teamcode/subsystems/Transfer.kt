package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.util.Constrained
import org.firstinspires.ftc.teamcode.util.NotNaN

class Transfer(
    hardwareMap: HardwareMap,
    private val configuration: Transfer.Configuration = Transfer.Configuration.DEFAULT
): Subsystem {
    private val servo = hardwareMap["transferServo"] as Servo

    init {
        servo.direction = configuration.servoDirection
        servo.position  = configuration.downPosition
    }

    var targetPosition: Double = 0.0

    var state: State = DOWN
        private set

    fun up() {
        targetPosition = configuration.upPosition
    }

    fun down() {
        targetPosition = configuration.downPosition
    }

    override fun update() {
        state = when (targetPosition) {
            configuration.downPosition -> DOWN
            configuration.upPosition   -> UP
            else -> POSITION
        }

        servo.position = targetPosition
    }

    override fun debug(telemetry: Telemetry, verbose: Boolean) {
        telemetry.addLine()
        telemetry.addLine("--- Transfer ---")
        telemetry.addLine("Position: ${servo.position}")
        if (verbose) {
            telemetry.addLine("Direction: ${servo.direction}")
        }
    }

    enum class State {
        UP,
        DOWN,
        POSITION
    }

    class Configuration(upPosition: Double, downPosition: Double, minPosition: Double, maxPosition: Double, val servoDirection: Servo.Direction) {
        val upPosition by NotNaN(upPosition)
        val downPosition by NotNaN(downPosition)
        val minPosition by NotNaN(minPosition)
        val maxPosition by NotNaN(maxPosition)

        fun with(
            upPosition: Double = DEFAULT.upPosition,
            downPosition: Double = DEFAULT.downPosition,
            minPosition: Double = DEFAULT.minPosition,
            maxPosition: Double = DEFAULT.maxPosition,
            servoDirection: Servo.Direction = DEFAULT.servoDirection
        ): Transfer.Configuration {
            return Transfer.Configuration(
                upPosition,
                downPosition,
                minPosition,
                maxPosition,
                servoDirection
            )
        }

        companion object {
            val DEFAULT = Transfer.Configuration(
                upPosition     = 0.18,
                downPosition   = 0.03,
                minPosition    = 0.03,
                maxPosition    = 0.33,
                servoDirection = FORWARD
            )
        }
    }
}