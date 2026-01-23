package org.firstinspires.ftc.teamcode.opmodes.auto

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.Flywheel
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.subsystems.Transfer

@Autonomous(group = "Blue")
@Configurable
class BlueAuto: LinearOpMode() {

    companion object {

        @JvmField
        var START = Pose(21.0, 123.5, Math.toRadians(143.0))

        @JvmField
        var SHOOT = Pose(36.0, 113.0, Math.toRadians(143.0))

        @JvmField
        var INTAKE_START = Pose(44.0, 93.0, Math.toRadians(180.0))

        @JvmField
        var INTAKE_END = Pose(23.0, 93.0, Math.toRadians(180.0))

        @JvmField
        var END = Pose(53.0, 125.0, Math.toRadians(143.0))

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
            { TODO() },
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

    private val intake by lazy {
        Intake(hardwareMap)
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

        val shootToIntakeStart = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, INTAKE_START))
            .setLinearHeadingInterpolation(SHOOT.heading, INTAKE_START.heading)
            .build()

        val intakeStartToEnd = follower.pathBuilder()
            .addPath(BezierLine(INTAKE_START, INTAKE_END))
            .setLinearHeadingInterpolation(INTAKE_START.heading, INTAKE_END.heading)
            .setVelocityConstraint(0.3)
            .build()

        val intakeEndToShoot = follower.pathBuilder()
            .addPath(BezierLine(INTAKE_END, SHOOT))
            .setLinearHeadingInterpolation(INTAKE_END.heading, SHOOT.heading)
            .build()

        val shootToEnd = follower.pathBuilder()
            .addPath(BezierLine(SHOOT, END))
            .setLinearHeadingInterpolation(SHOOT.heading, END.heading)
            .build()

        waitForStart()

        flywheel.targetRPM = 4000.0

        followPath(startToShoot, intaking = false)

        spindexerToSlot(index = 2)
        transfer(time = 50)

        wait(800.0)

        spindexerToSlot(index = 1)
        transfer(time = 50)

        wait(800.0)

        spindexerToSlot(index = 0)
        transfer(time = 50)

        followPath(shootToIntakeStart, intaking = false)

        wait(800.0)

        followPath(intakeStartToEnd, intaking = true)

        wait(800.0)

        followPath(intakeEndToShoot, intaking = false)

        wait(800.0)

        followPath(shootToEnd, intaking = false)
    }

    private fun wait(time: Double) {
        val timer = ElapsedTime()

        while (timer.milliseconds() < time) {
            flywheel.update()
            spindexer.update()
        }
    }

    private fun followPath(path: PathChain, intaking: Boolean) {
        follower.followPath(path)
        follower.update()

        while (follower.isBusy && !isStopRequested && opModeIsActive()) {
            if (intaking) {
                intake.power = 1.0
                spindexer.targetPower = 0.4
            } else {
                intake.power = 0.0
            }

            intake.update()
            follower.update()
            flywheel.update()
            spindexer.update()
        }

        follower.breakFollowing()
    }

    fun spindexerToSlot(index: Int) {
        spindexer.toSlot(index, false)
        spindexer.update()

        val timer = ElapsedTime()

        while (!spindexer.atPosition && !isStopRequested && opModeIsActive() && timer.seconds() < 1.0) {
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