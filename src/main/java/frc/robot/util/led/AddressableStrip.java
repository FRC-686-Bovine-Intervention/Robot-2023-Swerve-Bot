package frc.robot.util.led;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class AddressableStrip implements LEDStrip {
    private final AddressableLED strip;
    private final AddressableLEDBuffer buffer;

    public AddressableStrip(int PWMPort, int length) {
        this.strip = new AddressableLED(PWMPort);
        this.buffer = new AddressableLEDBuffer(length);
        this.strip.setLength(this.buffer.getLength());
        this.strip.setData(this.buffer);
        this.strip.start();
    }

    @Override
    public int getLength() {
        return buffer.getLength();
    }

    @Override
    public void setLED(int ledIndex, int r, int g, int b, int w) {
        buffer.setRGB(ledIndex, r, g, b);
    }

    public void refresh() {
        strip.setData(buffer);
    }
}
