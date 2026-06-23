package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import org.firstinspires.ftc.robotcore.external.Telemetry

class SparkMiniGroup(vararg minis: SparkMini) {
    private val minis = minis.toList()

    var power: Double
        set(new) {
            minis.forEach { motor -> motor.power = new }
        }
        get() {
            return minis[0].power
        }

    var direction: Direction
        set(new) {
            minis.forEach { motor -> motor.direction = new }
        }
        get() {
            return minis[0].direction
        }

    fun debug(telemetry: Telemetry, name: String = "", simplified: Boolean = false) {
        minis.forEach { mini -> mini.debug(telemetry, name, simplified) }
    }
}