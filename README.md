# qtbot 

QuickTestBot is an extensible robot-program for testing various
hardware including actuators, sensors, and more. It operates
dynamically, allowing users to add and remove hardware at
runtime as well as interact with Joystick outputs. Hardware
configuration state can be saved and loaded so it will persist
between program restarts. Smartdashboard is used as a UI, and
Shuffleboard is suggested. There also is preliminary simulation
support with the bundled SnobotSim.

At the moment, qtbot supports PWM speed controllers tied to
Joystick throttle channels.

Support for new hardware can easily be added however, by
subclassing the HardwareProvider abstract class and following
the example of PWMProvider. Work on an EncoderProvider has been
started in the 'next' branch.

## Acknowledgements
Thanks to pjreiniger for [SnobotSim](https://github.com/snobotsim/SnobotSim)!
