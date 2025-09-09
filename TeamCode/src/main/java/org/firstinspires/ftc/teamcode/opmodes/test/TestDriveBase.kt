package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.subsytems.DriveBase

@DriveBase.Attach
@Mercurial.Attach
@TeleOp(group = "Test")
class TestDriveBase: OpMode() {

    override fun init() {
        DriveBase.defaultCommand = DriveBase.drive(
            Mercurial.gamepad1.leftStickY,
            Mercurial.gamepad1.leftStickX,
            Mercurial.gamepad1.rightStickX
        )
    }

    override fun loop() {}
}