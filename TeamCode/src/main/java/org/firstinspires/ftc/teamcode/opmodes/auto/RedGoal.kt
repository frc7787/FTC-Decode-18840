package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.PI
import kotlin.math.abs

@Autonomous(group = "Red")
class RedGoal: OpMode() {

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

    private var state = State.START

    override fun init() {
        follower.setStartingPose(START)
    }

    override fun loop() {
        flywheel.spinUp(2000.0)

        when (state) {
            State.START -> {
                follower.followPath(startToShoot.invoke())
                state = State.TO_SHOOT_PRELOAD
            }
            State.TO_SHOOT_PRELOAD -> {
                if (!follower.isBusy && !shootingDelayTimerHasBeenTriggered) {
                    shootingDelayTimer.reset()
                    shootingDelayTimerHasBeenTriggered = true
                }

                if (!follower.isBusy && shootingDelayTimer.seconds() > 2.5) {
                    state = State.SHOOTING_PRELOAD
                }
            }
            State.SHOOTING_PRELOAD -> {
                shoot(shootingIndex)

                if (shootingIndex > 5) {
                    shootingIndex = 0
                    follower.followPath(shootToEnd.invoke())
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

    private val timer = ElapsedTime()

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
            if (timer.seconds() > 3.0) {
                transfer.down()
                shootingIndex += 2
                timerHasBeenTriggered = false
                isAtShootingPosition  = false
            } else {
                transfer.up()
            }
        }
    }

    private enum class State {
        START,
        TO_SHOOT_PRELOAD,
        SHOOTING_PRELOAD,
        TO_END,
        END;
    }

    private companion object {
        val START = Pose(126.0, 120.0, Math.toRadians(305.0))
        val SHOOT = Pose(82.0, 76.0, Math.toRadians(45.0))
        val END   = Pose(82.0, 101.0, PI / 2.0)
    }
}