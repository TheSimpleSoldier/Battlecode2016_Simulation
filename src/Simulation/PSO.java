package Simulation;

public class PSO
{
    private double globalScale;
    private double localScale;
    private double randomScale;
    private FeedForwardNeuralNetwork net;
    private double[][] globalBest = null;

    private boolean runSoldier = false;
//    private boolean runArchon = false;
    private boolean runGuard = false;
    private boolean runViper = false;
//    private boolean runScout = false;
//    private boolean runTTM = false;
//    private boolean runTurret = false;

    public PSO(double globalScale, double localScale, double randomScale)
    {
        this.globalScale = globalScale;
        this.localScale = localScale;
        this.randomScale = randomScale;

        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
    }

    public double[][][] getBestFromStartingPop(double[][][] currentWeights, int rounds, int popSize)
    {
        double[][][] localBestWeights = new double[popSize][3][currentWeights[0][0].length];
        double[][] localBestScores = new double[popSize][3];
        double[] globalBestScore = new double[3];
        double[][] currentFitness = new double[popSize][3];
        double[][][] originalWeights = new double[popSize][3][currentWeights[0][0].length];


        int map = -1;

        for (int i = 0; i < rounds; i++)
        {
            if (i % 100 == 0)
            {
                map++;

                // reset global and local best for new map
                for (int k = 0; k < popSize; k++)
                {
                    for (int j = 0; j < 3; j++)
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

            if (i % 100 != 0)
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

                double[][] results = Main.runFightSimulation(currentWeights[j], currentWeights[j], 0, 1, false, map);
                double[][] results2 = Main.runFightSimulation(currentWeights[j], currentWeights[j], 1, 0, false, map);


                for (int l = 0; l < 3; l++)
                {
                    if (l == 0 && runSoldier || l == 1 && runGuard || l == 2 && runViper)
                    {
                        currentFitness[j][l] += results[0][l];
                        currentFitness[j][l] += results2[1][l];
                    }
                }

//                for (int k = 0; k < popSize; k++)
//                {
//                    // run match and record scores
//                    if (j != k)
//                    {
//                        double[][] results = Main.runFightSimulation(currentWeights[j], currentWeights[k], 0, 0, false, map);
//
//
//                            for (int l = 0; l < 3; l++)
//                            {
//                                if (l == 0 && runSoldier || l == 1 && runGuard || l == 2 && runViper)
//                                {
//                                    currentFitness[j][l] += results[0][l];
//                                    currentFitness[k][l] += results[1][l];
//                                }
//                            }
//
//                    }
//                }
            }

            // loop over scores and update local and global best as necessary
            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    if ((k == 0 && runSoldier) || (k == 1 && runGuard) || (k == 2 && runViper))
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

        return localBestWeights;
    }

    public double[][] getBestWeights(int rounds, int popSize)
    {
        double[][][] currentWeights = new double[popSize][3][];

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

            for (int j = 0; j < 3; j++)
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
        double[][][] currentWeights = new double[popSize][3][];

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

            for (int j = 0; j < 3; j++)
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
            if (i == 0 && runSoldier || i == 1 && runGuard || i == 2 && runViper)
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


    public void setRunGuard(boolean runGuard)
    {
        this.runGuard = runGuard;
    }

    public void setRunViper(boolean runViper)
    {
        this.runViper = runViper;
    }
}
