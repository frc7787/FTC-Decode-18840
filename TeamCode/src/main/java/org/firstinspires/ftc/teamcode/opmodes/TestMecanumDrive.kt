package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.abs

@TeleOp(group = "Test")
class TestMecanumDrive: OpMode() {

    private val mecanumDrive by lazy {
        Constants.createFollower(hardwareMap)
    }

    override fun init() {

    }

    override fun start() {
        mecanumDrive.startTeleOpDrive(true)
    }

    override fun loop() {
        var drivePower = -gamepad1.left_stick_y.toDouble()
        drivePower *= abs(drivePower)
        var strafePower = gamepad1.left_stick_x.toDouble()
        strafePower *= abs(strafePower)
        var turnPower = gamepad1.right_stick_x.toDouble()
        turnPower *= abs(turnPower)
        mecanumDrive.setTeleOpDrive(drivePower, strafePower, turnPower)
    }
}