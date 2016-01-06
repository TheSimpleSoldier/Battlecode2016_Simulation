package Simulation.Teams;

import battlecode.common.RobotController;

public class Gaurd extends Unit
{
    public Gaurd(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[2]);
    }
}
