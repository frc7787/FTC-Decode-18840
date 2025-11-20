package org.firstinspires.ftc.teamcode.math

import kotlin.jvm.Throws
import kotlin.math.abs

fun Double.isRoughlyZero(): Boolean {
    return abs(this) < 1e-9
}

/**
 * @throws IllegalArgumentException if the value is one of: Infinite or NaN.
 */
@Throws(IllegalArgumentException::class)
fun Double.enforceNotNaNOrInfinite(name: String) {
    require(this.isFinite()) {
        "Expected finite $name. Got: $this"
    }
    require(!this.isNaN()) {
        "Expected real $name. Got: $this"
    }
}