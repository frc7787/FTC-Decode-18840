package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.util.BluePositions
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Autonomous(group = "Blue")
class BlueGoal: OpMode() {

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val path = follower.pathBuilder()
        .addPath(
            BezierLine(
                START, SHOOT
            )
        )
        .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
        .addPath(
            BezierLine(
                SHOOT, BluePositions.GOAL_SPIKE_MARK_START
            )
        )
        .setLinearHeadingInterpolation(SHOOT.heading, BluePositions.GOAL_SPIKE_MARK_START.heading)
        .addPath(
            BezierLine(
                BluePositions.GOAL_SPIKE_MARK_START, BluePositions.GOAL_SPIKE_MARK_END
            )
        )
        .setTangentHeadingInterpolation()
        .addPath(
            BezierLine(
                BluePositions.GOAL_SPIKE_MARK_END, SHOOT
            )
        )
        .setLinearHeadingInterpolation(BluePositions.GOAL_SPIKE_MARK_END.heading, SHOOT.heading)
        .addPath(
            BezierLine(
                SHOOT,
                BluePositions.GATE_SPIKE_MARK_START
            )
        )
        .setLinearHeadingInterpolation(SHOOT.heading, BluePositions.GATE_SPIKE_MARK_START.heading)
        .addPath(
            BezierLine(
                BluePositions.GATE_SPIKE_MARK_START,
                BluePositions.GATE_SPIKE_MARK_END
            )
        )
        .setTangentHeadingInterpolation()
        .addPath(
            BezierLine(
                BluePositions.GATE_SPIKE_MARK_END, SHOOT
            )
        )
        .setLinearHeadingInterpolation(BluePositions.GATE_SPIKE_MARK_END.heading, SHOOT.heading)
        .addPath(
            BezierLine(
                SHOOT, END
            )
        )
        .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
        .build()!!

    override fun init() {
        follower.setMaxPower(MAX_POWER)
    }

    override fun start() {
        follower.setStartingPose(START)
        follower.followPath(path)
    }

    override fun loop() {
        follower.update()
        if (!follower.isBusy) {
            telemetry.addLine("Finished Path!")
        }
    }

    private companion object {
        val START = Pose(19.0, 122.0, Math.toRadians(53.0))
        val SHOOT = Pose(56.0, 99.0, Math.toRadians(143.0))

        val END = Pose(56.0, 105.0, PI)

        const val MAX_POWER = 0.6
    }
}