package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

class MecanumDrive(hardwareMap: HardwareMap) {
    private val frontLeftDriveMotor  = hardwareMap["frontLeftDriveMotor"]  as DcMotorEx
    private val frontRightDriveMotor = hardwareMap["frontRightDriveMotor"] as DcMotorEx
    private val backLeftDriveMotor   = hardwareMap["backLeftDriveMotor"]   as DcMotorEx
    private val backRightDriveMotor  = hardwareMap["backRightDriveMotor"]  as DcMotorEx
    private val imu = hardwareMap["imu"] as IMU

    init {
        frontLeftDriveMotor.direction = FRONT_LEFT_MOTOR_DIRECTION
        frontRightDriveMotor.direction = FRONT_RIGHT_MOTOR_DIRECTION
        backLeftDriveMotor.direction = BACK_LEFT_MOTOR_DIRECTION
        backRightDriveMotor.direction = BACK_RIGHT_MOTOR_DIRECTION

        frontLeftDriveMotor.zeroPowerBehavior  = FRONT_LEFT_MOTOR_ZERO_POWER_BEHAVIOR
        frontRightDriveMotor.zeroPowerBehavior = FRONT_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR
        backLeftDriveMotor.zeroPowerBehavior   = BACK_LEFT_MOTOR_ZERO_POWER_BEHAVIOR
        backRightDriveMotor.zeroPowerBehavior  = BACK_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR

        imu.initialize(IMU_PARAMETERS)
        imu.resetYaw()
    }

    var frontLeftPower = 0.0
        private set

    var frontRightPower = 0.0
        private set

    var backLeftPower = 0.0
        private set

    var backRightPower = 0.0
        private set

    var headingRadians = 0.0
        private set

    fun update(drive: Double, strafe: Double, turn: Double, fieldCentric: Boolean) {
        val thetaRadians = if (!fieldCentric) atan2(drive, strafe) else atan2(drive, strafe) - imu.robotYawPitchRollAngles.yaw

        val power = hypot(strafe, drive)

        val sinTheta = sin(thetaRadians - Math.PI / 4.0)
        val cosTheta = cos(thetaRadians - Math.PI / 4.0)

        val max = max(abs(cosTheta), abs(sinTheta))

        frontLeftPower  = power * cosTheta / max + turn
        frontRightPower = power * sinTheta / max - turn
        backLeftPower   = power * cosTheta / max + turn
        backRightPower  = power * sinTheta / max - turn

        val turnMagnitude = abs(turn)

        if ((power + turnMagnitude) > 1.0) {
            frontLeftPower  /= power + turnMagnitude
            frontRightPower /= power + turnMagnitude
            backLeftPower   /= power + turnMagnitude
            backRightPower  /= power + turnMagnitude
        }

        frontLeftDriveMotor.power  = frontLeftPower
        frontRightDriveMotor.power = frontRightPower
        backLeftDriveMotor.power   = backLeftPower
        backRightDriveMotor.power  = backRightPower
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine()
        telemetry.addLine("--- Mecanum Drive Debug ---")
        telemetry.addLine("Front Left Power: $frontLeftPower")
        telemetry.addLine("Front Right Power: $frontRightPower")
        telemetry.addLine("Back Left Power: $backLeftDriveMotor")
        telemetry.addLine("Back Right Power: $backRightDriveMotor")
        if (verbose) {
            telemetry.addLine("Front Left Direction: $FRONT_LEFT_MOTOR_DIRECTION")
            telemetry.addLine("Front Right Direction: $FRONT_RIGHT_MOTOR_DIRECTION")
            telemetry.addLine("Back Left Direction: $BACK_LEFT_MOTOR_DIRECTION")
            telemetry.addLine("Back Right Direction: $BACK_RIGHT_MOTOR_DIRECTION")
            telemetry.addLine("Front Left Zero Power Behavior: $FRONT_LEFT_MOTOR_ZERO_POWER_BEHAVIOR")
            telemetry.addLine("Front Right Zero Power Behavior: $FRONT_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR")
            telemetry.addLine("Back Left Zero Power Behavior: $BACK_LEFT_MOTOR_ZERO_POWER_BEHAVIOR")
            telemetry.addLine("Back Right Zero Power Behavior: $BACK_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR")
        }
    }

    fun resetIMU() {
        imu.resetYaw()
    }

    private companion object {
        const val FRONT_LEFT_MOTOR_NAME  = "frontLeftDriveMotor"
        const val FRONT_RIGHT_MOTOR_NAME = "frontRightDriveMotor"
        const val BACK_LEFT_MOTOR_NAME   = "backLeftDriveMotor"
        const val BACK_RIGHT_MOTOR_NAME  = "backRightDriveMotor"

        val FRONT_LEFT_MOTOR_DIRECTION  = Direction.FORWARD
        val FRONT_RIGHT_MOTOR_DIRECTION = Direction.FORWARD
        val BACK_LEFT_MOTOR_DIRECTION   = Direction.REVERSE
        val BACK_RIGHT_MOTOR_DIRECTION  = Direction.REVERSE

        val FRONT_LEFT_MOTOR_ZERO_POWER_BEHAVIOR  = ZeroPowerBehavior.BRAKE
        val FRONT_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE
        val BACK_LEFT_MOTOR_ZERO_POWER_BEHAVIOR   = ZeroPowerBehavior.BRAKE
        val BACK_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR  = ZeroPowerBehavior.BRAKE

        const val IMU_NAME = "imu"
        val IMU_PARAMETERS = IMU.Parameters(RevHubOrientationOnRobot(FORWARD, UP))
    }
}