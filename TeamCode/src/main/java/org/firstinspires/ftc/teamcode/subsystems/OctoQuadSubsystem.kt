package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.hardware.HardwareMap



class OctoQuadSubsystem(hardwareMap: HardwareMap) {
    private val octoquad = hardwareMap["octoquad"] as OctoQuad

    init {
        octoquad.channelBankConfig = ALL_PULSE_WIDTH
    }

    private val usedChannels = mutableSetOf<Int>()

    fun channel(id: Int): Channel {
        require(id in 0..7) {
            "Expected channel id in range 0 to 7. Got: $id"
        }
        require(id !in usedChannels) {
            "Channel with id $id is already in use"
        }

        usedChannels.add(id)

        return Channel(
            { octoquad.readSinglePosition_Caching(id).toDouble() },
            { octoquad.readSingleVelocity_Caching(id).toDouble() },
        )
    }

    class Channel internal constructor(
        private val positionSupplier: () -> Double,
        private val velocitySupplier: () -> Double,
    ) {
        val velocity: Double
            get() {
                return velocitySupplier.invoke()
            }
        val position: Double
            get() {
                return positionSupplier.invoke()
            }
    }
}