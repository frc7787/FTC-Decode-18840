package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.commands.Commands.conditional
import com.pedropathing.ivy.groups.Groups.*
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedropathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.*
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {

    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val camera by lazy {
        Camera(hardwareMap)
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    override fun init() {
        schedule(
            conditional(
                { flywheelToggle },
                flywheel.spinAt(3000.0),
                flywheel.spinAt(0.0)
            )
        )
    }

    private var flywheelToggle = false

    override fun loop() {
        camera.detectedTags().forEach { detection ->
            telemetry.addLine("Tag Detected With ID: ${detection.id}")
        }

        if (gamepad1.circleWasReleased()) {
            schedule(
                parallel(
                    transfer.up(),
                    spindexer.power(0.5)
                )
            )
        }

        if (gamepad1.triangleWasReleased()) {
            schedule(
                deadline(
                    spindexer.toNextIntakingPosition(),
                    transfer.down()
                )
            )
        }

        if (gamepad1.leftBumperWasReleased()) {
            schedule(intake.intake())
        }

        if (gamepad1.rightBumperWasReleased()) {
            schedule(intake.intake())
        }

        if (gamepad1.squareWasReleased()) {
            flywheelToggle = !flywheelToggle
        }

        follower.setTeleOpDrive(
            run {
                val raw = -gamepad1.left_stick_y.toDouble()
                raw * abs(raw)
            },
            run {
                val raw = gamepad1.left_stick_x.toDouble()
                raw * abs(raw)
            },
            run {
                val raw = gamepad1.right_stick_x.toDouble()
                raw * abs(raw)
            }
        )

        Scheduler.execute()
    }
}