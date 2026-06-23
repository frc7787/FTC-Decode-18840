package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.commands.Commands.conditional
import com.pedropathing.ivy.groups.Groups.deadline
import com.pedropathing.ivy.groups.Groups.parallel
import com.pedropathing.ivy.groups.Groups.sequential
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {

    private val intake by lazy {
        Intake.get(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel.get(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer.get(hardwareMap)
    }

    private val transfer by lazy {
        Transfer.get(hardwareMap)
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

        if (gamepad1.squareWasReleased()) flywheelToggle = !flywheelToggle

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