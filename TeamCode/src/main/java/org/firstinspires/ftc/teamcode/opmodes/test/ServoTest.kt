package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ServoImplEx

@TeleOp(group = "Test")
class ServoTest: OpMode() {

    private val servo by lazy {
        hardwareMap["testServo"] as ServoImplEx
    }

    private var servoTarget = 0.0

    private var displayInstructions = true

    override fun init() {}

    override fun init_loop() {
        run()
        debug()
        if (displayInstructions) displayInstructions()

        if (gamepad1.optionsWasReleased()) displayInstructions = !displayInstructions
    }

    override fun loop() {
        run()
        debug()
        if (displayInstructions) displayInstructions()
        if (gamepad1.optionsWasReleased()) displayInstructions = !displayInstructions

        servo.position = servoTarget
    }

    private fun run() {
        if (gamepad1.dpadUpWasReleased()) {
            servoTarget += 0.01
            servoTarget = servoTarget.coerceAtMost(1.0)
        }

        if (gamepad1.dpadDownWasReleased()) {
            servoTarget -= 0.01
            servoTarget = servoTarget.coerceAtLeast(0.0)
        }

        if (gamepad1.crossWasReleased()) {
            servo.direction = when (servo.direction) {
                FORWARD -> REVERSE
                REVERSE -> FORWARD
            }
        }

        if (gamepad1.triangleWasReleased()) {
            if (servo.isPwmEnabled) servo.setPwmDisable() else servo.setPwmDisable()
        }
    }

    private fun debug() {
        telemetry.addLine("Position: $servoTarget")
        telemetry.addLine("Direction: ${servo.direction}")
        telemetry.addLine("Enabled: ${servo.isPwmEnabled}")
    }

    fun displayInstructions() {
        telemetry.addLine("Press Options To Toggle Instructions")
        telemetry.addLine("Increment and Decrement The Position With Dpad Up/Down")
        telemetry.addLine("Press Cross (X) to toggle the direction")
        telemetry.addLine("Press Triangle to toggle whether the servo is enabled")
    }
}