package frc.robot.util.led;

public interface LEDStrip {
    public int getLength();
    public default SoftwareStrip concat(LEDStrip... strips) {
        LEDStrip[] newStrips = new LEDStrip[strips.length + 1];
        newStrips[0] = this;
        for(int i = 0; i < strips.length; i++) {
            newStrips[i + 1] = strips[i];
        }
        return new SoftwareStrip(newStrips);
    }
    public void setLED(int ledIndex, int r, int g, int b, int w);
    public static int getLength(LEDStrip[] strips) {
        int accumLength = 0;
        for(LEDStrip strip : strips) {
            accumLength += strip.getLength();
        }
        return accumLength;
    }

    public static class SoftwareStrip implements LEDStrip {
        private final LEDStrip[] strips;
        public final int startIndex;
        public final int endIndex;
        private final boolean reversed;

        public SoftwareStrip(LEDStrip... strips) {
            this(strips, false);
        }
        public SoftwareStrip(LEDStrip[] strips, boolean reversed) {
            this(strips, 0, reversed);
        }
        public SoftwareStrip(LEDStrip[] strips, int startIndex) {
            this(strips, startIndex, false);
        }
        public SoftwareStrip(LEDStrip[] strips, int startIndex, boolean reversed) {
            this(strips, startIndex, LEDStrip.getLength(strips), reversed);
        }
        public SoftwareStrip(LEDStrip[] strips, int startIndex, int endIndex) {
            this(strips, startIndex, endIndex, false);
        }
        public SoftwareStrip(LEDStrip[] strips, int startIndex, int endIndex, boolean reversed) {
            this.strips = strips;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.reversed = reversed;
        }

        @Override
        public int getLength() {
            return endIndex - startIndex;
        }

        @Override
        public void setLED(int ledIndex, int r, int g, int b, int w) {
            ledIndex = getLED(ledIndex);
            int accumLength = 0;
            for(LEDStrip strip : strips) {
                int stripLength = strip.getLength();
                accumLength += stripLength;
                if(accumLength >= ledIndex) {
                    accumLength -= stripLength;
                    ledIndex -= accumLength;
                    strip.setLED(ledIndex, r, g, b, w);
                    break;
                }
            }
        }

        public SoftwareStrip reverse() {
            return new SoftwareStrip(strips, getLength() - endIndex, getLength() - startIndex, !reversed);
        }
        public SoftwareStrip substrip(int startIndex) {
            return substrip(startIndex, endIndex);
        }
        public SoftwareStrip substrip(int startIndex, int endIndex) {
            return new SoftwareStrip(strips, startIndex + this.startIndex, endIndex + this.startIndex);
        }

        private int getLED(int ledIndex) {
            return getLED(ledIndex, reversed);
        }
        private int getLED(int ledIndex, boolean reversed) {
            ledIndex += startIndex;
            if(reversed) {
                ledIndex = getLength() - ledIndex - 1;
            }
            return ledIndex;
        }

    }
}
