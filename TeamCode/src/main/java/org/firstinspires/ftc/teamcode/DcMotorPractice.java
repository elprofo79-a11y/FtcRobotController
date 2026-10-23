package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@TeleOp
public class DcMotorPractice extends OpMode {
    TestBench bench = new TestBench();

    @Override
    public void init() {
        bench.init(hardwareMap);

    }

    @Override
    public void loop() {

        double power;

        if (gamepad1.a) {
            power = 0.2;
        } else if (gamepad1.b) {
            power = 0.5;
        } else if (gamepad1.x) {
            power = 0.8;
        } else {
            power = 0.0;
        }

        bench.setMotorSpeed(power);

        telemetry.addData("Requested Power", power);
        telemetry.addData("Motor Power", bench.getMotorPower());
        telemetry.addData("Motor Ticks", bench.getMotorTicks());
        telemetry.update();
    }
    //@Override
    //public void loop() {
      //  bench.setMotorSpeed(0.1);
        //telemetry.addData("Motor Ticks", bench.getMotorTicks());
        //telemetry.addData("Motor Revs", bench.getMotorRev());
        //telemetry.update();
    //}
}
