package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.util.BluePositions
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@Autonomous(group = "Blue")
class BlueGoal: OpMode() {

    private val follower by lazy {
        Constants.createFollower(hardwareMap)!!
    }

    private val startToShoot by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    START, SHOOT
                )
            )
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .setVelocityConstraint(0.8)
            .build()!!
    }

    private val shootToIntake by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    SHOOT, BluePositions.GOAL_SPIKE_MARK_START
                )
            )
            .setLinearHeadingInterpolation(SHOOT.heading, BluePositions.GOAL_SPIKE_MARK_START.heading)
            .setVelocityConstraint(0.8)
            .build()!!
    }

    private val goalSpikeMarkStartToEnd by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    BluePositions.GOAL_SPIKE_MARK_START, BluePositions.GOAL_SPIKE_MARK_END
                )
            )
            .setTangentHeadingInterpolation()
            .setVelocityConstraint(0.4)
            .build()!!
    }

    private val goalSpikeMarkToShoot by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    BluePositions.GOAL_SPIKE_MARK_END, SHOOT
                )
            )
            .setLinearHeadingInterpolation(BluePositions.GOAL_SPIKE_MARK_START.heading, SHOOT.heading)
            .setVelocityConstraint(0.8)
            .build()!!
    }

    private val shootToEnd by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    SHOOT, END
                )
            )
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()!!
    }

    private val timer = ElapsedTime()

    private var state = State.START_TO_SHOOT_PRELOAD

    override fun init() {
        follower.setMaxPower(MAX_POWER)
    }

    override fun start() {
        follower.setStartingPose(START)
        follower.followPath(startToShoot)
    }

    override fun loop() {
        when (state) {
            State.START_TO_SHOOT_PRELOAD -> {
                if (!follower.isBusy) {
                    follower.holdPoint(startToShoot.endPoint())
                    timer.reset()
                    state = State.SHOOTING_PRELOAD
                }
            }
            State.SHOOTING_PRELOAD -> {
                if (timer.seconds() > 1.5) {
                    follower.followPath(shootToIntake)
                    state = State.SHOOT_TO_INTAKE_GOAL
                }
            }
            State.SHOOT_TO_INTAKE_GOAL -> {
                if (!follower.isBusy) {
                    follower.followPath(goalSpikeMarkStartToEnd)
                    state = State.INTAKE_GOAL
                }
            }
            State.INTAKE_GOAL -> {
                if (!follower.isBusy) {
                    follower.followPath(goalSpikeMarkToShoot)
                    state = State.INTAKE_GOAL_TO_SHOOT
                }
            }
            State.INTAKE_GOAL_TO_SHOOT -> {
                if (!follower.isBusy) {
                    timer.reset()
                    follower.holdPoint(goalSpikeMarkToShoot.endPoint())
                    state = State.SHOOTING_PICKUP
                }
            }
            State.SHOOTING_PICKUP -> {
                if (timer.seconds() > 1.5) {
                    follower.followPath(shootToEnd)
                    state = State.PARK
                }
            }
            State.PARK -> {
                if (!follower.isBusy) {
                    follower.holdPoint(shootToEnd.endPoint())
                    state = State.FINISHED
                }
            }
            State.FINISHED -> {}
        }

        follower.update()
    }

    private companion object {
        val START = Pose(19.0, 122.0, Math.toRadians(53.0))
        val SHOOT = Pose(56.0, 99.0, Math.toRadians(143.0))

        val END = Pose(57.0, 112.0, PI)

        const val MAX_POWER = 0.6
    }

    enum class State {
        START_TO_SHOOT_PRELOAD,
        SHOOTING_PRELOAD,
        SHOOT_TO_INTAKE_GOAL,
        INTAKE_GOAL,
        INTAKE_GOAL_TO_SHOOT,
        SHOOTING_PICKUP,
        PARK,
        FINISHED
    }
}