package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Lambda
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.logging.MotorLogger

@Mercurial.Attach
@MotorLogger.Attach(false, "testMotorOne", "testMotorTwo")
@TeleOp(group = "Test")
class TestDualMotor: OpMode() {

    override fun init() {
        MotorLogger.logCurrent(CurrentUnit.AMPS)
        MotorLogger.logVelocity()

        val testMotorOne = hardwareMap["testMotorOne"] as DcMotorEx
        val testMotorTwo = hardwareMap["testMotorTwo"] as DcMotorEx

        Mercurial.gamepad1.leftBumper.onTrue(
            Lambda("power-dual-motors")
                .setExecute {
                    testMotorOne.power = 1.0
                    testMotorTwo.power = 1.0
                }
                .setFinish { false }
                .setEnd {
                    testMotorOne.power = 0.0
                    testMotorTwo.power = 0.0
                }
        )

        Mercurial.gamepad1.rightBumper
            .toggleTrue(
                Lambda("set-dual-motors-direction-forward")
                    .setExecute {
                        testMotorOne.direction = DcMotorSimple.Direction.FORWARD
                        testMotorTwo.direction = DcMotorSimple.Direction.FORWARD
                    }
            )
            .toggleFalse(
                Lambda("set-dual-motors-direction-reverse")
                    .setExecute {
                        testMotorOne.direction = DcMotorSimple.Direction.REVERSE
                        testMotorTwo.direction = DcMotorSimple.Direction.REVERSE
                    }
            )
    }

    override fun loop() {}
}