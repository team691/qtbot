package frc.team691.qtbot;

public abstract class HardwareProvider {
    public void robotInit() {
        // Override me!
    }
    public void robotPeriodic() {
        // Override me!
    }

    public void disabledInit() {
        // Override me!
    }
    public void disabledPeriodic() {
        // Override me!
    }

    public void autonomousInit() {
        // Override me!
    }
    public void autonomousPeriodic() {
        // Override me!
    }

    public void teleopInit() {
        // Override me!
    }
    public void teleopPeriodic() {
        // Override me!
    }
}
