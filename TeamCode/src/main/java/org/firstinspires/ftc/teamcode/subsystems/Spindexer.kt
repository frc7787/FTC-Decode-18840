package org.firstinspires.ftc.teamcode.subsystems

import android.graphics.Color
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.NormalizedColorSensor
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.control.FFCoefficients
import org.firstinspires.ftc.teamcode.control.FFController
import org.firstinspires.ftc.teamcode.control.PIDCoefficients
import org.firstinspires.ftc.teamcode.control.PIDController
import org.firstinspires.ftc.teamcode.subsystems.Spindexer.ArtifactColor.GREEN
import org.firstinspires.ftc.teamcode.subsystems.Spindexer.ArtifactColor.PURPLE
import org.firstinspires.ftc.teamcode.subsystems.Spindexer.ArtifactColor.UNKNOWN
import org.firstinspires.ftc.teamcode.util.NotNaN
import kotlin.math.abs
import kotlin.math.roundToInt

class Spindexer(
    hardwareMap: HardwareMap,
    private val tickSupplier: () -> Double,
    private val velocitySupplier: () -> Double,
    private val resetFunction: () -> Unit,
    private val configuration: Configuration = Configuration.DEFAULT,
) {
    private val motor               = hardwareMap["spindexerMotor"]      as DcMotorSimple
    private val intakeColorSensor   = hardwareMap["intakeColorSensor"]   as NormalizedColorSensor
    private val flywheelColorSensor = hardwareMap["flywheelColorSensor"] as NormalizedColorSensor

    private val pid = PIDController(configuration.pidCoefficients)
    private val feedforward  = FFController(configuration.ffCoefficients)

    init {
        motor.direction = configuration.direction
        pid.tolerance = configuration.tolerance
        pid.minOutput = configuration.minPower
        pid.maxOutput = configuration.maxPower
    }

    val slotStates: Array<SlotState> = arrayOf(EMPTY, EMPTY, EMPTY)

    val intakeSlotArtifactColor: ArtifactColor
        @Throws(IllegalStateException::class)
        get() {
            check(artifactInIntakeSlot) {
                "Cannot obtain color of artifact. There is not artifact in the intake slot."
            }
            return when(hsv[0].toInt()) {
                in 150..180 -> GREEN
                in 210..250 -> PURPLE
                else        -> UNKNOWN
            }
        }

    val artifactInIntakeSlot: Boolean
        get() {
            return when(hsv[0].toInt()) {
                in 150..180, in 210..250 -> true
                else -> false
            }
        }

    val spindexerVelocity: Double
        get() {
            return velocitySupplier.invoke()
        }

    val atPosition: Boolean
        get() {
            check(state == ANGLE) {
                "Cannot query whether the spindexer is at position if there is no target angle"
            }

            return abs(angle - targetAngle) < 2.0
        }

    val atIntakingPosition: Boolean
        get() {
            return abs(angle - 60.0) < 5.0 || abs(angle - 180.0) < 5.0 || abs(angle - 300.0) < 5.0
        }

    val position: Double
        get() {
            return tickSupplier.invoke()
        }

    val angle: Double
        get() {
            return ((position / TICKS_PER_ROTATION) * 360.0).mod(360.0)
        }

    var targetAngle: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(state == ANGLE) {
                "Cannot obtain target position if state is not current POSITION"
            }
            return field
        }
        set(target) {
            state = ANGLE
            field = target.mod(360.0)
        }

    var targetPower: Double = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(state == POWER) {
                "Cannot obtain target power if mode is not set to POWER"
            }
            return field
        }
        set(target) {
            state = POWER
            field = target.coerceIn(configuration.minPower, configuration.maxPower)
        }

    val motorPower: Double
        get() {
            return motor.power
        }

    var state: State = POWER
        private set

    private val hsv = FloatArray(3) { 3.0f }

    private var previousIndex = 0
    private var index = 0

    fun update() {
        Color.colorToHSV(intakeColorSensor.normalizedColors.toColor(), hsv)

        motor.power = when (state) {
            POWER    -> targetPower
            ANGLE -> run {
                val correctedTarget = if (abs(targetAngle - angle) > 180.0) {
                    targetAngle + if (targetAngle > 180.0) -360 else 360
                } else {
                    targetAngle
                }

                pid.calculate(angle, correctedTarget) + feedforward.calculate(correctedTarget)
            }
        }
    }

    fun toSlot(index: Int, intaking: Boolean) {
        require(index in 0..2) {
            "Expected slot index in range 1 to 2. Got: $index"
        }

        if (atIntakingPosition && abs(spindexerVelocity) < 25.0) {
            slotStates[index] = when (hsv[0].toInt()) {
                in 150..185 -> FULL(GREEN)
                in 210..250 -> FULL(PURPLE)
                else        -> EMPTY
            }
        }

        targetAngle = run {
            val intakeOffset = if (intaking) 60.0 else 0.0
            index * 120.0 + intakeOffset
        }
    }

    fun debug(telemetry: Telemetry) {
        telemetry.addLine("----- Spindexer -----")
        telemetry.addLine("Position: $position")
        telemetry.addLine("Angle: $angle")
        telemetry.addLine("Hue: ${hsv[0]}")

        when (state) {
            ANGLE -> {
                telemetry.addLine("Target Angle: $targetAngle")
                telemetry.addLine("Index: $index")
            }
            else -> {}
        }

        telemetry.addLine("Slot 0: ${slotStates[0]}")
        telemetry.addLine("Slot 1: ${slotStates[1]}")
        telemetry.addLine("Slot 2: ${slotStates[2]}")
        telemetry.addLine("At Intaking Position: $atIntakingPosition")
        telemetry.addLine("Velocity: $spindexerVelocity")

        telemetry.addLine("Power: ${motor.power}")
    }

    companion object {
        const val TICKS_PER_ROTATION = 725
    }

    class Configuration(val direction: DcMotorSimple.Direction, val zeroPowerBehavior: DcMotor.ZeroPowerBehavior, homingPower: Double, minPower: Double, maxPower: Double, tolerance: Double, p: Double, i: Double, d: Double, ks: Double) {
        init {
            require(minPower < maxPower) {
                "Min power must be less than max power."
            }

            require(homingPower in minPower..maxPower) {
                "Homing power must be within the defined minimum and maximum power."
            }
        }

        val pidCoefficients = PIDCoefficients(p, i, d)
        val ffCoefficients  = FFCoefficients(0.0, 0.0, ks)

        val tolerance   by NotNaN(tolerance)
        val homingPower by NotNaN(homingPower.coerceIn(-1.0, 1.0))
        val minPower    by NotNaN(minPower.coerceIn(-1.0, 1.0))
        val maxPower    by NotNaN(maxPower.coerceIn(-1.0, 1.0))

        fun with(
            direction: DcMotorSimple.Direction = DEFAULT.direction,
            zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DEFAULT.zeroPowerBehavior,
            homingPower: Double = DEFAULT.homingPower,
            minPower: Double = DEFAULT.minPower,
            maxPower: Double = DEFAULT.maxPower,
            tolerance: Double = DEFAULT.tolerance,
            p: Double = DEFAULT.pidCoefficients.kp,
            i: Double = DEFAULT.pidCoefficients.ki,
            d: Double = DEFAULT.pidCoefficients.kd,
            ks: Double = DEFAULT.ffCoefficients.ks
        ): Configuration {
            return Configuration(
                direction,
                zeroPowerBehavior,
                homingPower,
                minPower,
                maxPower,
                tolerance,
                p,
                i,
                d,
                ks
            )
        }

        companion object {
            val DEFAULT by lazy {
                Configuration(
                    direction = REVERSE,
                    zeroPowerBehavior = BRAKE,
                    homingPower = 0.4,
                    minPower = -0.7,
                    maxPower = 0.7,
                    tolerance = 1.0,
                    p = 0.03,
                    i = 0.0,
                    d = 0.00,
                    ks = 0.07
                )
            }
        }
    }

    enum class State {
        POWER,
        ANGLE,
    }

    enum class ArtifactColor {
        GREEN,
        PURPLE,
        UNKNOWN
    }

    sealed interface SlotState

    object EMPTY: SlotState {
        override fun toString(): String {
            return "Empty"
        }
    }
    data class FULL(val color: ArtifactColor): SlotState

}