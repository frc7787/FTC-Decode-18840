package org.firstinspires.ftc.teamcode.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.hardware.SparkMini

@Configurable
class Intake(hardwareMap: HardwareMap) {
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
        @JvmStatic var MIN_POWER     = -1.0
        @JvmStatic var MAX_POWER     = 1.0
        @JvmStatic var INTAKE_POWER  = 0.6
        @JvmStatic var OUTTAKE_POWER = -0.8
    }
}