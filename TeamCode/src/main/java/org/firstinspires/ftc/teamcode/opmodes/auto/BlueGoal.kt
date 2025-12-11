package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.util.BluePositions
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Autonomous(group = "Blue")
class BlueGoal: OpMode() {

    private val octoquad by lazy {
        (hardwareMap["octoquad"] as OctoQuad).also { octoquad ->
            octoquad.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_PULSE_WIDTH
            octoquad.saveParametersToFlash()
        }
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap) {
            octoquad.readSinglePosition_Caching(0)
        }
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap)
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
        transfer.down()
    }

    private var shootingIndex = 1
    private var shouldShoot = true

    override fun loop() {
        flywheel.spinUp(2000.0)

        shoot(shootingIndex)

        telemetry.addLine("Should Shoot: $shouldShoot")
        telemetry.addLine("Is At Shooting Position: $isAtShootingPosition")
        telemetry.addLine("Has Shoot: $hasShot")
        telemetry.addLine("Shooting Index: $shootingIndex")

        if (shootingIndex > 5) {
            shouldShoot = false
        }

        flywheel.update()
        spindexer.update(telemetry)
    }


    private val timer = ElapsedTime()

    private var isAtShootingPosition = false
    private var hasShot = false

    private var timerHasBeenTriggered = false

    private fun shoot(spindexerIndex: Int) {
        when (spindexerIndex) {
            1 -> {
                spindexer.toOuttakeOne()
                if (abs(spindexer.position - Spindexer.OUTTAKE_SLOT_ONE) < 10) {
                    isAtShootingPosition = true
                    if (!timerHasBeenTriggered) {
                        timerHasBeenTriggered = true
                        timer.reset()
                    }
                }
            }
            3 -> {
                spindexer.toOuttakeTwo()
                if (abs(spindexer.position - Spindexer.OUTTAKE_SLOT_TWO) < 10) {
                    isAtShootingPosition = true
                    if (!timerHasBeenTriggered) {
                        timerHasBeenTriggered = true
                        timer.reset()
                    }
                }
            }
            5 -> {
                spindexer.toOuttakeThree()
                if (abs(spindexer.position - Spindexer.OUTTAKE_SLOT_THREE) < 10) {
                    isAtShootingPosition = true
                    if (!timerHasBeenTriggered) {
                        timerHasBeenTriggered = true
                        timer.reset()
                    }
                }
            }
            else -> {
                telemetry.addLine("Invalid Index: $shootingIndex")
            }
        }

        if (isAtShootingPosition) {
            if (timer.seconds() > 0.5) {
                transfer.down()
                shootingIndex += 2
                timerHasBeenTriggered = false
                isAtShootingPosition = false
            } else {
                transfer.up()
            }
        }

        telemetry.addLine("Seconds: ${timer.seconds()}")
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