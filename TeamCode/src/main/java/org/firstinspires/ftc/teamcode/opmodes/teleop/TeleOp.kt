package org.firstinspires.ftc.teamcode.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveBase
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import kotlin.math.abs

@TeleOp(group = "$")
class TeleOp: OpMode() {
    private val intake by lazy {
        Intake(hardwareMap)
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap, telemetry) {
            val motor = hardwareMap["frontRightDriveMotor"] as DcMotorEx
            motor.velocity / 28.0 * 60.0 // Conversion from ticks/s to rpm
        }
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private val mecanum by lazy {
        MecanumDriveBase(hardwareMap)
    }

    private val spindexer by lazy {
        val motor = hardwareMap["frontLeftDriveMotor"] as DcMotorEx
        motor.mode = STOP_AND_RESET_ENCODER
        motor.mode = RUN_WITHOUT_ENCODER

        Spindexer(hardwareMap,
            { motor.currentPosition.toDouble() },
            { motor.velocity },
            {
                motor.mode = STOP_AND_RESET_ENCODER
                motor.mode = RUN_WITHOUT_ENCODER
            }
        )
    }

    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    private var flywheelActive = false

    private val automaticIntakeTimer = ElapsedTime()

    private var index = 1

    private var spindexerMovingToFreeSlot = false
    private var automaticIntakeTimerStarted = false

    enum class FlywheelHomingState {
        NOT_HOMED,
        HOMED
    }

    private var state: FlywheelHomingState = HOMED

    override fun init() {
        transfer.down()
        transfer.update()
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
        } else {
            turn *= abs(turn)
        }

        telemetry.addLine("Drive: $drive")
        telemetry.addLine("Strafe: $strafe")
        telemetry.addLine("Turn: $turn")

        mecanum.setDrivePowers(drive, strafe, turn)
        mecanum.update()

        // Flywheel

        if (currentGamepad.left_bumper && !previousGamepad.left_bumper) {
            flywheelActive = !flywheelActive
        }

        flywheel.targetRPM = if (flywheelActive) 3700.0 else 0.0
        flywheel.debug(telemetry, verbose = false)
        flywheel.update()

        // Transfer
        if (gamepad2.right_bumper) {
            transfer.up()
        } else {
            transfer.down()
        }
        transfer.update()

        // Intake
        intake.power = (gamepad2.left_trigger - gamepad2.right_trigger).toDouble()
        intake.update()

        // Spindexer

        if (intake.isActive()) {
            if (spindexer.artifactInIntakeSlot && !spindexerMovingToFreeSlot) {
                if (!automaticIntakeTimerStarted) {
                    automaticIntakeTimer.reset()
                    automaticIntakeTimerStarted = true
                }

                if (automaticIntakeTimer.seconds() > 0.1) {
                    spindexerMovingToFreeSlot = true
                    automaticIntakeTimerStarted = false

                    index = (index + 1).mod(3) // TODO Once we keep track of the balls, we can optimize the position
                }
            }
        } else { // We only allow manual control if the intake isn't active. Otherwise, it introduces too many edge cases

            when (state) {
                HOMED -> {
                    if (currentGamepad.dpad_right && !previousGamepad.dpad_right) {
                        index = (index + 1).mod(3)
                    }
                    if (currentGamepad.dpad_left && !previousGamepad.dpad_left) {
                        index = (index - 1).mod(3)
                    }
                }
                NOT_HOMED -> Unit
            }
        }

        when (state) {
            HOMED -> spindexer.toSlot(index, intake.mode == ACTIVE)
            NOT_HOMED -> spindexer.targetPower = gamepad2.right_stick_x.toDouble()
        }

        if (currentGamepad.options && !previousGamepad.options) {
            when (state) {
                HOMED -> {
                   state = NOT_HOMED
                }
                NOT_HOMED -> {
                    spindexer.reset()
                    state = HOMED
                }
            }
        }


        telemetry.addLine("State: $state")

        spindexer.debug(telemetry)
        spindexer.update()

        if (spindexerMovingToFreeSlot && spindexer.atPosition) {
            spindexerMovingToFreeSlot = false
        }
    }
}