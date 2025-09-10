package org.firstinspires.ftc.teamcode.opmodes.test

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.groups.Sequential

@Autonomous(group = "Test")
@Mercurial.Attach
class TestAuto: OpMode() {

    private val startPose   = Pose(0.0, 0.0)
    private val testPoseOne = Pose(12.0, 15.0)
    private val testPoseTwo = Pose(20.0, 18.0)
    private val endPose     = Pose(-2.0, -5.0)

    override fun start() {
        val testPathOne = DriveBase.follower.pathBuilder()
            .addPath(BezierLine(startPose, testPoseOne))
            .setLinearHeadingInterpolation(startPose.heading, endPose.heading)
            .build()

        val testPathTwo = DriveBase.follower.pathBuilder()
            .addPath(BezierLine(testPoseOne, testPoseTwo))
            .setLinearHeadingInterpolation(testPoseOne.heading, testPoseTwo.heading)
            .build()

        val testPathThree = DriveBase.follower.pathBuilder()
            .addPath(BezierLine(testPoseTwo, endPose))
            .setLinearHeadingInterpolation(testPoseTwo.heading, endPose.heading)
            .build()

        Sequential(
            DriveBase.followPath(testPathOne, false),
            DriveBase.followPath(testPathTwo, false),
            DriveBase.followPath(testPathThree, false)
        ).schedule()
    }

    override fun init() {}

    override fun loop() {}
}