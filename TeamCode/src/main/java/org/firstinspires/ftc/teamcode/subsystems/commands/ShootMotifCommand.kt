@file:Suppress("FunctionName")

package org.firstinspires.ftc.teamcode.subsystems.commands

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.match
import com.pedropathing.ivy.groups.Groups.sequential
import org.firstinspires.ftc.teamcode.commands.withTimeoutMs
import org.firstinspires.ftc.teamcode.decode.Motif
import org.firstinspires.ftc.teamcode.subsystems.Robot
import java.util.EnumMap

context(robot: Robot)
private fun ShootPGPMotifCommand(): Command {
    return sequential(
        robot.transfer.up()
            .withTimeoutMs(100.0),
        robot.spindexer.power(0.8)
            .withTimeoutMs(500.0)
    )
}

context(robot: Robot)
private fun ShootPPGMotifCommand(): Command {
    return sequential(
        robot.spindexer.toNextIntakingPosition(),
        robot.transfer.up()
            .withTimeoutMs(100.0),
        robot.spindexer.power(0.8)
            .withTimeoutMs(500.0)
    )
}

context(robot: Robot)
private fun ShootGPPMotifCommand(): Command {
    return sequential(
        robot.spindexer.toNextIntakingPosition(),
        robot.spindexer.toNextIntakingPosition(),
        robot.transfer.up()
            .withTimeoutMs(100.0),
        robot.spindexer.power(0.8)
            .withTimeoutMs(500.0)
    )
}

context(robot: Robot)
fun ShootMotifCommand(motif: Motif): Command {
    val cases  = EnumMap<Motif, Command>(Motif::class.java)
    cases[PGP] = ShootPGPMotifCommand()
    cases[PPG] = ShootPPGMotifCommand()
    cases[GPP] = ShootGPPMotifCommand()
    return match({ motif }, cases)
}