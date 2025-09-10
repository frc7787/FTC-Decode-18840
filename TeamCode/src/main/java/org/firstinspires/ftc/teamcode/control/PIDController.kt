package org.firstinspires.ftc.teamcode.control

import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs

open class PIDController(
    coefficients: PIDCoefficients,
    private val tolerance: Double = 10.0,
    private val minOutput: Double = -1.0,
    private val maxOutput: Double = 1.0
) {
    private val p = coefficients.p
    private val i = coefficients.i
    private val d = coefficients.d

    init {
        require(minOutput < maxOutput) { "minOutput must be less than maxOutput" }
    }

    private val timer = ElapsedTime()

    // ---------------------------------------------------------------------------------------------
    // State

    private var integralSum   = 0.0
    private var previousError = 0.0

    open fun calculate(position: Double, target: Double): Double {
        val error = position - target
        if (abs(error) < tolerance) {
            previousError = 0.0
            return 0.0
        }

        val deltaTime = timer.milliseconds()
        integralSum += error * deltaTime

        val output = (p * error + i * integralSum + d * (error - previousError) / deltaTime)
            .coerceIn(minOutput, maxOutput)

        return output.also { previousError = error }
    }

    open fun calculate(position: Int, target: Int): Double {
        return calculate(position.toDouble(), target.toDouble())
    }
}