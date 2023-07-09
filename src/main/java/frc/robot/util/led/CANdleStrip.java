package frc.robot.util.led;

import com.ctre.phoenix.led.CANdle;

public class CANdleStrip implements LEDStrip {
    private final CANdle candle;
    private final int length;

    public CANdleStrip(CANdle candle, int offboardStripLength) {
        this.candle = candle;
        this.length = offboardStripLength + 8;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLED(int ledIndex, int r, int g, int b, int w) {
        candle.setLEDs(r, g, b, w, ledIndex, 1);
    }

    public SoftwareStrip getOnboardLEDs() {
        return new SoftwareStrip(new LEDStrip[]{this}, 0, 8);
    }

    public SoftwareStrip getOffboardLEDs() {
        return new SoftwareStrip(new LEDStrip[]{this}, 8, length);
    }
}
