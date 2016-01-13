package Simulation.Teams;

import battlecode.common.RobotController;

public class Scout extends Unit
{
    public Scout(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[3]);
    }

    public void run()
    {
        if (rc.isCoreReady()) navigator.move(target);
    }
}
