package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx

class Encoder(private val internalMotor: DcMotorEx) {

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
            positionOffset = rawPosition + position
        }

    val rpm: Double
        get() {
            return internalMotor.getVelocity(DEGREES) / 6.0
        }

    var encoderReversed = false
}