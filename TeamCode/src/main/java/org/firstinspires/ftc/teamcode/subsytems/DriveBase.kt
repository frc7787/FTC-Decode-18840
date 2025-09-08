package org.firstinspires.ftc.teamcode.subsytems

import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.bindings.BoundDoubleSupplier
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.pedropathing.Constants

object DriveBase: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    val follower by subsystemCell {
        Constants.createFollower(FeatureRegistrar.activeOpMode.hardwareMap)
    }

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun postUserInitHook(opMode: Wrapper) { follower.update() }

    // ---------------------------------------------------------------------------------------------
    // Commands

    /**
     * Returns a command which runs the drive base. Does not end unless interrupted.
     */
    fun drive(
        driveSupplier: BoundDoubleSupplier,
        strafeSupplier: BoundDoubleSupplier,
        turnSupplier: BoundDoubleSupplier,
        brake: Boolean = true,
        robotCentric: Boolean = false
    ): Lambda {
        return Lambda("drive")
            .setInit {
                follower.startTeleopDrive(brake)
            }
            .setExecute {
                follower.setTeleOpDrive(
                    driveSupplier.state,
                    strafeSupplier.state,
                    turnSupplier.state,
                    robotCentric
                )
            }
            .setFinish { false }
    }

    /**
     * Returns a command which follows the supplied path. Finishes when
     * [com.pedropathing.follower.Follower.isBusy] returns false.
     */
    fun followPath(path: Path, holdEnd: Boolean = false): Lambda {
        return Lambda("follow-path")
            .setExecute {
                follower.followPath(path, holdEnd)
            }
            .setFinish {
                !follower.isBusy
            }
    }

    /**
     * Returns a command which follows the supplied path chain. Finishes when
     * [com.pedropathing.follower.Follower.isBusy] returns false.
     */
    fun followPath(path: PathChain, holdEnd: Boolean = false): Lambda {
        return Lambda("follow-path-chain")
            .setExecute {
                follower.followPath(path, holdEnd)
            }
            .setFinish {
                !follower.isBusy
            }
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