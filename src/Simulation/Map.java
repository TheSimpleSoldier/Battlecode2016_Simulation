package Simulation;

import Simulation.Teams.Soldier;
import Simulation.Teams.team044;
import battlecode.common.*;
import java.io.*;

public class Map
{
    double[][] weights1;
    double[][] weights2;
    public MockMapLocation[][] mapLayout;
    public MapLocation teamAHQ;
    public MapLocation teamBHQ;
    private boolean verbose;

    private double blueSoldierDamageDealt;
    private double blueSoldierTotalHealth;

    private double redSoldierDamageDealt;
    private double redSoldierTotalHealth;


    public Map(double[][] weights1, double[][] weights2, boolean verbose)
    {
        this.weights1 = weights1;
        this.weights2 = weights2;

        this.blueSoldierDamageDealt = 0;
        this.blueSoldierTotalHealth = 0;
        this.redSoldierDamageDealt = 0;
        this.redSoldierTotalHealth = 0;
        this.verbose = verbose;
    }

    /**
     * This method prints out the current map with all of the robots on it
     */
    public void print()
    {
        System.out.println();
        System.out.println("------------------------- Printing map ------------------------");

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                String location = "";
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null)
                {
                    location += current.getRobotPlayer().getTypeLetter();
                    location += current.getRobotPlayer().getTeamChar();
                }

                if (i == teamAHQ.x && j == teamAHQ.y)
                {
                    location += 'A';
                }
                else if (i == teamBHQ.x && j == teamBHQ.y)
                {
                    location += 'B';
                }
                else
                {
                    location += current.getRubble();
                }

                location += ' ';

