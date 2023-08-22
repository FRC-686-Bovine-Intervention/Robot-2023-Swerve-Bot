// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Arrays;
import java.util.Optional;

import org.littletonrobotics.junction.LoggedRobot;

import com.ctre.phoenix.led.CANdle;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.util.Event;
import frc.robot.util.led.LEDColor;
import frc.robot.util.led.animation.BarAnimation;
import frc.robot.util.led.animation.BarAnimation.BarData;
import frc.robot.util.led.animation.EndgameNotificationAnim;
import frc.robot.util.led.animation.FlashingAnimation;
import frc.robot.util.led.animation.LEDManager;
import frc.robot.util.led.animation.ScrollingAnimation;
import frc.robot.util.led.functions.Gradient;
import frc.robot.util.led.functions.Gradient.BasicGradient;
import frc.robot.util.led.functions.Gradient.BasicGradient.InterpolationStyle;
import frc.robot.util.led.functions.TilingFunction;
import frc.robot.util.led.strips.CANdleStrip;
import frc.robot.util.led.strips.LEDStrip.SoftwareStrip;

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
  private SoftwareStrip offboardStrip2 = offboardLEDs.substrip(60);

  private FlashingAnimation disabledAnimation = new FlashingAnimation(new BasicGradient(InterpolationStyle.Linear, LEDColor.Black, LEDColor.Red), TilingFunction.Sinusoidal, candleStrip).setPeriod(2);
  private ScrollingAnimation autoAnimation = new ScrollingAnimation(new BasicGradient(InterpolationStyle.Step, LEDColor.Blue, LEDColor.Yellow), TilingFunction.Modulo, offboardLEDs).setWavelength(5);
  private BarAnimation autoEndAnimation = new BarAnimation(
    () -> Arrays.asList(new BarData(LEDColor.Green, 5.0)), 
    Optional.of(LEDColor.Black), 
    offboardLEDs
  );
  private ScrollingAnimation teleAnimation = new ScrollingAnimation(Gradient.rainbow, TilingFunction.Modulo, offboardLEDs).setWavelength(5);
  private EndgameNotificationAnim endgameNotificationAnim = new EndgameNotificationAnim(offboardStrip2);

  private LEDManager ledManager = LEDManager.getInstance();

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    candle.configFactoryDefault();
    candle.configBrightnessScalar(0.125);
    ledManager.register(candleStrip);
  }

  @Override
  public void robotPeriodic() {
    ledManager.runLEDs();
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
    disabledAnimation.start();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {
    disabledAnimation.stop();
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    autoAnimation.start();
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {
    autoAnimation.stop();
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    teleAnimation.start();
  }

  @Override
  public void teleopPeriodic() {
    if(DriverStation.getMatchTime() <= 30 && DriverStation.getMatchTime() >= 29.5) {
      endgameNotificationAnim.start();
    }
    // offboardCounter.runAnimation();
    // rainbowAnimation.runAnimation();
    // rainbowAnimation1.runAnimation();
  }

  @Override
  public void teleopExit() {
    teleAnimation.stop();
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
