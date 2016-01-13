package Simulation.Teams;

import battlecode.common.RobotController;

public class Viper extends Unit {
    public Viper(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[2]);
    }
}
