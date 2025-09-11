package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import org.firstinspires.ftc.teamcode.subsytems.Turret

@Turret.Attach
@Mercurial.Attach
@TeleOp(group = "Test")
class TestConfigLoading: OpMode() {

    override fun init() {
        val config = Turret.configuration
        telemetry.addLine("Debug: ${config.debug}")
        telemetry.addLine("P: ${config.pidfCoefficients.p}")
        telemetry.addLine("Homing Direction: ${config.homingDirection}")
    }

    override fun loop() {

    }
}