package org.firstinspires.ftc.teamcode.datastructures

import kotlin.jvm.Throws

class CircularBuffer<T>(val capacity: Int) {
    private val buffer: Array<T?> = arrayOfNulls(capacity)

    private var head = 0
    private var tail = 0
    private var size = 0

    fun add(element: T) {
        buffer[head] = element
        head = (head + 1) % capacity
        if (size < capacity) {
            size++
        } else {
            tail = (tail + 1) % capacity
        }
    }

    @Throws(IndexOutOfBoundsException::class, BufferNotInitializedException::class)
    fun peek(index: Int): T {
        if (index !in 0..capacity) {
            throw IndexOutOfBoundsException()
        }
        if (index > size) {
            throw BufferNotInitializedException(index)
        }
        return buffer[index]!!
    }

    @Throws(BufferNotInitializedException::class)
    fun poll(): T {
        if (size == 0) {
            throw BufferNotInitializedException(0)
        }
        return buffer[tail]!!.also {
            buffer[tail] == null
            tail = (tail - 1) % capacity
        }
    }

    class BufferNotInitializedException(index: Int): Exception("Premature access of index $index")
}