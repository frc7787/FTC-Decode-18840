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

@Autonomous(group = "Red")
@Configurable
class RedAuto: LinearOpMode() {

    companion object {

        @JvmField
        var START = Pose(123.0, 124.0, Math.toRadians(36.0))

        @JvmField
        var SHOOT = Pose(116.0, 117.0, Math.toRadians(36.0))

        @JvmField
        var END = Pose(96.0, 128.0, Math.toRadians(36.0))

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

    private val transfer by lazy {
        Transfer(hardwareMap)
    }

    override fun runOpMode() {
        follower.setStartingPose(START)

        val startToShoot = follower.pathBuilder()
            .addPath(BezierLine(START, SHOOT))
            .setLinearHeadingInterpolation(START.heading, SHOOT.heading)
            .build()

        val shootToEnd = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, END))
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()

        waitForStart()

        flywheel.targetRPM = 4000.0

        followPath(startToShoot)

        spindexerToSlot(index = 0)
        transfer(time = 50)

        wait(800.0)

        spindexerToSlot(index = 1)
        transfer(time = 50)

        wait(800.0)

        spindexerToSlot(index = 2)
        transfer(time = 50)

        followPath(shootToEnd)
    }

    private fun wait(time: Double) {
        val timer = ElapsedTime()

        while (timer.milliseconds() < time) {
            flywheel.update()
            spindexer.update()
        }
    }

    private fun followPath(path: PathChain) {
        follower.followPath(path)
        follower.update()

        while (follower.isBusy && !isStopRequested) {
            follower.update()
            flywheel.update()
            spindexer.update()
        }

        follower.breakFollowing()
    }

    fun spindexerToSlot(index: Int) {
        spindexer.toSlot(index, false)
        spindexer.update()

        while (!spindexer.atPosition && !isStopRequested && opModeIsActive()) {
            telemetry.addLine("To Position")
            telemetry.update()
            flywheel.update()
            spindexer.update()
        }
    }

    fun transfer(time: Long) {
        transfer.up()
        transfer.update()
        wait(500.0)
        transfer.down()
        transfer.update()
    }
}