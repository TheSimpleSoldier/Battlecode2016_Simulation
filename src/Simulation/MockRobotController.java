package Simulation;

import battlecode.common.*;

public class MockRobotController implements RobotController
{
    private Team team;
    private RobotType robotType;
    private MapLocation location;
    private Map map;
    private double coreDelay;
    private double weaponsDelay;
    private double health;
    private double supply;
    private int xp;
    private int missileCount;
    private double totalDamageDealt;

    public MockRobotController(Team team, RobotType robotType, MapLocation location, Map map)
    {
        this.team = team;
        this.robotType = robotType;
        this.location = location;
        this.map = map;

        if (robotType == RobotType.SOLDIER)
        {
            this.health = RobotType.SOLDIER.maxHealth;
        }
        else
        {
            throw new Error("Robot Type does not get health assigned: " + robotType);
        }

        this.missileCount = 0;
        this.xp = 0;
        this.coreDelay = 0;
        this.weaponsDelay = 0;
        this.supply = 0;
        this.totalDamageDealt = 0;
    }

    /**
     * Activates the neutral robot at the given location, converting it to a robot of the same type but on your team.
     *
     * @param loc
     */
    public void activate(MapLocation loc)
    {
        throw new Error("Activate not implemented");
    }

    /**
     * Adds a custom observation to the match file, such that when it is analyzed, this observation will appear.
     *
     * @param observation
     */
    public void addMatchObservation(String observation)
    {
        throw new Error("addMatchObservation Not implemented");
    }

    /**
     * Queues an attack on the given location to be performed at the end of this turn.
     *
     * @param loc
     */
    public void attackLocation(MapLocation loc)
    {
        if (loc.distanceSquaredTo(getLocation()) <= getType().attackRadiusSquared)
        {
            totalDamageDealt += getType().attackPower;
            map.attackLocation(loc, getType().attackPower);
//            System.out.println("Robot on Team: " + getTeam() + " had dealt: " + getType().attackPower + " damage");
        }
    }

    /**
     * Broadcasts a message signal over a specific integer radius.
     *
     * @param message1
     * @param message2
     * @param radius
     */
    public void broadcastMessageSignal(int message1, int message2, int radius)
    {
        throw new Error("BroadcastMessageSignal not implemented");
    }

    /**
     * Broadcasts a regular signal over a specific integer radius.
     *
     * @param radius
     */
    public void broadcastSignal(int radius)
    {
        throw new Error("broadcastSignal not implemented");
    }

    /**
     * Builds a structure in the given direction, queued for the end of the turn.
     *
     * @param dir
     * @param type
     */
    public void	build(Direction dir, RobotType type)
    {
        throw new Error("build Not implemented");
    }

