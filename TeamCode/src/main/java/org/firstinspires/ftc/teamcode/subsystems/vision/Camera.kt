package org.firstinspires.ftc.teamcode.subsystems.vision

import android.util.Size
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.util.concurrent.TimeUnit

class Camera(hardwareMap: HardwareMap) {
    private val aprilTagProcessor: AprilTagProcessor = AprilTagProcessor.Builder()
        .setDrawTagID(DRAW_TAG_ID)
        .setDrawAxes(DRAW_AXIS)
        .setDrawTagOutline(DRAW_TAG_OUTLINE)
        .setDrawCubeProjection(DRAW_CUBE_PROJECTION)
        .setNumThreads(1)
        .setOutputUnits(INCH, DEGREES)
        .setLensIntrinsics(FX, FY, CX, CY)
        .build()

    private val visionPortal: VisionPortal = VisionPortal.Builder()
        .setCamera(hardwareMap["Webcam 1"] as WebcamName)
        .setCameraResolution(RESOLUTION)
        .enableLiveView(ENABLE_LIVE_VIEW)
        .setShowStatsOverlay(SHOW_STATS_OVERLAY)
        .setStreamFormat(STREAM_FORMAT)
        .addProcessor(aprilTagProcessor)
        .build()

    init {
        visionPortal.resumeStreaming()
    }

    fun detectedTags(): List<AprilTag> {
        return aprilTagProcessor.detections.map { detection ->
            AprilTag(detection.id, detection.robotPose)
        }
    }

    fun setExposure(exposure: Long, unit: TimeUnit = MILLISECONDS) {
        if (visionPortal.cameraState != STREAMING) {
            // TODO Add logging
            return
        }

        val control = visionPortal.getCameraControl(ExposureControl::class.java)
        if (control.isExposureSupported) {
            control.setExposure(exposure, unit)
        } else {
            // TODO Add logging
        }
    }

    fun setWhiteBalance(balance: Int) {
        if (visionPortal.cameraState != STREAMING) {
            // TODO Add logging
            return
        }

        val control = visionPortal.getCameraControl(WhiteBalanceControl::class.java)
        control.whiteBalanceTemperature = balance
    }

    fun setGain(gain: Int) {
        if (visionPortal.cameraState != STREAMING) {
            // TODO Add logging
            return
        }

        val control = visionPortal.getCameraControl(GainControl::class.java)
        control.gain = gain
    }

    fun setFocus(focus: Double) {
        if (visionPortal.cameraState != STREAMING) {
            // TODO Add logging
            return
        }

        val control = visionPortal.getCameraControl(FocusControl::class.java)
        if (control.isFocusLengthSupported) {
            control.focusLength = focus
        } else {
            // TODO Add logging
        }
    }

    companion object {
        private val RESOLUTION = Size(640, 480)
        private val STREAM_FORMAT = VisionPortal.StreamFormat.YUY2

        private const val ENABLE_LIVE_VIEW     = false
        private const val SHOW_STATS_OVERLAY   = false
        private const val DRAW_TAG_ID          = false
        private const val DRAW_AXIS            = false
        private const val DRAW_TAG_OUTLINE     = false
        private const val DRAW_CUBE_PROJECTION = false

        // Lens Intrinsics
        private const val FX = 0.0
        private const val FY = 0.0
        private const val CX = 0.0
        private const val CY = 0.0
    }
}