package org.firstinspires.ftc.teamcode.subsystems.shooter

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.math.isReal

class Shooter(hardwareMap: HardwareMap) {
    private val hood     = Hood(hardwareMap)
    private val flywheel = Flywheel(hardwareMap)

    // State

    val flywheelRpm: Double
        get() {
            return flywheel.rpm
        }

    val hoodAngle: Double
        get() {
            return hood.angle
        }

    val amps: Double
        get() {
            // Hood current draw is negligible
            return flywheel.amps
        }

    // -------------------------------------------------------

    private fun distanceToHoodAngle(distance: Double): Double {
        TODO()
    }

    fun adjustHood(distance: Double) {
        hood.angle = distanceToHoodAngle(distance)
    }

    fun spinUp() {
        flywheel.spinUp()
    }

    fun stop() {
        flywheel.stop()
    }


    // -------------------------------------------------------
}