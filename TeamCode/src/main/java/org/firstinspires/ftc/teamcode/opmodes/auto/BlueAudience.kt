package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import org.firstinspires.ftc.teamcode.util.BluePositions.AUDIENCE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.AUDIENCE_SPIKE_MARK_START
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_END
import org.firstinspires.ftc.teamcode.util.BluePositions.GATE_SPIKE_MARK_START
import kotlin.math.PI
import kotlin.math.abs

@Autonomous(group = "Blue")
class BlueAudience: OpMode() {

    private val octoquad by lazy {
        (hardwareMap["octoquad"] as OctoQuad).also { octoquad ->
            octoquad.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_PULSE_WIDTH
            octoquad.saveParametersToFlash()
        }
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap) {
            octoquad.readSinglePosition_Caching(0)
        }
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    private val startToEnd = {
        follower.pathBuilder()
            .addPath(
                BezierLine(START, END)
            )
            .setLinearHeadingInterpolation(START.heading, END.heading)
            .build()!!
    }

    private val timer = ElapsedTime()

    private var state = State.SHOOTING_PRELOAD

    override fun init() {
        follower.setStartingPose(START)
    }

    override fun loop() {
        flywheel.spinUp(2100.0)

        when (state) {
            State.SHOOTING_PRELOAD -> {
                shoot(shootingIndex)

                if (shootingIndex > 5) {
                    shootingIndex = 0
                    follower.followPath(startToEnd.invoke())
                    state = State.TO_END
                }
            }
            State.TO_END -> {
                if (!follower.isBusy) {
                    state = State.END
                }
            }
            State.END -> {
                telemetry.addLine("Finished!")
            }
        }

        follower.update()

        flywheel.update()
        spindexer.update(telemetry)
    }

    private var isAtShootingPosition               = false
    private var hasShot                            = false
    private var timerHasBeenTriggered              = false
    private var shootingDelayTimerHasBeenTriggered = false

    private var shootingIndex = 1

    private var shootingDelayTimer = ElapsedTime()

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
            if (timer.seconds() > 2.5) {
                transfer.down()
                shootingIndex += 2
                timerHasBeenTriggered = false
                isAtShootingPosition  = false
            } else {
                transfer.up()
            }
        }
    }

    private companion object {
        val START = Pose(59.0, 11.0, Math.toRadians(110.0))

        val END = Pose(59.0, 33.0, PI / 2.0)

        const val SLEEP_MILLISECONDS = 1000
        const val MAX_POWER = 1.0
    }

    private enum class State {
        SHOOTING_PRELOAD,
        TO_END,
        END;
    }
}