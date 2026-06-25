package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.bylazar.configurables.annotations.Configurable
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

    override fun loop() {
        with (robot) {
            flywheel.spinAt(TARGET_RPM)
        }
    }

    companion object {
        @JvmStatic var TARGET_RPM: Double = 1000.0
    }
}