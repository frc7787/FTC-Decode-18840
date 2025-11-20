package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@TeleOp(group = "$")
class TestShooter: OpMode() {

    private val shooter by lazy {
        Shooter(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    override fun init() {}

    override fun loop() {
        if (gamepad1.left_bumper && !gamepad1.leftBumperWasPressed()) {
            transfer.up()
            shooter.runAtVelocity(rpm = 1900.0, seconds = 2.0)
        }

        shooter.update()
        shooter.debug(telemetry)
        displayControls()
    }

    private fun displayControls() {
        telemetry.addLine()
        telemetry.addLine("Hold Left Bumper To Run The Shooter")
    }

}