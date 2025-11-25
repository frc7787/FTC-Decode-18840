package org.firstinspires.ftc.teamcode.subsystems.shooter

import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.math.isReal

internal class Flywheel(hardwareMap: HardwareMap) {
    private val leaderMotor   = hardwareMap[LEADER_MOTOR_NAME] as DcMotorEx
    private val followerMotor = hardwareMap[FOLLOWER_MOTOR_NAME] as DcMotorEx

    init {
        leaderMotor.zeroPowerBehavior   = MOTOR_ZERO_POWER_BEHAVIOR
        followerMotor.zeroPowerBehavior = MOTOR_ZERO_POWER_BEHAVIOR

        leaderMotor.direction   = MOTOR_DIRECTION
        followerMotor.direction = MOTOR_DIRECTION
    }

    private val pid = PIDController(P, I, D).also { pid ->
        pid.minOutput = MIN_POWER
        pid.maxOutput = MAX_POWER
    }

    private val ff = FFController(KV, KA, KS).also { ff ->
        ff.minOutput = MIN_POWER
        ff.maxOutput = MAX_POWER
    }

    // State

    val rpm: Double
        get() {
            return leaderMotor.velocity
        }

    val amps: Double
        get() {
            return leaderMotor.getCurrent(CurrentUnit.AMPS) + followerMotor.getCurrent(CurrentUnit.AMPS)
        }

    fun setPower(power: Double) {
        require(power.isReal()) {
            "Expected real flywheel power. Got: $power"
        }
        power.coerceIn(MIN_POWER, MAX_POWER).also { power ->
            leaderMotor.power   = power
            followerMotor.power = power
        }
    }

    fun spinUp() {
        val rpm = this.rpm // So that it doesn't change in between pid and ff calls
        pid.calculate(rpm, FLYWHEEL_TARGET_RPM) + ff.calculate(rpm).coerceIn(MIN_POWER, MAX_POWER).also { power ->
            leaderMotor.power   = power
            followerMotor.power = power
        }
    }

    fun stop() {
        leaderMotor.power   = 0.0
        followerMotor.power = 0.0
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine("---- Flywheel ----")
        telemetry.addLine("Power: ${leaderMotor.power}")
        telemetry.addLine("RPM: $rpm")
        telemetry.addLine("Current (Amps): $amps")
        if (verbose) {
            telemetry.addLine("Direction: ${leaderMotor.direction}")
            telemetry.addLine("Zero Power Behavior: ${leaderMotor.zeroPowerBehavior}")
        }
    }

    private companion object {
        const val LEADER_MOTOR_NAME   = "leaderFlywheelMotor"
        const val FOLLOWER_MOTOR_NAME = "followerFlywheelMotor"

        val MOTOR_DIRECTION           = Direction.REVERSE
        val MOTOR_ZERO_POWER_BEHAVIOR = ZeroPowerBehavior.BRAKE

        const val P = 0.1
        const val I = 0.0
        const val D = 0.01

        const val KV = 0.01
        const val KA = 0.0
        const val KS = 0.2

        const val MIN_RPM = -1900.0
        const val MAX_RPM = 1900.0

        const val FLYWHEEL_TARGET_RPM = 1900.0

        const val MIN_POWER = -1.0
        const val MAX_POWER = 1.0
    }
}