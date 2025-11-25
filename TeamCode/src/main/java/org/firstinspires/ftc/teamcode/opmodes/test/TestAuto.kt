package org.firstinspires.ftc.teamcode.opmodes.test

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Intake
import kotlin.math.PI

@Autonomous(group = "Test")
class TestAuto: LinearOpMode() {

    private companion object {
        val START = Pose(56.0, 88.0, PI / 2.0)
        val SHOOT = Pose(56.0, 99.0, Math.toRadians(145.0))

        val SPIKE_MARK_GOAL_START = Pose(42.0, 84.0, PI)
        val SPIKE_MARK_GOAL_END   = Pose(27.0, 84.0, PI)

        val END = Pose(56.0, 105.0, PI)

        const val SLEEP_MILLISECONDS = 1000L
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    override fun runOpMode() {
        val intake = Intake(hardwareMap)

        val pathBuilder = follower.pathBuilder()!!
        follower.setStartingPose(START)

        val startToShoot = pathBuilder
            .addPath(BezierLine(START, SHOOT))
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()

        val shootToGoalSpikeMarkStart = pathBuilder
            .addPath(BezierLine(SHOOT, SPIKE_MARK_GOAL_START))
            .setLinearHeadingInterpolation(SHOOT.heading, SPIKE_MARK_GOAL_START.heading)
            .build()

        val goalSpikeMarkStartToGoalSpikeMarkEnd = pathBuilder
            .addPath(BezierLine(SPIKE_MARK_GOAL_START, SPIKE_MARK_GOAL_END))
            .setLinearHeadingInterpolation(SPIKE_MARK_GOAL_START.heading, SPIKE_MARK_GOAL_END.heading)
            .build()

        val spikeMarkGoalEndToShoot = pathBuilder
            .addPath(BezierLine(SPIKE_MARK_GOAL_END, SHOOT))
            .setLinearHeadingInterpolation(SPIKE_MARK_GOAL_END.heading, SHOOT.heading)
            .build()

        val shootToEnd = pathBuilder
            .addPath(BezierLine(SHOOT, END))
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()

        waitForStart()

        followPath(startToShoot)

        // Shoot

        followPath(shootToGoalSpikeMarkStart)

        followPath(spikeMarkGoalEndToShoot)

        // Shoot

        followPath(shootToEnd)
    }

    private fun followPath(path: PathChain) {
        follower.followPath(path)

        while (opModeIsActive() && !isStopRequested && follower.isBusy) {
            follower.update()
        }

        sleep(SLEEP_MILLISECONDS)
    }

}