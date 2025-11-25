package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal

data class FFCoefficients(val kv: Double, val ka: Double, val ks: Double) {
    init {
        require(kv.isReal()) {
            "Expected real kv. Got: $kv"
        }

        require(kv >= 0.0) {
            "Expected non-zero positive kv. Got: $kv"
        }

        require(ka.isReal()) {
            "Expected real ka. Got: $ka"
        }
        require(ka >= 0.0)  {
            "Expected non-zero positive ka. Got: $ka"
        }

        require(ks.isReal()) {
            "Expected real ks. Got: $ks"
        }
        require(ks >= 0.0) {
            "Expected non-zero positive ka. Got: $ks"
        }
    }
}
