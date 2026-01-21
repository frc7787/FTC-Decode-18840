package org.firstinspires.ftc.teamcode.opmodes.auto

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.AprilTagSubsystem
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer
import org.firstinspires.ftc.teamcode.util.Motif
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

@Autonomous(group = "Blue")
@Configurable
class BlueAuto: LinearOpMode() {

    private companion object {
        val START = Pose(0.0, 0.0, 0.0)
        val SHOOT = Pose(0.0, 0.0, 0.0)

        @JvmField
        var GAIN = 100
        @JvmField
        var EXPOSURE = 10L
        @JvmField
        var WHITE_BALANCE = 2500
    }

    private val follower by lazy {
        Constants.createFollower(hardwareMap)
    }

    private val spindexer by lazy {
        val motor = hardwareMap["frontLeftDriveMotor"] as DcMotorEx
        motor.mode = STOP_AND_RESET_ENCODER
        motor.mode = RUN_WITHOUT_ENCODER

        Spindexer(hardwareMap,
            { motor.currentPosition.toDouble() },
            { motor.velocity },
            {
                motor.mode = STOP_AND_RESET_ENCODER
                motor.mode = RUN_WITHOUT_ENCODER
            }
        )
    }

    private val flywheel by lazy {
        Flywheel(hardwareMap, telemetry) {
            val motor = hardwareMap["frontRightDriveMotor"] as DcMotorEx
            motor.velocity / 28.0 * 60.0 // Conversion from ticks/s to rpm
        }
    }

    private val camera by lazy {
        AprilTagSubsystem(hardwareMap)
    }

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    override fun runOpMode() {
        follower.setStartingPose(START)

        val startToShoot = follower.pathBuilder()
            .addPath(BezierLine(START, SHOOT))
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()

        val pattern = Motif.PPG

        while (opModeInInit() && !isStopRequested) {
            val detections = camera.detections()
            camera.setManualExposure(telemetry, EXPOSURE.toInt(), GAIN, WHITE_BALANCE)
            detections.forEach { detection ->
                telemetry.addLine("Id: ${detection.id}")
                telemetry.addLine("Bearing: ${detection.ftcPose.bearing}")
                telemetry.addLine()
            }
            telemetry.update()
        }

        waitForStart()

        flywheel.targetRPM = 3500.0

        followPath(startToShoot)

        sleep(500)
    }

    private fun followPath(path: PathChain) {
        follower.followPath(path)
        follower.update()

        while (follower.isBusy && !isStopRequested) {
            follower.update()
            flywheel.update()
        }

        follower.breakFollowing()
    }

    private fun shoot(index: Int) {
        require(index in 0..2) {
            "Index must be in range 0..2"
        }

        transfer.down()
        transfer.update()

        spindexer.toSlot(index, false)
        while (!isStopRequested) {
            // Duplication is intentional
            spindexer.update()
            flywheel.update()
            transfer.update()

            if (spindexer.atPosition) {
                transfer.up()
                transfer.update()
                break;
            }

            spindexer.update()
            flywheel.update()
            transfer.update()
        }

        transfer.down()
        transfer.update()
    }
}