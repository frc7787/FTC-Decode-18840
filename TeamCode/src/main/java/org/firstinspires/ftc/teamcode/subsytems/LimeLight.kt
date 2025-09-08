package org.firstinspires.ftc.teamcode.subsytems

import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D

object LimeLight: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val limeLight by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap["limeLight"] as Limelight3A
    }

    // ---------------------------------------------------------------------------------------------
    // State

    lateinit var pose: Pose3D
        private set

    // ---------------------------------------------------------------------------------------------
    // Internal

    private fun updateState(state: LLResult?) {
        if (state == null) return
        state.botpose_MT2?.let { pose = it }
    }

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun preUserInitHook(opMode: Wrapper) {
        limeLight.pipelineSwitch(0)
        limeLight.start()
    }

    override fun preUserLoopHook(opMode: Wrapper) {
        updateState(limeLight.latestResult)
    }

    // ---------------------------------------------------------------------------------------------
    // Subsystem Boilerplate

    override var dependency: Dependency<*>
            = Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    annotation class Attach

    // ---------------------------------------------------------------------------------------------
}