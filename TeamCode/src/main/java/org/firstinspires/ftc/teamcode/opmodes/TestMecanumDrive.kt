package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.MecanumDrive

@TeleOp(group = "Test")
class TestMecanumDrive: OpMode() {

    private val mecanumDrive by lazy {
        MecanumDrive(hardwareMap)
    }

    // State

    private val fieldCentricToggle: Boolean = false

    override fun init() {
        mecanumDrive.resetIMU()
    }

    override fun start() {
        mecanumDrive.resetIMU()
    }

    override fun loop() {
    }
}