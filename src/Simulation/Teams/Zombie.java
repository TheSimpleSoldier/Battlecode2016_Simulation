package Simulation.Teams;

import battlecode.common.RobotController;

public abstract class Zombie extends Unit
{
    public Zombie(RobotController rc, double[][] weights)
    {
        super(rc, weights);
    }
}
