package frc.team691.qtbot;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PWMProvider extends HardwareProvider {
    private static final String[] MOTOR_TYPES = new String[] {
        "Spark", "Talon"
    };
    private static final String[] SINGLE_ACTIONS = new String[] {
        "remove", "clear"
    };
    private static final String[] LIST_ACTIONS = new String[] {
        "add"
    };

    ArrayList<PWMSpeedController> motors = new ArrayList<>();

    @Override
    public void robotInit() {
        for (String la : LIST_ACTIONS) {
            for (String sc : MOTOR_TYPES) {
                SmartDashboard.putBoolean(String.format("%s %s", la, sc), false);
            }
        }
        for (String sa : SINGLE_ACTIONS) {
            SmartDashboard.putBoolean(sa, false);
        }
    }

    @Override
    public void disabledPeriodic() {
        if (updatePeriodic()) {
            SmartDashboard.putNumber("numMotors", motors.size());
        }
    }

    public boolean updatePeriodic() {
        boolean updated = false;
        for (int i = 0; i < LIST_ACTIONS.length; i++) {
            String la = LIST_ACTIONS[i];
            for (String sc : MOTOR_TYPES) {
                String sci = String.format("%s %s", la, sc);
                if (SmartDashboard.getBoolean(sci, false)) {
                    SmartDashboard.putBoolean(sci, false);
                    updated = handleListAction(i, sc);
                }
            }
        }
        for (int i = 0; i < SINGLE_ACTIONS.length; i++) {
            String sa = SINGLE_ACTIONS[i];
            if (SmartDashboard.getBoolean(sa, false)) {
                SmartDashboard.putBoolean(sa, false);
                updated = handleSingleAction(i);
            }
        }
        return updated;
    }

    private boolean handleListAction(int i, String item) {
        switch (i) {
            case 0 :
                return addMotor(item);
        }
        return false;
    }

    private boolean handleSingleAction(int i) {
        switch (i) {
            case 0 :
                return popMotor();
            case 1 :
                return clearMotors();
        }
        return false;
    }

    private boolean addMotor(String motorType) {
        return addMotor(motorType, 1);
    }

    private boolean addMotor(String motorType, double maxOut) {
        int m = motors.size();
        try {
            Class<?> mClass = Class.forName("edu.wpi.first.wpilibj." + motorType);
            Constructor<?> cst = mClass.getConstructor(Integer.TYPE);
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
}
