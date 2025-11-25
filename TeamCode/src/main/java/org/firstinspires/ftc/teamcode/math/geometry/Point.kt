package org.firstinspires.ftc.teamcode.math.geometry

import org.firstinspires.ftc.teamcode.math.isReal

data class Point(val x: Double, val y: Double) {
    init {
        require(x.isReal()) {
            "Expected point to have real, finite x. Got: $x"
        }
        require(y.isReal()) {
            "Expected point to have real, finite y. Got: $y"
        }
    }

    fun transformX(x: Double): Point {
        require(x.isReal()) {
            "Expected point to have real, finite x. Got: $x"
        }
        return Point(this.x + x, y)
    }

    fun transformY(y: Double): Point {
        require(y.isReal()) {
            "Expected point to have real, finite y. Got: $y"
        }
        return Point(x, this.y + y)
    }

    fun reflectX(): Point {
        return Point(x, -y)
    }

    fun reflectY(): Point {
        return Point(y, -x)
    }

    companion object {
        val ORIGIN = Point(0.0, 0.0)
    }
}
