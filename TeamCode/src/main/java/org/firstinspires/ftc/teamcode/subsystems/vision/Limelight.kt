package org.firstinspires.ftc.teamcode.subsystems.vision

import com.qualcomm.hardware.limelightvision.LLResultTypes.ColorResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.HardwareMap

class Limelight(hardwareMap: HardwareMap) {
    private val limelight = hardwareMap["limelight"] as Limelight3A

    init {
        limelight.start()
        limelight.setPollRateHz(POLL_RATE_HZ)
        enableAprilTagDetection()
    }

    fun aprilTagDetections(): List<AprilTag> {
        val result = limelight.latestResult
        if (result.pipelineIndex != APRIL_TAG_PIPELINE) return listOf()
        return result.fiducialResults.map { result ->
            AprilTag(result.fiducialId, result.targetPoseRobotSpace)
        }
    }

    fun colourDetections(): List<ColorResult> {
        val result = limelight.latestResult
        if (result.pipelineIndex != COLOUR_DETECTION_PIPELINE) return listOf()
        return result.colorResults
    }

    fun enableAprilTagDetection() {
        limelight.pipelineSwitch(APRIL_TAG_PIPELINE)
    }

    fun enableColourDetection() {
        limelight.pipelineSwitch(COLOUR_DETECTION_PIPELINE)
    }

    fun stop() {
        limelight.stop()
    }

    companion object {
        private const val APRIL_TAG_PIPELINE        = 0
        private const val COLOUR_DETECTION_PIPELINE = 1
        private const val POLL_RATE_HZ = 100
    }
}