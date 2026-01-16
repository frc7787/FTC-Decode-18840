package org.firstinspires.ftc.teamcode.opmodes.test

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.control.FFCoefficients
import org.firstinspires.ftc.teamcode.control.PIDCoefficients
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@TeleOp(group = "Test")
@Configurable
class TestFlywheel: OpMode() {

    private val flywheel by lazy {
        Flywheel(hardwareMap) {
            val motor = hardwareMap["frontRightDriveMotor"] as DcMotorEx
            -(motor.velocity / 28.0 * 60.0) // Conversion from ticks/s to rpm
        }
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    private var targetRPM: Double = 2000.0
        set(target) {
            field = target.coerceIn(-6000.0, 6000.0)
        }

    private val currentGamepad  = Gamepad()
    private val previousGamepad = Gamepad()

    companion object {
        @JvmField
        var ffCoefficients: FFCoefficients = FFCoefficients(0.0, 0.0, 0.0)

        @JvmField
        var pidCoefficients: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)
    }

    override fun init() {}

    override fun loop() {
        flywheel.ffCoefficients = ffCoefficients
        flywheel.pidCoefficients = pidCoefficients

        previousGamepad.copy(currentGamepad)
        currentGamepad.copy(gamepad1)

        if (currentGamepad.dpad_up && !previousGamepad.dpad_up) {
            targetRPM += 50.0
        }

        if (currentGamepad.dpad_down && !previousGamepad.dpad_down) {
            targetRPM -= 50.0
        }

        flywheel.targetRPM = targetRPM

        if (currentGamepad.triangle) transfer.up() else transfer.down()
        transfer.update()

        telemetry.addLine("Dpad Up To Increment")
        telemetry.addLine("Dpad Down To Decrement")
        telemetry.addLine()

        flywheel.debug(telemetry, verbose = true)
        flywheel.update()
    }
}