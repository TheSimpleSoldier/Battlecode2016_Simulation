package Simulation;

public class PSO
{
    private double globalScale;
    private double localScale;
    private double randomScale;
    private FeedForwardNeuralNetwork net;
    private double[][] globalBest = null;

    private boolean runSoldier = false;
    private boolean runArchon = false;
    private boolean runGuard = false;
    private boolean runViper = false;
    private boolean runScout = false;
    private boolean runTTM = false;
    private boolean runTurret = false;

    public PSO(double globalScale, double localScale, double randomScale)
    {
        this.globalScale = globalScale;
        this.localScale = localScale;
        this.randomScale = randomScale;

        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
    }

    public double[][][] getBestFromStartingPop(double[][][] currentWeights, int rounds, int popSize)
    {
        double[][][] localBestWeights = new double[popSize][7][currentWeights[0][0].length];
        double[][] localBestScores = new double[popSize][7];
        double[] globalBestScore = new double[7];
        double[][] currentFitness = new double[popSize][7];
        double[][][] originalWeights = new double[popSize][7][currentWeights[0][0].length];



        int map = -1;

        for (int i = 0; i < rounds; i++)
        {
            if (i % (rounds / 4) == 0)
            {
                map++;

                // reset global and local best for new map
                for (int k = 0; k < popSize; k++)
                {
                    for (int j = 0; j < 7; j++)
                    {
                        for (int l = 0; l < currentWeights[k][j].length; l++)
                        {
                            if (i == 0)
                                localBestWeights[k][j][l] = currentWeights[k][j][l];
                            else
                                currentWeights[k][j][l] = localBestWeights[k][j][l];
                        }

                        if (i == 0)
                        {
                            localBestScores[k][j] = 0;
                            globalBestScore[j] = 0;
                        }
                    }
                }

                if (i == 0)
                {
                    globalBest = currentWeights[0];
                }
            }

            if (i % 50 != 0)
            {
                // first update all of the particles
                for (int j = 0; j < popSize; j++)
                {
                    currentWeights[j] = updateParticle(currentWeights[j], localBestWeights[j], globalBest);
                }
            }

            // update all of the scores for global and local best
            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < popSize; k++)
                {
                    // run match and record scores
                    if (j != k)
                    {
                        double[][] results = Main.runFightSimulation(currentWeights[j], currentWeights[k], 0, 0, false, map);


                            for (int l = 0; l < 7; l++)
                            {
                                if (l == 0 && runSoldier || l == 1 && runArchon || l == 2 && runGuard || l == 3 && runScout
                                        || l == 4 && runTurret || l == 5 && runTTM || l == 6 && runViper)
                                {
                                    currentFitness[j][l] += results[0][l];
                                    currentFitness[k][l] += results[1][l];
                                }
                            }

                    }
                }
            }

            // loop over scores and update local and global best as necessary
            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < 7; k++)
                {
                    if ((k == 0 && runSoldier) || (k == 1 && runArchon) || (k == 2 && runGuard) || (k == 3 && runScout)
                            || (k == 4 && runTurret) || (k == 5 && runTTM) || (k == 6 && runViper))
                    {
                        if (currentFitness[j][k] > localBestScores[j][k])
                        {
                            localBestScores[j][k] = currentFitness[j][k];
                            localBestWeights[j][k] = currentWeights[j][k];
                        }
                        if (currentFitness[j][k] > globalBestScore[k])
                        {
                            globalBestScore[k] = currentFitness[j][k];
                            globalBest[k] = currentWeights[j][k];
                        }
                    }
                }
            }
//            System.out.println("Finished round " + i + " of PSO");
        }

//        for (int i = 0; i < localBestWeights.length; i++)
//        {
//            if (!runSoldier)
//                localBestWeights[i][0] = currentWeights[i][0];
//            if (!runArchon)
//                localBestWeights[i][1] = currentWeights[i][1];
//            if (!runGuard)
//                localBestWeights[i][2] = currentWeights[i][2];
//            if (!runScout)
//                localBestWeights[i][3] = currentWeights[i][3];
//            if (!runTurret)
//                localBestWeights[i][4] = currentWeights[i][4];
//            if (!runTTM)
//                localBestWeights[i][5] = currentWeights[i][5];
//            if (!runViper)
//                localBestWeights[i][6] = currentWeights[i][6];
//        }

        return localBestWeights;
    }

    public double[][] getBestWeights(int rounds, int popSize)
    {
        double[][][] currentWeights = new double[popSize][7][];

        // initialization
        for (int i = 0; i < popSize; i++)
        {
            // 0 -> Soldier
            // 1 -> Archon
            // 2 -> Guard
            // 3 -> Scout
            // 4 -> Turret
            // 5 -> TTM
            // 6 -> Viper

            for (int j = 0; j < 7; j++)
            {
                net.generateRandomWeights();
                currentWeights[i][j] = net.getWeights();
            }
        }


        getBestFromStartingPop(currentWeights, rounds, popSize);

        return globalBest;
    }

    public double[][][] getInitialPopulation(int rounds, int popSize)
    {
        double[][][] currentWeights = new double[popSize][7][];

        // initialization
        for (int i = 0; i < popSize; i++)
        {
            // 0 -> Soldier
            // 1 -> Archon
            // 2 -> Guard
            // 3 -> Scout
            // 4 -> Turret
            // 5 -> TTM
            // 6 -> Viper

            for (int j = 0; j < 7; j++)
            {
                net.generateRandomWeights();
                currentWeights[i][j] = net.getWeights();
            }
        }

        return getBestFromStartingPop(currentWeights, rounds, popSize);
    }

    public double[][] updateParticle(double[][] current, double[][] localBest, double[][] globalBest)
    {
        double[][] newWeights = current;

        for (int i = 0; i < newWeights.length; i++)
        {
            if (i == 0 && runSoldier || i == 1 && runArchon || i == 2 && runGuard || i == 3 && runScout
                    || i == 4 && runTurret || i == 5 && runTTM || i == 6 && runViper)
            {
                for (int j = 0; j < newWeights[i].length; j++)
                {
                    newWeights[i][j] = current[i][j] + (((2 * Math.random() * this.randomScale) - this.randomScale) + this.localScale * (localBest[i][j] - current[i][j]) + this.globalScale * (globalBest[i][j] - current[i][j]));
                }
            }
        }

        return newWeights;
    }


    public void setRunSoldier(boolean runSoldier)
    {
        this.runSoldier = runSoldier;
    }

    public void setRunArchon(boolean runArchon)
    {
        this.runArchon = runArchon;
    }

    public void setRunGuard(boolean runGuard)
    {
        this.runGuard = runGuard;
    }

    public void setRunViper(boolean runViper)
    {
        this.runViper = runViper;
    }

    public void setRunScout(boolean runScout)
    {
        this.runScout = runScout;
    }

    public void setRunTTM(boolean runTTM)
    {
        this.runTTM = runTTM;
    }

    public void setRunTurret(boolean runTurret)
    {
        this.runTurret = runTurret;
    }
}
