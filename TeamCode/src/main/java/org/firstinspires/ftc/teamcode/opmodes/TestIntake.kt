package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Intake

@TeleOp(group = "Test")
class TestIntake: OpMode() {

    private val intake by lazy {
        Intake(hardwareMap)
    }

    override fun init() {}

    override fun loop() {
        val intakePower = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()
        intake.update(power = intakePower)
        telemetry.addLine("Control the intake with the left and right triggers")
        intake.debug(telemetry)
    }
}