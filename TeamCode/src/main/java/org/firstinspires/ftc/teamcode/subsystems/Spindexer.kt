package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.control.PIDController
import kotlin.math.abs

class Spindexer(hardwareMap: HardwareMap) {
    private val servo    = hardwareMap[SERVO_NAME] as CRServo
    private val octoquad = hardwareMap["octoquad"] as OctoQuad

    init {
        servo.power     = 0.0
        servo.direction = SERVO_DIRECTION
        octoquad.channelBankConfig = OctoQuad.ChannelBankConfig.ALL_PULSE_WIDTH
        octoquad.saveParametersToFlash()
    }

    fun getPosition(): Int {
        return octoquad.readSinglePosition_Caching(0)
    }

    fun reset() {
        octoquad.resetAllPositions()
        octoquad.saveParametersToFlash()
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
                val position = getPosition()
                var clippedPosition = targetPosition

                if (abs(position - targetPosition) > 525) {
                    if (targetPosition > 525) {
                       clippedPosition -= 1039
                    } else {
                        clippedPosition += 1039
                    }
                }

                servo.power = pid.calculate(clippedPosition.toDouble(), getPosition().toDouble())
            }

        }

        telemetry.addLine("Position: ${getPosition()}")
        telemetry.addLine("Target Position: $targetPosition")
        telemetry.addLine("Servo Power: ${servo.power}")
    }

    fun slotOne() {
        setTargetPosition(OUTTAKE_SLOT_ONE)
    }

    fun slotTwo() {
        setTargetPosition(OUTTAKE_SLOT_TWO)
    }

    fun slotThree() {
        setTargetPosition(OUTTAKE_SLOT_THREE)
    }

    private var pid = PIDController(0.005, 0.0, 0.00).also { pid ->
        pid.tolerance = 0.01
    }

    private companion object {
        const val SERVO_NAME = "spindexerServo"
        val SERVO_DIRECTION  = DcMotorSimple.Direction.FORWARD

        const val MIN_POWER = -1.0
        const val NOMINAL_POWER = 0.8
        const val MAX_POWER = 1.0

        const val OUTTAKE_SLOT_ONE   = 821
        const val OUTTAKE_SLOT_TWO   = 116
        const val OUTTAKE_SLOT_THREE = 458
    }

    enum class Mode {
        POWER,
        POSITION
    }
}