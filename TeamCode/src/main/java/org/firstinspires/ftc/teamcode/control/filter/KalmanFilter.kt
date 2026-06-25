package org.firstinspires.ftc.teamcode.control.filter

class KalmanFilter(val q: Double, val r: Double) {
    private var x = 0.0
    private var p = 0.0
    private var k = 0.0
    private var initialized = false

    fun update(measurement: Double): Double {
        if (!initialized) {
            x = measurement
            p = q
            initialized = true
            return x
        }

        p += q

        k = p / (p + r)
        x += k * (measurement - x)
        p *= (1 - k)

        return x
    }
}