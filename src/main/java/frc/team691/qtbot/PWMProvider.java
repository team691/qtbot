package frc.team691.qtbot;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PWMProvider extends HardwareProvider {
    private ArrayList<PWMSpeedController> motors = new ArrayList<>();

    public PWMProvider() {
        types = new String[] {"Spark", "Talon"};
        singleActions = new String[] {"remove", "clear"};
        typeActions = new String[] {"add"};
    }

    @Override
    public void disabledPeriodic() {
        if (runActions()) {
            SmartDashboard.putNumber("numMotors", motors.size());
        }
    }

    @Override
    public void autonomousPeriodic() {
        for (int i = 0; i < motors.size(); i++) {
            motors.get(i).set(SmartDashboard.getNumber("motor" + i, 0));
        }
    }

    @Override
    public void teleopPeriodic(Joystick[] sticks) {
        for (int i = 0; i < sticks.length; i++) {
            int am = Math.abs((int) SmartDashboard.getNumber("stick" + i, i));
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
    protected boolean handleAction(int singleActionPos) {
        switch (singleActionPos) {
            case 0 :
                return popMotor();
            case 1 :
                return clearMotors();
        }
        return false;
    }

    @Override
    protected boolean handleAction(int typeActionPos, int typePos) {
        switch (typeActionPos) {
            case 0 :
                return addMotor(types[typePos]);
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

    @Override
    public ProviderState saveState() {
        ProviderState state = new ProviderState();
        for (int i = 0; i < motors.size(); i++) {
            double maxOut = SmartDashboard.getNumber(String.format("motor%dmax", i), 1);
            state.lines.add(String.format("%s %f", motors.get(i).getClass().getSimpleName(), maxOut));
        }
        return state;
    }

    @Override
    public boolean loadState(ProviderState state) {
        String[] lineArr = null;
        clearMotors();
        for (String line : state.lines) {
            lineArr = line.split(" ");
            addMotor(lineArr[0], Double.parseDouble(lineArr[1]));
        }
        SmartDashboard.putNumber("numMotors", motors.size());
        return lineArr != null;
    }
}
