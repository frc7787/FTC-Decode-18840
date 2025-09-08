package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.subsytems.DriveBase

@TeleOp(group = "$")
@DriveBase.Attach
@Mercurial.Attach
class TeleOp: OpMode() {

    override fun init() {
        DriveBase.defaultCommand = DriveBase.drive(
            { -gamepad1.left_stick_y.toDouble() },
            { gamepad1.left_stick_x.toDouble()  },
            { gamepad1.right_stick_x.toDouble() }
        )
    }

    override fun loop() {}
}