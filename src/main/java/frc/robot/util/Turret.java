package frc.robot.util;

import java.util.Optional;

import edu.wpi.first.math.MathUtil;

public class Turret {
    public static class Axis {
        public Axis() {this(0);}
        public Axis(double axisOffset) {
            setAxisOffset(axisOffset);
        }

        private double  axisOffset;
        public double   getAxisOffset()                     {return axisOffset;}
        public Axis     setAxisOffset(double axisOffset)    {this.axisOffset = axisOffset; return this;}

        public double getDistanceTo(double encoderAngle, double axisTarget) {
            return Math.abs(encoderAngle + axisOffset - axisTarget);
        }
    }
    
    private final Axis[] axes;
    private double encoderTarget;
    private double encoderAngle;

    public static class WrappingConfig {
        private boolean         wrappingEnabled;
        public boolean          isWrappingEnabled()                         {return wrappingEnabled;}
        public WrappingConfig   setWrappingEnabled(boolean wrappingEnabled) {this.wrappingEnabled = wrappingEnabled; return this;}

        private double          unitsPerRotation;
        public double           getUnitsPerRotation()                                   {return unitsPerRotation;}
        public WrappingConfig   setUnitsPerRotation(double unitsPerRotation)            {this.unitsPerRotation = unitsPerRotation; return this;}

        private double          positiveWrapThreshold;
        public double           getPositiveWrapThreshold()                              {return positiveWrapThreshold;}
        public WrappingConfig   setPositiveWrapThreshold(double positiveWrapThreshold)  {this.positiveWrapThreshold = positiveWrapThreshold; return this;}

        private double          negativeWrapThreshold;
        public double           getNegativeWrapThreshold()                              {return negativeWrapThreshold;}
        public WrappingConfig   setNegativeWrapThreshold(double negativeWrapThreshold)  {this.negativeWrapThreshold = negativeWrapThreshold; return this;}

        public WrappingConfig() {}
        public WrappingConfig(WrappingConfig copyConfig) {
            setWrappingEnabled(copyConfig.isWrappingEnabled());
            setUnitsPerRotation(copyConfig.getUnitsPerRotation());
            setPositiveWrapThreshold(copyConfig.getPositiveWrapThreshold());
            setNegativeWrapThreshold(copyConfig.getNegativeWrapThreshold());
        }
    }

    private WrappingConfig          wrappingConfig;
    public Optional<WrappingConfig> getWrappingConfig()                                 {return Optional.ofNullable(wrappingConfig);}
    public Turret                   setWrappingConfig(WrappingConfig wrappingConfig)    {this.wrappingConfig = wrappingConfig; return this;}

    public static class SoftLimitConfig {
        private boolean         softLimitsEnabled;
        public boolean          isSoftLimitsEnabled()                           {return softLimitsEnabled;}
        public SoftLimitConfig  setSoftLimitsEnabled(boolean softLimitsEnabled) {this.softLimitsEnabled = softLimitsEnabled; return this;}

        private double          positiveSoftLimit;
        public double           getPositiveSoftLimit()                          {return positiveSoftLimit;}
        public SoftLimitConfig  setPositiveSoftLimit(double positiveSoftLimit)  {this.positiveSoftLimit = positiveSoftLimit; return this;}

        private double          negativeSoftLimit;
        public double           getNegativeSoftLimit()                          {return negativeSoftLimit;}
        public SoftLimitConfig  setNegativeSoftLimit(double negativeSoftLimit)  {this.negativeSoftLimit = negativeSoftLimit; return this;}

        public SoftLimitConfig() {}
        public SoftLimitConfig(SoftLimitConfig copyConfig) {
            setSoftLimitsEnabled(copyConfig.isSoftLimitsEnabled());
            setPositiveSoftLimit(copyConfig.getPositiveSoftLimit());
            setNegativeSoftLimit(copyConfig.getNegativeSoftLimit());
        }
    }

    private SoftLimitConfig             softLimitConfig;
    public Optional<SoftLimitConfig>    getSoftLimitConfig()                                {return Optional.ofNullable(softLimitConfig);}
    public Turret                       setSoftLimitConfig(SoftLimitConfig softLimitConfig) {this.softLimitConfig = softLimitConfig; return this;}

    public Turret() {
        axes = new Axis[]{new Axis()};
    }

    public Turret setEncoderAngle(double encoderAngle) {
        this.encoderAngle = encoderAngle;
        return this;
    }

    public Turret setEncoderTarget(double encoderTarget) {
        this.encoderTarget = encoderTarget;
        return this;
    }

    public Turret setAxisTarget(Axis axis, double axisTarget) {
        return setEncoderTarget(axisTarget - axis.getAxisOffset());
    }

    public Axis getClosestAxisTo(double axisTarget) {
        Axis closestAxis = axes[0];
        for(Axis axis : axes) {
            if(axis.getDistanceTo(encoderAngle, axisTarget) < closestAxis.getDistanceTo(encoderAngle, axisTarget)) {
                closestAxis = axis;
            }
        }
        return closestAxis;
    }

    public Turret setClosestAxisTarget(double axisTarget) {
        return setAxisTarget(getClosestAxisTo(axisTarget), axisTarget);
    }

    public Turret applyWrapping() {
        if(getWrappingConfig().isEmpty()) {return this;}
        WrappingConfig config = getWrappingConfig().get();
        if(!config.isWrappingEnabled()) {return this;}
        if(encoderTarget > config.getPositiveWrapThreshold()) {
            double targetRelativeToThreshold = encoderTarget - config.getPositiveWrapThreshold();
            double rotationsSinceThreshold = targetRelativeToThreshold / config.getUnitsPerRotation();
            int rotationsToSubtract = (int)Math.floor(rotationsSinceThreshold) + 1;
            encoderTarget -= rotationsToSubtract * config.getUnitsPerRotation();
        }
        if(encoderTarget < config.getNegativeWrapThreshold()) {
            double targetRelativeToThreshold = config.getNegativeWrapThreshold() - encoderTarget;
            double rotationsSinceThreshold = targetRelativeToThreshold / config.getUnitsPerRotation();
            int rotationsToAdd = (int)Math.floor(rotationsSinceThreshold) + 1;
            encoderTarget += rotationsToAdd * config.getUnitsPerRotation();
        }
        return this;
    }

    public double getTurretTargetAngle() {
        applyWrapping();
        if(getSoftLimitConfig().isEmpty()) {return encoderTarget;}
        SoftLimitConfig config = getSoftLimitConfig().get();
        if(!config.isSoftLimitsEnabled()) {return encoderTarget;}
        return MathUtil.clamp(encoderAngle, config.getNegativeSoftLimit(), config.getPositiveSoftLimit());
    }
}
