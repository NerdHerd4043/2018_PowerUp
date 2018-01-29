/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4043.robot;

import org.usfirst.frc.team4043.robot.subsystems.DriveTrain;
import org.usfirst.frc.team4043.robot.subsystems.Evelator;
import org.usfirst.frc.team4043.robot.subsystems.Intake;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static OI m_oi;
	public static DriveTrain driveTrain;
	public static Intake intake;
	public static Evelator evelator;
	AHRS ahrs;
	int state = 1;

	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		driveTrain = new DriveTrain();
		ahrs = new AHRS(SPI.Port.kMXP);
		intake = new Intake();
		evelator = new Evelator();
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
		
		//This should set the feedback from motorFR as 1ms per sample and unlimited bandwidth
		RobotMap.motorFR.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1, 10);
		//Sets the feedback device as a quad encoder, which is what the cimcoder is
		RobotMap.motorFR.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		
		System.out.println(RobotMap.motorFR.getSelectedSensorPosition(0));
	}
	
	
	public double turnToAngle(double wantedAngle){ //Takes in a wanted angle and returns the turnSpeed to get there
		double currentAngle = ahrs.getAngle(); //In order to determine where we are, take in the current gyro value from the navx
		double rotateSpeed;
		
		if (currentAngle > wantedAngle + 2) { 					//If we are too far to the right of where we want to be...
			rotateSpeed = (wantedAngle - currentAngle) / 20;	//turn left (negative number)
		} else if (currentAngle < wantedAngle - 2) {			//Otherwise, if we are too far left ...
			rotateSpeed = (wantedAngle - currentAngle) / 20;	//turn right (positive number)
		} else {												//If we are right on track ...
			rotateSpeed = 0d;									//don't rotate
		}
		
		//Just sanity checks for our output. Turning for arcade drive has to be between -1 and 1
		if (rotateSpeed > 1) {												
			rotateSpeed = 1;
		} else if (rotateSpeed < -1) {
			rotateSpeed = -1;
		} else if (rotateSpeed < .1 && rotateSpeed > 0) {		//Checks for a value small enough that it won't turn the robot
			rotateSpeed = .1;
		} else if (rotateSpeed > -.1 && rotateSpeed < 0) {		//Checks for a value small enough that it won't turn the robot
			rotateSpeed = -.1;
		}
		
		return rotateSpeed;
	}
	
	public double driveToFeet(double wantedDistance) { 
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance < wantedDistance) {
			driveSpeed = (wantedDistance - currentDistance) / 50;
		} else {
			driveSpeed = 0d;
		}
		
		if (driveSpeed > 1) {
			driveSpeed = 1;
		} else if (driveSpeed < .1 && driveSpeed > 0) {
			driveSpeed = .1d;
		} else if (driveSpeed < -1) {
			driveSpeed = -1;
		} else if (driveSpeed > -.1 && driveSpeed < 0) {
			driveSpeed = -.1d;
		}
		
		return driveSpeed;
	}
	
	public double backToFeet(double wantedDistance) {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double driveSpeed;
		
		if (currentDistance > wantedDistance) {
			driveSpeed = (wantedDistance - currentDistance) / 50;
		} else {
			driveSpeed = 0d;
		}
		
		if (driveSpeed > 1) {
			driveSpeed = 1;
		} else if (driveSpeed < .1 && driveSpeed > 0) {
			driveSpeed = .1d;
		} else if (driveSpeed < -1) {
			driveSpeed = -1;
		} else if (driveSpeed > -.1 && driveSpeed < 0) {
			driveSpeed = -.1d;
		}
		
		return driveSpeed;
	}
	
	public void ds1L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle;
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 140 /12) {
				driveTrain.drive.arcadeDrive(driveToFeet(140/12), turnToAngle(0));
				evelator.elevatorUP();
			} else {
				state = 2;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 2) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 3;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 3) {
			if (currentDistance > 130 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(130 / 12), turnToAngle(0));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			currentAngle = ahrs.getAngle();
			if (currentAngle > -90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			}
		}
	}
	public void ds3R() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle;
		double time = 0;
		
		if (state == 1) {
			if (currentDistance < 140 /12) {
				driveTrain.drive.arcadeDrive(driveToFeet(140/12), turnToAngle(0));
				evelator.elevatorUP();
			} else {
				state = 2;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 2) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
			} else {
				state = 3;
				time = Timer.getFPGATimestamp();
			}
		} else if (state == 3) {
			if (currentDistance > 130 /12) {
				evelator.elevatorDOWN();
				driveTrain.drive.arcadeDrive(backToFeet(130 / 12), turnToAngle(0));
			} else {
				state = 4;
			}
		} else if (state == 4) {
			currentAngle = ahrs.getAngle();
			if (currentAngle < 90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(90));
			} else {
				state = 5;
			}
		}
	}
	
	public void ds2L() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;
	
	//stage one we move 70/12 degrees forward from DS2
		if (state == 1) {
			if (currentDistance < 70 /12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
			} else {
				state = 2;
			}
		}
			//second stage begins, we are turning -90 degrees 
		else if (state == 2) {
			if (currentAngle > -90) {
				driveTrain.drive.arcadeDrive(0,turnToAngle(-90));
				ahrs.reset(); 
			} else {
				state = 3;
			}
		}
		
		else if (state == 3) {
			if (currentDistance >  72/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(72/12), turnToAngle(0));
				//zoom zoom
				} else {
					state = 4;
			}					
		}
			
		else if (state==4) {
			if (currentAngle < 90) {
			driveTrain.drive.arcadeDrive(0,turnToAngle(90));
			ahrs.reset();
			} else {
				state = 5;
			}
		}
			
		else if (state == 5) {
			if (currentDistance < 70/12) {
				driveTrain.drive.arcadeDrive(driveToFeet(70/12), turnToAngle(0));
				evelator.elevatorUP();
				time = Timer.getFPGATimestamp();
			} else {
				state = 6;
			}
		}
		 
		else if (state == 6) {
			if (Timer.getFPGATimestamp() < time + .4) {
				intake.startYeet();
				//empty yeet
			} else {
				state = 7;
			}
		}
		else if (state == 7) { 
			if (currentDistance > 50/12) {
				driveTrain.drive.arcadeDrive(backToFeet(50/12), turnToAngle(0));
			} else {
				state = 8;
			}			
		}
		else if (state == 8) {
			if (currentAngle > -90) {
				driveTrain.drive.arcadeDrive(0, turnToAngle(-90));
			} else {
				state = 9;
			}
		}
	}
	
	public void ds1cross() {
		double currentDistance = RobotMap.motorFR.getSelectedSensorPosition(0);
		double currentAngle = ahrs.getAngle();
		double time = 0;

		if (state == 1) {
			if (currentDistance < 24 / 12) { //if the robot hasn't moved forward
				driveTrain.drive.arcadeDrive(driveToFeet(24/12), turnToAngle(0)); //drive 2 feet forward
			} else {
				state = 2;
			}
		} else if (state == 2) { 
			if (currentAngle > -30 +2){ //if the angle more than -30
				driveTrain.drive.arcadeDrive(0, turnToAngle(-30)); //turn to -30 degrees
			//reset encoder
			} else {
				state = 3;
			}
		} else if (state == 3) {
			if (currentDistance < 60/12){ //if the robot has moved less than 5 feet
				driveTrain.drive.arcadeDrive(driveToFeet(60/12), turnToAngle (-30)); // move 3 feet
			} else {
				state = 4;
			}
		} else if (state == 4) {
			if (currentAngle < 0 - 2){ //if the angle less than 0
				driveTrain.drive.arcadeDrive(0, turnToAngle(0)); //turn to 0 degrees
			//reset encoder
			} else {
				state = 5;
			}
		} else if (state == 5) {
			if (currentDistance < 240/12) { //if the robot isn't in the null territory
				driveTrain.drive.arcadeDrive(driveToFeet(240/12), turnToAngle(0)); //drive 20 feet forward
			} else {
				state = 6;
			}
		}
	}
	
	
	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
