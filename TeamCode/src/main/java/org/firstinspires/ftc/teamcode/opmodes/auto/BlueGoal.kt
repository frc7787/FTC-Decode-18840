package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.util.BluePositions
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

@Autonomous(group = "Blue")
class BlueGoal: OpMode() {

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val startToShoot = {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    START, SHOOT
                )
            )
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()!!
    }

    private val shootToGoalSpikeMarkStart = {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    SHOOT, BluePositions.GOAL_SPIKE_MARK_START
                )
            )
            .setLinearHeadingInterpolation(SHOOT.heading, BluePositions.GOAL_SPIKE_MARK_START.heading)
            .build()!!
    }

    private val goalSpikeMarkStartToEnd = {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    BluePositions.GOAL_SPIKE_MARK_START, BluePositions.GOAL_SPIKE_MARK_END
                )
            )
            .setTangentHeadingInterpolation()
            .build()!!
    }

    private val goalSpikeMarkEndToShoot = {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    BluePositions.GOAL_SPIKE_MARK_END, SHOOT
                )
            )
            .setLinearHeadingInterpolation(BluePositions.GOAL_SPIKE_MARK_END.heading, SHOOT.heading)
            .build()!!
    }

    private val shootToEnd = {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    SHOOT, END
                )
            )
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()!!
    }

    private var state: State = State.START

    override fun init() {
        follower.setMaxPower(MAX_POWER)
    }

    override fun start() {
        follower.setStartingPose(START)
    }

    override fun loop() {
        when (state) {
            State.START -> {
                follower.followPath(startToShoot.invoke(), true)
                state = state.next()
            }
            State.TO_SHOOT_PRELOAD -> {
                if (!follower.isBusy) {
                    state = state.next()
                }
            }
            State.SHOOTING_PRELOAD -> {
                telemetry.addLine("It is finished!")
            }
            else -> {
                telemetry.addLine("How Did We Get Here?")
            }
        }
    }

    private companion object {
        val START = Pose(19.0, 122.0, Math.toRadians(53.0))
        val SHOOT = Pose(56.0, 99.0, Math.toRadians(143.0))

        val END = Pose(56.0, 105.0, PI)

        const val MAX_POWER = 0.6
    }

    private enum class State {
        START,
        TO_SHOOT_PRELOAD,
        SHOOTING_PRELOAD,
        TO_INTAKE_GOAL,
        INTAKING_GOAL,
        TO_SHOOT_PICKUP,
        SHOOTING_PICKUP,
        TO_END,
        END;

        fun next(): State {
            return entries[min(entries.size - 1, ordinal + 1)]
        }

        fun previous(): State {
            return entries[max(0, ordinal - 1)]
        }
    }
}