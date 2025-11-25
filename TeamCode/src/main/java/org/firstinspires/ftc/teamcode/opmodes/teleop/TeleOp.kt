package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {

    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val shooter by lazy {
        Shooter(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private var shooterToggle = false

    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    override fun init() {
        follower.setStartingPose(Pose())
    }

    override fun start() {
        follower.startTeleOpDrive()
    }

    override fun loop() {
        previousGamepad.copy(currentGamepad)
        currentGamepad.copy(gamepad2)

        // Drive

        val drive = run {
            val raw = gamepad1.left_stick_y.toDouble()
            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
        }

        val strafe = run {
            val raw = gamepad1.left_stick_x.toDouble()
            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
        }

        val turn = run {
            val raw = gamepad1.right_stick_x.toDouble()
            if (abs(raw) < 0.05) 0.0 else (raw * abs(raw)) * 0.9
        }

        follower.setTeleOpDrive(drive, strafe, turn)

        // Shooter

        if (currentGamepad.left_bumper && !previousGamepad.left_bumper) {
            shooterToggle = !shooterToggle
        }

        if (shooterToggle) {
            shooter.spinUp()
        } else {
            shooter.stop()
        }

        // Intake

        intake.power = (gamepad2.left_trigger - gamepad2.right_trigger)
            .toDouble()
            .coerceIn(-1.0, 0.8)

        // Transfer

        if (currentGamepad.right_bumper) {
            transfer.up()
        } else {
            transfer.down()
        }

        shooter.update(0.0)
        intake.update()
        transfer.update()

        follower.update()
    }
}