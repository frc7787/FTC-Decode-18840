package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.util.RedPositions.AUDIENCE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.RedPositions.AUDIENCE_SPIKE_MARK_START
import org.firstinspires.ftc.teamcode.util.RedPositions.GATE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.RedPositions.GATE_SPIKE_MARK_START
import kotlin.math.PI

@Autonomous(group = "Red")
class RedAudience: LinearOpMode() {

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val timer = ElapsedTime()

    override fun runOpMode() {
        follower.setStartingPose(START)
        follower.setMaxPower(MAX_POWER)

        val paths = listOf(
            follower.pathBuilder()
                .addPath(BezierLine(START, SHOOT))
                .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(SHOOT, AUDIENCE_SPIKE_MARK_START))
                .setLinearHeadingInterpolation(SHOOT.heading, AUDIENCE_SPIKE_MARK_START.heading)
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(AUDIENCE_SPIKE_MARK_START, AUDIENCE_SPIKE_MARK_END))
                .setTangentHeadingInterpolation()
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(AUDIENCE_SPIKE_MARK_END, SHOOT))
                .setLinearHeadingInterpolation(AUDIENCE_SPIKE_MARK_END.heading, SHOOT.heading)
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(SHOOT, GATE_SPIKE_MARK_START))
                .setLinearHeadingInterpolation(SHOOT.heading, GATE_SPIKE_MARK_START.heading)
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(GATE_SPIKE_MARK_START, GATE_SPIKE_MARK_END))
                .setTangentHeadingInterpolation()
                .build(),
            follower.pathBuilder()
                .addPath(BezierLine(GATE_SPIKE_MARK_END, SHOOT))
                .setLinearHeadingInterpolation(GATE_SPIKE_MARK_END.heading, SHOOT.heading)
                .build(),
        )

        waitForStart()

        paths.forEach { path ->
            followPath(path)
            waitAndHold(path.endPose())
        }
    }

    private fun followPath(path: PathChain) {
        follower.followPath(path)
        follower.update()

        while (opModeIsActive() && !isStopRequested && follower.isBusy) {
            follower.update()
        }
    }

    private fun waitAndHold(point: Pose) {
        timer.reset()

        follower.holdPoint(point)
        while (timer.milliseconds() < SLEEP_MILLISECONDS && opModeIsActive() && !isStopRequested) {
            follower.update()
        }
    }

    private companion object {
        const val SLEEP_MILLISECONDS = 1000
        const val MAX_POWER = 0.6

        val START = Pose(88.0, 10.0, PI / 2.0)
        val SHOOT = Pose(83.0, 23.0, Math.toRadians(70.0))
    }
}