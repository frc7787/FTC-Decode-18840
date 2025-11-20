package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.enforceNotNaNOrInfinite

data class FFCoefficients(val kv: Double, val ka: Double, val ks: Double) {
    init {
        kv.enforceNotNaNOrInfinite("kv")
        require(kv >= 0.0) {
            "Expected non-zero positive kv. Got: $kv"
        }

        ka.enforceNotNaNOrInfinite("ka")
        require(ka >= 0.0)  {
            "Expected non-zero positive ka. Got: $ka"
        }

        ks.enforceNotNaNOrInfinite("ks")
        require(ks >= 0.0) {
            "Expected non-zero positive ka. Got: $ks"
        }
    }
}
