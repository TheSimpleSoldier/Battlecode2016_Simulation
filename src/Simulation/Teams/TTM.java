package Simulation.Teams;

import battlecode.common.RobotController;

public class TTM extends Unit
{
    public TTM(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[5]);
    }
}
