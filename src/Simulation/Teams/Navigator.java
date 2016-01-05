package Simulation.Teams;

import battlecode.common.*;

public class Navigator
{
    RobotController rc;

    public Navigator(RobotController rc)
    {
        this.rc = rc;
    }

    /**
     * This function causes the unit to move
     *
     * @param target
     */
    public void move(MapLocation target) {
        if (rc.isCoreReady()) {
            Direction dir = getDir(target);
            try {
                if (rc.canMove(dir)) {
                    rc.move(dir);
                } else if (rc.canMove(dir.rotateRight())) {
                    rc.move(dir.rotateRight());
                } else if (rc.canMove(dir.rotateLeft())) {
                    rc.move(dir.rotateLeft());
                } else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
                    rc.move(dir.rotateLeft().rotateLeft());
                } else if (rc.canMove(dir.rotateRight().rotateRight())) {
                    rc.move(dir.rotateRight().rotateRight());
                } else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
                    rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
                } else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
                    rc.move(dir.rotateRight().rotateRight().rotateRight());
                } else if (rc.canMove(dir.opposite())) {
                    rc.move(dir.opposite());
                }
            } catch (Exception e) {
                System.out.println("Failed to move");
                e.printStackTrace();
            }
        }
    }

    private Direction getDir(MapLocation target) {
        return rc.getLocation().directionTo(target);
    }

}
