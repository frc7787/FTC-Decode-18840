package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.pedropathing.Constants
import org.firstinspires.ftc.teamcode.subsystems.vision.Camera

class Robot(hardwareMap: HardwareMap) {
    val flywheel by lazy {
        Flywheel(hardwareMap)
    }

    val intake by lazy {
        Intake(hardwareMap)
    }

    val transfer by lazy {
        Transfer(hardwareMap)
    }

    val spindexer by lazy {
        Spindexer(hardwareMap)
    }

    val camera by lazy {
        Camera(hardwareMap)
    }

    val follower by lazy {
        Constants.createFollower(hardwareMap)
    }
}