package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.util.BluePositions.AUDIENCE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.AUDIENCE_SPIKE_MARK_START
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_START
import kotlin.math.PI

@Autonomous(group = "Blue")
class BlueAudience: LinearOpMode() {

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val timer = ElapsedTime()

    override fun runOpMode() {
        follower.setStartingPose(START)
        follower.setMaxPower(MAX_POWER)

        val startToShoot = follower.pathBuilder()
            .addPath(BezierLine(START, SHOOT))
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()

        val shootToAudienceSpikeMarkStart = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, AUDIENCE_SPIKE_MARK_START))
            .setLinearHeadingInterpolation(SHOOT.heading, AUDIENCE_SPIKE_MARK_START.heading)
            .build()

        val audienceSpikeMarkStartToEnd = follower.pathBuilder()
            .addPath(BezierLine(AUDIENCE_SPIKE_MARK_START, AUDIENCE_SPIKE_MARK_END))
            .setLinearHeadingInterpolation(AUDIENCE_SPIKE_MARK_START.heading, AUDIENCE_SPIKE_MARK_END.heading)
            .build()

        val audienceSpikeMarkEndToShoot = follower.pathBuilder()
            .addPath(BezierLine(AUDIENCE_SPIKE_MARK_END, SHOOT))
            .setLinearHeadingInterpolation(AUDIENCE_SPIKE_MARK_END.heading, SHOOT.heading)
            .build()

        val shootToGateSpikeMarkStart = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, GATE_SPIKE_MARK_START))
            .setLinearHeadingInterpolation(SHOOT.heading, GATE_SPIKE_MARK_START.heading)
            .build()

        val gateSpikeMarkStartToEnd = follower.pathBuilder()
            .addPath(BezierLine(GATE_SPIKE_MARK_START, GATE_SPIKE_MARK_END))
            .setLinearHeadingInterpolation(GATE_SPIKE_MARK_START.heading, GATE_SPIKE_MARK_END.heading)
            .build()

        val gateSpikeMarkEndToShoot = follower.pathBuilder()
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
        followPath(shootToAudienceSpikeMarkStart)
        waitAndHold(AUDIENCE_SPIKE_MARK_START)
        followPath(audienceSpikeMarkStartToEnd)
        waitAndHold(AUDIENCE_SPIKE_MARK_END)
        followPath(audienceSpikeMarkEndToShoot)
        waitAndHold(SHOOT)
        followPath(shootToGateSpikeMarkStart)
        waitAndHold(GATE_SPIKE_MARK_START)
        followPath(gateSpikeMarkStartToEnd)
        waitAndHold(GATE_SPIKE_MARK_END)
        followPath(gateSpikeMarkEndToShoot)
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

    private companion object {
        val START = Pose(56.0, 6.0, PI)
        val SHOOT = Pose(61.0, 23.0, Math.toRadians(110.0))

        val END = Pose(50.0, 23.0, PI)

        const val SLEEP_MILLISECONDS = 1000
        const val MAX_POWER = 0.6
    }
}