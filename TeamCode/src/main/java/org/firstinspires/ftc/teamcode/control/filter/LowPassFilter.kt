package org.firstinspires.ftc.teamcode.control.filter

class LowPassFilter(private val alpha: Double) {
    init {
        require(alpha in 0.0..1.0) { "Alpha must be between 0.0 and 1.0" }
    }

    fun update(current: Double, previous: Double): Double {
        return previous + alpha * (current - previous)
    }
}