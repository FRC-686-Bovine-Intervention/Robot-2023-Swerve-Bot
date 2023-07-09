package frc.robot.util.led;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public interface LEDAnimation {
    public void runAnimation();
    
    public static class RainbowAnimation implements LEDAnimation {
        private final LEDStrip[] strips;
        private final Timer timer = new Timer();
        private double          velocity = 1;
        public double           getVelocity()                   {return velocity;}
        public RainbowAnimation setVelocity(double velocity)    {this.velocity = velocity; return this;}
        private double          wavelength = 1;
        public double           getWavelength()                     {return wavelength;}
        public RainbowAnimation setWavelength(double wavelength)    {this.wavelength = wavelength; return this;}

        public RainbowAnimation(LEDStrip... strips) {
            this.strips = strips;
            timer.start();
        }

        private double sinusoid(double x) {
            return Math.max(Math.cos(2*Math.PI*x/getWavelength())*(2.0/3)+(1.0/3), 0);
        }

        private Color getColorAtPosition(double pos) {
            double r = 0, g = 0, b = 0;
            timer.advanceIfElapsed(1/getVelocity());
            pos -= timer.get()*getVelocity();
            // pos %= 1;
            r = sinusoid(pos - 0.0*getWavelength()/3);
            g = sinusoid(pos - 1.0*getWavelength()/3);
            b = sinusoid(pos - 2.0*getWavelength()/3);
            return new Color(r, g, b);
        }

        @Override
        public void runAnimation() {
            for(LEDStrip ledStrip : strips) {
                for(int i = 0; i < ledStrip.getLength(); i++) {
                    double pos = (double) i / ledStrip.getLength();
                    Color color = getColorAtPosition(pos);
                    ledStrip.setLED(i, (int)(color.red*255), (int)(color.green*255), (int)(color.blue*255), 0);
                }
            }
        }
    }

    public static class StripCounterAnimation implements LEDAnimation {
        private final LEDStrip[] strips;

        public StripCounterAnimation(LEDStrip... strips) {
            this.strips = strips;
        }

        @Override
        public void runAnimation() {
            for(LEDStrip ledStrip : strips) {
                for(int i = 0; i < ledStrip.getLength(); i++) {
                    int ind = i + 1;
                    if(ind % 50 == 0) {
                        ledStrip.setLED(i, 0, 255, 0, 255);
                    } else if(ind % 10 == 0) {
                        ledStrip.setLED(i, 255, 0, 0, 255);
                    } else if(ind % 5 == 0) {
                        ledStrip.setLED(i, 0, 0, 255, 255);
                    } else {
                        ledStrip.setLED(i, 0, 0, 0, 0);
                    }
                }
            }
        }

    }
}
