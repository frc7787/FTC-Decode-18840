package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Spindexer

@TeleOp(group = "Test")
@Disabled
class SpindexerSlotTest: OpMode() {

    private val octoquad by lazy {
        (hardwareMap["octoquad"] as OctoQuad).also { octoquad ->
            octoquad.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_PULSE_WIDTH
        }
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap) { octoquad.readSinglePosition_Caching(0) }
    }

    override fun init() {
    }

    override fun loop() {
        spindexer.setPower(gamepad1.left_stick_y.toDouble())

        spindexer.update(telemetry)
    }
}