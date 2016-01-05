package Simulation.Teams;

import Simulation.ActivationFunction;
import Simulation.FeedForwardNeuralNetwork;
import Simulation.MockRobotPlayer;
import battlecode.common.RobotController;
import battlecode.common.*;

public class Soldier extends Unit {
    public Soldier(RobotController rc, double[][] weights) {
        super(rc, weights);
        net.setWeights(weights[0]);
    }
}
