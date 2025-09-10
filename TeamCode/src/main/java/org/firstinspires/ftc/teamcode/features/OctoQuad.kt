package org.firstinspires.ftc.teamcode.features

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import dev.frozenmilk.dairy.core.Feature
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.util.OpModeLazyCell
import dev.frozenmilk.dairy.core.wrapper.Wrapper

object OctoQuad: Feature {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val sensor by OpModeLazyCell {
        val hardwareMap = FeatureRegistrar.activeOpMode.hardwareMap
        hardwareMap["OctoQuad"] as OctoQuad
    }

    // ---------------------------------------------------------------------------------------------
    // State

    private var positions = listOf<Int>()

    private var velocities = listOf<Int>()

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun preUserInitHook(opMode: Wrapper) {
        sensor.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_QUADRATURE
    }

    override fun preUserLoopHook(opMode: Wrapper) {
        val encoderData = sensor.readAllEncoderData()
        if (!encoderData.isDataValid) return

        positions  = encoderData.positions.toList()
        velocities = encoderData.velocities.toList().map { it.toInt() }
    }

    // ---------------------------------------------------------------------------------------------
    // Getters

    fun position(channel: Int): Int {
        require(channel > 0 && channel < 4) { "Invalid channel: Must be between 0 and 4." }
        return positions[channel]
    }

    fun velocity(channel: Int): Int {
        require(channel > 0 && channel < 4) { "Invalid channel: Must be between 0 and 4." }
        return velocities[channel]
    }

    // ---------------------------------------------------------------------------------------------
    // Feature Boilerplate

    override var dependency: Dependency<*> = SingleAnnotation(Attach::class.java)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    annotation class Attach

    // ---------------------------------------------------------------------------------------------
}