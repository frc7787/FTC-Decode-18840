package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.SparkMini

class Intake private constructor(hardwareMap: HardwareMap) {
    private val motor = SparkMini(hardwareMap["intakeMotor"] as CRServo)

    init {
        motor.minPower = MIN_POWER
        motor.maxPower = MAX_POWER
    }

    fun run(power: Double): Command {
        return CommandBuilder()
            .setStart {
                motor.power = power
            }
            .setEnd {
                motor.power = 0.0
            }
            .requiring(this)
    }

    fun intake(): Command {
        return run(INTAKE_POWER)
    }

    fun outtake(): Command {
        return run(OUTTAKE_POWER)
    }

    companion object {
        private const val MIN_POWER     = -1.0
        private const val MAX_POWER     = 1.0
        private const val INTAKE_POWER  = 0.6
        private const val OUTTAKE_POWER = -0.8

        private var INSTANCE: Intake? = null

        fun get(hardwareMap: HardwareMap): Intake {
            if (INSTANCE == null) INSTANCE = Intake(hardwareMap)
            return INSTANCE!!
        }

        fun destroy() {
            INSTANCE = null
            System.gc()
        }
    }
}