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
            .forwardZeroPowerAcceleration(-25.791)
            .lateralZeroPowerAcceleration(-45.169)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0.0, 0.016, 0.0015))
            .headingPIDFCoefficients(new PIDFCoefficients(1.1, 0.0, 0.1, 0.02))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.011, 0.0, 0.0, 0.6, 0.0))
            .mass(7.0);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1.2, 1.0);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(pinpointConstants)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .build();
    }

    public static PinpointConstants pinpointConstants = new PinpointConstants()
            .strafePodX(-6.299)
            .forwardPodY(2.346)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static MecanumConstants mecanumConstants = new MecanumConstants()
            .leftFrontMotorName("frontLeftDriveMotor")
            .rightFrontMotorName("frontRightDriveMotor")
            .leftRearMotorName("backLeftDriveMotor")
            .rightRearMotorName("backRightDriveMotor")
            .leftFrontMotorDirection(Direction.FORWARD)
            .rightFrontMotorDirection(Direction.REVERSE)
            .leftRearMotorDirection(Direction.FORWARD)
            .rightRearMotorDirection(Direction.REVERSE)
            .xVelocity(93.629)
            .yVelocity(80.157)
            .useBrakeModeInTeleOp(true);
}
