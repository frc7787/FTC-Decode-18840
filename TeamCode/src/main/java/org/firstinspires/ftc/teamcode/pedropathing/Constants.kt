package org.firstinspires.ftc.teamcode.pedropathing

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.paths.PathConstraints
import com.qualcomm.robotcore.hardware.HardwareMap

object Constants {
    private val FOLLOWER_CONSTANTS: FollowerConstants = FollowerConstants()

    private val PATH_CONSTRAINTS: PathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0)

    @JvmStatic
    fun createFollower(hardwareMap: HardwareMap): Follower {
        return FollowerBuilder(FOLLOWER_CONSTANTS, hardwareMap)
            .pathConstraints(PATH_CONSTRAINTS)
            .build()
    }
}
