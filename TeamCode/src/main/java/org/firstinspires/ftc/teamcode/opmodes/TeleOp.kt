package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.features.OctoQuad
import org.firstinspires.ftc.teamcode.subsytems.DriveBase
import org.firstinspires.ftc.teamcode.subsytems.Intake
import org.firstinspires.ftc.teamcode.subsytems.TurnTable

@TeleOp(group = "$")
@OctoQuad.Attach
@DriveBase.Attach
@Intake.Attach
@TurnTable.Attach
@Mercurial.Attach
class TeleOp: OpMode() {

    override fun init() {
        DriveBase.defaultCommand = DriveBase.drive(
            Mercurial.gamepad1.leftStickY,
            Mercurial.gamepad1.leftStickX,
            Mercurial.gamepad1.rightStickX
        )
    }

    override fun loop() {}
}