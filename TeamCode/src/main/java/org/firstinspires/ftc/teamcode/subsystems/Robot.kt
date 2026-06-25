package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedropathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.vision.Camera

class Robot(hardwareMap: HardwareMap) {
    val flywheel  = Flywheel(hardwareMap)
    val intake    = Intake(hardwareMap)
    val transfer  = Transfer(hardwareMap)
    val spindexer = Spindexer(hardwareMap)
    val camera    = Camera(hardwareMap)
    val follower  = Constants.createFollower(hardwareMap)

    fun init() {

    }
}