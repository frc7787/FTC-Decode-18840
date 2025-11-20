package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Shooter
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

        val leftStickY  = gamepad1.left_stick_y.toDouble()
        val leftStickX  = gamepad1.left_stick_x.toDouble()
        val rightStickX = gamepad1.right_stick_x.toDouble()

        follower.setTeleOpDrive(
            leftStickY * abs(leftStickY),
            leftStickX * abs(leftStickX),
            rightStickX * abs(rightStickX)
        )

        // Shooter

        if (currentGamepad.left_bumper && !previousGamepad.left_bumper) {
            shooterToggle = !shooterToggle
        }

        if (shooterToggle) {
            shooter.runAtPower(1.0)
        } else {
            shooter.runAtPower(0.0)
        }

        // Intake

        val intakePower = (gamepad2.left_trigger - gamepad2.right_trigger).toDouble()
        intake.runAtPower(intakePower)

        // Transfer

        if (currentGamepad.right_bumper) {
            transfer.up()
        } else {
            transfer.down()
        }

        //

        shooter.debug(telemetry)
        transfer.debug(telemetry)
        intake.debug(telemetry)

        transfer.update()
        intake.update()
        shooter.update()
        follower.update()
    }
}