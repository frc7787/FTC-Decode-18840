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
        Flywheel(hardwareMap) {
            val motor = hardwareMap["frontRightDriveMotor"] as DcMotorEx
            motor.currentPosition.toDouble() / 28.0 * 60.0 // Conversion from ticks/s to rpm
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

    private val timer = ElapsedTime()

    private var index = 1

    private var targetRPM = 2000.0
        set(value) {
            field = value.coerceIn(0.0, 6000.0)
        }

    private var spindexerMovingToFreeSlot = false

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

        if (currentGamepad.dpad_up && !previousGamepad.dpad_up) {
            targetRPM += 50.0
        }

        if (currentGamepad.dpad_down && !previousGamepad.dpad_down) {
            targetRPM -= 50.0
        }

        flywheel.targetRPM = if (flywheelActive) targetRPM else 0.0

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
                spindexerMovingToFreeSlot = true
                index = (index + 1).mod(3) // TODO Once we keep track of the balls, we can optimize the position
            }
        } else { // We only allow manual control if the intake isn't active. Otherwise, it introduces too many edge cases
            if (currentGamepad.dpad_up && !previousGamepad.dpad_up) {
                index = (index + 1).mod(3)
            }
            if (currentGamepad.dpad_down && !previousGamepad.dpad_down) {
                index = (index - 1).mod(3)
            }
        }

        spindexer.toSlot(index, intake.mode == ACTIVE)

        spindexer.debug(telemetry)
        spindexer.update()

        if (spindexerMovingToFreeSlot && spindexer.atPosition) {
            spindexerMovingToFreeSlot = false
        }
    }
}