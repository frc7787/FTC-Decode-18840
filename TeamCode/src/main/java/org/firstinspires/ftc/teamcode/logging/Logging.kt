package org.firstinspires.ftc.teamcode.logging

import com.qualcomm.robotcore.util.RobotLog

fun log(message: String, tag: String = "Team Code") {
    RobotLog.ii(tag, message)
}

fun logAndThrow(tag: String = "Team Code", message: String, error: Throwable): Nothing {
    RobotLog.ee(tag, error, message)
    throw error
}