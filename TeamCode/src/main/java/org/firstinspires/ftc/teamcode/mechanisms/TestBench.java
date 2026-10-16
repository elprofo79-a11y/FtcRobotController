package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestBench {

    private DcMotor motor;

    private double ticksPerRev; //revolution


    public void init(HardwareMap hardwareMap){

        motor = hardwareMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
    }

    public void setMotorSpeed(double Speed){
        //accepts values from -1.0 to 1.0
        motor.setPower(Speed);
    }

    public double getMotorRev(){
        return motor.getCurrentPosition()/ticksPerRev; //normalizing ticks to revolution
    }

    public int getMotorTicks() {
        return motor.getCurrentPosition();
    }
}
