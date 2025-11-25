package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.subsystems.shooter.Flywheel
import java.io.File

@Autonomous(group = "Test")
@Disabled
class TestFlywheelCurrentDraw: OpMode() {

    private val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    val data = mutableListOf<String>()

    private val timer = ElapsedTime()

    override fun init() {}

    override fun start() {
        timer.reset()
    }

    override fun loop() {
        val milliseconds = timer.milliseconds()

        if (milliseconds > 15000) {
            saveData()
            terminateOpModeNow()
        }

        val milliamps = flywheel.amps * 1000.0
        val rpm       = flywheel.rpm

        data.add("$milliamps,$rpm")
    }

    private fun saveData() {
        val writer = File("sdcard/FIRST/java/src/org/firstinspires/ftc/teamcode/ShooterLog.txt").writer()

        writer.write("amps,velocity\n")
        data.forEach { element ->
            writer.write("${element}\n")
        }

        writer.flush()
    }
}