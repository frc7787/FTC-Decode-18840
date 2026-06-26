package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Pose
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.ivy.pedro.PedroCommands.follow
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.decode.Motif
import org.firstinspires.ftc.teamcode.decode.Motif.GPP
import org.firstinspires.ftc.teamcode.decode.Motif.PGP
import org.firstinspires.ftc.teamcode.decode.Motif.PPG
import org.firstinspires.ftc.teamcode.decode.positions.RedPositions
import org.firstinspires.ftc.teamcode.subsystems.commands.ShootMotifCommand

@TeleOp(group = "Red")
class RedClose: Auto() {
    private val startToMotif by lazy {
        robot.follower.pathBuilder()
            .addPath(BezierCurve(START_POSITION, RedPositions.MOTIF_READ_POSITION))
            .build()
    }

    private val motifToShoot by lazy {
        robot.follower.pathBuilder()
            .addPath(BezierCurve(RedPositions.MOTIF_READ_POSITION, RedPositions.CLOSE_SHOOTING_POSITION))
            .build()
    }

    private var motif: Motif = PGP

    override val auto: Command = with (robot) {
        sequential(
            follow(follower, startToMotif),
            instant {
                val tags = camera.detectedTags()
                if (tags.isEmpty()) return@instant

                motif = when (tags[0].id) {
                    PPG.tagId -> PPG
                    GPP.tagId -> GPP
                    PGP.tagId -> PGP
                    else      -> motif
                }
            },
            follow(follower, motifToShoot),
            ShootMotifCommand(motif)
        )
    }

    companion object {
        @JvmStatic var START_POSITION = Pose(0.0, 0.0)
    }
}