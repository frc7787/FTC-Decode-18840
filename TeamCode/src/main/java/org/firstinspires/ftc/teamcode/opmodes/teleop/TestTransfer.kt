package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@TeleOp(group = "Test")
class TestTransfer: OpMode() {

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    override fun init() {}

    override fun loop() {
        if (gamepad1.left_bumper) {
            transfer.up()
        } else {
            transfer.down()
        }
        transfer.update()
    }
}