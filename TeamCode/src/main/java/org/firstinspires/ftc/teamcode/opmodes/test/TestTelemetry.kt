package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(group = "Test")
class TestTelemetry: OpMode() {

    override fun init() {
        telemetry.setDisplayFormat(HTML)
        telemetry.addLine(
            "<img src=java/org/firstinspires/ftc/teamcode/subsystems/IMG_4331.jpg alt=Foo>"
        )
    }

    override fun loop() {}
}