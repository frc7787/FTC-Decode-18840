package org.firstinspires.ftc.teamcode.subsystems

import org.firstinspires.ftc.robotcore.external.Telemetry

interface Subsystem {
    fun update()

    fun debug(telemetry: Telemetry, verbose: Boolean)
}