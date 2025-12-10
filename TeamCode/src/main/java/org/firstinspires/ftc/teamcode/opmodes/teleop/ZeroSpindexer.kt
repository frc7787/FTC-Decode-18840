package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Spindexer

@TeleOp(group = "$")
class ZeroSpindexer: OpMode() {

    private val spindexer by lazy {
        Spindexer(hardwareMap)
    }

    override fun init() {}

    override fun loop() {
        if (gamepad1.left_bumper) {
            spindexer.reset()
        }
        telemetry.addLine("Position: ${spindexer.getPosition()}")
    }
}