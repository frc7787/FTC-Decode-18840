package org.firstinspires.ftc.teamcode.control

import kotlinx.serialization.Serializable

@Serializable
data class PIDCoefficients(
    val p: Double,
    val i: Double,
    val d: Double
) {
    constructor(p: Int, i: Int, d: Int): this(p.toDouble(), i.toDouble(), d.toDouble())

    fun toPIDF(f: Double): PIDFCoefficients {
        return PIDFCoefficients(p, i, d, f)
    }

    fun toPIDF(f: Int): PIDFCoefficients {
        return toPIDF(f.toDouble())
    }
}