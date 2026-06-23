package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.pedropathing.ivy.behaviors.BlockedBehavior
import com.pedropathing.ivy.behaviors.ConflictBehavior
import com.pedropathing.ivy.behaviors.EndCondition
import com.pedropathing.ivy.behaviors.InterruptedBehavior
import com.qualcomm.hardware.rev.RevTouchSensor
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.hardware.SparkMini
import kotlin.math.abs

class Spindexer private constructor(hardwareMap: HardwareMap) {
    private val motor = SparkMini(hardwareMap["spindexerMotor"] as CRServo)
    private val limitSwitch = hardwareMap["spindexerLimitSwitch"] as RevTouchSensor

    init {
        motor.direction = MOTOR_DIRECTION
    }

    fun power(power: Double): Command {
        return CommandBuilder()
            .setStart {
                motor.power = if (power < 0.0) {
                    RobotLog.ww("SPINDEXER", "Cannot Spin Spindexer Clockwise! Taking Abs.")
                    abs(power)
                } else {
                    power
                }
            }
            .setDone { false }
            .requiring(this)
    }

    fun toNextIntakingPosition(): Command {
        return ToNextIntakingPosition()
    }

    private inner class ToNextIntakingPosition: Command {

        private val limitSwitchPressedAtStart by lazy {
            limitSwitch.isPressed
        }

        private var limitSwitchWasPressed = false

        override fun requirements(): Set<Any?> = setOf(this@Spindexer)

        override fun priority(): Int = 0

        override fun interruptedBehavior(): InterruptedBehavior = END

        override fun conflictBehavior(): ConflictBehavior = CANCEL

        override fun blockedBehavior(): BlockedBehavior = CANCEL

        override fun start() {
            ::limitSwitchPressedAtStart.invoke()
        }

        override fun done(): Boolean {
            val limitSwitchPressed = limitSwitch.isPressed

            return if (limitSwitchPressedAtStart) {
                // If the limit switch was pressed at the start, wait until it isn't pressed
                !limitSwitchPressed
            } else {
                // If it wasn't pressed at the start, wait until it is pressed, and then it isn't
                limitSwitchWasPressed && !limitSwitchPressed
            }
        }

        override fun execute() {
            if (!limitSwitchPressedAtStart) {
                limitSwitchWasPressed = limitSwitch.isPressed
            }
        }

        override fun end(p0: EndCondition) {
            motor.power = 0.0
        }
    }

    companion object {
        private val MOTOR_DIRECTION: Direction = REVERSE

        private var INSTANCE: Spindexer? = null

        fun get(hardwareMap: HardwareMap): Spindexer {
            if (INSTANCE == null) INSTANCE = Spindexer(hardwareMap)
            return INSTANCE!!
        }

        fun destroy() {
            INSTANCE = null
            System.gc()
        }
    }
}