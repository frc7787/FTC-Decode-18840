package org.firstinspires.ftc.teamcode.subsystems

import android.content.Context
import com.qualcomm.ftccommon.FtcEventLoop
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier
import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop

object SubsystemManager: OpModeManagerNotifier.Notifications {

    override fun onOpModePreInit(opmode: OpMode) {
        // For sanity
        Intake.destroy()
        Flywheel.destroy()
        Spindexer.destroy()
        Transfer.destroy()
    }

    override fun onOpModePreStart(opmode: OpMode) {}

    override fun onOpModePostStop(opmode: OpMode) {
        Intake.destroy()
        Flywheel.destroy()
        Spindexer.destroy()
        Transfer.destroy()
    }

    @JvmStatic
    @OnCreateEventLoop
    fun register(context: Context, eventLoop: FtcEventLoop) {
        eventLoop.opModeManager.registerListener(this)
    }
}