package org.firstinspires.ftc.teamcode.opmodes.test

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Autonomous(group = "Test")
class TestStraightLine: LinearOpMode() {

    private companion object {
        val START_POSE = Pose(72.0, 72.0, PI / 2.0)
        val END_POSE   = Pose(72.0, 82.0, PI / 2.0)
    }

    override fun runOpMode() {
        val follower = Constants.createFollower(hardwareMap)
        follower.setStartingPose(START_POSE)

        val straightPath = follower.pathBuilder()
            .addPath(BezierLine(START_POSE, END_POSE))
            .setLinearHeadingInterpolation(START_POSE.heading, END_POSE.heading)
            .build()

        waitForStart()

        follower.followPath(straightPath)

        while (opModeIsActive() && !isStopRequested) {
            follower.update()
        }
    }
}