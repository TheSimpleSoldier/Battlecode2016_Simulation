package Simulation.Teams;

import battlecode.common.RobotController;

public class Turret extends Unit
{
    public Turret(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[4]);
    }
}
