package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {

    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap)
    }


    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    private var transferActive       = false
    private var transferTimerStarted = false

    private var shootingNear = false
    private var flywheelActive = false

    private var spindexerIndex = 1

    private val timer = ElapsedTime()

    override fun init() {
        //follower.setStartingPose(Pose())
    }

    override fun start() {
        //follower.startTeleOpDrive()
    }

    override fun loop() {
        previousGamepad.copy(currentGamepad)
        currentGamepad.copy(gamepad2)

        // Drive

//        val drive = run {
//            val raw = gamepad1.left_stick_y.toDouble()
//            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
//        }
//
//        val strafe = run {
//            val raw = -gamepad1.left_stick_x.toDouble()
//            if (abs(raw) < 0.05) 0.0 else raw * abs(raw)
//        }
//
//        val turn = run {
//            val raw = -gamepad1.right_stick_x.toDouble()
//            if (abs(raw) < 0.05) 0.0 else (raw * abs(raw)) * 0.9
//        }

//        follower.setTeleOpDrive(drive, strafe, turn)
//        follower.update()

        // Shooter

        // Intake

        if (currentGamepad.triangle && !previousGamepad.triangle) {
             shootingNear = !shootingNear
        }

        if (shootingNear) {
            telemetry.addLine("Near")
        } else {
            telemetry.addLine("Far")
        }

        // Flywheel

        if (currentGamepad.left_bumper && !previousGamepad.left_bumper) {
            flywheelActive = !flywheelActive
        }

        if (flywheelActive) {
            if (shootingNear) {
                flywheel.setPower(0.5)
            } else {
                flywheel.setPower(1.0)
            }
        } else {
            flywheel.setPower(0.0)
        }

        // Transfer

        if (currentGamepad.right_bumper && !previousGamepad.right_bumper && !transferTimerStarted) {
            transferActive = true
        }

        if (transferActive) {
            if (!transferTimerStarted) {
                transfer.up()
                timer.reset()
                transferTimerStarted = true
            }
            if (timer.seconds() > 0.3) {
                transfer.down()
                transferTimerStarted = false
                transferActive = false
            }
        }

        transfer.debug(telemetry, verbose = true)

        // Intake

        intake.power = (gamepad2.left_trigger - gamepad2.right_trigger).toDouble()

        // Spindexer

        if (currentGamepad.dpad_right && !previousGamepad.dpad_right) {
            spindexerIndex++
            if (spindexerIndex > 3) {
                spindexerIndex = 1
            }
        }

        if (currentGamepad.dpad_left && !previousGamepad.dpad_left) {
            spindexerIndex--
            if (spindexerIndex < 1) {
                spindexerIndex = 3
            }
        }

        val spindexerPower = currentGamepad.right_stick_x.toDouble()

        if (spindexerPower != 0.0) {
            spindexer.setPower(spindexerPower)
        } else {
            when (spindexerIndex) {
                1 -> spindexer.slotOne()
                2 -> spindexer.slotTwo()
                3 -> spindexer.slotThree()
            }
        }

        telemetry.addLine("Index: $spindexerIndex")

        spindexer.update(telemetry)
    }
}