package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

class SparkMini(private val internalMotor: DcMotorSimple) {
    constructor(internalMotor: CRServo): this(internalMotor as DcMotorSimple)

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

            internalMotor.power = scaledPower
            field = scaledPower
        }

    var direction: Direction = FORWARD

    fun debug(telemetry: Telemetry, name: String = "", simplified: Boolean = false) {
        if (!name.isEmpty()) telemetry.addLine("----- Motor Debug ($name) ----- ")
        telemetry.addLine("Power: $power")
        telemetry.addLine("Direction: $direction")

        if (!simplified) {
            telemetry.addLine("Power Tolerance: $powerTolerance")
        }
    }
}