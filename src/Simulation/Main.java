package Simulation;

public class Main
{
    static boolean zombie = false;
    static boolean soldier = false;
    static boolean viper = false;
    static boolean guard = false;

    static final int soldierIndex = 0;
    static final int archonIndex = 1;
    static final int viperIndex = 2;
    static final int guardIndex = 3;
    static final int turretIndex = 4;
    static final int ttmIndex = 5;
    static final int scoutIndex = 6;


    static boolean[] units = new boolean[3];

    static final boolean[][] unitCombos = new boolean[][] {
            {
                    true, false, false, false, false, false, false
            },
            {
                    false, true, false, false, false, false, false
            },
            {
                    false, false, true, false, false, false, false
            },
            {
                    false, false, false, true, false, false, false
            },
            {
                    false, false, false, false, false, true, false
            },
            {
                    false, false, false, false, false, false, true
            },
            {
                    true, false, false, true, false, false, false
            },
            {
                    true, false, true, false, false, false, false
            },
            {
                    false, false, false, true, true, false, false
            },
            {
                    true, true, true, true, true, true, true
            },
    };

    /**
     * This is the main function which triggers simulations
     * @param args
     */
    public static void main(String[] args)
    {
        double mutationRate = 0.3;
        double crossOverRate = 0.1;
        double mutationAmount = 0.1;
        boolean verbose = false;
        double globalScale = 0.1;
        double localScale = 0.2;
        double randomScale = 0.3;
        long unitStartTime = 0;
        long unitEndTime = 0;

        //runFightSimulation(gaWeights, gaWeights, 0, 0, true, 0);

        reset();
        System.out.println("Training");

        long startTime = System.currentTimeMillis();
        PSO pso = new PSO(globalScale, localScale, randomScale);
        setPSO(pso);
        double[][][] idealWeights = pso.getInitialPopulation(1, 20);

        double startScore = getResultsForCurrentMaps(idealWeights[0], idealWeights[0], 0, 1)[0];
        double currentScore;
        double newScore;

        // this loop runs over all map training sessions
        for (int i = 0; i < unitCombos.length; i++)
        {
            unitStartTime = System.currentTimeMillis();
            setUnits(unitCombos[i]);
            currentScore = getResultsForCurrentMaps(idealWeights[0], idealWeights[0], 0, 1)[0];
            setPSO(pso);
            idealWeights = pso.getBestFromStartingPop(idealWeights, 1000, 20);
            newScore = getResultsForCurrentMaps(idealWeights[0], idealWeights[0], 0, 1)[0];
            System.out.println("Current Score: " + currentScore + " New Score: " + newScore);
            System.out.println("Round " + i + " of training took:");
            unitEndTime = System.currentTimeMillis();
            printTime(unitStartTime, unitEndTime);

            printCurrentWeights(idealWeights[0]);
        }

        double[][] bestWeights = getBestWeights(idealWeights);
        newScore = getScores(bestWeights, bestWeights, 0, 1)[0];
        System.out.println("Start Score: " + startScore + " End Score: " + newScore);

        System.out.println("The best weights so far");
        printCurrentWeights(bestWeights);

        System.out.println("Training is complete");
        printTime(startTime, System.currentTimeMillis());
    }

    public static void printCurrentWeights(double[][] bestWeights)
    {
        System.out.println("PSO Weights:");

        for (int i = 0; i < bestWeights.length; i++)
        {
            for (int j = 0; j < bestWeights[i].length; j++)
            {
                System.out.print(bestWeights[i][j] + ", ");
            }
            System.out.println();
        }
    }

    public static void printTime(long startTime, long stopTime)
    {
        long diff = stopTime - startTime;
        diff /= 1000;
        long hours = diff / (60 * 60);
        long minutes = (diff % (60 * 60)) / 60;
        long seconds = diff % 60;

        System.out.println("Running Time was " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds");
    }

    /**
     * This method loops over a population and finds the one with the highest scores
     *
     * @param currentPop
     * @return
     */
    public static double[][] getBestWeights(double[][][] currentPop)
    {
        double[][] bestWeights = null;

        double[] scores = new double[currentPop.length];

        for (int i = 0; i < currentPop.length; i++)
        {
            for (int j = 0; j < currentPop.length; j++)
            {
                if (i != j)
                {
                    double[] currentScores = getScores(currentPop[i], currentPop[j], 0, 0);
                    scores[i] += currentScores[0];
                    scores[j] += currentScores[1];
                }
            }
        }

        double bestScore = 0;

        for (int i = 0; i < scores.length; i++)
        {
            if (bestScore < scores[i])
            {
                bestScore = scores[i];
                bestWeights = currentPop[i];
            }
        }

        return bestWeights;
    }

    /**
     * This method returns the total score for two teams after running them on every map
     *
     * @param weights1
     * @param weights2
     * @param teamA
     * @param teamB
     * @return
     */
    public static double[] getScores(double[][] weights1, double[][] weights2, int teamA, int teamB)
    {
        double[] results = new double[2];
        double[][][] currentResults = new double[80][2][7];
        int index = 0;

        for (int j = 0; j < unitCombos.length; j++)
        {
            setUnits(unitCombos[j]);

            for (int i = 0; i < 4; i++)
            {
                currentResults[index] = runFightSimulation(weights1, weights2, teamA, teamB, false, i);
                index++;
                currentResults[index] = runFightSimulation(weights2, weights1, teamB, teamA, false, i);
                index++;
            }
        }

        for (int i = 0; i < currentResults.length; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 7; j++)
                {
                    results[0] += currentResults[i][0][j];
                    results[1] += currentResults[i][1][j];
                }
            }
            else
            {
                for (int j = 0; j < 7; j++)
                {
                    results[0] += currentResults[i][1][j];
                    results[1] += currentResults[i][0][j];
                }
            }
        }

