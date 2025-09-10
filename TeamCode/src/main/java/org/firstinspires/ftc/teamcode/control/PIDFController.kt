package org.firstinspires.ftc.teamcode.control

import kotlin.math.sign

class PIDFController(
    coefficients: PIDFCoefficients,
    tolerance: Double = 10.0,
    minOutput: Double = -1.0,
    maxOutput: Double = 1.0
): PIDController(coefficients.toPID(), tolerance, minOutput, maxOutput) {
    private val f = coefficients.f

    override fun calculate(position: Double, target: Double): Double {
        return super.calculate(position, target) + f * (position - target).sign
    }

    override fun calculate(position: Int, target: Int): Double {
        return calculate(position.toDouble(), target.toDouble())
    }
}