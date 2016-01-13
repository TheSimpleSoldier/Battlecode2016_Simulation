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
        double enemyHealth = 0;
        double offensiveEnemies = 0;
        double enemiesInRangeOfUs = 0;
        int enemy_x = 0;
        int enemy_y = 0;
        int ally_x = 0;
        int ally_y = 0;

        for (int i = 0; i < enemies.length; i++)
        {
            enemy_x += enemies[i].location.x;
            enemy_y += enemies[i].location.y;
            switch(enemies[i].type)
            {
                case VIPER:
                case SOLDIER:
                case GUARD:
                case BIGZOMBIE:
                case FASTZOMBIE:
                case STANDARDZOMBIE:
                case RANGEDZOMBIE:
                case TURRET:
                    offensiveEnemies++;
                    enemyHealth += enemies[i].health;
                    if (rc.getLocation().distanceSquaredTo(enemies[i].location) <= enemies[i].type.attackRadiusSquared)
                        enemiesInRangeOfUs++;
            }
        }

        enemy_x /= enemies.length;
        enemy_y /= enemies.length;

        for (int i = allies.length; --i >= 0; )
        {
            alliedHealth += allies[i].health;
            ally_x += allies[i].location.x;
            ally_y += allies[i].location.y;
        }

        if (allies.length > 0)
        {
            ally_x /= allies.length;
            ally_y /= allies.length;
        }

        double[] inputs = new double[] {
                offensiveEnemies / 10,
                allies.length / 10,
                alliedHealth / 10,
                enemyHealth / 10,
                nearByEnemies.length / 10,
                enemiesInRangeOfUs / 10
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
                MapLocation enemy = new MapLocation(enemy_x, enemy_y);
                dir = rc.getLocation().directionTo(enemy).opposite();
                moveDir(dir);
            }

            if (rc.isCoreReady() && cluster) {
                MapLocation ally = new MapLocation(ally_x, ally_y);
                dir = rc.getLocation().directionTo(ally);
                moveDir(dir);
            }

            if (rc.isCoreReady() && advance) {
                dir = getDir(target);
                moveDir(dir);
            }

            if (rc.isCoreReady() && pursue) {
                MapLocation enemy = new MapLocation(enemy_x, enemy_y);
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

    public boolean runPassiveFightMicro(RobotInfo[] enemies, RobotInfo[] nearByAllies, RobotInfo[] allies, MapLocation target, RobotInfo[] nearByEnemies, FeedForwardNeuralNetwork net) throws GameActionException
    {
        if (enemies.length == 0)
        {
            return false;
        }

        double alliedHealth = 0;
        double enemyHealth = 0;
        double offensiveEnemies = 0;
        double enemiesInRangeOfUs = 0;
        int enemy_x = 0;
        int enemy_y = 0;
        int ally_x = 0;
        int ally_y = 0;

        for (int i = 0; i < enemies.length; i++)
        {
            enemy_x += enemies[i].location.x;
            enemy_y += enemies[i].location.y;
            switch(enemies[i].type)
            {
                case VIPER:
                case SOLDIER:
                case GUARD:
                case BIGZOMBIE:
                case FASTZOMBIE:
                case STANDARDZOMBIE:
                case RANGEDZOMBIE:
                case TURRET:
                    offensiveEnemies++;
                    enemyHealth += enemies[i].health;
                    if (rc.getLocation().distanceSquaredTo(enemies[i].location) <= enemies[i].type.attackRadiusSquared)
                        enemiesInRangeOfUs++;
            }
        }

        enemy_x /= enemies.length;
        enemy_y /= enemies.length;

        for (int i = allies.length; --i >= 0; )
        {
            alliedHealth += allies[i].health;
            ally_x += allies[i].location.x;
            ally_y += allies[i].location.y;
        }

        if (allies.length > 0)
        {
            ally_x /= allies.length;
            ally_y /= allies.length;
        }

        double[] inputs = new double[] {
                offensiveEnemies / 10,
                allies.length / 10,
                alliedHealth / 10,
                enemyHealth / 10,
                nearByEnemies.length / 10,
                enemiesInRangeOfUs / 10
        };

        Direction dir;

        boolean retreat = false;
        boolean advance = false;
        boolean cluster = false;
        boolean standGround = false;
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

        // standGround
        if (output[3] > 0.5) {
            standGround = true;
        }

        if (rc.isCoreReady()) {
            if (retreat) {
                MapLocation enemy = new MapLocation(enemy_x, enemy_y);
                dir = rc.getLocation().directionTo(enemy).opposite();
                moveDir(dir);
            }

            if (rc.isCoreReady() && cluster) {
                MapLocation ally = new MapLocation(ally_x, ally_y);
                dir = rc.getLocation().directionTo(ally);
                moveDir(dir);
            }

            if (rc.isCoreReady() && advance) {
                dir = getDir(target);
                moveDir(dir);
            }

            if (rc.isCoreReady() && standGround) {
                // do nothing
            }
        }

        return true;
    }


    /**
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


    public boolean runAdvancedFightMicro(RobotInfo[] allies, RobotInfo[] nearByEnemies, RobotInfo[] enemies, Direction direction, FeedForwardNeuralNetwork net) throws GameActionException
    {
        if (enemies.length == 0) return false;

        double[] inputs = getAdvancedInputs(allies, nearByEnemies, enemies);

        double[] output = net.compute(new double[]{inputs[0], inputs[1],inputs[2],inputs[3],inputs[4],inputs[5],inputs[6],inputs[7],inputs[8],inputs[9],inputs[10]});

        boolean flee = false;
        boolean rush = false;
        boolean retreat = false;
        boolean cluster = false;
        boolean pursue = false;
        boolean advance = false;
        boolean kite = false;

        if (output[0] > 0.5) flee = true;
        if (output[1] > 0.5) rush = true;
        if (output[2] > 0.5) retreat = true;
        if (output[3] > 0.5) cluster = true;
        if (output[4] > 0.5) pursue = true;
        if (output[5] > 0.5) advance = true;
        if (output[6] > 0.5) kite = true;

        MapLocation allyCOM = new MapLocation((int) inputs[13], (int) inputs[14]);
        MapLocation enemyCOM = new MapLocation((int) inputs[11], (int) inputs[12]);
        MapLocation us = rc.getLocation();

        if (flee)
        {
            if (rc.isCoreReady()) moveDir(us.directionTo(enemyCOM).opposite());
        }

        if (rush)
        {
            if (rc.isCoreReady()) moveDir(us.directionTo(enemyCOM));
        }

        if (!rush && !flee)
        {
            if (rc.isWeaponReady() && nearByEnemies.length > 0)
            {
                RobotInfo weakest = findWeakestEnemy(nearByEnemies);

                if (rc.canAttackLocation(weakest.location))
                {
                    rc.attackLocation(weakest.location);
                }
            }
            else
            {
                if (!rc.isCoreReady()) return true;

                if (retreat) moveDir(us.directionTo(enemyCOM).opposite());
                if (cluster && rc.isCoreReady()) moveDir(us.directionTo(allyCOM));
                if (pursue && rc.isCoreReady()) moveDir(us.directionTo(enemyCOM));
                if (advance && rc.isCoreReady()) moveDir(direction);

                if (kite)
                {
                    Direction dir = rc.getLocation().directionTo(enemyCOM).opposite();
                    MapLocation forward = us.add(dir);
                    MapLocation right = us.add(dir.rotateRight());
                    MapLocation left = us.add(dir.rotateLeft());
                    int attackRange = rc.getType().attackRadiusSquared;

                    for (int i = nearByEnemies.length; --i>=0;)
                    {
                        if (rc.canMove(dir) && nearByEnemies[i].location.distanceSquaredTo(forward) <= attackRange)
                        {
                            rc.move(dir);
                            return true;
                        }
                        else if (rc.canMove(dir.rotateRight()) && nearByEnemies[i].location.distanceSquaredTo(right) <= attackRange)
                        {
                            rc.move(dir.rotateRight());
                            return true;
                        }
                        else if (rc.canMove(dir.rotateLeft()) && nearByEnemies[i].location.distanceSquaredTo(left) <= attackRange)
                        {
                            rc.move(dir.rotateLeft());
                            return true;
                        }
                    }
                }

                return true;
            }
        }



        return false;
    }

    private double[] getAdvancedInputs(RobotInfo[] allies, RobotInfo[] nearByEnemies, RobotInfo[] enemies)
    {
        double[] inputs = new double[15];

        inputs[0] = rc.getHealth();

        double alliedHealth = 0;
        double enemyHealth = 0;
        double offensiveEnemies = 0;
        double enemiesInRangeOfUs = 0;
        double alliedAttackPower = 0;
        double enemyAttackPower = 0;
        int enemy_x = 0;
        int enemy_y = 0;
        int ally_x = 0;
        int ally_y = 0;

        for (int i = 0; i < enemies.length; i++)
        {
            enemy_x += enemies[i].location.x;
            enemy_y += enemies[i].location.y;
            switch(enemies[i].type)
            {
                case VIPER:
                case SOLDIER:
                case GUARD:
                case BIGZOMBIE:
                case FASTZOMBIE:
                case STANDARDZOMBIE:
                case RANGEDZOMBIE:
                case TURRET:
                    enemyAttackPower += enemies[i].attackPower;
                    offensiveEnemies++;
                    enemyHealth += enemies[i].health;
                    if (rc.getLocation().distanceSquaredTo(enemies[i].location) <= enemies[i].type.attackRadiusSquared)
                        enemiesInRangeOfUs++;
            }
        }

        enemy_x /= enemies.length;
        enemy_y /= enemies.length;

        MapLocation enemyCOM = new MapLocation(enemy_x, enemy_y);
        int ourDist = rc.getLocation().distanceSquaredTo(enemyCOM);
        int alliesInFront = 0;
        int alliesBehind = 0;
        int offensiveAllies = 0;

        for (int i = allies.length; --i >= 0; )
        {
            switch(allies[i].type)
            {
                case VIPER:
                case SOLDIER:
                case GUARD:
                case TURRET:
                    offensiveAllies++;
                    alliedHealth += allies[i].health;
                    alliedAttackPower += allies[i].attackPower;

                    if (allies[i].location.distanceSquaredTo(enemyCOM) < ourDist)
                    {
                        alliesInFront++;
                    }
                    else
                    {
                        alliesBehind++;
                    }

                    break;
            }
            ally_x += allies[i].location.x;
            ally_y += allies[i].location.y;
        }

        if (allies.length > 0)
        {
            ally_x /= allies.length;
            ally_y /= allies.length;
        }

        inputs[1] = offensiveEnemies;
        inputs[2] = nearByEnemies.length;
        inputs[3] = enemiesInRangeOfUs;
        inputs[4] = enemyAttackPower;
        inputs[5] = alliedAttackPower;
        inputs[6] = enemyHealth;
        inputs[7] = alliedHealth;
        inputs[8] = offensiveAllies;
        inputs[9] = alliesBehind;
        inputs[10] = alliesInFront;
        inputs[11] = enemy_x;
        inputs[12] = enemy_y;
        inputs[13] = ally_x;
        inputs[14] = ally_y;

        return inputs;
    }
}
