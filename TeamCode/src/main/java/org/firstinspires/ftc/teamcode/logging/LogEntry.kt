package org.firstinspires.ftc.teamcode.logging

class LogEntry(time: Long = System.currentTimeMillis()) {
    private var content = time.toString()

    fun append(value: Any) { content += ",$value," }

    fun formatted(): String { return "${content.removeSuffix(",")}\r\n" }
}