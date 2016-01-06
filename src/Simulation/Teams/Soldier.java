package Simulation.Teams;

import battlecode.common.*;

public class Soldier extends Unit {
    public Soldier(RobotController rc, double[][] weights) {
        super(rc, weights);
        net.setWeights(weights[0]);
    }
}
