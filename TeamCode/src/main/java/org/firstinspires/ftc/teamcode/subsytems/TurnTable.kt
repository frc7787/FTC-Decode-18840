package org.firstinspires.ftc.teamcode.subsytems

import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.subsystems.Subsystem

object TurnTable: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    // ---------------------------------------------------------------------------------------------
    // Config

    var configuration = Configuration()

    data class Configuration(val debug: Boolean = false)

    // ---------------------------------------------------------------------------------------------
    // State

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun postUserInitHook(opMode: Wrapper) {}

    override fun preUserLoopHook(opMode: Wrapper) {}

    // ---------------------------------------------------------------------------------------------
    // Commands

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