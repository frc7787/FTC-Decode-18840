package org.firstinspires.ftc.teamcode.math

import kotlin.math.abs

fun Double.isRoughlyZero(): Boolean {
    return abs(this) < 1e-9
}

fun Double.isReal(): Boolean {
    return this.isFinite() && !this.isNaN()
}