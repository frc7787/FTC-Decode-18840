package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal

class LowPassFilter(val alpha: Double) {
    constructor(alpha: Int): this(alpha.toDouble())

    init {
        require(alpha.isReal()) {
            "Expected real alpha. Got: $alpha"
        }
        require(alpha in 0.0..1.0) {
            "Expected alpha in range 0.0 to 1.0. Got: $alpha"
        }
    }

    private var initialized = false

    /**
     * The current state of the low pass filter. You must call [update] at least once before calling
     * this function.
     *
     * @throws IllegalStateException If the low pass filter has not yet been initialized ([update]
     * has not been called).
     */
    var state = 0.0
        @Throws(IllegalStateException::class)
        get() {
            check(initialized) {
                "Cannot read state before low pass filter has been initialized"
            }
            return field
        }

    /**
     * Updates the low pass filter with a new data point
     *
     * @throws IllegalArgumentException If data is one of: NaN, Infinity
     */
    fun update(data: Double) {
        require(data.isReal()) {
            "Expected real data. Got: $data"
        }

        if (initialized) {
            state = data
            initialized = false
        } else {
            state = alpha * data + (1 - alpha) * state
        }
    }

    /**
     * Resets the low pass filter.
     *
     * @param initialState The initial state of the low pass filter
     *
     * @throws IllegalArgumentException If initialState if one of: NaN, Infinity
     */
    fun reset(initialState: Double) {
        require(initialState.isReal()) {
            "Expected real initial state. Got: $state"
        }
        state = initialState
    }

    /**
     * Resets the low pass filter. After calling reset, you must call [update] at least once before
     * the value of [state] is available again.
     */
    fun reset() {
        initialized = false
    }
}