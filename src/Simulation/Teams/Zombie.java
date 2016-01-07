package Simulation.Teams;

import battlecode.common.*;

public class Zombie extends Unit
{
    public Zombie(RobotController rc, double[][] weights)
    {
        super(rc, weights);
    }

    public void run()
    {
        RobotInfo[] nearByEnemiesA = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.A);
        RobotInfo[] nearByEnemiesB = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.B);
        RobotInfo[] enemiesA = rc.senseNearbyRobots(2500, Team.A);
        RobotInfo[] enemiesB = rc.senseNearbyRobots(2500, Team.B);


        try
        {
            if (nearByEnemiesA.length > 0 && rc.isWeaponReady())
            {
                int choice = (int) (Math.random() * nearByEnemiesA.length);
                if (rc.canAttackLocation(nearByEnemiesA[choice].location))
                {
                    rc.attackLocation(nearByEnemiesA[choice].location);
                }
            }
            else if (nearByEnemiesB.length > 0 && rc.isWeaponReady())
            {
                int choice = (int) (Math.random() * nearByEnemiesB.length);
                if (rc.canAttackLocation(nearByEnemiesB[choice].location))
                {
                    rc.attackLocation(nearByEnemiesB[choice].location);
                }
            }
            else if (rc.isCoreReady())
            {
                MapLocation target = null;
                int closestDist = 99999;

                for (int i = 0; i < enemiesA.length; i++)
                {
                    if (closestDist > rc.getLocation().distanceSquaredTo(enemiesA[i].location))
                    {
                        closestDist = rc.getLocation().distanceSquaredTo(enemiesA[i].location);
                        target = enemiesA[i].location;
                    }
                }

                for (int i = 0; i < enemiesB.length; i++)
                {
                    if (closestDist > rc.getLocation().distanceSquaredTo(enemiesB[i].location))
                    {
                        closestDist = rc.getLocation().distanceSquaredTo(enemiesB[i].location);
                        target = enemiesB[i].location;
                    }
                }

                if (target != null && rc.canMove(rc.getLocation().directionTo(target)))
                {
//                    System.out.println("we are moving!!!");
                    rc.move(rc.getLocation().directionTo(target));
                }
                else if (target != null && rc.senseRubble(rc.getLocation().add(rc.getLocation().directionTo(target))) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
                {
                    rc.clearRubble(rc.getLocation().directionTo(target));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
