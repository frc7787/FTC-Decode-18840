package org.firstinspires.ftc.teamcode.opmodes.test

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Autonomous(group = "Test")
class TestAuto: LinearOpMode() {

    private companion object {
        val START_POSE = Pose(56.0, 88.0, PI / 2.0)
        val SHOOT_POSE = Pose(56.0, 99.0, Math.toRadians(145.0))

        val SPIKE_MARK_GOAL_START = Pose(42.0, 84.0, PI)
        val SPIKE_MARK_GOAL_END   = Pose(27.0, 84.0, PI)

        val END_POSE = Pose(56.0, 105.0, PI)
    }

    override fun runOpMode() {
        val follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(START_POSE)

        val path = follower.pathBuilder()
            .addPath(BezierLine(START_POSE, SHOOT_POSE))
            .setLinearHeadingInterpolation(START_POSE.heading, SHOOT_POSE.heading)
            .addPath(BezierLine(SHOOT_POSE, SPIKE_MARK_GOAL_START))
            .setLinearHeadingInterpolation(SHOOT_POSE.heading, SPIKE_MARK_GOAL_START.heading)
            .addPath(BezierLine(SPIKE_MARK_GOAL_START, SPIKE_MARK_GOAL_END))
            .setLinearHeadingInterpolation(SPIKE_MARK_GOAL_START.heading, SPIKE_MARK_GOAL_END.heading)
            .addPath(BezierLine(SPIKE_MARK_GOAL_END, SHOOT_POSE))
            .setLinearHeadingInterpolation(SPIKE_MARK_GOAL_END.heading, SHOOT_POSE.heading)
            .addPath(BezierLine(SHOOT_POSE, END_POSE))
            .setLinearHeadingInterpolation(START_POSE.heading, END_POSE.heading)
            .build()

        waitForStart()

        follower.followPath(path)

        while (opModeIsActive() && !isStopRequested) {
            follower.update()
        }
    }
}