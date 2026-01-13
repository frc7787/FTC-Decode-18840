package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.*

class MecanumDriveBase(hardwareMap: HardwareMap) {
    private val frontLeftMotor  = hardwareMap[FRONT_LEFT_MOTOR_NAME]  as DcMotorEx
    private val frontRightMotor = hardwareMap[FRONT_RIGHT_MOTOR_NAME] as DcMotorEx
    private val backLeftMotor   = hardwareMap[BACK_LEFT_MOTOR_NAME]   as DcMotorEx
    private val backRightMotor  = hardwareMap[BACK_RIGHT_MOTOR_NAME]  as DcMotorEx

    init {
        frontLeftMotor.zeroPowerBehavior  = ZeroPowerBehavior.BRAKE
        frontRightMotor.zeroPowerBehavior = ZeroPowerBehavior.BRAKE
        backLeftMotor.zeroPowerBehavior   = ZeroPowerBehavior.BRAKE
        backRightMotor.zeroPowerBehavior  = ZeroPowerBehavior.BRAKE
    }

    var frontLeftPower: Double = 0.0
        private set

    var frontRightPower: Double = 0.0
        private set

    var backLeftPower: Double = 0.0
        private set

    var backRightPower: Double = 0.0
        private set

    fun setDrivePowers(drive: Double, strafe: Double, turn: Double) {
        val thetaRadians = atan2(drive, strafe)

        val power = hypot(drive, strafe)

        val sinTheta = sin(thetaRadians - Math.PI / 4.0)
        val cosTheta = cos(thetaRadians - Math.PI / 4.0)
        val max = max(abs(sinTheta), abs(cosTheta))

        frontLeftPower  = power * cosTheta / max + turn
        frontRightPower = power * sinTheta / max - turn
        backLeftPower   = power * sinTheta / max + turn
        backRightPower  = power * cosTheta / max - turn

        val turnMagnitude = abs(turn)

        if (power + turnMagnitude > 1.0) {
            frontLeftPower  /= turnMagnitude
            frontRightPower /= turnMagnitude
            backLeftPower   /= turnMagnitude
            backRightPower  /= turnMagnitude
        }
    }

    fun update() {
        frontLeftMotor.power  = frontLeftPower.coerceIn(-0.9, 0.9)
        frontRightMotor.power = frontRightPower.coerceIn(-0.9, 0.9)
        backLeftMotor.power   = backLeftPower.coerceIn(-0.9, 0.9)
        backRightMotor.power  = backRightPower.coerceIn(-0.9, 0.9)
    }

    companion object {
        // -------------------------
        // Configuration
        private const val FRONT_LEFT_MOTOR_NAME = "frontLeftDriveMotor"
        private const val FRONT_RIGHT_MOTOR_NAME = "frontRightDriveMotor"
        private const val BACK_LEFT_MOTOR_NAME = "backLeftDriveMotor"
        private const val BACK_RIGHT_MOTOR_NAME = "backRightDriveMotor"

        private val FRONT_LEFT_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE
        private val FRONT_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE
        private val BACK_LEFT_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE
        private val BACK_RIGHT_MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE

        private val FRONT_LEFT_MOTOR_DIRECTION = DcMotorSimple.Direction.FORWARD
        private val FRONT_RIGHT_MOTOR_DIRECTION = DcMotorSimple.Direction.REVERSE
        private val BACK_LEFT_MOTOR_DIRECTION = DcMotorSimple.Direction.FORWARD
        private val BACK_RIGHT_MOTOR_DIRECTION = DcMotorSimple.Direction.REVERSE
    }
}
