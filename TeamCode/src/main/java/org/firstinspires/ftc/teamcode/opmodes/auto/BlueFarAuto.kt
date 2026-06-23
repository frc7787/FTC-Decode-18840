package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.deadline
import com.pedropathing.ivy.groups.Groups.parallel
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.pedropathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@Autonomous(group = "Blue")
class BlueFarAuto: LinearOpMode() {

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

    private val startToShootPath by lazy {
        PathChain(
            Path(
                BezierLine(
                    Pose(0.0, 0.0, 0.0),
                    Pose(10.0, 10.0, 10.0)
                )
            )
        )
    }

    override fun runOpMode() {
        schedule(
            parallel(
                sequential(
                    follow(follower, startToShootPath),
                    deadline(
                        waitMs(1000.0),
                        transfer.up(),
                        spindexer.power(0.5)
                    )
                ),
                flywheel.spinAt(2000.0)
            )
        )
    }
}