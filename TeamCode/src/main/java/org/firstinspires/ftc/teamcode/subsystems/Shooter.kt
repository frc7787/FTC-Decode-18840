package org.firstinspires.ftc.teamcode.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDController
import kotlin.math.abs

@Configurable
class Shooter(hardwareMap: HardwareMap) {
    // -------------------------------------------------------
    // Configuration

    companion object {
        private const val LEADER_MOTOR_NAME   = "leaderShooterMotor"
        private const val FOLLOWER_MOTOR_NAME = "followerShooterMotor"

        @JvmField
        var P = 0.1
        @JvmField
        var I = 0.0
        @JvmField
        var D = 0.01

        @JvmField
        var KV = 0.01
        @JvmField
        var KA = 0.0
        @JvmField
        var KS = 0.2

        @JvmField
        var MAX_VELOCITY = 1900.0
        @JvmField
        var MIN_VELOCITY = -1900.0

        @JvmField
        var MAX_POWER = 1.0
        @JvmField
        var MIN_POWER = -1.0
    }

    // -------------------------------------------------------
    // Hardware

    private val pidController = PIDController(P, I, D)
    private val ffController = FFController(KV, KA, KS)

    private val leaderMotor = hardwareMap[LEADER_MOTOR_NAME] as DcMotorEx
    private val followerMotor = hardwareMap[FOLLOWER_MOTOR_NAME] as DcMotorEx

    // -------------------------------------------------------
    // Construction

    init {
        leaderMotor.direction   = Direction.REVERSE
        followerMotor.direction = Direction.REVERSE
    }

    // -------------------------------------------------------
    // State

    var power = 0.0
        private set

    var targetRPM = 0.0
        private set

    private var mode = Mode.POWER

    val rpm: Double
        get() {
            return leaderMotor.velocity
        }

    val amps: Double
        get() {
            return leaderMotor.getCurrent(CurrentUnit.AMPS) + followerMotor.getCurrent(CurrentUnit.AMPS)
        }

    private var actionDurationSeconds = 0.0
    private var beginActionTimer = false
    private val actionTimer = ElapsedTime()

    // -------------------------------------------------------

    fun runAtPower(power: Double, seconds: Double) {
        require(power.isFinite() && !power.isNaN()) {
            "Expected finite, real power. Got: $power"
        }
        require(seconds > 0.0 && !seconds.isNaN()) {
            "Expected positive, real time. Got: $seconds"
        }

        this.power = power.coerceIn(MIN_POWER, MAX_POWER)

        beginActionTimer = false
        actionDurationSeconds = seconds

        mode = Mode.POWER
    }

    fun runAtPower(power: Double) {
        runAtPower(power, Double.POSITIVE_INFINITY)
    }

    fun runAtVelocity(rpm: Double, seconds: Double) {
        require(seconds > 0.0 && !seconds.isNaN()) {
            "Expected positive time. Got: $seconds"
        }
        require(rpm.isFinite() && !rpm.isNaN()) {
            "Expected finite, real rpm. Got: $rpm"
        }

        targetRPM = rpm.coerceIn(MIN_VELOCITY, MAX_VELOCITY)

        beginActionTimer = false
        actionDurationSeconds = seconds

        mode = Mode.VELOCITY
    }

    fun runAtVelocity(rpm: Double) {
        runAtVelocity(rpm, seconds = Double.POSITIVE_INFINITY)
    }

    // -------------------------------------------------------

    fun update() {
        when (mode) {
            Mode.VELOCITY -> {
                if (rpm > targetRPM && !beginActionTimer) {
                    beginActionTimer = true
                    actionTimer.reset()
                }
            }
            Mode.POWER -> {
                if (!beginActionTimer) {
                    beginActionTimer = true
                    actionTimer.reset()
                }
            }
        }

        if (actionTimer.seconds() > actionDurationSeconds) {
            runAtPower(0.0)
        }

        var power = when (mode) {
            Mode.POWER -> { this.power }
            Mode.VELOCITY -> {
                (pidController.calculate(rpm, targetRPM) + ffController.calculate(rpm))
                    .coerceIn(MIN_POWER, MAX_POWER)
            }
        }

        if (abs(power) < 0.05) power = 0.0

        leaderMotor.power   = power
        followerMotor.power = power
    }

    fun debug(telemetry: Telemetry, verbose: Boolean = false) {
        telemetry.addLine()
        telemetry.addLine("--- Shooter ---")
        telemetry.addLine("Mode: $mode")
        telemetry.addLine("Power: $power")
        telemetry.addLine("Velocity: $rpm")
        telemetry.addLine("Current (Amps): $amps")
        if (mode == Mode.VELOCITY) {
            telemetry.addLine("Target Velocity: $targetRPM")
            telemetry.addLine("Error :${targetRPM - rpm}")
        }
        if (verbose) {
            telemetry.addLine("Leader Motor Direction: ${leaderMotor.direction}")
            telemetry.addLine("Follower Motor Direction: ${followerMotor.direction}")
            telemetry.addLine("Leader Motor Zero Power Behavior: ${leaderMotor.zeroPowerBehavior}")
            telemetry.addLine("Follower Motor Zero Power Behavior: ${followerMotor.zeroPowerBehavior}")
            telemetry.addLine("Min Power: $MIN_POWER")
            telemetry.addLine("Max Power: $MAX_POWER")
            telemetry.addLine("Min Velocity: $MIN_VELOCITY")
            telemetry.addLine("Max Velocity: $MAX_VELOCITY")
        }
    }

    private enum class Mode {
        VELOCITY,
        POWER
    }
}
