package Simulation.Teams;

import battlecode.common.*;

public class Archon extends Unit
{
    public Archon(RobotController rc, double[][] weights)
    {
        super(rc, weights);
//        net.setWeights(weights[1]);
    }


    public void run()
    {
        if (rc.isCoreReady()) navigator.move(target);

        if (nearByAllies.length > 0)
        {
            double weakestHealth = 9999;
            RobotInfo weakest = null;

            for (int i = nearByAllies.length; --i>=0; )
            {
                double health = nearByAllies[i].health;
                if (health < nearByAllies[i].maxHealth)
                {
                    if (health < weakestHealth)
                    {
                        weakestHealth = health;
                        weakest = nearByAllies[i];
                    }
                }
            }

            if (weakest != null)
            {
                try
                {
                    rc.repair(weakest.location);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
