package org.usfirst.frc.team4043.robot.subsystems;

import org.usfirst.frc.team4043.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Intake extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void startSuck(){
    	RobotMap.intakeL.set(-.3);
    	RobotMap.intakeR.set(.3);
    }
    public void startYeet(){
    	RobotMap.intakeL.set(.2);
    	RobotMap.intakeR.set(-.2);
    }
    public void keepCube() {
    	RobotMap.intakeL.set(-.2);
        RobotMap.intakeR.set(.2);
    }
    
    public void armsDown() {
    	RobotMap.armVert.set(-.3);
    }
    
    public void armsStop() {
    	RobotMap.armVert.set(0);;
    }
    
    public void stopAll() {
    	RobotMap.intakeL.set(0);
    	RobotMap.intakeR.set(0);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

