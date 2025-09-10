package org.firstinspires.ftc.teamcode.utility

import kotlinx.serialization.Serializable

@Serializable
enum class Direction {
    LEFT,
    RIGHT
}

@Serializable
enum class RotationDirection {
    CLOCKWISE,
    COUNTERCLOCKWISE
}