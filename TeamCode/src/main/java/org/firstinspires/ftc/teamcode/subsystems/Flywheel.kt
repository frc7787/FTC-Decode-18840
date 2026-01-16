package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.control.FFCoefficients
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDCoefficients
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.math.isReal
import org.firstinspires.ftc.teamcode.util.NotNaN
import org.firstinspires.ftc.teamcode.util.unreachable
import org.firstinspires.ftc.teamcode.util.warnIf
import kotlin.math.abs

class Flywheel(
    hardwareMap: HardwareMap,
    private val configuration: Configuration = Configuration.DEFAULT,
    private val rpmSupplier: () -> Double,
): Subsystem {
    private val leaderMotor   = hardwareMap["leaderFlywheelMotor"]   as DcMotorSimple
    private val followerMotor = hardwareMap["followerFlywheelMotor"] as DcMotorSimple
    private val voltageSensor = hardwareMap.getAll(VoltageSensor::class.java)[0]!!

    private val ff  = FFController(configuration.ffCoefficients)
    private val pid = PIDController(configuration.pidCoefficients)

    init {
        leaderMotor.direction   = configuration.leaderMotorDirection
        followerMotor.direction = configuration.followerMotorDirection
        pid.tolerance = configuration.tolerance
    }

    val rpm: Double
        get() {
            return rpmSupplier()
        }

    val motorPower: Double
        get() {
            return leaderMotor.power
        }

    var pidCoefficients: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)
        set(value) {
            pid.coefficients = value
            field = value
        }

    var ffCoefficients: FFCoefficients = FFCoefficients(0.0, 0.0, 0.0)
        set(value) {
            ff.coefficients = value
            field = value
        }

    // State

    var controlMode: ControlMode = VOLTAGE
        private set

    /**
     * @throws IllegalArgumentException If the value of target RPM is set to NaN or Infinity
     */
    var targetRPM: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(controlMode == VELOCITY) { "Cannot Obtain Target RPM Unless 'mode' is set to RPM"}
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

            controlMode = VELOCITY
            field = target
        }

    var flywheelState: FlywheelState = AT_VELOCITY
        @Throws(IllegalArgumentException::class)
        get() {
            check(controlMode == VELOCITY) { "Cannot obtain flywheel state unless control mode is set to velocity" }
            return field
        }
        private set

    var rawPower: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(controlMode == VOLTAGE) { "Cannot obtain raw power unless the state is RAW. To get the power of the motor use 'motorPower'" }
            return field
        }
        @Throws(IllegalArgumentException::class)
        set(target) {
            require(target.isReal()) { "Expected Real Target Power. Got: $target" }

            controlMode = VOLTAGE

            field = target.coerceIn(-configuration.maxPower, configuration.maxPower)
        }

    // State

    @Throws(IllegalStateException::class)
    override fun update() {
        val voltage = voltageSensor.voltage
        warnIf(voltage == 0.0) { "Voltage Is 0.0" }

        val voltageCompensationScalar = if (voltage == 0.0) 1.0 else 12.0 / voltage
        warnIf(voltageCompensationScalar > 1.5) { "Voltage compensation output saturated" }

        val ffOutput  = ff.calculate(targetRPM)
        val pidOutput = pid.calculate(rpm, targetRPM)

        flywheelState = when {
            abs(targetRPM - rpm) < configuration.tolerance -> AT_VELOCITY
            rpm < targetRPM                                -> UNDER_VELOCITY
            rpm > targetRPM                                -> OVER_VELOCITY
            else                                           -> unreachable()
        }

        when (controlMode) {
            VOLTAGE -> {
                leaderMotor.power   = rawPower
                followerMotor.power = rawPower
            }
            VELOCITY -> when (flywheelState) {
                UNDER_VELOCITY, AT_VELOCITY -> {
                    val outputPower = ffOutput * voltageCompensationScalar
                    leaderMotor.power   = outputPower
                    followerMotor.power = outputPower
                }
                OVER_VELOCITY               -> {
                    leaderMotor.power   = pidOutput * voltageCompensationScalar
                    followerMotor.power = ffOutput * voltageCompensationScalar
                }
            }
        }
    }

    fun stop() {
        rawPower = 0.0
    }

    override fun debug(telemetry: Telemetry, verbose: Boolean) {
        telemetry.addLine("---- Flywheel ----")
        telemetry.addLine("Control Mode: $controlMode")
        telemetry.addLine("Voltage: ${voltageSensor.voltage}")
        when (controlMode) {
            VOLTAGE  -> {
                telemetry.addLine("Power: $rawPower")
            }
            VELOCITY -> {
                telemetry.addLine("Power: $motorPower")
                telemetry.addLine("Current RPM: $rpm")
                telemetry.addLine("Target RPM: $targetRPM")
                telemetry.addLine("Flywheel State: $flywheelState")
            }
        }

        if (verbose) {
            telemetry.addLine("Direction: ${leaderMotor.direction}")
        }
    }

    class Configuration(val pidCoefficients: PIDCoefficients, val ffCoefficients: FFCoefficients, val leaderMotorDirection: DcMotorSimple.Direction, val followerMotorDirection: DcMotorSimple.Direction, maxPower: Double, val tolerance: Double) {
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
            maxPower: Double                                = DEFAULT.maxPower,
            tolerance: Double                               = DEFAULT.tolerance
        ): Configuration {
            return Configuration(
                pidCoefficients,
                ffCoefficients,
                leaderMotorDirection,
                followerMotorDirection,
                maxPower,
                tolerance
            )
        }



        companion object {
            val DEFAULT = Configuration(
                pidCoefficients        = PIDCoefficients(0.0, 0.0, 0.0),
                ffCoefficients         = FFCoefficients(1.48e-4, 0.0, 0.0),
                leaderMotorDirection   = FORWARD,
                followerMotorDirection = REVERSE,
                maxPower               = 1.0,
                tolerance              = 100.0,
            )
        }
    }

    enum class ControlMode {
        VOLTAGE,
        VELOCITY,
    }

    enum class FlywheelState {
        UNDER_VELOCITY,
        AT_VELOCITY,
        OVER_VELOCITY
    }
}