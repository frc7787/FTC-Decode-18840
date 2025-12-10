package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.math.isReal

internal class Flywheel(hardwareMap: HardwareMap) {
    private val leaderMotor   = hardwareMap[LEADER_MOTOR_NAME]   as DcMotorSimple
    private val followerMotor = hardwareMap[FOLLOWER_MOTOR_NAME] as DcMotorSimple

    private val shooterEncoder = hardwareMap["frontLeftDriveMotor"] as DcMotor

    init {
        leaderMotor.direction   = LEADER_MOTOR_DIRECTION
        followerMotor.direction = FOLLOWER_MOTOR_DIRECTION
    }

    // State

    fun setPower(power: Double) {
        require(power.isReal()) {
            "Expected real flywheel power. Got: $power"
        }
        power.coerceIn(0.0, MAX_POWER).also { power ->
            leaderMotor.power   = power
            followerMotor.power = power
        }
    }

    fun spinUp(targetRpm: Double) {
        require(targetRpm.isReal()) {
            "Expected real target rpm. Got: $targetRpm"
        }

        val targetDegreesPerSecond = targetRpm * 6.0
    }

    fun stop() {
        leaderMotor.power   = 0.0
        followerMotor.power = 0.0
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine("---- Flywheel ----")
        telemetry.addLine("Power: ${leaderMotor.power}")
        if (verbose) {
            telemetry.addLine("Direction: ${leaderMotor.direction}")
        }
    }

    private companion object {
        const val LEADER_MOTOR_NAME   = "leaderFlywheelMotor"
        const val FOLLOWER_MOTOR_NAME = "followerFlywheelMotor"

        val LEADER_MOTOR_DIRECTION    = DcMotorSimple.Direction.FORWARD
        val FOLLOWER_MOTOR_DIRECTION  = DcMotorSimple.Direction.REVERSE
        val MOTOR_ZERO_POWER_BEHAVIOR = DcMotor.ZeroPowerBehavior.FLOAT

        const val P = 0.001
        const val I = 0.0
        const val D = 0.0001

        const val KV = 0.00005
        const val KA = 0.0
        const val KS = 0.05

        const val MAX_RPM = 6200

        const val MAX_POWER = 1.0
    }
}