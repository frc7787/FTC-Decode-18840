package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .forwardZeroPowerAcceleration(-26.208)
            .lateralZeroPowerAcceleration(-62.220)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.25, 0.0, 0.0, 0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(1.1, 0.0, 0.1, 0.0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.004, 0.0, 0.0, 0.6, 0.0))
            .mass(8.65);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 0.7, 1.1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(pinpointConstants)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .build();
    }

    public static PinpointConstants pinpointConstants = new PinpointConstants()
            .forwardPodY(6.5)
            .strafePodX(-0.8)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static MecanumConstants mecanumConstants = new MecanumConstants()
            .leftFrontMotorName("frontLeftDriveMotor")
            .rightFrontMotorName("frontRightDriveMotor")
            .leftRearMotorName("backLeftDriveMotor")
            .rightRearMotorName("backRightDriveMotor")
            .leftFrontMotorDirection(Direction.FORWARD)
            .rightFrontMotorDirection(Direction.REVERSE)
            .leftRearMotorDirection(Direction.FORWARD)
            .rightRearMotorDirection(Direction.REVERSE)
            .xVelocity(91.107)
            .yVelocity(75.501)
            .useBrakeModeInTeleOp(true);
}
