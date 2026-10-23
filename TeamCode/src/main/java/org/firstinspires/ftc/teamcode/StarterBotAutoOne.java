package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
public class StarterBotAutoOne extends OpMode {
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotorEx launcher = null;
    private DcMotor intake = null;
    private CRServo leftIntakeServo = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;
    private CRServo windServo = null;

    // Autonomous constants
    private static final double DRIVE_POWER = 0.25;
    private static final long DRIVE_TIME_MS = 700;

    private static final double TARGET_VELOCITY = 1160; //try to use 1500 like in teleop
    private static final double VELOCITY_TOLERANCE = 50;
    private static final double LAUNCH_TIME_SECONDS = 15;


    // ==========================================
    // 3. STATE MACHINE
    // ==========================================

    private enum AutoState {
        INITIALIZED,
        DRIVE_AWAY,
        LAUNCH_POLLEN,
        COMPLETE,
        PARK
    }

    private AutoState currentState = AutoState.INITIALIZED;

    // Timer for the current autonomous state
    private final ElapsedTime stateTimer = new ElapsedTime();

    @Override
    public void init() {

        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmill");
        leftIntakeServo = hardwareMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.get(CRServo.class, "right_intake_servo");
        windServo = hardwareMap.get(CRServo.class, "helper");


        //put initialization blocks here
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        leftDrive.setZeroPowerBehavior(BRAKE);
        rightDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);
        windServo.setPower(0);
        /*
         * Much like our drivetrain motors, we set the right intake servo to reverse so that both
         * servos work to pull elements into the intake.
         */
        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        stopAllActuators();

        currentState = AutoState.INITIALIZED;

        telemetry.addLine("BIOBUZZ Autonomous");
        telemetry.addLine("Initialization complete.");
        telemetry.addLine("Waiting for START...");
        telemetry.update();
    }

    @Override
    public void start(){
        transitionTo(AutoState.DRIVE_AWAY);
    }

    @Override
    public void loop() {
        // Repeatedly execute the logic for the current state.
        switch (currentState) {

            // ----------------------------------
            // STATE 0: INITIALIZED
            // ----------------------------------
            case INITIALIZED:

                stopAllActuators();
                break;

            // ----------------------------------
            // STATE 1: DRIVE AWAY FROM WALL
            // ----------------------------------
            case DRIVE_AWAY:

                leftDrive.setPower(DRIVE_POWER);
                rightDrive.setPower(DRIVE_POWER);

                // Move for the specified duration.
                if (stateTimer.milliseconds() >= DRIVE_TIME_MS) {

                    // Stop the drivetrain.
                    leftDrive.setPower(0);
                    rightDrive.setPower(0);

                    // Move to launcher state.
                    transitionTo(AutoState.LAUNCH_POLLEN);
                }
                break;

            // ----------------------------------
            // STATE 2: LAUNCH PRELOADED POLLEN
            // ----------------------------------
            case LAUNCH_POLLEN:

                // Command the launcher to reach target speed.
                launcher.setVelocity(TARGET_VELOCITY);

                // Measure actual launcher velocity.
                double actualVelocity = launcher.getVelocity();

                // Check if the launcher is within the target range.
                boolean launcherAtSpeed =
                        Math.abs(actualVelocity - TARGET_VELOCITY)
                                <= VELOCITY_TOLERANCE;

                // Feed pollen only when the launcher is at speed.
                if (launcherAtSpeed) {

                    intake.setPower(1.0);
                    windmillServo.setPower(1.0);
                    leftIntakeServo.setPower(1.0);
                    rightIntakeServo.setPower(1.0);
                    windServo.setPower(1.0);

                } else {

                    // Prevent feeding while the launcher is
                    // accelerating or below the target range.
                    stopFeedingMechanisms();
                }

                // Display live information on the Driver Station.
                telemetry.addData(
                        "Target Velocity", TARGET_VELOCITY);

                telemetry.addData(
                        "Actual Velocity", actualVelocity);

                telemetry.addData(
                        "Launcher At Speed", launcherAtSpeed);

                telemetry.addData(
                        "State Time",
                        "%.2f seconds", stateTimer.seconds());

                telemetry.addData(
                        "Feeders Active", launcherAtSpeed);

                // Finish the launch state after 15 seconds.
                if (stateTimer.seconds() >= LAUNCH_TIME_SECONDS) {

                    transitionTo(AutoState.COMPLETE);
                }
                break;

            case PARK:
                leftDrive.setPower(DRIVE_POWER * -1);
                rightDrive.setPower(DRIVE_POWER);
                if (stateTimer.seconds() >= 26.0) {
                    // Move to launcher state.
                    transitionTo(AutoState.COMPLETE);
                }
                break;




            // ----------------------------------
            // STATE 3: AUTONOMOUS COMPLETE
            // ----------------------------------

            case COMPLETE:

                stopAllActuators();

                telemetry.addLine("AUTONOMOUS COMPLETE");
                telemetry.addLine("All actuators stopped.");
                break;
        }

        // Show the current state on the Driver Station.
        telemetry.addData("Current State", currentState);
        telemetry.update();
    }

    // ==========================================
    // 7. STATE TRANSITION HELPER
    // ==========================================

    private void transitionTo(AutoState newState) {

        currentState = newState;

        // Reset the timer whenever a new state begins.
        stateTimer.reset();

        telemetry.addData("Transitioned To", newState);
    }

    // ==========================================
    // 8. STOP FEEDING MECHANISMS
    // ==========================================

    private void stopFeedingMechanisms() {

        intake.setPower(0);

        windmillServo.setPower(0);
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windServo.setPower(0);
    }

    // ==========================================
    // 9. STOP ALL ACTUATORS
    // ==========================================

    private void stopAllActuators() {

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        intake.setPower(0);
        launcher.setPower(0);

        windmillServo.setPower(0);
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windServo.setPower(0);
    }

    // ==========================================
    // 10. STOP CALLBACK
    // ==========================================
    @Override
    public void stop() {

        // Stop everything if STOP is pressed or the OpMode exits.
        stopAllActuators();
    }


}

//}
