package org.firstinspires.ftc.teamcode.opmodes.auto

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.groups.Groups.sequential
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(group = "Red")
class RedFar: Auto() {
    override val auto: Command = sequential(

    )
}