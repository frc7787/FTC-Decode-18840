package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
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
        flywheel.setPower(1.0)
    }

    override fun loop() {
        val milliseconds = timer.milliseconds()

        if (milliseconds > RUN_TIME_MILLISECONDS) {
            saveData()
            terminateOpModeNow()
        }

        val milliamps = flywheel.amps * 1000.0
        val rpm       = flywheel.rpm

        data.add("$milliseconds,$milliamps,$rpm")
    }

    private fun saveData() {
        val file   = File("sdcard/FIRST/java/src/org/firstinspires/ftc/teamcode/ShooterLog.txt")
        val writer = file.writer()

        writer.write("time,amps,velocity\n")
        data.forEach { element ->
            writer.write("${element}\n")
        }
        writer.flush()
    }

    private companion object {
        const val RUN_TIME_MILLISECONDS = 5500.0
        const val FLYWHEEL_POWER = 0.7
    }
}