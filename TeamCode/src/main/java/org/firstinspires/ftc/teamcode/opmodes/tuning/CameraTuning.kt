package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystems.Robot
import java.util.concurrent.TimeUnit

@TeleOp(group = "Tuning")
@Configurable
class CameraTuning: OpMode() {

    private val robot by lazy {
        Robot(hardwareMap)
    }

    override fun init() {
        robot.init()
    }

    override fun loop() {
        with (robot) {
            camera.setExposure(EXPOSURE, EXPOSURE_UNIT)
            camera.setGain(GAIN)
            camera.setWhiteBalance(WHITE_BALANCE)

            camera.detectedTags().forEach { tag ->
                telemetry.addLine("Tag Detected With ID: ${tag.id}")
            }
        }
    }

    companion object {
        @JvmStatic private var GAIN: Int               = 200
        @JvmStatic private var WHITE_BALANCE: Int      = 2000
        @JvmStatic private var EXPOSURE: Long          = 5
        @JvmStatic private var EXPOSURE_UNIT: TimeUnit = MILLISECONDS
    }
}