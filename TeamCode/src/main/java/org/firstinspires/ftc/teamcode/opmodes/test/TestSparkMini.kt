package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorSimple

@Autonomous(group = "Test")
@Disabled
class TestSparkMini: OpMode() {

    private val motor by lazy {
        hardwareMap["testSparkMini"] as DcMotorSimple
    }

    override fun init() {}

    override fun loop() {
        motor.power = 1.0
    }
}