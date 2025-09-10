package org.firstinspires.ftc.teamcode.utility

import java.util.Properties

class PropertiesHandler(private val name: String) {
    val properties = Properties()

    fun load() {
        val classLoader = this::class.java.classLoader!!
        properties.load(classLoader.getResourceAsStream(name))
    }

    fun readString(name: String): String {
        return properties.getProperty(name)
    }

    fun readDouble(name: String): Double {
        return properties.getProperty(name).toDouble()
    }

    fun readInt(name: String): Int {
        return properties.getProperty(name).toInt()
    }

    fun readBoolean(name: String): Boolean {
        return properties.getProperty(name).toBoolean()
    }

    inline fun <reified T: Enum<T>> readEnum(name: String): T {
        return enumValueOf<T>(properties.getProperty(name))
    }

    fun writeString(name: String, value: String) {
        properties.setProperty(name, value)
    }

    fun writeBoolean(name: String, value: Boolean) {
        properties.setProperty(name, value.toString())
    }

    fun writeDouble(name: String, value: Double) {
        properties.setProperty(name, value.toString())
    }

    fun writeInt(name: String, value: Int) {
        properties.setProperty(name, value.toString())
    }
}