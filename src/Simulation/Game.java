package Simulation;

import battlecode.common.Team;

public class Game
{
    private double[][] team1Inputs;
    private double[][] team2Inputs;
    private Map map;
    private boolean verbose;

    public Game(double[][] team1Inputs, double[][] team2Inputs, boolean verbose)
    {
        this.team1Inputs = team1Inputs;
        this.team2Inputs = team2Inputs;
        map = new Map(team1Inputs, team2Inputs, verbose);
        this.verbose = verbose;
    }

    public void println(String string)
    {
        if (this.verbose)
        {
            System.out.println(string);
        }
    }

    public void print(String string)
    {
        if (this.verbose)
        {
            System.out.print(string);
        }
    }

    public void runMatch(String MapName, int teamA, int teamB)
    {
        map.readInMap(MapName, map, teamA, teamB);

        MockRobotPlayer[] robotPlayers;

        //map.print();

        int roundLimit = 200;

        for (int i = 0; i < roundLimit; i++)
        {
            robotPlayers = map.getRobotPlayers();
            boolean allZombies = true;

            for (int j = 0; j < robotPlayers.length; j++)
            {
                robotPlayers[j].run();
                robotPlayers[j].runTurnEnd();

                if (allZombies && robotPlayers[j].getRc().getTeam() != Team.ZOMBIE)
                {
                    allZombies = false;
                }
            }

            if (robotPlayers.length == 0 || allZombies)
            {
                break;
            }

            if (i % 5 == 0)
            {
//                map.print();
            }

            if (i == (roundLimit - 1))
            {
                break;
            }
        }

        if (verbose)
        {
            map.printRedDamage();
            map.printRedHealth();
            map.printBlueDamage();
            map.printBlueHealth();
            map.printZombie();
        }

//        map.print();
    }

    public double[] getTeamResults(int team)
    {
        double[] results = new double[7];

        double[] redDamageDealt = map.getRedDamageDealt();
        double[] redTotalHealth = map.getRedTotalHealth();
        double[] blueDamageDealt = map.getBlueDamageDealt();
        double[] blueTotalHealth = map.getBlueTotalHealth();
        double[] redInfectedAmount = map.getRedInfectedAmount();
        double[] blueInfectedAmount = map.getBlueInfectedAmount();

        // 0 -> Soldier
        // 1 -> Archon
        // 2 -> Guard
        // 3 -> Scout
        // 4 -> Turret
        // 5 -> TTM
        // 6 -> Viper
        if (team == 0)
        {
            results[0] = redDamageDealt[0] + 3 * redTotalHealth[0] - 2 * redInfectedAmount[0];
            results[1] = redTotalHealth[5];
            results[2] = 2 * redDamageDealt[1] + redTotalHealth[1] - 2 * redInfectedAmount[1];
            results[3] = redTotalHealth[4];
            results[4] = 3 * redDamageDealt[2] + redTotalHealth[2];
            results[5] = redTotalHealth[6];
            results[6] = 2 * redDamageDealt[3] + redTotalHealth[3] - 2 * redInfectedAmount[2];
        }
        else
        {
            results[0] = blueDamageDealt[0] + 3 * blueTotalHealth[0] - 2 * blueInfectedAmount[0];
            results[1] = blueTotalHealth[5];
            results[2] = 2 * blueDamageDealt[1] + blueTotalHealth[1] - 2 * blueInfectedAmount[1];
            results[3] = blueTotalHealth[4];
            results[4] = 3 * blueDamageDealt[2] + blueTotalHealth[2];
            results[5] = redTotalHealth[6];
            results[6] = 2 * blueDamageDealt[3] + blueTotalHealth[3] - 2 * blueInfectedAmount[2];
        }

        return results;
    }
}
