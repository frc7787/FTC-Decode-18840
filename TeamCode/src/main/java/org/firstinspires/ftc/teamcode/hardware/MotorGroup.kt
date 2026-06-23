package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import org.firstinspires.ftc.robotcore.external.Telemetry

class MotorGroup(vararg motors: Motor) {
    private val motors = motors.toList()

    init {
        motors.forEach { motor ->
            motor.ignoreSoftwarePositionLimits = true
        }
    }

    var minPower: Double
        set(new) {
            motors.forEach { motor -> motor.minPower = new  }
        }
        get() {
            return motors[0].minPower
        }

    var maxPower: Double
        set(new) {
            motors.forEach { motor -> motor.maxPower = new }
        }
        get() {
            return motors[0].maxPower
        }

    var power: Double
        set(new) {
            motors.forEach { motor -> motor.power = new }
        }
        get() {
            return motors[0].power
        }

    var direction: Direction
        set(new) {
            motors.forEach { motor -> motor.direction = new }
        }
        get() {
            return motors[0].direction
        }

    var zeroPowerBehavior: ZeroPowerBehavior
        set(new) {
            motors.forEach { motor -> motor.zeroPowerBehavior = new }
        }
        get() {
            return motors[0].zeroPowerBehavior
        }

    var position: Double
        set(new) {
            motors[0].position = new
        }
        get() {
            return motors[0].position
        }

    val currentsAmps: List<Double>
        get() {
            return motors.map { motor -> motor.currentAmps }
        }

    val currentAmps: Double
        get() {
            return motors.sumOf { motor -> motor.currentAmps }
        }

    val stalled: Boolean
        get() {
            return motors.all { motor -> motor.stalled }
        }

    fun debug(telemetry: Telemetry, name: String = "", simplified: Boolean = false) {
        motors.forEach { motor -> motor.debug(telemetry, name, simplified) }
    }
}