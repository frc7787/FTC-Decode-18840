package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveBase
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {

    private val octoquad by lazy {
        (hardwareMap["octoquad"] as OctoQuad).also { octoquad ->
            octoquad.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_PULSE_WIDTH
            octoquad.saveParametersToFlash()
        }
    }

    private val indicatorChannelOne by lazy {
        hardwareMap["indicatorChannelOne"] as DigitalChannel
    }

    private val indicatorChannelTwo by lazy {
        hardwareMap["indicatorChannelTwo"] as DigitalChannel
    }

    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val mecanum by lazy {
        MecanumDriveBase(hardwareMap)
    }

    private val spindexer by lazy {
        Spindexer(hardwareMap) { octoquad.readSinglePosition_Caching(0) }
    }


    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    private var transferActive       = false
    private var transferTimerStarted = false

    private var shootingNear = false
    private var flywheelActive = false

    private var spindexerAuto = true

    private var spindexerIndex = 1

    private val timer = ElapsedTime()

    override fun init() {
        indicatorChannelOne.mode = DigitalChannel.Mode.OUTPUT
        indicatorChannelTwo.mode = DigitalChannel.Mode.OUTPUT
    }

    override fun loop() {
        previousGamepad.copy(currentGamepad)
        currentGamepad.copy(gamepad2)

        // Drive

        var drive = -gamepad1.left_stick_y.toDouble()
        if (abs(drive) < 0.05) {
            drive = 0.0
        }

        var strafe = gamepad1.left_stick_x.toDouble()
        if (abs(strafe) < 0.05) {
            strafe = 0.0
        }

        var turn = gamepad1.right_stick_x.toDouble()
        if (abs(turn) < 0.05) {
            turn = 0.0
        }

        telemetry.addLine("Drive: $drive")
        telemetry.addLine("Strafe: $strafe")
        telemetry.addLine("Turn: $turn")

        mecanum.driveFieldCentric(drive, strafe, turn)

        // Shooter

        // Intake

        if (currentGamepad.triangle && !previousGamepad.triangle) {
             shootingNear = !shootingNear
        }

        if (shootingNear) {
            indicatorChannelOne.state = false
            indicatorChannelTwo.state = true
        } else {
            indicatorChannelOne.state = true
            indicatorChannelTwo.state = false
        }

        // Flywheel

        if (currentGamepad.left_bumper && !previousGamepad.left_bumper) {
            flywheelActive = !flywheelActive
        }

        telemetry.addLine("Flywheel Active: $flywheelActive")
        telemetry.addLine("Shooting Near: $shootingNear")

        if (flywheelActive) {
            if (shootingNear) {
                flywheel.spinUp(2000.0)
            } else {
                flywheel.spinUp(2200.0)
            }
        } else {
            flywheel.powerFlywheel(0.0)
        }

        flywheel.update()

        telemetry.addLine("Mode: ${flywheel.mode}")

        telemetry.addLine("RPM: ${flywheel.rpm}")

        // Transfer

        if (currentGamepad.right_bumper && !previousGamepad.right_bumper && !transferTimerStarted) {
            transferActive = true
        }

        if (transferActive) {
            if (!transferTimerStarted) {
                transfer.up()
                timer.reset()
                transferTimerStarted = true
            }
            if (timer.seconds() > 0.3) {
                transfer.down()
                transferTimerStarted = false
                transferActive = false
            }
        }

        transfer.debug(telemetry, verbose = true)

        // Intake

        intake.power = (gamepad2.left_trigger - gamepad2.right_trigger).toDouble()

        // Spindexer

        if (currentGamepad.dpad_right && !previousGamepad.dpad_right) {
            spindexerIndex -= if (spindexerIndex % 2 == 0) {
                1
            } else {
                2
            }

            if (spindexerIndex < 1) {
                spindexerIndex = if (spindexerIndex == -1) {
                    5
                } else {
                    6
                }
            }
        }

        if (currentGamepad.dpad_left && !previousGamepad.dpad_left) {
            spindexerIndex += if (spindexerIndex % 2 == 0) {
                1
            } else {
                2
            }
            if (spindexerIndex > 6) {
                spindexerIndex = if (spindexerIndex == 7) {
                    1
                } else {
                    2
                }
            }
        }

        if (currentGamepad.dpad_up && !previousGamepad.dpad_up) {
            spindexerIndex += if (spindexerIndex % 2 == 0) {
                2
            } else {
                1
            }

            if (spindexerIndex > 6) {
                spindexerIndex = if (spindexerIndex == 8) {
                    2
                } else {
                    1
                }
            }
        }

        if (currentGamepad.dpad_down && !previousGamepad.dpad_down) {
            spindexerIndex -= if (spindexerIndex % 2 == 0) {
                2
            } else {
                1
            }

            if (spindexerIndex < 0) {
                spindexerIndex = if (spindexerIndex == -2) {
                    1
                } else {
                    2
                }
            }
        }


        val spindexerPower = -currentGamepad.right_stick_x.toDouble()
        telemetry.addLine("Spindexer Power: $spindexerPower")

        if (abs(spindexerPower) < 0.03) {
            if (spindexerAuto) {
                when (spindexerIndex) {
                    1 -> spindexer.toOuttakeOne()
                    2 -> spindexer.toIntakeOne()
                    3 -> spindexer.toOuttakeTwo()
                    4 -> spindexer.toIntakeTwo()
                    5 -> spindexer.toOuttakeThree()
                    6 -> spindexer.toIntakeThree()
                }
            }
        } else {
            spindexer.setPower(spindexerPower)
        }

        telemetry.addLine("Index: $spindexerIndex")

        spindexer.update(telemetry)
    }
}