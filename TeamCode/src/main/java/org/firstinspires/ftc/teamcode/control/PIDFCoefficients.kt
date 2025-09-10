package org.firstinspires.ftc.teamcode.control

data class PIDFCoefficients(
    val p: Double,
    val i: Double,
    val d: Double,
    val f: Double
) {
    constructor(p: Int, i: Int, d: Int, f: Int): this(
        p.toDouble(),
        i.toDouble(),
        d.toDouble(),
        f.toDouble()
    )

    fun toPID(): PIDCoefficients {
        return PIDCoefficients(p, i, d)
    }
}