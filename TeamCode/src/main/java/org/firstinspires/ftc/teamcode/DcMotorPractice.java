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
        bench.setMotorSpeed(0.2);
        telemetry.addData("Motor Ticks", bench.getMotorTicks());
        telemetry.addData("Motor Revs", bench.getMotorRev());
        telemetry.update();
    }
}
