package org.firstinspires.ftc.teamcode.commands

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.groups.Groups.deadline

fun Command.withTimeoutMs(timeoutMs: Double): Command {
    return deadline(
        waitMs(timeoutMs),
        this
    )
}