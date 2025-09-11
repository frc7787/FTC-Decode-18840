package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.subsytems.Intake
import org.firstinspires.ftc.teamcode.subsytems.TurnTable
import org.firstinspires.ftc.teamcode.subsytems.Turret

@TeleOp(group = "$")
@Intake.Attach
@TurnTable.Attach
@Turret.Attach
@Mercurial.Attach
class TeleOp: OpMode() {

    override fun init() {}

    override fun start() {
        Turret.home().schedule()
    }

    override fun loop() {}
}