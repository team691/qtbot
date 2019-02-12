package frc.team691.qtbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private static final String STATE_FILE_NAME = "qtbot.save";

    private File stateFile;
    private Joystick[] sticks;
    // TODO: dynamically load providers
    private HardwareProvider[] providers = new HardwareProvider[] {
        new PWMProvider()
    };

    @Override
    public void robotInit() {
        stateFile = new File(Filesystem.getOperatingDirectory(), STATE_FILE_NAME);
        SmartDashboard.putBoolean("save", false);
        SmartDashboard.putBoolean("load", false);

        for (HardwareProvider hp : providers) {
            hp.robotInit();
        }

        if (loadState()) {
            System.out.println("Loaded state from save file");
        }
    }

    @Override
    public void robotPeriodic() {
        for (HardwareProvider hp : providers) {
            hp.robotPeriodic();
        }
    }

    @Override
    public void disabledInit() {
        for (HardwareProvider hp : providers) {
            hp.disabledInit();
        }
    }

    @Override
    public void disabledPeriodic() {
        if (SmartDashboard.getBoolean("save", false)) {
            SmartDashboard.putBoolean("save", false);
            saveState();
        }
        if (SmartDashboard.getBoolean("load", false)) {
            SmartDashboard.putBoolean("load", false);
            loadState();
        }
        for (HardwareProvider hp : providers) {
            hp.disabledPeriodic();
        }
    }

    @Override
    public void autonomousInit() {
        for (HardwareProvider hp : providers) {
            hp.autonomousInit();
        }
    }
    
    @Override
    public void autonomousPeriodic() {
        for (HardwareProvider hp : providers) {
            hp.autonomousPeriodic();
        }
    }

    @Override
    public void teleopInit() {
        updateSticks();
        for (HardwareProvider hp : providers) {
            hp.teleopInit(sticks);
        }
    }

    @Override
    public void teleopPeriodic() {
        for (HardwareProvider hp : providers) {
            hp.teleopPeriodic(sticks);
        }
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    private boolean updateSticks() {
        int i;
        for (i = 0; DriverStation.getInstance().getJoystickType(i) != 0 && i < 5; i++);
        if (sticks != null && sticks.length == i) {
            return false;
        }
        sticks = new Joystick[i];
        for (i = 0; i < sticks.length; i++) {
            sticks[i] = new Joystick(i);
            //sticks[i].setThrottleChannel(sticks[i].getAxisCount() - 1);
            String si = "stick" + i;
            SmartDashboard.putNumber(si, SmartDashboard.getNumber(si, i));
        }
        return true;
    }

    private boolean saveState() {
        try {
            PrintWriter fout = new PrintWriter(stateFile);
            for (int i = 0; i < providers.length; i++) {
                ProviderState state = providers[i].saveState();
                if (state != null) {
                    for (String line : state.lines) {
                        fout.format("%d %s\n", i, line);
                    }
                }
            }
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadState() {
        if (!stateFile.exists() || !stateFile.canRead()) {
            return false;
        }
        boolean res = false;
        try {
            Scanner scan = new Scanner(new FileInputStream(stateFile));
            int pi = 0;
            ProviderState state = new ProviderState();
            while (scan.hasNext()) {
                int i = scan.nextInt();
                if (i != pi) {
                    providers[pi].loadState(state);
                    state = new ProviderState();
                    pi = i;
                }
                res = res || state.lines.add(scan.nextLine().trim());
            }
            providers[pi].loadState(state);
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }
}
