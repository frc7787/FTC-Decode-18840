package org.firstinspires.ftc.teamcode.control

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.math.isReal
import kotlin.math.abs

class PIDController(val coefficients: PIDCoefficients) {

    /**
     * @throws IllegalArgumentException If any of kp, ki, or kd is one of: Nan, Infinity
     */
    @Throws(IllegalArgumentException::class)
    constructor(kp: Double, ki: Double, kd: Double): this(PIDCoefficients(kp, ki, kd))

    constructor(kp: Int, ki: Int, kd: Int): this(kp.toDouble(), ki.toDouble(), kd.toDouble())

    // ----------
    // State

    private val timer = ElapsedTime()

    private var isFirstIteration = false

    private var previousTime      = 0.0
    private var previousError     = 0.0
    private var previousReference = 0.0
    private var integralSum       = 0.0

    // ----------
    // Config

    /**
     * The maximum value of the integral. Useful for preventing integral windup
     *
     * @throws IllegalArgumentException If the integral max is set to NaN
     */
    var integralMax = 0.25
        @Throws(IllegalArgumentException::class)
        set(max) {
            require(max.isReal()) {
                "Expected real integral max. Got: $max"
            }

            reset()

            field = max
        }

    /**
     * The minimum value of the integral. Useful for preventing integral windup.
     *
     * @throws IllegalArgumentException If the integral min is set to NaN
     */
    var integralMin = -0.25
        @Throws(IllegalArgumentException::class)
        set(min) {
            require(min.isReal()) {
                "Expected real integral min. Got: $min"
            }

            reset()

            field = min
        }

    /**
     * The absolute value of the tolerance
     *
     * @throws IllegalArgumentException If the tolerance is NaN
     * @throws IllegalArgumentException If the tolerance is less than 0.0
     */
    var tolerance = 10.0
        @Throws(IllegalArgumentException::class)
        set(tolerance) {
            require(tolerance.isReal()) {
                "Expected real tolerance. Got: $tolerance"
            }
            require(tolerance >= 0.0) {
                "Expected positive tolerance. Got: $tolerance"
            }

            reset()

            field = tolerance
        }

    /**
     * The minimum output of the PID controller.
     *
     * @throws IllegalArgumentException If the value is set to NaN
     */
    var minOutput = -1.0
        @Throws(IllegalArgumentException::class)
        set(min) {
            require(min.isReal()) {
                "Expected real min output. Got: $min"
            }
            field = min
        }

    /**
     * The maximum output of the PID controller.
     *
     * @throws IllegalArgumentException If the value is set to NaN
     */
    var maxOutput = 1.0
        @Throws(IllegalArgumentException::class)
        set(max) {
            require(max.isReal()) {
                "Expected real max output. Got: $max"
            }
            field = max
        }

    // ----------
    // Core

    /**
     * Calculates the output power based on the current state (state) and the desired state
     * (reference).
     *
     * @throws IllegalArgumentException if either state or reference are one of: Nan, Infinity
     */
    @Throws(IllegalArgumentException::class)
    fun calculate(state: Double, reference: Double): Double {
        require(state.isReal()) {
            "Expected real state. Got: $state"
        }

        require(reference.isReal()) {
            "Expected real reference. Got: $reference"
        }

        if (isFirstIteration) {
            timer.reset()
            isFirstIteration = false
        }

        val currentTime = timer.milliseconds()
        val deltaTime = currentTime - previousTime

        val error = run {
            val raw = reference - state
            if (abs(raw) > tolerance) raw else 0.0
        }

        val deltaError = error - previousError

        val proportional = coefficients.kp * error

        // Reset the integral sum if the reference changes (and we aren't already within tolerance)
        if (reference != previousReference && abs(error) < tolerance) {
            integralSum = 0.0
        }

        integralSum += error * deltaTime
        integralSum.coerceIn(integralMin, integralMax)

        val integral = integralSum * coefficients.ki

        val derivative = if (deltaTime == 0.0) 0.0 else (deltaError / deltaTime) * coefficients.kd

        previousTime      = currentTime
        previousError     = error
        previousReference = reference

        return (proportional + integral + derivative).coerceIn(minOutput, maxOutput)
    }

    /**
     * Calculates the output power based on the current state (state) and the desired state
     * (reference).
     *
     * @throws IllegalArgumentException if either state or reference are one of: Nan, Infinity
     */
    @Throws(IllegalArgumentException::class)
    fun calculate(state: Int, reference: Int): Double {
        return calculate(state.toDouble(), reference.toDouble())
    }

    /**
     * Resets the PID controller.
     */
    fun reset() {
        isFirstIteration = true
        previousError     = 0.0
        previousReference = 0.0
        previousTime      = 0.0
        integralSum       = 0.0
    }
}