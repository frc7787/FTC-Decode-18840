package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.control.PIDController
import kotlin.math.abs

class Spindexer(hardwareMap: HardwareMap, private val positionSupplier: () -> Int) {
    private val servo    = hardwareMap[SERVO_NAME] as CRServo

    init {
        servo.power     = 0.0
        servo.direction = SERVO_DIRECTION
    }

    val position: Int
        get() {
            return positionSupplier.invoke()
        }

    fun spin() {
        setPower(NOMINAL_POWER)
    }

    private var targetPosition = 0
    var targetPower = 0.0

    var mode = Mode.POWER

    fun setTargetPosition(position: Int) {
        targetPosition = position
        mode = Mode.POSITION
    }

    fun setPower(power: Double) {
        targetPower = power
        mode = Mode.POWER
    }

    fun update(telemetry: Telemetry) {
        when (mode) {
            Mode.POWER -> {
                servo.power = targetPower
            }
            Mode.POSITION -> {
                val targetPosition = if (abs(targetPosition - position) > 526) {
                    if (targetPosition > 525) {
                        targetPosition - 1039
                    } else {
                        targetPosition + 1039
                    }
                } else {
                    targetPosition
                }

                servo.power = pid.calculate(targetPosition, position)
            }
        }

        telemetry.addLine("Position: $position")
        telemetry.addLine("Target Position: $targetPosition")
        telemetry.addLine("Servo Power: ${servo.power}")
    }

    fun toOuttakeOne() {
        setTargetPosition(OUTTAKE_SLOT_ONE)
    }

    fun toOuttakeTwo() {
        setTargetPosition(OUTTAKE_SLOT_TWO)
    }

    fun toOuttakeThree() {
        setTargetPosition(OUTTAKE_SLOT_THREE)
    }

    fun toIntakeOne() {
        setTargetPosition(INTAKE_SLOT_ONE)
    }

    fun toIntakeTwo() {
        setTargetPosition(INTAKE_SLOT_TWO)
    }

    fun toIntakeThree() {
        setTargetPosition(INTAKE_SLOT_THREE)
    }


    private var pid = PIDController(0.006, 0.0, 0.00).also { pid ->
        pid.tolerance = 3.0
    }

    companion object {
        private const val SERVO_NAME = "spindexerServo"
        private val SERVO_DIRECTION  = DcMotorSimple.Direction.FORWARD

        const val MIN_POWER = -1.0
        const val NOMINAL_POWER = 0.8
        const val MAX_POWER = 1.0

        const val OUTTAKE_SLOT_ONE   = 821
        const val OUTTAKE_SLOT_TWO   = 116
        const val OUTTAKE_SLOT_THREE = 458

        const val INTAKE_SLOT_ONE   = 980
        const val INTAKE_SLOT_TWO   = 289
        const val INTAKE_SLOT_THREE = 640
    }

    enum class Mode {
        POWER,
        POSITION
    }
}