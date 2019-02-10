package frc.team691.qtbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private static final String STATE_FILE_NAME = "qtbot.save";
    private static final String[] SPEED_CONTROLLERS = new String[] {
        "Spark", "Talon"
    };

    File stateFile;
    ArrayList<PWMSpeedController> motors = new ArrayList<>();
    Joystick[] sticks;

    @Override
    public void robotInit() {
        stateFile = new File(Filesystem.getOperatingDirectory(), STATE_FILE_NAME);
        for (String sc : SPEED_CONTROLLERS) {
            SmartDashboard.putBoolean("add " + sc, false);
        }
        SmartDashboard.putBoolean("remove", false);
        SmartDashboard.putBoolean("clear", false);
        SmartDashboard.putBoolean("save", false);
        SmartDashboard.putBoolean("load", false);
        if (loadState()) {
            System.out.println("Loaded state from file");
            SmartDashboard.putNumber("numMotors", motors.size());
        }
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
        if (updateMotors()) {
            SmartDashboard.putNumber("numMotors", motors.size());
        }
    }

    @Override
    public void autonomousInit() {
    }
    
    @Override
    public void autonomousPeriodic() {
        for (int i = 0; i < motors.size(); i++) {
            motors.get(i).set(SmartDashboard.getNumber("motor" + i, 0));
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
            if (am < motors.size()) {
                double t = (sticks[i].getThrottle() + 1) / 2;
                t *= SmartDashboard.getNumber("motor" + am + "max", 1);
                double out = t;
                motors.get(am).set(out);
                SmartDashboard.putNumber("motor" + am, out);
            }
        }
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    private boolean updateMotors() {
        boolean updated = false;
        for (String sc : SPEED_CONTROLLERS) {
            String sci = "add " + sc;
            if (SmartDashboard.getBoolean(sci, false)) {
                SmartDashboard.putBoolean(sci, false);
                updated = true;
                addMotor(sc);
            }
        }
        if (SmartDashboard.getBoolean("remove", false)) {
            SmartDashboard.putBoolean("remove", false);
            updated = true;
            popMotor();
        }
        if (SmartDashboard.getBoolean("clear", false)) {
            SmartDashboard.putBoolean("clear", false);
            updated = true;
            clearMotors();
        }
        if (SmartDashboard.getBoolean("save", false)) {
            SmartDashboard.putBoolean("save", false);
            saveState();
        }
        if (SmartDashboard.getBoolean("load", false)) {
            SmartDashboard.putBoolean("load", false);
            updated = true;
            loadState();
        }
        return updated;
    }

    private boolean addMotor(String type) {
        return addMotor(type, 1);
    }

    private boolean addMotor(String scType, double maxOut) {
        int m = motors.size();
        try {
            Class<?> scClass = Class.forName("edu.wpi.first.wpilibj." + scType);
            Constructor<?> cst = scClass.getConstructor(Integer.TYPE);
            motors.add((PWMSpeedController) cst.newInstance(m));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String mi = "motor" + m;
        SmartDashboard.putNumber(mi, 0);
        mi += "max";
        SmartDashboard.putNumber(mi, maxOut);
        return true;
    }

    private boolean clearMotors() {
        boolean res = popMotor();
        while (popMotor());
        return res;
    }

    private boolean popMotor() {
        int m = motors.size() - 1;
        if (m < 0) {
            return false;
        }
        /*
        String mi = "motor" + m;
        SmartDashboard.delete(mi);
        mi += "max";
        SmartDashboard.delete(mi);
        */
        motors.remove(m).close();
        return true;
    }

    private boolean updateSticks() {
        int i;
        for (i = 0; DriverStation.getInstance().getJoystickType(i) != 0; i++);
        if (sticks != null && sticks.length == i) {
            return false;
        }
        sticks = new Joystick[i];
        for (i = 0; i < sticks.length; i++) {
            sticks[i] = new Joystick(i);
            sticks[i].setThrottleChannel(sticks[i].getAxisCount() - 1);
            String si = "stick" + i;
            SmartDashboard.putNumber(si, SmartDashboard.getNumber(si, i));
        }
        return true;
    }

    private boolean saveState() {
        try {
            PrintWriter fout = new PrintWriter(stateFile);
            for (int i = 0; i < motors.size(); i++) {
                double maxOut = SmartDashboard.getNumber(String.format("motor%dmax", i), 1);
                fout.format("%s %f\n", motors.get(i).getClass().getSimpleName(), maxOut);
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
        clearMotors();
        try {
            Scanner scan = new Scanner(new FileInputStream(stateFile));
            while (scan.hasNext()) {
                addMotor(scan.next(), scan.nextDouble());
            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
