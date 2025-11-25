package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal

class Interpolator(val function: (Double) -> Double) {

    fun interpolate(input: Double): Double {
        require(input.isReal()) {
            "Expected real interpolation input. Got: $input"
        }

        return function.invoke(input)
    }

}