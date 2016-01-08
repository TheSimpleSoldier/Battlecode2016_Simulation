package Simulation.Teams;

import Simulation.ActivationFunction;
import Simulation.FeedForwardNeuralNetwork;
import Simulation.MockRobotPlayer;
import battlecode.common.*;

public abstract class Unit extends MockRobotPlayer
{
    FeedForwardNeuralNetwork net;
    FightMicro fightMicro;
    Navigator navigator;

    RobotInfo[] nearByEnemies;
    RobotInfo[] enemies;
    RobotInfo[] nearByAllies;
    RobotInfo[] allies;
    RobotInfo[] nearByZombies;
    RobotInfo[] zombies;

    public Unit(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 4}, ActivationFunction.STEP, ActivationFunction.STEP);
        this.fightMicro = new FightMicro(rc);
        navigator = new Navigator(rc);
    }

    public void run()
    {
        nearByEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
        enemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent());

        nearByAllies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
        allies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());

        nearByZombies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
        zombies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);


        try
        {
            if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.GUARD  || rc.getType() == RobotType.VIPER)
            {
                if (this.fightMicro.runFightMicro(enemies, nearByAllies, allies, target, nearByEnemies, net));
                else if (this.fightMicro.runFightMicro(zombies, nearByAllies, allies, target, nearByZombies, net));
                else if (rc.isCoreReady()) navigator.move(target);
            }
            else
            {
                if (this.fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies, net));
                else if (this.fightMicro.runPassiveFightMicro(zombies, nearByAllies, allies, target, nearByZombies, net));
                else if (rc.isCoreReady()) navigator.move(target);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
