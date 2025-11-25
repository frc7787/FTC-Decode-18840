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

    val amps: Double
        get() {
            // Hood current draw is negligible
            return flywheel.amps
        }

    var state: State = State.STOPPED

    // -------------------------------------------------------

    private fun distanceToHoodAngle(distance: Double): Double {
        TODO()
    }

    fun spinUp() {
        state = State.ACTIVE
    }

    fun stop() {
        state = State.STOPPED
    }

    fun update(distance: Double) {
        require(distance.isReal()) {
            "Expected real april tag distance. Got: $distance"
        }
        require(distance > 0.0) {
            "Expected positive april tag distance. Got: $distance"
        }

        when (state) {
            State.ACTIVE -> {
                 flywheel.spinUp()
            }
            State.STOPPED -> {
                 flywheel.stop()
            }
        }
        hood.setAngle(distanceToHoodAngle(distance))
    }

    // -------------------------------------------------------

    enum class State {
        ACTIVE,
        STOPPED
    }
}