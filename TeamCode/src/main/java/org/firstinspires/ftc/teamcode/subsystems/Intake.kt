package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap

class Intake(hardwareMap: HardwareMap) {
    private val motor  = hardwareMap["intakeMotor"] as DcMotorEx

    private var power: Double = 0.0

    fun runAtPower(power: Double) {
        this.power = power
    }

    fun update() {
        motor.power = power
    }
}