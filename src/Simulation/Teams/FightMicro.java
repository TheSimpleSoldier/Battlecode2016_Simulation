package Simulation.Teams;

import Simulation.FeedForwardNeuralNetwork;
import battlecode.common.*;

public class FightMicro
{
    RobotController rc;

    public FightMicro(RobotController rc)
    {
        this.rc = rc;
    }

    /**
     * This method returns the RobotInfo for the Robot with the lowest health
     */
    public static RobotInfo findWeakestEnemy(RobotInfo[] nearByEnemies)
    {
        RobotInfo weakest = nearByEnemies[nearByEnemies.length - 1];

        for (int i = nearByEnemies.length-1; --i > 0; )
        {
            if (nearByEnemies[i] == null)
            {
                System.out.println("Enemy is null");
            }
            else if (nearByEnemies[i].health < weakest.health)
            {
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
    }

    private Direction getDir(MapLocation target)
    {
        return rc.getLocation().directionTo(target);
    }

    private void moveDir(Direction dir) throws GameActionException
    {
        if (rc.isCoreReady())
        {
            if (rc.canMove(dir))
            {
                rc.move(dir);
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                rc.move(dir.rotateLeft());
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                rc.move(dir.rotateRight());
            }
        }
    }

    /**
     * This function runs the fight micro
     *
     *  this.fightMicro.runFightMicro(zombies, nearByAllies, allies, target, nearByZombies, net);
     */
    public boolean runFightMicro(RobotInfo[] enemies, RobotInfo[] nearByAllies, RobotInfo[] allies, MapLocation target, RobotInfo[] nearByEnemies, FeedForwardNeuralNetwork net) throws GameActionException
    {
        if (enemies == null || enemies.length == 0)
        {
            return false;
        }

        double alliedHealth = 0;

        for (int i = allies.length; --i >= 0; )
        {
            alliedHealth += allies[i].health;
        }

        double enemyHealth = 0;

        for (int i = enemies.length; --i >= 0; )
        {
            enemyHealth += enemies[i].health;
        }

        double[] inputs = new double[] {
                enemies.length / 10,
                allies.length / 10,
                alliedHealth / 10,
                enemyHealth / 10,
                nearByEnemies.length / 10,
                nearByAllies.length / 10
        };

        Direction dir;

        boolean retreat = false;
        boolean advance = false;
        boolean cluster = false;
        boolean pursue = false;
        double[] output = net.compute(inputs);

        // retreat
        if (output[0] > 0.5)
        {
            retreat = true;
        }

        // advance
        if (output[1] > 0.5) {
            advance = true;
        }

        // cluster
        if (output[2] > 0.5) {
            cluster = true;
        }

        // pursue
        if (output[3] > 0.5) {
            pursue = true;
        }

        if (rc.isCoreReady()) {
            if (retreat) {
                MapLocation enemy = new MapLocation((int) inputs[0] + rc.getLocation().x, (int) inputs[1] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(enemy).opposite();
                moveDir(dir);
            }

            if (rc.isCoreReady() && cluster) {
                MapLocation ally = new MapLocation((int) inputs[2] + rc.getLocation().x, (int) inputs[3] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(ally);
                moveDir(dir);
            }

            if (rc.isCoreReady() && advance) {
                dir = getDir(target);
                moveDir(dir);
            }

            if (rc.isCoreReady() && pursue) {
                MapLocation enemy = new MapLocation((int) inputs[0] + rc.getLocation().x, (int) inputs[1] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(enemy);
                moveDir(dir);
            }
        }


        if (rc.isWeaponReady() && nearByEnemies.length > 0) {
            try {
                RobotInfo weakEnemy = findWeakestEnemy(nearByEnemies);
                if (weakEnemy != null) {
                    MapLocation attackSpot = weakEnemy.location;
                    if (attackSpot != null && rc.canAttackLocation(attackSpot)) {
                        rc.attackLocation(attackSpot);
                    }
                }
            } catch (Exception e) {
                System.out.println("failed when trying to attack");
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Creates inputs for network
     * 2 are for center of mass of enemy
     * 2 are for center of mass of allies
     * 1 is for standard deviation of enemy
     * 1 is for standard deviation of allies
     * 1 is for number of enemy
     * 1 is for number of allies
     * 1 is for core delay
     * 1 is for weapon delay
     * 8 are for terrain around bot
     * 21 for each type of bot and is count of that type of enemy
     * 21 for each type of bot and is count of that type of ally
     *
     * each count is divided by 10 to normalize
     *
     * @param nearByBots all near by bots
     * @return
     */
    private double[] getInputs(RobotInfo[] nearByBots) throws Exception
    {
        double enemyCount = 0;
        double allyCount = 0;
        double averageAllyX = 0;
        double averageAllyY = 0;
        double averageEnemyX = 0;
        double averageEnemyY = 0;
        double totalEnemyHealth = 0;
        double totalAllyHealth = 0;
        double closeEnemies = 0;
        double closeAllies = 0;
        MapLocation us = rc.getLocation();
        int x = us.x;
        int y = us.y;
        Team team = rc.getTeam();
        RobotType type = rc.getType();

        for (RobotInfo bot : nearByBots)
        {
            MapLocation spot = bot.location;
            if(bot.team.equals(team))
            {
                switch (bot.type)
                {
                    case SOLDIER:
                        allyCount++;
                        break;
                    case GUARD:
                        allyCount++;
                        break;
                    case TURRET:
                        allyCount++;
                        break;
                    case VIPER:
                        allyCount++;
                        break;
                }
                averageAllyX += spot.x - x;
                averageAllyY += spot.y - y;
                totalAllyHealth += bot.health;

                if (spot.distanceSquaredTo(us) <= type.attackRadiusSquared)
                {
                    closeAllies++;
                }
            }
            else
            {
                switch (bot.type)
                {
                    case SOLDIER:
                        enemyCount++;
                        break;
                    case GUARD:
                        enemyCount++;
                        break;
                    case TURRET:
                        enemyCount++;
                        break;
                    case VIPER:
                        enemyCount++;
                        break;
                }
                averageEnemyX += spot.x - x;
                averageEnemyY += spot.y - y;
                totalEnemyHealth += bot.health;

                if (spot.distanceSquaredTo(us) <= type.attackRadiusSquared)
                {
                    closeEnemies++;
                }
            }
        }

        double[] toReturn = new double[10];

        toReturn[0] = averageEnemyX / enemyCount;
        toReturn[1] = averageEnemyY / enemyCount;
        toReturn[2] = averageAllyX / allyCount;
        toReturn[3] = averageAllyY / allyCount;
        toReturn[4] = enemyCount / 10;
        toReturn[5] = allyCount / 10;
        toReturn[6] = totalAllyHealth / 10;
        toReturn[7] = totalEnemyHealth / 10;
        toReturn[8] = closeEnemies / 10;
        toReturn[9] = closeAllies / 10;

        return toReturn;
    }
}
