package org.firstinspires.ftc.teamcode.control

import com.pedropathing.control.KalmanFilter
import org.firstinspires.ftc.teamcode.math.isReal

data class KalmanFilterParameters(val modelCovariance: Double, val dataCovariance: Double) {

    init {
        require(modelCovariance.isReal()) {
            "Expected real model covariance. Got: $modelCovariance"
        }
        require(dataCovariance.isReal()) {
            "Expected real data covariance. Got: $dataCovariance"
        }
    }
}