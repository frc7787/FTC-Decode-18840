package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

class Motor(private val internalMotor: DcMotorEx) {

    init {
        internalMotor.resetDeviceConfigurationForOpMode()
        internalMotor.mode              = RUN_WITHOUT_ENCODER
        internalMotor.zeroPowerBehavior = BRAKE
    }

    var ignoreSoftwarePositionLimits = false

    var minPosition: Double = Double.NEGATIVE_INFINITY

    var maxPosition: Double = Double.POSITIVE_INFINITY

    private var positionOffset = 0.0

    val rawPosition: Double
        get() {
            return internalMotor.currentPosition.toDouble()
        }

    var position: Double
        get() {
            return (rawPosition - positionOffset) * if (encoderReversed) -1.0 else 1.0
        }
        set(position) {
            positionOffset = (rawPosition + position).coerceIn(minPosition, maxPosition)
        }

    val rpm: Double
        get() {
            return internalMotor.getVelocity(DEGREES) / 6.0
        }

    var powerTolerance: Double = 0.02
        set(new) {
            field = new.coerceIn(-1.0, 1.0)
        }

    var minPower = -1.0
        set(new) {
            field = new.coerceIn(-1.0, 0.0)
        }

    var maxPower = 1.0
        set(new) {
            field = new.coerceIn(0.0, 1.0)
        }

    var power: Double = 0.0
        set(new) {
            val correctedPower = if (direction == FORWARD) new else -new
            val scaledPower = correctedPower.coerceIn(minPower, maxPower)
            if (abs(scaledPower - new) < powerTolerance) return

            // We need this variable, because if we simply set scaledPower to 0.0 then it won't be
            // cached properly, and we will lose out on our precious loop time.
            var outputPower = scaledPower

            if (!ignoreSoftwarePositionLimits) {
                outputPower = outputPower.coerceIn(minPower, maxPower)

                if (position > maxPosition && scaledPower > 0.0) outputPower = 0.0
                if (position < minPosition && scaledPower < 0.0) outputPower = 0.0
            }

            internalMotor.power = outputPower
            field = scaledPower
        }

    var zeroPowerBehavior: ZeroPowerBehavior = BRAKE
        set(new) {
            if (field == new || field == UNKNOWN) return
            internalMotor.zeroPowerBehavior = new
            field = new
        }

    var direction: Direction = FORWARD

    val currentAmps: Double
        get() {
            return internalMotor.getCurrent(AMPS)
        }

    val stalled: Boolean
        get() {
            return internalMotor.isOverCurrent && rpm.toInt() == 0
        }

    var encoderReversed: Boolean = false

    fun debug(telemetry: Telemetry) {
        telemetry.addLine("Power Tolerance: $powerTolerance")
        telemetry.addLine("Power: $power")
        telemetry.addLine("Zero Power Behavior: $zeroPowerBehavior")
        telemetry.addLine("Direction: $direction")
        telemetry.addLine("Position Limit: $maxPosition")
        telemetry.addLine("Raw Position: $rawPosition")
        telemetry.addLine("Position: $position")
        telemetry.addLine("RPM: $rpm")
        telemetry.addLine("Current (Amps) ${currentAmps}")
        telemetry.addLine("Stalled $stalled")
    }
}