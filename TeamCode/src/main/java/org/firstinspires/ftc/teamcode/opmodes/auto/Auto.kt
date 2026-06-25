package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.subsystems.Robot

abstract class Auto: LinearOpMode() {

    private val robot by lazy {
        Robot(hardwareMap)
    }

    abstract val auto: Command

    override fun runOpMode() {
        robot.init()

        waitForStart()

        schedule(auto)

        while (opModeIsActive() && !isStopRequested) {
            Scheduler.execute()
        }
    }
}