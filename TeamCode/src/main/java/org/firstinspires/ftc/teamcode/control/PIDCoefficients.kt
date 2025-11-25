package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal

data class PIDCoefficients(val kp: Double, val ki: Double, val kd: Double) {

    init {
        require(kp.isReal()) {
            "Expected real, finite kp. Got: $kp"
        }
        require(kp >= 0.0) {
            "Expected kp > 0.0. Got: $kp"
        }

        require(ki.isReal()) {
            "Expected real, finite ki. Got: $ki"
        }
        require(ki >= 0.0) {
            "Expected ki > 0.0. Got: $ki"
        }

        require(kd.isReal()) {
            "Expected real, finite kd. Got: $kd"
        }
        require(kd >= 0.0) {
            "Expected real kd. Got: $kd"
        }
    }
}