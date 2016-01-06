package Simulation.Teams;

import battlecode.common.RobotController;

public class Archon extends Unit
{
    public Archon(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[1]);
    }
}
