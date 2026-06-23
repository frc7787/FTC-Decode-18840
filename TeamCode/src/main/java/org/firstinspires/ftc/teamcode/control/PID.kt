package org.firstinspires.ftc.teamcode.control

import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs

class PID(coefficients: Coefficients) {
    constructor(p: Double, i: Double, d: Double): this(Coefficients(p, i, d))

    var coefficients: Coefficients = coefficients
        set(new) {
            field = new
            reset()
        }

    var tolerance: Double = 10.0
        set(new) {
            field = new
            reset()
        }

    var integralMin: Double = -1.0
        set(new) {
            field = new
            if (abs(current.error) < tolerance) reset()
        }

    var integralMax: Double = 1.0
        set(new) {
            field = new
            reset()
        }

    var minOutput: Double = -1.0

    var maxOutput: Double = 1.0

    private val timer = ElapsedTime()

    private var isFirstIteration: Boolean = false

    private var current  = State()
    private var previous = State()

    fun calculate(state: Double, reference: Double): Double {

        if (isFirstIteration) {
            reset()
            isFirstIteration = false
        }

        current.time      = timer.milliseconds()
        current.reference = reference

        current.error = run {
            val raw = reference - state
            if (abs(raw) > tolerance) raw else 0.0
        }

        val deltaTime = current.time - previous.time

        // ---------
        // Proportional

        val proportional = coefficients.p * current.error

        // ----------
        // Integral

        // Reset the integral sum if the reference changes (and we aren't already within tolerance)
        if (current.reference != previous.reference && abs(current.error) < tolerance) {
            current.integralSum = 0.0
        }

        current.integralSum += current.error * deltaTime
        current.integralSum.coerceIn(integralMin..integralMax)

        val integral = current.integralSum * coefficients.i

        // ----------
        // Derivative

        val deltaError = current.error - previous.error

        val derivative = if (deltaTime == 0.0) 0.0 else (deltaError / deltaTime) * coefficients.d

        // ----------

        previous = current

        return (proportional + integral + derivative).coerceIn(minOutput, maxOutput)
    }

    fun reset() {
        current  = State()
        previous = State()
        timer.reset()
    }

    data class Coefficients(
        val p: Double,
        val i: Double,
        val d: Double,
    )

    data class State(
        var error: Double       = 0.0,
        var time: Double        = 0.0,
        var integralSum: Double = 0.0,
        var reference: Double   = 0.0,
    )
}