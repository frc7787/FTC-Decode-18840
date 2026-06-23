package org.firstinspires.ftc.teamcode.subsystems

import com.pedropathing.ivy.Command
import com.pedropathing.ivy.CommandBuilder
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.control.Feedforward
import org.firstinspires.ftc.teamcode.control.PID
import org.firstinspires.ftc.teamcode.hardware.Encoder
import org.firstinspires.ftc.teamcode.hardware.SparkMini
import org.firstinspires.ftc.teamcode.hardware.SparkMiniGroup
import java.net.InetSocketAddress

class Flywheel private constructor(hardwareMap: HardwareMap) {
    val motors = SparkMiniGroup(
        SparkMini(hardwareMap["flywheelMotorOne"] as CRServo).also { motor ->
            motor.direction = FLYWHEEL_MOTOR_ONE_DIRECTION
        },
        SparkMini(hardwareMap["flywheelMotorTwo"] as CRServo).also { motor ->
            motor.direction = FLYWHEEL_MOTOR_TWO_DIRECTION
        }
    )

    val encoder = Encoder(hardwareMap["frontLeftDriveMotor"] as DcMotorEx)

    private val feedforward = Feedforward(KV, KA, KS)
    private val pid = PID(KP, KI, KD)

    fun spinAt(targetRpm: Double): Command {
        return CommandBuilder()
            .setStart {
                if (targetRpm > MAX_ACHIEVABLE_RPM) {
                    // TODO Add Warning To Log
                }
            }
            .setExecute {
                val rpm = encoder.rpm
                motors.power = feedforward.calculate(rpm , targetRpm) + pid.calculate(rpm, targetRpm)
            }
            .setDone { false }
            .requiring(this)
    }

    companion object {
        private val FLYWHEEL_MOTOR_ONE_DIRECTION: Direction = FORWARD
        private val FLYWHEEL_MOTOR_TWO_DIRECTION: Direction = FORWARD
        private const val MAX_ACHIEVABLE_RPM = 4500

        private const val KV = 0.0001
        private const val KA = 0.0
        private const val KS = 0.11

        private const val KP = 0.0064
        private const val KI = 0.0
        private const val KD = 0.00056

        private var INSTANCE: Flywheel? = null

        fun get(hardwareMap: HardwareMap): Flywheel {
            if (INSTANCE == null) INSTANCE = Flywheel(hardwareMap)
            return INSTANCE!!
        }

        fun destroy() {
            INSTANCE = null
            System.gc()
        }
    }
}