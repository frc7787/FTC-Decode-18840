package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.abs

@TeleOp(group = "Test")
@Disabled
class TestMecanumDrive: OpMode() {

    private val mecanumDrive by lazy {
        Constants.createFollower(hardwareMap)
    }

    override fun init() {}

    override fun start() {
        mecanumDrive.startTeleOpDrive(true)
    }

    override fun loop() {
        val drive = run {
            val raw = gamepad1.left_stick_y.toDouble()
            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
        }

        val strafe = run {
            val raw = gamepad1.left_stick_x.toDouble()
            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
        }

        val turn = run {
            val raw = gamepad1.right_stick_x.toDouble()
            if (abs(raw) < 0.05) 0.0 else (raw * abs(raw))
        }

        mecanumDrive.setTeleOpDrive(drive, strafe, turn)
    }
}