                System.out.print(location);
            }
            System.out.println();
        }
    }

    /**
     * This method will create MockMapLocations for a read in Map
     *
     * @return
     */
    public void readInMap(String mapName, Map map, int teamA, int teamB)
    {
        int[] mapDimensions = getMapWidthHeight(mapName);

        mapLayout = getInitialMap(mapName, mapDimensions, map, teamA, teamB);
    }

    /**
     * This method will return MockRobotPlayers for the start of a Match
     *
     * @return
     */
    public MockRobotPlayer[] getRobotPlayers()
    {
        MockRobotPlayer[] mockRobotPlayers;

        int robotCount = 0;

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                if (mapLayout[i][j].getRobotPlayer() != null)
                {
                    robotCount++;
                }
            }
        }

        mockRobotPlayers = new MockRobotPlayer[robotCount];

        int index = 0;
        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                if (mapLayout[i][j].getRobotPlayer() != null)
                {
                    mockRobotPlayers[index] = mapLayout[i][j].getRobotPlayer();
                    index++;
                }
            }
        }

        return mockRobotPlayers;
    }

    /**
     * This method will find the width and height of the map
     *
     * @return
     */
    private int[] getMapWidthHeight(String mapName)
    {
        int height = 0;
        int width = 0;

//        System.out.println("got here");
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(new File(mapName)));

            for (String x = in.readLine(); x != null; x = in.readLine())
            {
                if (x.contains("height"))
                {
                    int index = x.indexOf("height");
                    index += 8;
                    char numb = x.charAt(index);

                    while (numb >= '0' && numb <= '9')
                    {
                        height *= 10;
                        height += Integer.parseInt("" + numb);
                        index++;
                        numb = x.charAt(index);
                    }
                }

                if (x.contains("width"))
                {
                    int index = x.indexOf("width");
                    index += 7;
                    char numb = x.charAt(index);

                    while (numb >= '0' && numb <= '9')
                    {
                        width *= 10;
                        width += Integer.parseInt("" + numb);
                        index++;
                        numb = x.charAt(index);
                    }
                }
                if (width != 0 && height != 0)
                {
                    break;
                }
            }

            in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }


        return new int[] {height, width};
    }

    /**
     * This method will return a two d array of the map terrain
     *
     * @param mapName
     * @return
     */
    private MockMapLocation[][] getInitialMap(String mapName, int[] mapDimensions, Map map, int teamA, int teamB)
    {
        MockMapLocation[][] initialMap = new MockMapLocation[mapDimensions[0]][mapDimensions[1]];

        teamBHQ = new MapLocation(0, mapDimensions[1] - 1);
        teamAHQ = new MapLocation(mapDimensions[0] - 1, 0);

        String[][] rubbleAmounts = new String[mapDimensions[0]][mapDimensions[1]];
        String[][] unitStrings = new String[mapDimensions[0]][mapDimensions[1]];

        for (int i = 0; i < unitStrings.length; i++)
        {
            for (int j = 0; j < unitStrings[i].length; j++)
            {
                unitStrings[i][j] = "";
            }
        }

        int index = 0;
        int index2 = 0;
        boolean rubble = false;
        boolean units = false;
        boolean done = false;

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(new File(mapName)));

            for (String x = in.readLine(); x != null; x = in.readLine())
            {
                if (!rubble)
                {
                    if (x.contains("initialRubble"))
                    {
                        rubble = true;
                    }
                }
                // we are in the rubble
                else if (!units)
                {
                    // we have reached the end of the file
                    if (x.contains("initialRubble"))
                    {
                        units = true;
                    }
                    else
                    {
                        String[] row = x.split(">");
                        row = row[1].split("<");
                        row = row[0].split(",");
//                        for (int i = 0; i < row.length; i++)
//                        {
//                            System.out.print(row[i] + ", ");
//                        }
//                        System.out.println();
                        rubbleAmounts[index] = row;
                        index++;
                    }
                }
                else
                {
                    if (!done)
                    {
                        if (x.contains("initialRobots"))
                        {
                            done = true;
                        }
                    }
                    else
                    {
                        if (x.contains("initialRobots"))
                        {
                            break;
                        }
                        else
                        {
                            int startIndex = x.indexOf("originOffsetX") + 15;
                            int endIndex = startIndex + 2;
                            if (x.charAt(endIndex-1) < '0' || x.charAt(endIndex-1) > '9')
                            {
                                endIndex--;
                            }

//                            System.out.println(x);
//                            System.out.println(x.substring(startIndex, endIndex));
                            int unit_x = Integer.parseInt(x.substring(startIndex, endIndex));

                            startIndex = x.indexOf("originOffsetY") + 15;
                            endIndex = startIndex + 2;
                            if (x.charAt(endIndex-1) < '0' || x.charAt(endIndex-1) > '9')
                            {
                                endIndex--;
                            }

                            int unit_y = Integer.parseInt(x.substring(startIndex, endIndex));

                            boolean isTeamA = false;

                            startIndex = x.indexOf("team=") + 6;

                            if (x.substring(startIndex).contains("A"))
                            {
                                isTeamA = true;
                            }

                            if (x.contains("SOLDIER"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "sa";
                                }
                                else
                                {
                                    unitStrings[unit_x][unit_y] = "sb";
                                }
                            }
                        }
                    }
                }
            }

            in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }

        for (int i = 0; i < rubbleAmounts.length; i++)
        {
            for (int j = 0; j < rubbleAmounts[i].length; j++)
            {
                double rubbleAmount = 0;

                for (int k = 0; k < rubbleAmounts[i][j].length(); k++)
                {
                    if (rubbleAmounts[i][j].charAt(k) >= '0' && rubbleAmounts[i][j].charAt(k) <= '9')
                    {
                        rubbleAmount *= 10;
                        rubbleAmount += Integer.parseInt(rubbleAmounts[i][j].charAt(k) + "");// - '0';
                    }
                    else if (rubbleAmounts[i][j].charAt(k) == '.')
                    {
                        break;
                    }
                }

                Team team = null;
                if (unitStrings[i][j].contains("a"))
                {
                    team = Team.A;
                }
                else if (unitStrings[i][j].contains("b"))
                {
                    team = Team.B;
                }


                RobotType robotType = null;

                if (unitStrings[i][j].contains("s"))
                {
                    robotType = RobotType.SOLDIER;
                }

                if (team != null && robotType != null)
                {
                    MockRobotController robotController = new MockRobotController(team, robotType, new MapLocation(i, j), map);

                    // This is the team that we are training
                    MockRobotPlayer robotPlayer;

                    if (team == Team.A)
                    {
                        if (teamA == 1)
                        {
                            robotPlayer = new team044(robotController, weights1);
                        }
                        else
                        {
                            if (RobotType.SOLDIER == robotType)
                            {
                                robotPlayer = new Soldier(robotController, weights1);
                            }
                            else
                            {
                                robotPlayer = new Soldier(robotController, weights1);
                            }
                        }
                    }
                    else
                    {
                        if (teamB == 1)
                        {
                            robotPlayer = new team044(robotController, weights2);
                        }
                        else
                        {
                            robotPlayer = new Soldier(robotController, weights2);
                        }
                    }

                    initialMap[i][j] = new MockMapLocation(i, j, rubbleAmount, robotPlayer);
                }
                else
                {
                    initialMap[i][j] = new MockMapLocation(i, j, rubbleAmount);
                }
            }
        }

        return initialMap;
    }

    public MapLocation getTeamAHQ()
    {
        return this.teamAHQ;
    }

    public MapLocation getTeamBHQ()
    {
        return this.teamBHQ;
    }

    /**
     * This method is used by SenseNearbyRobots
     *
     * @param center
     * @param distanceSquared
     * @return
     */
    public RobotInfo[] getAllRobotsInRange(MapLocation center, int distanceSquared)
    {
        int count = 0;
        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null && current.distanceSquaredTo(center) <= distanceSquared)
                {
                    count++;
                }
            }
        }

        RobotInfo[] robotInfos = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null && current.distanceSquaredTo(center) <= distanceSquared)
                {
                    robotInfos[count] = current.getRobotPlayer().getBotInfo();
                    count++;
                }
            }
        }

        return robotInfos;
    }

    /**
     * This method returns true if a location is occupied and false otherwise
     *
     * @param location
     * @return
     */
    public boolean locationOccupied(MapLocation location)
    {
        if (location.x < 0 || location.x >= mapLayout.length)
        {
            return false;
        }
        if (location.y < 0 || location.y >= mapLayout[location.x].length)
        {
            return false;
        }

        if (mapLayout[location.x][location.y].getRobotPlayer() == null)
        {
            return false;
        }

        return true;
    }


    /**
     * This method takes in a robot type and map location and returns if a unit can traverse it
     *
     * @param location
     * @param robotType
     * @return
     */
    public boolean terranTraversalbe(MapLocation location, RobotType robotType)
    {
        // can't go out of bounds
        if (location.x >= mapLayout.length || location.x < 0)
        {
            return false;
        }

        if (location.y >= mapLayout[location.x].length || location.y < 0)
        {
            return false;
        }

        if (robotType == RobotType.SCOUT)
        {
            return true;
        }

        if (mapLayout[location.x][location.y].getRubble() > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
        {
            return false;
        }

        return true;
    }

    /**
     * This method moves a robot from one location to another
     *
     * @param startLoc
     * @param newLoc
     */
    public void moveRobot(MapLocation startLoc, MapLocation newLoc)
    {
        MockRobotPlayer robotPlayer = mapLayout[startLoc.x][startLoc.y].getRobotPlayer();

        if (robotPlayer == null)
        {
            return;
        }

        mapLayout[startLoc.x][startLoc.y].removeRobotPlayer();

        if (!robotPlayer.removeFromGame())
        {
            mapLayout[newLoc.x][newLoc.y].setRobotPlayer(robotPlayer);
        }
        else
        {
            // record stats for bot
            if (robotPlayer.getRc().getTeam() == Team.A)
            {
                this.redSoldierTotalHealth += robotPlayer.getHealth();
//                this.redSoldierDamageDealt += ((MockRobotController) robotPlayer.getRc()).getTotalDamageDealt();
            }
            else
            {
                this.blueSoldierTotalHealth += robotPlayer.getHealth();
//                this.blueSoldierDamageDealt += ((MockRobotController) robotPlayer.getRc()).getTotalDamageDealt();
            }

//            System.out.println("Player removed from game");
        }
    }

    public void countRedRobot(MockRobotPlayer robotPlayer)
    {
        this.redSoldierTotalHealth += robotPlayer.getHealth();
    }

    public void countBlueRobot(MockRobotPlayer robotPlayer)
    {
        this.blueSoldierTotalHealth += robotPlayer.getHealth();
    }


    public MapLocation getHQLocation(Team team)
    {
        if (team == Team.A)
        {
            return teamAHQ;
        }
        return teamBHQ;
    }

    public void attackLocation(MapLocation loc, double attackAmount)
    {
        MockRobotPlayer player = mapLayout[loc.x][loc.y].getRobotPlayer();

        if (player != null)
        {
            player.takeDamage(attackAmount);
            double damageAmount = attackAmount;

            if (player.getHealth() <= 0)
            {
                damageAmount += player.getHealth();
                mapLayout[loc.x][loc.y].removeRobotPlayer();
            }

            if (player.getRc().getTeam() == Team.A)
            {
                this.blueSoldierDamageDealt += damageAmount;
            }
            else
            {
                this.redSoldierDamageDealt += damageAmount;
            }

        }
    }

    public double getBlueSoldierDamageDealt()
    {
        return this.blueSoldierDamageDealt;
    }

    public double getBlueSoldierTotalHealth()
    {
        return this.blueSoldierTotalHealth;
    }

    public double getRedSoldierDamageDealt()
    {
        return this.redSoldierDamageDealt;
    }

    public double getRedSoldierTotalHealth()
    {
        return this.redSoldierTotalHealth;
    }
}
