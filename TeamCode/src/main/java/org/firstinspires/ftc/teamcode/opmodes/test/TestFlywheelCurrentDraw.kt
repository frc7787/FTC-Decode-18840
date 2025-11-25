package org.firstinspires.ftc.teamcode.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import java.io.File

@Autonomous(group = "Test")
@Disabled
class TestFlywheelCurrentDraw: OpMode() {

    private val leaderShooterMotor by lazy {
        hardwareMap.get("leaderShooterMotor") as DcMotorEx
    }

    private val followerShooterMotor by lazy {
        hardwareMap.get("followerShooterMotor") as DcMotorEx
    }

    val data = mutableListOf<String>()

    private val timer = ElapsedTime()

    override fun init() {}

    override fun start() {
        leaderShooterMotor.power   = 1.0
        followerShooterMotor.power = 1.0
        timer.reset()
    }

    override fun loop() {
        val milliseconds = timer.milliseconds()

        if (milliseconds > 15000) {
            saveData()
            terminateOpModeNow()
        }

        val current = leaderShooterMotor.getCurrent(CurrentUnit.MILLIAMPS) + followerShooterMotor.getCurrent(
            CurrentUnit.MILLIAMPS)
        val velocity = leaderShooterMotor.velocity

        data.add("$current,$velocity")
    }

    private fun saveData() {
        val file = File("sdcard/FIRST/java/src/org/firstinspires/ftc/teamcode/ShooterLog.txt")
        val writer = file.writer()

        writer.write("millliamps,velocity\n")
        data.forEach { element ->
            writer.write("${element}\n")
        }

        writer.flush()
    }
}