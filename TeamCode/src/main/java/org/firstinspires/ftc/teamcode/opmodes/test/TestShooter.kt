package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.subsytems.Shooter

@Shooter.Attach
@Mercurial.Attach
@TeleOp(group = "Test")
class TestShooter: OpMode() {

    override fun init() {}

    override fun loop() {}
}