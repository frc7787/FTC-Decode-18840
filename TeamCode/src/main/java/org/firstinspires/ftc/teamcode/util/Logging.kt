package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.util.RobotLog

fun warnIf(condition: Boolean, message: () -> String) {
    if (condition) {
        RobotLog.ww("TEAM CODE", message())
    }
}