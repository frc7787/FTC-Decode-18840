package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Scheduler
import com.pedropathing.ivy.Scheduler.schedule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Robot

@TeleOp(group = "Test")
@Configurable
class FlywheelTuning: OpMode() {

    private val robot by lazy {
        Robot(hardwareMap)
    }

    override fun init() {}

    override fun start() {
        with (robot) {
            schedule(flywheel.spinAt { TARGET_RPM })
        }
    }

    override fun loop() {
        Scheduler.execute()
    }

    companion object {
        @JvmStatic var TARGET_RPM: Double = 1000.0
    }
}