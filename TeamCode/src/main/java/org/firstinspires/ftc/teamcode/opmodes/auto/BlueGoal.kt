package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_START
import org.firstinspires.ftc.teamcode.util.BluePositions.GOAL_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.GOAL_SPIKE_MARK_START
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Autonomous(group = "Test")
class BlueGoal: LinearOpMode() {

    private companion object {
        val START = Pose(19.0, 122.0, Math.toRadians(53.0))
        val SHOOT = Pose(56.0, 99.0, Math.toRadians(143.0))

        val END = Pose(56.0, 105.0, PI)

        const val SLEEP_MILLISECONDS = 1000
        const val MAX_POWER = 0.6
    }

    private val timer = ElapsedTime()

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    override fun runOpMode() {
        follower.setMaxPower(MAX_POWER)
        follower.setStartingPose(START)

        val startToShoot = follower.pathBuilder()
            .addPath(BezierLine(START, SHOOT))
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()

        val shootToGoalSpikeMarkStart = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, GOAL_SPIKE_MARK_START))
            .setLinearHeadingInterpolation(SHOOT.heading, GOAL_SPIKE_MARK_START.heading)
            .build()

        val goalSpikeMarkStartToEnd = follower.pathBuilder()
            .addPath(BezierLine(GOAL_SPIKE_MARK_START, GOAL_SPIKE_MARK_END))
            .setLinearHeadingInterpolation(GOAL_SPIKE_MARK_START.heading, GOAL_SPIKE_MARK_END.heading)
            .build()

        val spikeMarkGoalEndToShoot = follower.pathBuilder()
            .addPath(BezierLine(GOAL_SPIKE_MARK_END, SHOOT))
            .setLinearHeadingInterpolation(GOAL_SPIKE_MARK_END.heading, SHOOT.heading)
            .build()

        val shootToGateSpikeMark = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, GATE_SPIKE_MARK_START))
            .setLinearHeadingInterpolation(SHOOT.heading, GATE_SPIKE_MARK_START.heading)
            .build()

        val spikeMarkGateStartToEnd = follower.pathBuilder()
            .addPath(BezierLine(GATE_SPIKE_MARK_START, GATE_SPIKE_MARK_END))
            .setLinearHeadingInterpolation(GATE_SPIKE_MARK_START.heading, GATE_SPIKE_MARK_END.heading)
            .build()

        val spikeMarkGateEndToShoot = follower.pathBuilder()
            .addPath(BezierLine(GATE_SPIKE_MARK_END, SHOOT))
            .setLinearHeadingInterpolation(GATE_SPIKE_MARK_END.heading, SHOOT.heading)
            .build()

        val shootToEnd = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, END))
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()

        waitForStart()

        followPath(startToShoot)
        waitAndHold(SHOOT)
        followPath(shootToGoalSpikeMarkStart)
        waitAndHold(GOAL_SPIKE_MARK_START)
        followPath(goalSpikeMarkStartToEnd)
        waitAndHold(GOAL_SPIKE_MARK_END)
        followPath(spikeMarkGoalEndToShoot)
        waitAndHold(SHOOT)
        followPath(shootToGateSpikeMark)
        waitAndHold(GATE_SPIKE_MARK_START)
        followPath(spikeMarkGateStartToEnd)
        waitAndHold(GATE_SPIKE_MARK_END)
        followPath(spikeMarkGateEndToShoot)
        waitAndHold(SHOOT)
        followPath(shootToEnd)
        waitAndHold(END)
    }

    private fun followPath(path: PathChain) {
        telemetry.addLine("Following Path")
        follower.followPath(path)
        follower.update()

        while (opModeIsActive() && !isStopRequested && follower.isBusy) {
            follower.update()
        }
    }

    private fun waitAndHold(point: Pose) {
        telemetry.addLine("Holding position: $point")
        telemetry.update()
        timer.reset()

        follower.holdPoint(point)
        while (timer.milliseconds() < SLEEP_MILLISECONDS && opModeIsActive() && !isStopRequested) {
            follower.update()
        }
    }
}