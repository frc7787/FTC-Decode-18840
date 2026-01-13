package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal
import org.firstinspires.ftc.teamcode.util.warnIf
import kotlin.math.sign

class BangBangController(val power: Double) {

    init {
        require(power.isReal()) {
            "Expected real power. Got: $power"
        }
        warnIf(power < 0.0) {
            "Negative Power May Result In Divergent Error"
        }
    }

    fun calculate(state: Double, target: Double): Double {
        require(state.isReal()) {
            "Expected real state. Got: $state"
        }
        require(target.isReal()) {
            "Expected real target. Got: $target"
        }

        val error = target - state
        return power * sign(error)
    }
}