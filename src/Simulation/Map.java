package Simulation;

import Simulation.Teams.*;
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
    private double blueGaurdDamageDealt;
    private double blueGaurdTotalHealth;
    private double blueTurretDamageDealt;
    private double blueTurretTotalHealth;
    private double blueViperDamageDealt;
    private double blueViperTotalHealth;
    private double blueTTMTotalHealth;
    private double blueScoutTotalHealth;
    private double blueArchonTotalHealth;

    private double redSoldierDamageDealt;
    private double redSoldierTotalHealth;
    private double redGaurdDamageDealt;
    private double redGaurdTotalHealth;
    private double redTurretDamageDealt;
    private double redTurretTotalHealth;
    private double redViperDamageDealt;
    private double redViperTotalHealth;
    private double redTTMTotalHealth;
    private double redScoutTotalHealth;
    private double redArchonTotalHealth;

    private double totalZombieDamage;
    private double totalZombieHealth;

    private double redSoldierInfectedAmount;
    private double redGaurdInfectedAmount;
    private double redViperInfectedAmount;

    private double blueSoldierInfectedAmount;
    private double blueGaurdInfectedAmount;
    private double blueViperInfectedAmount;

    private double redViperInfectionDamage;
    private double blueViperInfectionDamage;

    private int redRepairAmount;
    private int blueRepairAmount;

    public Map(double[][] weights1, double[][] weights2, boolean verbose)
    {
        this.weights1 = weights1;
        this.weights2 = weights2;

        this.blueSoldierDamageDealt = 0;
        this.blueSoldierTotalHealth = 0;
        this.redSoldierDamageDealt = 0;
        this.redSoldierTotalHealth = 0;
        this.blueGaurdDamageDealt = 0;
        this.blueGaurdTotalHealth = 0;
        this.redGaurdDamageDealt = 0;
        this.redGaurdTotalHealth = 0;
        this.blueTurretDamageDealt = 0;
        this.blueTTMTotalHealth = 0;
        this.blueViperDamageDealt = 0;
        this.blueViperTotalHealth = 0;
        this.blueTTMTotalHealth = 0;
        this.blueScoutTotalHealth = 0;
        this.blueArchonTotalHealth = 0;
        this.redTurretDamageDealt = 0;
        this.redTTMTotalHealth = 0;
        this.redViperDamageDealt = 0;
        this.redViperTotalHealth = 0;
        this.redTTMTotalHealth = 0;
        this.redScoutTotalHealth = 0;
        this.redArchonTotalHealth = 0;
        this.totalZombieDamage = 0;
        this.totalZombieHealth = 0;

        this.redSoldierInfectedAmount = 0;
        this.redGaurdInfectedAmount = 0;
        this.redViperInfectedAmount = 0;
        this.blueSoldierInfectedAmount = 0;
        this.blueGaurdInfectedAmount = 0;
        this.blueViperInfectedAmount = 0;

        this.redViperInfectionDamage = 0;
        this.blueViperInfectionDamage = 0;

        this.redRepairAmount = 0;
        this.blueRepairAmount = 0;

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
                } else if (i == teamBHQ.x && j == teamBHQ.y)
                {
                    location += 'B';
                } else
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
    public void readInMap(String mapName, Map map, int teamA, int teamB, boolean zombie)
    {
        int[] mapDimensions = getMapWidthHeight(mapName);

        mapLayout = getInitialMap(mapName, mapDimensions, map, teamA, teamB, zombie);
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
        } catch (IOException e)
        {
            System.out.println(e);
        }


        return new int[]{height, width};
    }

    /**
     * This method will return a two d array of the map terrain
     *
     * @param mapName
     * @return
     */
    private MockMapLocation[][] getInitialMap(String mapName, int[] mapDimensions, Map map, int teamA, int teamB, boolean zombie)
    {
        MockMapLocation[][] initialMap = new MockMapLocation[mapDimensions[0]][mapDimensions[1]];

        if (zombie)
        {
            teamAHQ = new MapLocation(0, mapDimensions[1] - 1);
            teamBHQ = new MapLocation(mapDimensions[0] - 1, 0);
        }
        else
        {
            teamBHQ = new MapLocation(0, mapDimensions[1] - 1);
            teamAHQ = new MapLocation(mapDimensions[0] - 1, 0);
        }

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
                    } else
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
                } else
                {
                    if (!done)
                    {
                        if (x.contains("initialRobots"))
                        {
                            done = true;
                        }
                    } else
                    {
                        if (x.contains("initialRobots"))
                        {
                            break;
                        } else
                        {
                            int startIndex = x.indexOf("originOffsetX") + 15;
                            int endIndex = startIndex + 2;
                            if (x.charAt(endIndex - 1) < '0' || x.charAt(endIndex - 1) > '9')
                            {
                                endIndex--;
                            }

//                            System.out.println(x);
//                            System.out.println(x.substring(startIndex, endIndex));
                            int unit_x = Integer.parseInt(x.substring(startIndex, endIndex));

                            startIndex = x.indexOf("originOffsetY") + 15;
                            endIndex = startIndex + 2;
                            if (x.charAt(endIndex - 1) < '0' || x.charAt(endIndex - 1) > '9')
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
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "sb";
                                }
                            } else if (x.contains("GUARD"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "ga";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "gb";
                                }
                            } else if (x.contains("VIPER"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "va";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "vb";
                                }
                            } else if (x.contains("TURRET"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "ta";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "tb";
                                }
                            } else if (x.contains("ARCHON"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "ra";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "rb";
                                }
                            } else if (x.contains("TTM"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "ma";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "mb";
                                }
                            } else if (x.contains("SCOUT"))
                            {
                                if (isTeamA)
                                {
                                    unitStrings[unit_x][unit_y] = "ca";
                                } else
                                {
                                    unitStrings[unit_x][unit_y] = "cb";
                                }
                            }
                            else if (x.contains("STANDARDZOMBIE"))
                            {
                                unitStrings[unit_x][unit_y] = "zs";
                            }
                            else if (x.contains("RANGEDZOMBIE"))
                            {
                                unitStrings[unit_x][unit_y] = "zr";
                            }
                            else if (x.contains("FASTZOMBIE"))
                            {
                                unitStrings[unit_x][unit_y] = "zf";
                            }
                            else if (x.contains("BIGZOMBIE"))
                            {
                                unitStrings[unit_x][unit_y] = "zb";
                            }
                        }
                    }
                }
            }

            in.close();
        } catch (IOException e)
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
                    } else if (rubbleAmounts[i][j].charAt(k) == '.')
                    {
                        break;
                    }
                }

                Team team = null;
                if (unitStrings[i][j].contains("a"))
                {
                    team = Team.A;
                } else if (unitStrings[i][j].contains("b"))
                {
                    team = Team.B;
                }


                RobotType robotType = null;

                if (unitStrings[i][j].contains("zs"))
                {
                    team = Team.ZOMBIE;
                    robotType = RobotType.STANDARDZOMBIE;
                }
                else if (unitStrings[i][j].contains("zr"))
                {
                    team = Team.ZOMBIE;
                    robotType = RobotType.RANGEDZOMBIE;
                }
                else if (unitStrings[i][j].contains("zf"))
                {
                    team = Team.ZOMBIE;
                    robotType = RobotType.FASTZOMBIE;
                }
                else if (unitStrings[i][j].contains("zb"))
                {
                    team = Team.ZOMBIE;
                    robotType = RobotType.BIGZOMBIE;
                }
                else if (unitStrings[i][j].contains("s"))
                {
                    robotType = RobotType.SOLDIER;

                    if (team == Team.A)
                        redSoldierTotalHealth += RobotType.SOLDIER.maxHealth;
                    else
                        blueSoldierTotalHealth += RobotType.SOLDIER.maxHealth;
                }
                else if (unitStrings[i][j].contains("r"))
                {
                    robotType = RobotType.ARCHON;

                    if (team == Team.A)
                        redArchonTotalHealth += RobotType.ARCHON.maxHealth;
                    else
                        blueArchonTotalHealth += RobotType.ARCHON.maxHealth;
                }
                else if (unitStrings[i][j].contains("t"))
                {
                    robotType = RobotType.TURRET;

                    if (team == Team.A)
                        redTurretTotalHealth += RobotType.TURRET.maxHealth;
                    else
                        blueTurretTotalHealth += RobotType.TURRET.maxHealth;
                }
                else if (unitStrings[i][j].contains("m"))
                {
                    robotType = RobotType.TTM;

                    if (team == Team.A)
                        redTTMTotalHealth += RobotType.TTM.maxHealth;
                    else
                        blueTTMTotalHealth += RobotType.TTM.maxHealth;
                }
                else if (unitStrings[i][j].contains("c"))
                {
                    robotType = RobotType.SCOUT;

                    if (team == Team.A)
                        redScoutTotalHealth += RobotType.SCOUT.maxHealth;
                    else
                        blueScoutTotalHealth += RobotType.SCOUT.maxHealth;
                }
                else if (unitStrings[i][j].contains("g"))
                {
                    robotType = RobotType.GUARD;

                    if (team == Team.A)
                        redGaurdTotalHealth += RobotType.GUARD.maxHealth;
                    else
                        blueGaurdTotalHealth += RobotType.GUARD.maxHealth;
                }
                else if (unitStrings[i][j].contains("v"))
                {
                    robotType = RobotType.VIPER;

                    if (team == Team.A)
                        redViperTotalHealth += RobotType.VIPER.maxHealth;
                    else
                        blueViperTotalHealth += RobotType.VIPER.maxHealth;
                }

                if (team != null && robotType != null)
                {
                    MockRobotController robotController = new MockRobotController(team, robotType, new MapLocation(i, j), map);

                    // This is the team that we are training
                    MockRobotPlayer robotPlayer;

                    if (team == Team.ZOMBIE)
                    {
                        robotPlayer = new Zombie(robotController, weights1);
                        totalZombieHealth += robotController.getHealth();
                    }
                    else if (team == Team.A)
                    {
                        if (teamA == 1)
                        {
                            robotPlayer = new team044(robotController, weights1);
                        } else
                        {
                            if (RobotType.SOLDIER == robotType)
                            {
                                robotPlayer = new Soldier(robotController, weights1);
                            } else if (robotType == RobotType.ARCHON)
                            {
                                robotPlayer = new Archon(robotController, weights1);
                            } else if (robotType == RobotType.SCOUT)
                            {
                                robotPlayer = new Scout(robotController, weights1);
                            } else if (robotType == RobotType.GUARD)
                            {
                                robotPlayer = new Gaurd(robotController, weights1);
                            } else if (robotType == RobotType.VIPER)
                            {
                                robotPlayer = new Viper(robotController, weights1);
                            } else if (robotType == RobotType.TURRET)
                            {
                                robotPlayer = new Turret(robotController, weights1);
                            } else if (robotType == RobotType.TTM)
                            {
                                robotPlayer = new TTM(robotController, weights1);
                            } else
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
                        } else
                        {
                            if (RobotType.SOLDIER == robotType)
                            {
                                robotPlayer = new Soldier(robotController, weights2);
                            } else if (robotType == RobotType.ARCHON)
                            {
                                robotPlayer = new Archon(robotController, weights2);
                            } else if (robotType == RobotType.SCOUT)
                            {
                                robotPlayer = new Scout(robotController, weights2);
                            } else if (robotType == RobotType.GUARD)
                            {
                                robotPlayer = new Gaurd(robotController, weights2);
                            } else if (robotType == RobotType.VIPER)
                            {
                                robotPlayer = new Viper(robotController, weights2);
                            } else if (robotType == RobotType.TURRET)
                            {
                                robotPlayer = new Turret(robotController, weights2);
                            } else if (robotType == RobotType.TTM)
                            {
                                robotPlayer = new TTM(robotController, weights2);
                            } else
                            {
                                robotPlayer = new Soldier(robotController, weights2);
                            }

                        }
                    }

                    initialMap[i][j] = new MockMapLocation(i, j, rubbleAmount, robotPlayer);
                } else
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
    }

    public void clearRubble(double amount, MapLocation loc)
    {
        mapLayout[loc.x][loc.y].rubble -= amount;
    }

    public double getRubble(MapLocation loc)
    {
        return mapLayout[loc.x][loc.y].getRubble();
    }

    public void repair(MapLocation loc)
    {
        MockRobotPlayer mockRobotPlayer = mapLayout[loc.x][loc.y].getRobotPlayer();

        if (mockRobotPlayer != null)
        {
            mockRobotPlayer.repair();

            if (mapLayout[loc.x][loc.y].getRobotPlayer().getRc().getTeam() == Team.A)
            {
                this.redRepairAmount++;
            }
            else
            {
                this.blueRepairAmount++;
            }
        }
    }

    public MapLocation getHQLocation(Team team)
    {
        if (team == Team.A)
        {
            return teamAHQ;
        }
        return teamBHQ;
    }

    public void unitDiedToViper(MapLocation loc, RobotType type)
    {
        mapLayout[loc.x][loc.y].removeRobotPlayer();
        if (type.turnsInto != null)
        {
            MockRobotController rc = new MockRobotController(Team.ZOMBIE, type.turnsInto, loc, this);
            MockRobotPlayer mockRobotPlayer = new Zombie(rc, weights1);
            this.totalZombieHealth += rc.getHealth();
            mapLayout[loc.x][loc.y].setRobotPlayer(mockRobotPlayer);
        }
    }

    public void attackLocation(MapLocation loc, double attackAmount, MapLocation attackFrom)
    {
        MockRobotPlayer player = mapLayout[loc.x][loc.y].getRobotPlayer();
        MockRobotPlayer attacker = mapLayout[attackFrom.x][attackFrom.y].getRobotPlayer();

        if (player != null && attacker != null)
        {
            MockRobotController playerRC = (MockRobotController) player.getRc();
            MockRobotController attackerRC = (MockRobotController) attacker.getRc();
            Team playerTeam = playerRC.getTeam();
            Team attackerTeam = attackerRC.getTeam();
            RobotType attackerType = attackerRC.getType();
            RobotType playerType = playerRC.getType();

            if (attackerType == RobotType.VIPER)
            {
                if (attackerTeam == Team.A)
                    this.redViperInfectionDamage += (20 - playerRC.getViperInfectedTurns()) * 2;
                else
                    this.blueViperInfectionDamage += (20 - playerRC.getViperInfectedTurns()) * 2;

                playerRC.infectedByViper(20);
            }
            else if (attackerTeam == Team.ZOMBIE)
            {
                playerRC.infectedByZombie(10);
            }

            player.takeDamage(attackAmount);
            double damageAmount = attackAmount;

            if (playerTeam == Team.ZOMBIE && attackerType == RobotType.GUARD)
            {
                player.takeDamage(attackAmount);
                damageAmount *= 2;
            }

            if (player.getHealth() <= 0)
            {
                damageAmount += player.getHealth();
                mapLayout[loc.x][loc.y].removeRobotPlayer();
                if (playerTeam != Team.ZOMBIE && playerRC.isInfected())
                {
                    MockRobotController mockRobotController = new MockRobotController(Team.ZOMBIE, playerType.turnsInto, loc, this);
                    MockRobotPlayer zombie = new Zombie(mockRobotController, weights1);
                    this.totalZombieHealth += mockRobotController.getHealth();
                    mapLayout[loc.x][loc.y].setRobotPlayer(zombie);

                    if (playerTeam == Team.A)
                    {
                        if (playerType == RobotType.SOLDIER)
                            this.redSoldierInfectedAmount += mockRobotController.getHealth();
                        else if (playerType == RobotType.GUARD)
                            this.redGaurdInfectedAmount += mockRobotController.getHealth();
                        else if (playerType == RobotType.VIPER)
                            this.redViperInfectedAmount += mockRobotController.getHealth();
                    }
                    else
                    {
                        if (playerType == RobotType.SOLDIER)
                            this.blueSoldierInfectedAmount += mockRobotController.getHealth();
                        else if (playerType == RobotType.GUARD)
                            this.blueGaurdInfectedAmount += mockRobotController.getHealth();
                        else if (playerType == RobotType.VIPER)
                            this.blueViperInfectedAmount += mockRobotController.getHealth();
                    }
                }
            }

            if (playerTeam == Team.A)
            {
                switch (playerType)
                {
                    case SOLDIER:
                        this.redSoldierTotalHealth -= damageAmount;
                        break;
                    case GUARD:
                        this.redGaurdTotalHealth -= damageAmount;
                        break;
                    case TURRET:
                        this.redTurretTotalHealth -= damageAmount;
                        break;
                    case TTM:
                        this.redTTMTotalHealth -= damageAmount;
                        break;
                    case SCOUT:
                        this.redScoutTotalHealth -= damageAmount;
                        break;
                    case VIPER:
                        this.redViperTotalHealth -= damageAmount;
                        break;
                    case ARCHON:
                        this.redArchonTotalHealth -= damageAmount;
                        break;

                }

                switch (attackerType)
                {
                    case SOLDIER:
                        this.blueSoldierDamageDealt += damageAmount;
                        break;
                    case GUARD:
                        this.blueGaurdDamageDealt += damageAmount;
                        break;
                    case TURRET:
                        this.blueTurretDamageDealt += damageAmount;
                        break;
                    case VIPER:
                        this.blueViperDamageDealt += damageAmount;
                        break;
                    default:
                        this.totalZombieDamage += damageAmount;
                }
            }
            else if (playerTeam == Team.B)
            {
                switch (playerType)
                {
                    case SOLDIER:
                        this.blueSoldierTotalHealth -= damageAmount;
                        break;
                    case GUARD:
                        this.blueGaurdTotalHealth -= damageAmount;
                        break;
                    case TURRET:
                        this.blueTurretTotalHealth -= damageAmount;
                        break;
                    case TTM:
                        this.blueTTMTotalHealth -= damageAmount;
                        break;
                    case SCOUT:
                        this.blueScoutTotalHealth -= damageAmount;
                        break;
                    case VIPER:
                        this.blueViperTotalHealth -= damageAmount;
                        break;
                    case ARCHON:
                        this.blueArchonTotalHealth -= damageAmount;
                        break;

                }

                switch (attackerType)
                {
                    case SOLDIER:
                        this.redSoldierDamageDealt += damageAmount;
                        break;
                    case GUARD:
                        this.redGaurdDamageDealt += damageAmount;
                        break;
                    case TURRET:
                        this.redTurretDamageDealt += damageAmount;
                        break;
                    case VIPER:
                        this.redViperDamageDealt += damageAmount;
                        break;
                    default:
                        this.totalZombieDamage += damageAmount;
                }
            }
            else if (playerTeam == Team.ZOMBIE)
            {
                this.totalZombieHealth -= damageAmount;
                if (attackerTeam == Team.A)
                {
                    switch (attackerType)
                    {
                        case SOLDIER:
                            this.redSoldierDamageDealt += damageAmount;
                            break;
                        case GUARD:
                            this.redGaurdDamageDealt += damageAmount;
                            break;
                        case TURRET:
                            this.redTurretDamageDealt += damageAmount;
                            break;
                        case VIPER:
                            this.redViperDamageDealt += damageAmount;
                            break;
                    }
                }
                else if (attackerTeam == Team.B)
                {
                    switch (attackerType)
                    {
                        case SOLDIER:
                            this.blueSoldierDamageDealt += damageAmount;
                            break;
                        case GUARD:
                            this.blueGaurdDamageDealt += damageAmount;
                            break;
                        case TURRET:
                            this.blueTurretDamageDealt += damageAmount;
                            break;
                        case VIPER:
                            this.blueViperDamageDealt += damageAmount;
                            break;
                        default:
                            this.totalZombieDamage += damageAmount;
                    }
                }
            }
        }
    }

    public double[] getBlueDamageDealt()
    {
        return new double[]{
                this.blueSoldierDamageDealt, this.blueGaurdDamageDealt, this.blueTurretDamageDealt, this.blueViperDamageDealt
        };
    }

    public double[] getBlueTotalHealth()
    {
        return new double[]{
                this.blueSoldierTotalHealth, this.blueGaurdTotalHealth, this.blueTurretTotalHealth, this.blueViperTotalHealth, this.blueScoutTotalHealth, this.blueArchonTotalHealth, this.blueTTMTotalHealth
        };
    }

    public double[] getRedDamageDealt()
    {
        return new double[] {
                this.redSoldierDamageDealt, this.redGaurdDamageDealt, this.redTurretDamageDealt, this.redViperDamageDealt
        };
    }

    public double[] getRedTotalHealth()
    {
        return new double[] {
                this.redSoldierTotalHealth, this.redGaurdTotalHealth, this.redTurretTotalHealth, this.redViperTotalHealth, this.redScoutTotalHealth, this.redArchonTotalHealth, this.redTTMTotalHealth
        };
    }

    public double[] getRedInfectedAmount()
    {
        return new double[] {
                this.redSoldierInfectedAmount, this.redGaurdInfectedAmount, this.redViperInfectedAmount
        };
    }

    public double[] getBlueInfectedAmount()
    {
        return new double[] {
                this.blueSoldierInfectedAmount, this.blueGaurdInfectedAmount, this.blueViperInfectedAmount
        };
    }

    public double getRedViperInfectionDamage()
    {
        return this.redViperInfectionDamage;
    }

    public double getBlueViperInfectionDamage()
    {
        return this.blueViperInfectionDamage;
    }

    public double getRedRepairAmount()
    {
        return this.redRepairAmount;
    }

    public double getBlueRepairAmount()
    {
        return this.blueRepairAmount;
    }

    public void printRedDamage()
    {
        System.out.print("Red Soldier Damage: " + this.redSoldierDamageDealt);
        System.out.print(" Red Guard Damage: " + this.redGaurdDamageDealt);
        System.out.print(" Red Turret Damage: " + this.redTurretDamageDealt);
        System.out.print(" Red Viper Damage: " + this.redViperDamageDealt);
        System.out.println();
        System.out.print("Red Soldier Infected Amount: " + this.redSoldierInfectedAmount);
        System.out.print(" Red Guard Infected Amount: " + this.redGaurdInfectedAmount);
        System.out.print(" Red Viper Infected Amount: " + this.redViperInfectedAmount);
        System.out.println();
    }

    public void printRedHealth()
    {
        System.out.print("Red Soldier Health: " + this.redSoldierTotalHealth);
        System.out.print(" Red Guard Health: " + this.redGaurdTotalHealth);
        System.out.print(" Red Turret Health: " + this.redTurretTotalHealth);
        System.out.print(" Red Viper Health: " + this.redViperTotalHealth);
        System.out.print(" Red Scout Health: " + this.redScoutTotalHealth);
        System.out.print(" Red Archon Health: " + this.redArchonTotalHealth);
        System.out.print(" Red TTM Health: " + this.redTTMTotalHealth);
        System.out.print(" Red Repair Amount: " + this.redRepairAmount);
        System.out.println();
    }

    public void printBlueDamage()
    {
        System.out.print("Blue Soldier Damage: " + this.blueSoldierDamageDealt);
        System.out.print(" Blue Guard Damage: " + this.blueGaurdDamageDealt);
        System.out.print(" Blue Turret Damage: " + this.blueTurretDamageDealt);
        System.out.print(" Blue Viper Damage: " + this.blueViperDamageDealt);
        System.out.println();
        System.out.print("Blue Soldier Infected Amount: " + this.blueSoldierInfectedAmount);
        System.out.print(" Blue Guard Infected Amount: " + this.blueGaurdInfectedAmount);
        System.out.print(" Blue Viper Infected Amount: " + this.blueViperInfectedAmount);
        System.out.println();
    }

    public void printBlueHealth()
    {
        System.out.print("blue Soldier Health: " + this.blueSoldierTotalHealth);
        System.out.print(" blue Guard Health: " + this.blueGaurdTotalHealth);
        System.out.print(" blue Turret Health: " + this.blueTurretTotalHealth);
        System.out.print(" blue Viper Health: " + this.blueViperTotalHealth);
        System.out.print(" blue Scout Health: " + this.blueScoutTotalHealth);
        System.out.print(" blue Archon Health: " + this.blueArchonTotalHealth);
        System.out.print(" blue TTM Health: " + this.blueTTMTotalHealth);
        System.out.print(" blue Repair Amount: " + this.blueRepairAmount);
        System.out.println();
    }

    public void printZombie()
    {
        System.out.print("Zombie Health: " + this.totalZombieHealth);
        System.out.print(" Zombie Damage: " + this.totalZombieDamage);
        System.out.println();
    }
}
