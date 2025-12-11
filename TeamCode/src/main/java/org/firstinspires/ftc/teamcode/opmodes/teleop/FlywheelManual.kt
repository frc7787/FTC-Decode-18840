package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Flywheel

@TeleOp(group = "Test")
class FlywheelManual: OpMode() {

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    override fun init() {
    }

    override fun loop() {
        val power = gamepad1.left_stick_y
        flywheel.powerFlywheel(power.toDouble())
        telemetry.addLine("Power: $power")
    }
}