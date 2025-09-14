package org.firstinspires.ftc.teamcode.logging

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.RobotLog
import dev.frozenmilk.dairy.core.Feature
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.util.OpModeLazyCell
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.dairy.core.wrapper.Wrapper.OpModeState
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.utility.ONBOT_JAVA_PATH
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.Locale.getDefault

typealias MotorDataProvider = Pair<String, DcMotorEx.() -> Any>

typealias Motor = Pair<DcMotorEx, String>

object MotorLogger: Feature {
    // ---------------------------------------------------------------------------------------------
    // Hardware

    private val motors by OpModeLazyCell {
        // This is safe because it will never be called if there isn't an Attach annotation present
        val annotation = FeatureRegistrar.activeOpModeWrapper.inheritedAnnotations
            .find { it is Attach }!! as Attach
        val motors = mutableListOf<Motor>()
        annotation.names.forEach { name ->
            motors.add(Pair(FeatureRegistrar.activeOpMode.hardwareMap[name] as DcMotorEx, name))
        }
        motors.toList()
    }

    // ---------------------------------------------------------------------------------------------
    // State

    private val logDuringInitLoop by OpModeLazyCell {
        val annotation = FeatureRegistrar.activeOpModeWrapper.inheritedAnnotations
            .find { it is Attach}!! as Attach
        annotation.logDuringInitLoop
    }

    private val dataProviders = mutableListOf<MotorDataProvider>()

    private val telemetrySubscribers = mutableSetOf<Telemetry>()

    private val logs by OpModeLazyCell {
        val dataList = mutableListOf<MutableList<LogEntry>>()
        motors.forEach { motor -> dataList.add(mutableListOf()) }
        dataList.toList()
    }

    private val timer = ElapsedTime()

    // ---------------------------------------------------------------------------------------------
    // Core

    fun logCurrent(unit: CurrentUnit) = addDataProvider("Current ($unit)") { getCurrent(unit) }

    fun logPosition() = addDataProvider("Position") { currentPosition }

    fun logVelocity() = addDataProvider("Velocity") { velocity }

    fun logPower() = addDataProvider("Power") { power }


    fun addDataProvider(name: String, provider: DcMotorEx.() -> Any) {
        require(FeatureRegistrar.activeOpModeWrapper.state == OpModeState.INIT) {
            "Cannot attach provider outside of OpMode init"
        }
        dataProviders.add(MotorDataProvider(name, provider))
    }

    fun addTelemetrySubscriber(subscriber: Telemetry) {
        require(FeatureRegistrar.activeOpModeWrapper.state == OpModeState.INIT) {
            "Cannot attach provider outside of OpMode init"
        }
        telemetrySubscribers.add(subscriber)
    }

    private fun updateTelemetrySubscribers(name: String, data: Any) {
        telemetrySubscribers.forEach { telemetry ->
            telemetry.addLine("$name: $data")
        }
    }

    private fun internalPreUserCode() {
        motors.zip(logs).forEach { (motorWrapper, log) ->
            val logEntry = LogEntry()
            dataProviders.forEach { (providerName, supplier) ->
                val (motor, motorName) = motorWrapper
                val data = motor.supplier()
                logEntry.append(data)
                updateTelemetrySubscribers("$motorName $providerName", data)
            }
            log.add(logEntry)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Hooks

    override fun postUserInitHook(opMode: Wrapper) {
       if (logDuringInitLoop) timer.reset()
    }

    override fun preUserInitLoopHook(opMode: Wrapper) {
        if (logDuringInitLoop) internalPreUserCode()
    }

    override fun postUserStartHook(opMode: Wrapper) {
        if (!logDuringInitLoop) timer.reset()
    }

    override fun preUserLoopHook(opMode: Wrapper) {
        internalPreUserCode()
    }

    override fun cleanup(opMode: Wrapper) {
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-mm-dd-hh-ss"))!!
        logs.zip(motors).forEach { (log, motor) ->
            val (_, motorName) = motor
            try {
                val logFile = File("$ONBOT_JAVA_PATH/logs/MotorLog${motorName}${dateTime}.csv").writer()
                log.forEach { entry -> logFile.write(entry.formatted())}
                logFile.close()
            } catch (exception: Exception) {
                RobotLog.ee("Exception while creating log file.", exception, exception.message)
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Feature Boilerplate

    override var dependency: Dependency<*> = SingleAnnotation(Attach::class.java).onResolve { annotation ->
        require(setOf(annotation.names).size == annotation.names.size) {
            val duplicateNames = annotation.names
                .groupingBy { it }
                .eachCount()
                .filter { (_, count) -> count > 2 }
            "Duplicate motors names found: ${duplicateNames.keys}"
        }
    }

    annotation class Attach(val logDuringInitLoop: Boolean, vararg val names: String)

    // ---------------------------------------------------------------------------------------------
}