        return results;
    }

    /**
     * This method gets the results for running on just the current set maps
     *
     * @param weights1
     * @param weights2
     * @param teamA
     * @param teamB
     * @return
     */
    public static double[] getResultsForCurrentMaps(double[][] weights1, double[][] weights2, int teamA, int teamB)
    {
        double[] results = new double[2];
        double[][][] currentResults = new double[80][2][7];
        int index = 0;

        for (int i = 0; i < 4; i++)
        {
            currentResults[index] = runFightSimulation(weights1, weights2, teamA, teamB, false, i);
            index++;
            currentResults[index] = runFightSimulation(weights2, weights1, teamB, teamA, false, i);
            index++;
        }

        for (int i = 0; i < currentResults.length; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 7; j++)
                {
                    results[0] += currentResults[i][0][j];
                    results[1] += currentResults[i][1][j];
                }
            }
            else
            {
                for (int j = 0; j < 7; j++)
                {
                    results[0] += currentResults[i][1][j];
                    results[1] += currentResults[i][0][j];
                }
            }
        }

        return results;
    }

    /**
     * This method sets the current unit run types to pso
     *
     * @param pso
     */
    public static void setPSO(PSO pso)
    {
        pso.setRunSoldier(soldier);
        pso.setRunGuard(guard);
//        pso.setRunArchon(archon);
//        pso.setRunScout(scout);
        pso.setRunViper(viper);
//        pso.setRunTTM(ttm);
//        pso.setRunTurret(turret);
    }

    /**
     * This method sets all run types to false
     */
    public static void reset()
    {
        soldier = false;
        viper = false;
        guard = false;
    }

    public static void setUnits(boolean[] units)
    {
        soldier = units[soldierIndex];
        viper = units[viperIndex];
        guard = units[guardIndex];
    }

    /**
     * This method returns an array with the total fitness values of all units
     * for both teams in the form
     *
     * [
     *      Team 1: [
     *                  Soldiers: [
     *
     *                             ]
     *                   Tanks: [
     *
     *                              ]
     *                        etc...
     *              ]
     *
     *      Team 2: [
     *
     *              ]
     * ]
     *
     *
     * @param team1Inputs
     * @param team2Inputs
     * @return
     */
    public static double[][] runFightSimulation(double[][] team1Inputs, double[][] team2Inputs, int teamA, int teamB, boolean verbose, int round)
    {
        if (verbose)
        {
            System.out.println("Simulating a match");
        }

        Game game = new Game(team1Inputs, team2Inputs, verbose, zombie);

        // Battlecode2016_Simulation/src/Simulation
        String map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest1.xml";

        if (zombie)
        {
            if (soldier && viper && guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/AllZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/AllZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/AllZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/AllZombie4.xml";
                }
            }
            else if (soldier && guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuardZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuardZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuardZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuardZombie4.xml";
                }
            }
            else if (soldier && viper)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViperZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViperZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViperZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViperZombie4.xml";
                }
            }
            else if (guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTurretZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTurretZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTurretZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTurretZombie4.xml";
                }
            }
            else if (soldier)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierZombie4.xml";
                }
            }
            else if (viper)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/ViperZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/ViperZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/ViperZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/ViperZombie4.xml";
                }
            }
            else if (guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardZombie1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardZombie2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardZombie3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardZombie4.xml";
                }
            }
        }
        else
        {
            if (soldier && viper && guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/All1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/All2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/All3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/All4.xml";
                }
            }
            else if (soldier && guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGaurd1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuard2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuard3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierGuard4.xml";
                }
            }
            else if (soldier && viper)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViper1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViper2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViper3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierViper4.xml";
                }
            }
            else if (soldier)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest4.xml";
                }
            }
            else if (viper)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/Viper1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/Viper2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/Viper3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/Viper4.xml";
                }
            }
            else if (guard)
            {
                if (round % 4 == 0)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTest1.xml";
                }
                else if (round % 4 == 1)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTest2.xml";
                }
                else if (round % 4 == 2)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTest3.xml";
                }
                else if (round % 4 == 3)
                {
                    map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/GuardTest4.xml";
                }
            }
        }

        game.runMatch(map, teamA, teamB);

        double[][] results = new double[2][];

        results[0] = game.getTeamResults(0);
        results[1] = game.getTeamResults(1);

        return results;
    }
}
