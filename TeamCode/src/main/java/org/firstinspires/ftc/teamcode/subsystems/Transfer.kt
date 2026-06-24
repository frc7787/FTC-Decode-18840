package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction

class Transfer(hardwareMap: HardwareMap) {
    private val servo = hardwareMap["transferServo"] as Servo

    init {
       servo.direction = DIRECTION
    }

    fun toPosition(position: Double): Command {
        return CommandBuilder()
            .setStart {
                servo.position = position
            }
            .requiring(this)
    }

    fun up(): Command {
        return toPosition(UP_POSITION)
    }

    fun down(): Command {
        return toPosition(DOWN_POSITION)
    }

    companion object {
        private const val UP_POSITION    = 0.02
        private const val DOWN_POSITION  = 0.00
        private val DIRECTION: Direction = FORWARD
    }
}