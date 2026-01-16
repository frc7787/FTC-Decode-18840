package org.firstinspires.ftc.teamcode.util

class UnreachableError(message: String): Exception(message)

@Throws(UnreachableError::class)
fun unreachable(message: () -> String = { "" }): Nothing {
    throw UnreachableError(message.invoke())
}