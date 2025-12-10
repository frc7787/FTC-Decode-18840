package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Spindexer

@TeleOp(group = "Test")
class SpindexerSlotTest: OpMode() {

    private val spindexer by lazy {
        Spindexer(hardwareMap)
    }

    override fun init() {
    }

    override fun loop() {
        if (gamepad1.left_bumper) {
            spindexer.slotOne()
        } else if (gamepad1.right_bumper) {
            spindexer.slotTwo()
        } else if (gamepad1.dpad_up) {
            spindexer.slotThree()
        }

        spindexer.update(telemetry)
    }
}