    /**
     * Returns whether the given location is within the robot's attack range.
     *
     * @param loc
     * @return
     */
    public boolean canAttackLocation(MapLocation loc)
    {
        if (loc.distanceSquaredTo(getLocation()) <= getType().attackRadiusSquared)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the robot can build a structure of the given type in the given direction, without taking delays into account.
     *
     * @param dir
     * @param type
     * @return
     */
    public boolean canBuild(Direction dir, RobotType type)
    {
        throw new Error("canBuild Not implemented");
    }

    /**
     * Tells whether this robot can move in the given direction, without taking any sort of delays into account.
     *
     * @param dir
     * @return
     */
    public boolean canMove(Direction dir)
    {
        if (this.coreDelay >= 1)
        {
            return false;
        }

        if (dir == Direction.OMNI || dir == Direction.NONE)
        {
            return false;
        }

        if (map.locationOccupied(getLocation().add(dir)))
        {
            return false;
        }

        return map.terranTraversalbe(getLocation().add(dir), getType());
    }

    /**
     * Returns true if the given location is within the robot's sensor range, or within the sensor range of some ally.
     *
     * @param loc
     * @return
     */
    public boolean canSenseLocation(MapLocation loc)
    {
        throw new Error("canSenseLocation Not implemented");
    }

    /**
     * Returns true if the given robot is within the robot's sensor range.
     *
     * @param id
     * @return
     */
    public boolean	canSenseRobot(int id)
    {
        throw new Error("canSenseRobot Not implemented");
    }

    /**
     * Clears rubble in the specified direction.
     *
     * @param dir
     */
    public void clearRubble(Direction dir)
    {
        throw new Error("Clear Rubble not implemented");
    }

    /**
     * Kills your robot and ends the current round.
     */
    public void	disintegrate()
    {
        throw new Error("Disintegrate Not implemented");
    }

    /**
     * Retrieves an array of all the messages in your incoming message queue.
     */
    public Signal[] emptySignalQueue()
    {
        throw new Error("emptySignalQueue not implemented");
    }

    /**
     * Gets this robot's 'control bits' for debugging purposes.
     *
     * @return
     */
    public long	getControlBits()
    {
        throw new Error("getControlBits Not implemented");
    }

    /**
     * Returns the amount of core delay a robot has accumulated.
     *
     * @return
     */
    public double getCoreDelay()
    {
        return this.coreDelay;
    }

    /**
     * Gets the robot's current health.
     *
     * @return
     */
    public double getHealth()
    {
        return this.health;
    }

    /**
     * Use this method to access your ID.
     *
     * @return
     */
    public int getID()
    {
        throw new Error("getID Not implemented");
    }

    /**
     * Gets the robot's current location.
     *
     * @return
     */
    public MapLocation getLocation()
    {
        return location;
    }

    /**
     * Gets the number of rounds in the game.
     *
     * @return
     */
    public int getRoundLimit()
    {
        throw new Error("getRoundLimit Not implemented");
    }

    /**
     * Returns the current round number, where round 0 is the first round of the match.
     *
     * @return
     */
    public int getRoundNum()
    {
        throw new Error("getRoundNum not implemented");
    }

    /**
     * Gets the Team of this robot.
     *
     * @return
     */
    public Team	getTeam()
    {
        return this.team;
    }

    /**
     * Returns the team memory from the last game of the match.
     *
     * @return
     */
    public long[] getTeamMemory()
    {
        throw new Error("getTeamMemory Not implemented");
    }

    /**
     * Gets the team's total parts.
     *
     * @return
     */
    public double getTeamParts()
    {
        throw new Error("getTeamParts Not implemented");
    }

    /**
     * Gets this robot's type (SOLDIER, etc.).
     *
     * @return
     */
    public RobotType getType()
    {
        return this.robotType;
    }

    /**
     * Returns the amount of weapon delay a robot has accumulated.
     *
     * @return
     */
    public double getWeaponDelay()
    {
        return this.weaponsDelay;
    }

    /**
     * Returns a copy of the zombie spawn schedule for the game.
     *
     * @return
     */
    public ZombieSpawnSchedule getZombieSpawnSchedule()
    {
        throw new Error("getZombieSpawnSchedule not implemented");
    }

    /**
     * Returns whether you have the ore and the dependencies to build the given robot, and that the robot can build structures.
     *
     * @param type
     * @return
     */
    public boolean hasBuildRequirements(RobotType type)
    {
        throw new Error("hasBuildRequirements Not implemented");
    }

    /**
     * Returns whether the core delay is strictly less than 1 (whether the robot can perform a core action in the given turn).
     *
     * @return
     */
    public boolean isCoreReady()
    {
        return this.coreDelay < 1;
    }

    /**
     * Returns whether there is a robot at the given location.
     *
     * @param loc
     * @return
     */
    public boolean isLocationOccupied(MapLocation loc)
    {
        throw new Error("isLocationOccupied Not implemented");
    }

    /**
     * Returns whether the weapon delay is less than 1 (whether the robot can attack in the given turn).
     *
     * @return
     */
    public boolean isWeaponReady()
    {
        if (this.weaponsDelay < 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Queues a move in the given direction to be performed at the end of this turn.
     *
     * @param dir
     */
    public void move(Direction dir)
    {
        if (dir == Direction.NONE || dir == Direction.OMNI)
        {
            System.out.println("Moving in dir None or Omni");
        }
        else if (canMove(dir))
        {
            map.moveRobot(getLocation(), getLocation().add(dir));
            location = getLocation().add(dir);

            if (robotType == RobotType.SOLDIER)
            {
                coreDelay += RobotType.SOLDIER.movementDelay;
                weaponsDelay += RobotType.SOLDIER.cooldownDelay;
            }
        }
        else
        {
            System.out.println("Trying to move where we can't");
        }
    }

    /**
     * Senses whether a MapLocation is on the map.
     *
     * @param loc
     * @return
     */
    public boolean onTheMap(MapLocation loc)
    {
        throw new Error("onTheMap not implemented");
    }

    /**
     * Turret only.
     */
    public void pack()
    {
        throw new Error("pack not implemented");
    }

    /**
     * Retrieve the next message waiting in your incoming message queue.
     *
     * @return
     */
    public Signal readSignal()
    {
        throw new Error("readSignal not implemented");
    }

    /**
     * Repairs the robot at the given location.
     *
     * @param loc
     */
    public void repair(MapLocation loc)
    {
        throw new Error("repair not implemented");
    }

    /**
     * Causes your team to lose the game.
     */
    public void	resign()
    {
        throw new Error("resign Not implemented");
    }

    /**
     * Returns all robots that can be sensed on the map.
     *
     * @return
     */
    public RobotInfo[] senseNearbyRobots()
    {
        return map.getAllRobotsInRange(getLocation(), 1000000);
    }

    /**
     * Returns all robots that can be sensed within a certain radius of the robot.
     *
     * @param radiusSquared
     * @return
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared)
    {
        return map.getAllRobotsInRange(getLocation(), radiusSquared);
    }

    /**
     * Returns all robots of a given team that can be sensed within a certain radius of the robot.
     *
     * @param radiusSquared
     * @param team
     * @return
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared, Team team)
    {
        RobotInfo[] allBots = map.getAllRobotsInRange(getLocation(), radiusSquared);
        RobotInfo[] teamBots;
        int count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        teamBots = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                teamBots[count] = allBots[i];
                count++;
            }
        }

        return teamBots;
    }

    /**
     * Returns all robots of a givin team that can be sensed within a certain radius of a specified location.
     *
     * @param center
     * @param radiusSquared
     * @param team
     * @return
     */
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radiusSquared, Team team)
    {
        RobotInfo[] allBots = map.getAllRobotsInRange(center, radiusSquared);
        RobotInfo[] teamBots;
        int count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        teamBots = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        return teamBots;
    }

    /**
     * Senses the parts at the given location.
     *
     * @param loc
     * @return
     */
    public double senseParts(MapLocation loc)
    {
        throw new Error("senseOre Not implemented");
    }

    /**
     * Senses information about a particular robot given its ID.
     *
     * @param id
     * @return
     */
    public RobotInfo senseRobot(int id)
    {
        throw new Error("senseRobot Not implemented");
    }

    /**
     * Returns the robot at the given location, or null if there is no object there.
     *
     * @param loc
     * @return
     */
    public RobotInfo senseRobotAtLocation(MapLocation loc)
    {
        throw new Error("senseRobotAtLocation Not implemented");
    }

    /**
     * Senses the rubble at the given location.
     *
     * @param loc
     * @return
     */
    public double senseRubble(MapLocation loc)
    {
        throw new Error("senseRubble not implemented");
    }

    /**
     * Draws a dot on the game map, for debugging purposes.
     *
     * @param loc
     * @param red
     * @param green
     * @param blue
     */
    public void	setIndicatorDot(MapLocation loc, int red, int green, int blue)
    {
        throw new Error("setIndicatorDot Not implemented");
    }

    /**
     * Draws a line on the game map, for debugging purposes.
     *
     * @param from
     * @param to
     * @param red
     * @param green
     * @param blue
     */
    public void	setIndicatorLine(MapLocation from, MapLocation to, int red, int green, int blue)
    {
        throw new Error("setIndicatorLine Not implemented");
    }

    /**
     * Sets one of this robot's 'indicator strings' for debugging purposes.
     *
     * @param stringIndex
     * @param newString
     */
    public void	setIndicatorString(int stringIndex, String newString)
    {
        throw new Error("setIndicatorString Not implemented");
    }

    /**
     * Sets the team's "memory", which is saved for the next game in the match.
     *
     * @param index
     * @param value
     */
    public void	setTeamMemory(int index, long value)
    {
        throw new Error("setTeamMemory Not implemented");
    }

    /**
     * Sets this team's "memory".
     *
     * @param index
     * @param value
     * @param mask
     */
    public void	setTeamMemory(int index, long value, long mask)
    {
        throw new Error("setTeamMemory Not implemented");
    }

    /**
     * TTM only.
     */
    public void unpack()
    {
        throw new Error("unpack not implemented");
    }


    /**
     * Ends the current round.
     */
    public void	yield()
    {
        if (coreDelay > 1)
        {
            coreDelay--;
        }
        else
        {
            coreDelay = 0;
        }

        if (weaponsDelay > 1)
        {
            weaponsDelay--;
        }
        else
        {
            weaponsDelay = 0;
        }
    }


    public void takeDamage(double damage)
    {
        this.health -= damage;
    }

    public double getTotalDamageDealt()
    {
        return this.totalDamageDealt;
    }

    public MapLocation getTarget()
    {
        if (getTeam() == Team.A)
        {
            return map.getTeamAHQ();
        }
        return map.getTeamBHQ();
    }
}
