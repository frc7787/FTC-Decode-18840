package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@TeleOp(group = "Test")
@Disabled
class FlywheelRPMTesting: OpMode() {

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap) { 0 }
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private var target = 3000.0

    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    override fun init() {}

    override fun loop() {
        previousGamepad.copy(currentGamepad)
        currentGamepad.copy(gamepad1)

        flywheel.spinUp(target)

        if (currentGamepad.dpad_up && !previousGamepad.dpad_up) {
            target += 100
        }

        if (currentGamepad.dpad_down && !previousGamepad.dpad_down) {
            target -= 100
        }

        telemetry.addLine("Velocity: ${flywheel.rpm}")
        telemetry.addLine("Target Velocity: $target")
        telemetry.addLine("Power: ${flywheel.power}")

        if (gamepad1.left_bumper) {
            transfer.up()
        } else {
            transfer.down()
        }

        spindexer.setPower(-gamepad1.left_stick_y.toDouble())
        spindexer.update(telemetry)

        flywheel.update()
    }
}