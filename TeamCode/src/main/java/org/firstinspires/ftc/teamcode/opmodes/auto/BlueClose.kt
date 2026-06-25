package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.pedropathing.ivy.groups.Groups.sequential
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Robot

@TeleOp(group = "Blue")
class BlueClose: LinearOpMode() {

    private val robot by lazy {
        Robot(hardwareMap)
    }

    private val auto by lazy {
        sequential(
            // TODO
        )
    }

    override fun runOpMode() {
        robot.init()

        waitForStart()

        schedule(auto)

        while (opModeIsActive() && !isStopRequested) {
            Scheduler.execute()
        }
    }
}