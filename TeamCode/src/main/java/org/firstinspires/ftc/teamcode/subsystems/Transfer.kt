package org.firstinspires.ftc.teamcode.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.Servo.Direction

@Configurable
class Transfer(hardwareMap: HardwareMap) {
    private val servo = hardwareMap["transferServo"] as Servo

    init {
       servo.direction = DIRECTION
    }

    fun toPosition(position: Double): Command {
        return CommandBuilder()
            .setStart {
                servo.position = position.coerceIn(MIN_POSITION..MAX_POSITION)
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
        @JvmStatic var MIN_POSITION   = 0.00
        @JvmStatic var MAX_POSITION   = 0.10
        @JvmStatic var UP_POSITION    = 0.02
        @JvmStatic var DOWN_POSITION  = 0.00
        @JvmStatic var DIRECTION: Direction = FORWARD
    }
}