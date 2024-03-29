package frc.team691.qtbot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class HardwareProvider {
    protected String[] types = new String[0];
    protected String[] typeActions = new String[0];
    protected String[] singleActions = new String[0];

    public void robotInit() {
        setupActions();
    }
    public void robotPeriodic() {
        // Override me!
    }

    public void disabledInit() {
        // Override me!
    }
    public void disabledPeriodic() {
        runActions();
    }

    public void autonomousInit() {
        // Override me!
    }
    public void autonomousPeriodic() {
        // Override me!
    }

    public void teleopInit(Joystick[] sticks) {
        // Override me!
    }
    public void teleopPeriodic(Joystick[] sticks) {
        // Override me!
    }

    protected void setupActions() {
        for (String la : typeActions) {
            for (String sc : types) {
                SmartDashboard.putBoolean(String.format("%s %s", la, sc), false);
            }
        }
        for (String sa : singleActions) {
            SmartDashboard.putBoolean(sa, false);
        }
    }
    protected boolean runActions() {
        boolean updated = false;
        for (int i = 0; i < singleActions.length; i++) {
            String sa = singleActions[i];
            if (SmartDashboard.getBoolean(sa, false)) {
                SmartDashboard.putBoolean(sa, false);
                updated = handleAction(i);
            }
        }
        for (int i = 0; i < typeActions.length; i++) {
            String la = typeActions[i];
            for (int j = 0; j < types.length; j++) {
                String sci = String.format("%s %s", la, types[j]);
                if (SmartDashboard.getBoolean(sci, false)) {
                    SmartDashboard.putBoolean(sci, false);
                    updated = handleAction(i, j);
                }
            }
        }
        return updated;
    }
    protected boolean handleAction(int singleActionPos) {
        // Override me!
        return false;
    }
    protected boolean handleAction(int typeActionPos, int typePos) {
        // Override me!
        return false;
    }

    public ProviderState saveState() {
        // Override me!
        return null;
    }
    public boolean loadState(ProviderState state) {
        // Override me!
        return false;
    }
}
