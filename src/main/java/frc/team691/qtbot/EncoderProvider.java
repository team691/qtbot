package frc.team691.qtbot;

import java.util.ArrayList;
//import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EncoderProvider extends HardwareProvider {
    private ArrayList<Encoder> encoders = new ArrayList<>();

    public EncoderProvider() {
        singleActions = new String[] {"remove", "clear", "add"};
    }

    @Override
    public void robotPeriodic() {
        for (int i = 0; i < encoders.size(); i++) {
            SmartDashboard.putNumber("enc" + i * 2, encoders.get(i).get());
        }
    }

    @Override
    public void disabledPeriodic() {
        if (runActions()) {
            SmartDashboard.putNumber("numEncoders", encoders.size());
        }
    }

    @Override
    protected boolean handleAction(int singleActionPos) {
        if (singleActionPos == 2) {
            return add();
        }
        return super.handleAction(singleActionPos);
    }

    private boolean add() {
        int m = encoders.size() * 2;
        encoders.add(new Encoder(m, m + 1));
        return true;
    }

    /*
    private boolean add(CounterBase.EncodingType encodingType) {
        int m = encoders.size() * 2;
        encoders.add(new Encoder(m, m + 1, false, encodingType));
        return true;
    }
    */

    @Override
    protected boolean pop() {
        int m = encoders.size() - 1;
        if (m < 0) {
            return false;
        }
        encoders.remove(m).close();
        return true;
    }

    @Override
    public ProviderState saveState() {
        ProviderState state = new ProviderState();
        for (int i = 0; i < encoders.size(); i++) {
            state.lines.add(encoders.get(i).getClass().getSimpleName());
        }
        return state;
    }

    @Override
    public boolean loadState(ProviderState state) {
        int numEncoders = encoders.size();
        clear();
        for (String line : state.lines) {
            add(line);
        }
        SmartDashboard.putNumber("numEncoders", encoders.size());
        return numEncoders != encoders.size();
    }
}
