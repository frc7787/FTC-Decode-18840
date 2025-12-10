package org.firstinspires.ftc.teamcode.datastructures

import java.util.function.Supplier


class MutableArray<T>(val capacity: Int, default: Supplier<T>) {
    val internal = buildList {
        for (number in 0..capacity) {
            add(default.get())
        }
    }
    
}