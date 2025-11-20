package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.enforceNotNaNOrInfinite

data class PIDCoefficients(val kp: Double, val ki: Double, val kd: Double) {

    init {
        kp.enforceNotNaNOrInfinite("kp")
        require(kp >= 0.0) {
            "Expected kp > 0.0. Got: $kp"
        }

        ki.enforceNotNaNOrInfinite("ki")
        require(ki >= 0.0) {
            "Expected ki > 0.0. Got: $ki"
        }

        kd.enforceNotNaNOrInfinite("kd")
        require(kd >= 0.0) {
            "Expected real kd. Got: $kd"
        }
    }
}