package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.subsystems.Robot

abstract class Auto: LinearOpMode() {

    @Suppress("unused")
    protected val robot by lazy {
        Robot(hardwareMap)
    }

    protected abstract val auto: Command

    override fun runOpMode() {
        waitForStart()

        schedule(auto)

        while (opModeIsActive() && !isStopRequested) {
            Scheduler.execute()
        }
    }
}