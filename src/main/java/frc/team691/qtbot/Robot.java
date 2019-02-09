package frc.team691.qtbot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private static final int NUM_MOTORS = 2;

    SpeedController[] motors = new SpeedController[NUM_MOTORS];
    Joystick[] sticks;

    @Override
    public void robotInit() {
        int i;

        for (i = 0; i < NUM_MOTORS; i++) motors[i] = new Spark(i);

        for (i = 0; i < NUM_MOTORS; i++) {
            String mi = "motor" + i;
            SmartDashboard.putNumber(mi, SmartDashboard.getNumber(mi, 0));
            mi += "max";
            SmartDashboard.putNumber(mi, SmartDashboard.getNumber(mi, 1));
        }
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
    }
    
    @Override
    public void autonomousPeriodic() {
        for (int i = 0; i < NUM_MOTORS; i++) {
            motors[i].set(SmartDashboard.getNumber("motor" + i, 0));
        }
    }

    @Override
    public void teleopInit() {
        updateSticks();
    }

    @Override
    public void teleopPeriodic() {
        for (int i = 0; i < sticks.length; i++) {
            int m = (int) SmartDashboard.getNumber("stick" + i, i);
            int am = Math.abs(m);
            if (am < NUM_MOTORS) {
                double t = (sticks[i].getThrottle() + 1) / 2;
                t *= SmartDashboard.getNumber("motor" + am + "max", 1);
                double out = t;
                motors[am].set(out);
                SmartDashboard.putNumber("motor" + am, out);
            }
        }
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    private boolean updateSticks() {
        int i;
        for (i = 0; DriverStation.getInstance().getJoystickType(i) != 0; i++);
        if (sticks != null && sticks.length == i) return false;
        sticks = new Joystick[i];
        for (i = 0; i < sticks.length; i++) {
            sticks[i] = new Joystick(i);
            sticks[i].setThrottleChannel(sticks[i].getAxisCount() - 1);
            String si = "stick" + i;
            SmartDashboard.putNumber(si, SmartDashboard.getNumber(si, i));
        }
        return true;
    }
}
