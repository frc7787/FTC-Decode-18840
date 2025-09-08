package org.firstinspires.ftc.teamcode.subsytems

import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.util.supplier.numeric.EnhancedDoubleSupplier
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.pedropathing.Constants

object DriveBase: Subsystem {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val follower by subsystemCell {
        Constants.createFollower(FeatureRegistrar.activeOpMode.hardwareMap)
    }

    // ---------------------------------------------------------------------------------------------
    // Commands

    fun drive(
        driveSupplier: () -> Double,
        strafeSupplier: () -> Double,
        turnSupplier: () -> Double,
        brake: Boolean = true,
        robotCentric: Boolean = false
    ): Lambda {
        return Lambda("drive")
            .setInit {
                follower.startTeleopDrive(brake)
            }
            .setExecute {
                follower.setTeleOpDrive(
                    driveSupplier.invoke(),
                    strafeSupplier.invoke(),
                    turnSupplier.invoke(),
                    robotCentric
                )
            }
            .setFinish { false }
    }

    fun followPath(path: Path, holdEnd: Boolean = false): Lambda {
        return Lambda("follow-path")
            .setExecute {
                follower.followPath(path, holdEnd)
            }
            .setFinish { !follower.isBusy }
    }

    fun followPathChain(path: PathChain, holdEnd: Boolean = false): Lambda {
        return Lambda("follow-path-chain")
            .setExecute {
                follower.followPath(path, holdEnd)
            }
            .setFinish { !follower.isBusy }
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