package org.firstinspires.ftc.teamcode.control

import kotlin.math.sign

class BangBang(var controlPolicy: ControlPolicy = ZERO_POWER) {

    fun update(state: Double, reference: Double): Double {
        val error = state - reference

        return when (controlPolicy) {
            NEGATIVE_POWER -> {
                sign(error)
            }
            ZERO_POWER -> {
                if (error > 0.0) 1.0 else 0.0
            }
        }
    }

    enum class ControlPolicy {
        NEGATIVE_POWER,
        ZERO_POWER,
    }
}