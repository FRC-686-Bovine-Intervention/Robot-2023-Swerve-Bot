// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;

import com.ctre.phoenix.led.CANdle;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.led.CANdleStrip;
import frc.robot.util.led.LEDAnimation.RainbowAnimation;
import frc.robot.util.led.LEDAnimation.StripCounterAnimation;
import frc.robot.util.led.LEDStrip.SoftwareStrip;

public class Robot extends LoggedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  private CANdle candle = new CANdle(0);
  private CANdleStrip candleStrip = new CANdleStrip(candle, 120);
  private SoftwareStrip onboardLEDs = candleStrip.getOnboardLEDs();
  private SoftwareStrip onboardStrip1 = onboardLEDs.substrip(0, 4).reverse();
  private SoftwareStrip onboardStrip2 = onboardLEDs.substrip(4);
  private SoftwareStrip offboardLEDs = candleStrip.getOffboardLEDs();
  private SoftwareStrip offboardStrip1 = offboardLEDs.substrip(0, 60);
  private SoftwareStrip offboardStrip2 = offboardLEDs.substrip(60).reverse();
  // private RainbowAnimation rainbowAnimation = new RainbowAnimation(offboardStrip2).setWavelength(1).setVelocity(1);
  // private RainbowAnimation rainbowAnimation1 = new RainbowAnimation(onboardStrip1, onboardStrip2).setWavelength(1).setVelocity(0.1);
  private StripCounterAnimation offboardCounter = new StripCounterAnimation(offboardLEDs);

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    candle.configFactoryDefault();
    candle.configBrightnessScalar(0.125);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    offboardCounter.runAnimation();
    // rainbowAnimation.runAnimation();
    // rainbowAnimation1.runAnimation();
  }

  @Override
  public void teleopExit() {
    for(int i = onboardLEDs.startIndex; i < onboardLEDs.endIndex; i++) {
      onboardLEDs.setLED(i, 255, 0, 0, 0);
    }
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
