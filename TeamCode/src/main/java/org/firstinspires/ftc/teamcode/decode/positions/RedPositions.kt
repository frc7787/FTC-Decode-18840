package org.firstinspires.ftc.teamcode.decode.positions

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose

@Configurable
object RedPositions {
    var CLOSE_SHOOTING_POSITION = Pose(0.0, 0.0)
    var FAR_SHOOTING_POSITION   = Pose(0.0, 0.0)
    var GOAL_SPIKE_MARK_START   = Pose(0.0, 0.0)
    var GOAL_SPIKE_MARK_END     = Pose(0.0, 0.0)
    var GATE_SPIKE_MARK_START   = Pose(0.0, 0.0)
    var GATE_SPIKE_MARK_END     = Pose(0.0, 0.0)
    var MOTIF_READ_POSITION     = Pose(0.0, 0.0)
}