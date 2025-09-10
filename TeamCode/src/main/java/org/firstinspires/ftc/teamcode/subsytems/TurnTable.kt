package org.firstinspires.ftc.teamcode.subsytems

import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.subsystems.Subsystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

object TurnTable: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    // ---------------------------------------------------------------------------------------------
    // Config

    @Serializable
    data class Configuration(val debug: Boolean = false) {

        companion object {

            fun fromJson(): Configuration {
                try {
                    val rawText = File("turn-table-config.json").readText()
                    return Json.decodeFromString(rawText)
                } catch (_: Exception) {
                    return Configuration()
                }
            }

        }

    }

    var configuration = Configuration.fromJson()

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