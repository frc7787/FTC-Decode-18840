package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction

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
}