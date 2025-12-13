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

    private val shooterEncoder = hardwareMap["frontRightDriveMotor"] as DcMotorEx

    init {
        leaderMotor.direction   = LEADER_MOTOR_DIRECTION
        followerMotor.direction = FOLLOWER_MOTOR_DIRECTION
    }

    private val feedforward = FFController(KV, KA, KS)
    private val pid = PIDController(P, I, D)

    var mode = Mode.POWER

    // State

    var targetRPM: Double = 0.0

    fun update() {
        when (mode) {
            Mode.POWER -> {
                leaderMotor.power   = targetPower
                followerMotor.power = targetPower
            }

            Mode.RPM -> {
                val power = feedforward.calculate(targetRPM) + pid.calculate(rpm, targetRPM)
                leaderMotor.power   = power
                followerMotor.power = power
            }
        }
    }

    var targetPower: Double = 0.0

    fun powerFlywheel(power: Double) {
        this.targetPower = power
        mode = Mode.POWER
    }

    fun spinUp(targetRpm: Double) {
        require(targetRpm.isReal()) {
            "Expected real target rpm. Got: $targetRpm"
        }

        this.targetRPM = targetRpm
        mode = Mode.RPM
    }

    val rpm: Double
        get() {
            return shooterEncoder.velocity
        }

    val power: Double
        get() {
            return leaderMotor.power
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

        const val P = 0.0012
        const val I = 0.0
        const val D = 0.00001

        const val KV = 0.00032
        const val KA = 0.0
        const val KS = 0.05

        const val MAX_RPM = 6200

        const val MAX_POWER = 1.0
    }

    enum class Mode {
        POWER,
        RPM
    }
}