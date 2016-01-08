package Simulation.Teams;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Turret extends Unit
{
    public Turret(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net.setWeights(weights[4]);
    }

    public void run()
    {
        if (!rc.isWeaponReady())
        {
            return;
        }

        nearByEnemies = rc.senseNearbyRobots(24, rc.getTeam().opponent());
        nearByZombies = rc.senseNearbyRobots(24, Team.ZOMBIE);

        if (nearByEnemies.length > 0)
        {
            for (int i = nearByEnemies.length; --i>=0; )
            {
                MapLocation enemy = nearByEnemies[i].location;
                if (enemy.distanceSquaredTo(rc.getLocation()) > 5 && rc.canAttackLocation(enemy))
                {
                    try { rc.attackLocation(enemy); } catch (Exception e) { e.printStackTrace(); }
                    return;
                }
            }
        }

        if (nearByZombies.length > 0)
        {
            for (int i = nearByZombies.length; --i>=0; )
            {
                MapLocation enemy = nearByZombies[i].location;
                if (enemy.distanceSquaredTo(rc.getLocation()) > 5 && rc.canAttackLocation(enemy))
                {
                    try { rc.attackLocation(enemy); } catch (Exception e) { e.printStackTrace(); }
                    return;
                }
            }
        }

    }
}
