package org.firstinspires.ftc.teamcode.control

import org.firstinspires.ftc.teamcode.math.isReal

class KalmanFilter(val parameters: KalmanFilterParameters) {

    /**
     * @throws IllegalArgumentException If modelCovariance is NaN or Infinity
     * @throws IllegalArgumentException If dataCovariance is NaN or Infinity
     */
    @Throws(IllegalArgumentException::class)
    constructor(modelCovariance: Double, dataCovariance: Double): this(KalmanFilterParameters(modelCovariance, dataCovariance))

    constructor(modelCovariance: Int, dataCovariance: Int): this(modelCovariance.toDouble(), dataCovariance.toDouble())

    var state = 0.0
        private set

    private var previousState    = 0.0
    private var variance         = 0.0
    private var kalmanGain       = 0.0
    private var previousVariance = 0.0

    /**
     * Updates the state of the kalman filter
     *
     * @param data The current measurement
     * @param projection The projected measurement
     *
     * @throws IllegalArgumentException If data is NaN or Infinity
     * @throws IllegalArgumentException If projection is NaN or Infinity
     */
    @Throws(IllegalArgumentException::class)
    fun update(data: Double, projection: Double) {
        require(data.isReal()) {
            "Expected real data. Got: $data"
        }
        require(projection.isReal()) {
            "Expected real projection. Got: $projection"
        }

        state      = previousState + data
        variance   = previousVariance + parameters.modelCovariance
        kalmanGain = variance / (variance + parameters.dataCovariance)

        state    += kalmanGain * (projection - state)
        variance *= (1.0 - kalmanGain)

        previousState    = state
        previousVariance = variance
    }

    /**
     * Updates the state of the kalman filter
     *
     * @param data The current measurement
     * @projection The projected measurement
     */
    fun update(data: Int, projection: Int) {
        update(data.toDouble(), projection.toDouble())
    }
}