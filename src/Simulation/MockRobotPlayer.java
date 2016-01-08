package Simulation;

import battlecode.common.*;

/**
 *
 * This class is implemented for a team and is used to control the individual units for a team
 *
 * Note:  It is very important that any class that extends this class to NOT use static variables or functions
 * as this will ruin the simulation as all of the bots will be run together
 *
 */
public abstract class MockRobotPlayer
{
    public MapLocation target;
    public RobotController rc;
    public double[][] weights;
    public Direction[] dirs;

    public MockRobotPlayer()
    {
        throw new Error("Mock Robot Player was not initialized");
    }

    public MockRobotPlayer(RobotController robotController, double[][] weights)
    {
        this.rc = robotController;
        this.target = ((MockRobotController) rc).getTarget();
        this.weights = weights;
        dirs = Direction.values();
    }

    public char getTypeLetter()
    {
        if (rc.getType() == RobotType.SOLDIER)
        {
            return 's';
        }
        else if (rc.getType() == RobotType.GUARD)
        {
            return 'g';
        }
        else if (rc.getType() == RobotType.VIPER)
        {
            return 'v';
        }
        else if (rc.getType() == RobotType.SCOUT)
        {
            return 'c';
        }
        else if (rc.getType() == RobotType.ARCHON)
        {
            return 'a';
        }
        else if (rc.getType() == RobotType.TURRET)
        {
            return 't';
        }
        else if (rc.getType() == RobotType.TTM)
        {
            return 'm';
        }
        else if (rc.getType() == RobotType.RANGEDZOMBIE)
        {
            return 'r';
        }
        else if (rc.getType() == RobotType.BIGZOMBIE)
        {
            return 'b';
        }
        else if (rc.getType() == RobotType.STANDARDZOMBIE)
        {
            return 's';
        }
        else if (rc.getType() == RobotType.FASTZOMBIE)
        {
            return 'f';
        }

        return ' ';
    }

    public char getTeamChar()
    {
        if (rc.getTeam() == Team.A) return 'a';
        else if (rc.getTeam() == Team.ZOMBIE) return 'z';
        return 'b';
    }

    public RobotInfo getBotInfo()
    {
        int ID = 0;
        Team team = rc.getTeam();
        RobotType type = rc.getType();
        MapLocation location = rc.getLocation();
        double coreDelay = rc.getCoreDelay();
        double weaponDelay = rc.getWeaponDelay();
        double attackPower = rc.getType().attackPower;
        double health = rc.getHealth();
        double maxHealth = rc.getType().maxHealth;
        int zombieInfectedTurns = 0;
        int viperInfectedTurns = 0;

        return new RobotInfo(ID, team, type, location, coreDelay, weaponDelay, attackPower, health, maxHealth, zombieInfectedTurns, viperInfectedTurns);
    }

    public void runTurnEnd()
    {
        ((MockRobotController) rc).yield();
    }

    public abstract void run();

    public void takeDamage(double damage)
    {
//        System.out.println("Taking damage");
        ((MockRobotController) rc).takeDamage(damage);
    }

    public double getHealth()
    {
        return this.rc.getHealth();
    }

    public boolean noCloseEnemies()
    {
        if (this.rc.senseNearbyRobots(rc.getLocation(), 49, rc.getTeam().opponent()).length == 0)
        {
            return true;
        }
        return false;
    }

    public boolean removeFromGame()
    {
        if (target != null)
        {
            if (rc.getLocation().distanceSquaredTo(target) <= 4)// && noCloseEnemies())
            {
                return true;
            }
        }
        return false;
    }

    public void repair()
    {
        ((MockRobotController) rc).takeDamage(-1);
    }

    public RobotController getRc()
    {
        return this.rc;
    }
}
