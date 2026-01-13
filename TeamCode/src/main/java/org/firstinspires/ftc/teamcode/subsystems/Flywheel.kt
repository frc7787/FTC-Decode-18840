package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.control.FFCoefficients
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDCoefficients
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.math.isReal
import org.firstinspires.ftc.teamcode.util.NotNaN
import org.firstinspires.ftc.teamcode.util.warnIf

class Flywheel(
    hardwareMap: HardwareMap,
    private val configuration: Configuration = Configuration.DEFAULT,
    private val rpmSupplier: () -> Double,
): Subsystem {
    private val leaderMotor   = hardwareMap[LEADER_MOTOR_NAME]   as DcMotorSimple
    private val followerMotor = hardwareMap[FOLLOWER_MOTOR_NAME] as DcMotorSimple


    private val ff  = FFController(configuration.ffCoefficients)
    private val pid = PIDController(configuration.pidCoefficients)

    init {
        leaderMotor.direction   = configuration.leaderMotorDirection
        followerMotor.direction = configuration.followerMotorDirection
    }

    val rpm: Double
        get() {
            return rpmSupplier()
        }

    val motorPower: Double
        get() {
            return leaderMotor.power
        }

    // State

    var state: State = STOPPED
        private set

    /**
     * @throws IllegalArgumentException If the value of target RPM is set to NaN or Infinity
     */
    var targetRPM: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(state == VELOCITY) { "Cannot Obtain Target RPM Unless 'mode' is set to RPM"}
            return field
        }
        @Throws(IllegalArgumentException::class)
        set(target) {
            require(target.isReal()) {
                "Expected Real Target RPM. Got: $target"
            }
            warnIf(target > 6000.0) {
                "Attempting To Set Flywheel RPM to $target. This is above motor free speed."
            }

            state = when (target) {
                0.0  -> STOPPED
                else -> VELOCITY
            }
            field = target
        }

    var rawPower: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(state == RAW) { "Cannot obtain raw power unless the state is RAW. To get the power of the motor use 'motorPower'" }
            return field
        }
        @Throws(IllegalArgumentException::class)
        set(target) {
            require(target.isReal()) {
                "Expected Real Target Power. Got: $target"
            }

            state = when (target) {
                0.0  -> STOPPED
                else -> RAW
            }

            field = target.coerceIn(-configuration.maxPower, configuration.maxPower)
        }

    // State

    var ffOutput  = 0.0
    var pidOutput = 0.0

    override fun update() {
        val power = when (state) {
            RAW      -> rawPower
            VELOCITY -> {
                ffOutput  = ff.calculate(targetRPM)
                pidOutput = pid.calculate(rpm, targetRPM)
                ffOutput + pidOutput
            }
            STOPPED  -> 0.0
        }

        leaderMotor.power   = power
        followerMotor.power = power
    }

    fun stop() {
        state = STOPPED
    }

    override fun debug(telemetry: Telemetry, verbose: Boolean) {
        telemetry.addLine("---- Flywheel ----")
        telemetry.addLine("State: $state")
        when (state) {
            STOPPED -> {}
            RAW     -> {
                telemetry.addLine("Raw Power: $rawPower")
            }
            VELOCITY -> {
                telemetry.addLine("Current Power: $motorPower")
                telemetry.addLine("Current RPM: $rpm")
                telemetry.addLine("Target RPM: $targetRPM")
                telemetry.addLine("FF Output: $ffOutput")
                telemetry.addLine("PID Output: $pidOutput")
            }
        }

        if (verbose) {
            telemetry.addLine("Direction: ${leaderMotor.direction}")
        }
    }

    class Configuration(val pidCoefficients: PIDCoefficients, val ffCoefficients: FFCoefficients, val leaderMotorDirection: DcMotorSimple.Direction, val followerMotorDirection: DcMotorSimple.Direction, maxPower: Double) {
        val maxPower by NotNaN(maxPower)

        init {
            require(maxPower.isReal()) {
                "Expected Real Max Power. Got: $maxPower"
            }
        }

        fun with(
            pidCoefficients: PIDCoefficients                = DEFAULT.pidCoefficients,
            ffCoefficients: FFCoefficients                  = DEFAULT.ffCoefficients,
            leaderMotorDirection: DcMotorSimple.Direction   = DEFAULT.leaderMotorDirection,
            followerMotorDirection: DcMotorSimple.Direction = DEFAULT.followerMotorDirection,
            maxPower: Double                                = DEFAULT.maxPower
        ): Configuration {
            return Configuration(
                pidCoefficients,
                ffCoefficients,
                leaderMotorDirection,
                followerMotorDirection,
                maxPower
            )
        }

        companion object {
            val DEFAULT = Configuration(
                pidCoefficients        = PIDCoefficients(2.7e-3, 0.0, 0.0),
                ffCoefficients         = FFCoefficients(1.6e-4, 0.0, 5.0e-2),
                leaderMotorDirection   = FORWARD,
                followerMotorDirection = REVERSE,
                maxPower               = 1.0
            )
        }
    }

    private companion object {
        const val LEADER_MOTOR_NAME   = "leaderFlywheelMotor"
        const val FOLLOWER_MOTOR_NAME = "followerFlywheelMotor"
    }

    enum class State {
        RAW,
        VELOCITY,
        STOPPED
    }
}