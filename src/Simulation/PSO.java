package Simulation;

public class PSO
{
    private double globalScale;
    private double localScale;
    private double randomScale;
    private FeedForwardNeuralNetwork net;

    public PSO(double globalScale, double localScale, double randomScale)
    {
        this.globalScale = globalScale;
        this.localScale = localScale;
        this.randomScale = randomScale;

        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
    }

    public double[][] getBestWeights(int rounds, int popSize)
    {
        double[][][] currentWeights = new double[popSize][7][];
        double[][][] localBestWeights = new double[popSize][][];
        double[][] globalBest = null;
        double[][] localBestScores = new double[popSize][7];
        double[] globalBestScore = new double[7];
        double[][] currentFitness = new double[popSize][7];
        int map = -1;

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

            localBestWeights[i] = new double[7][];


            for (int j = 0; j < 7; j++)
            {
                net.generateRandomWeights();
                currentWeights[i][j] = net.getWeights();
                localBestWeights[i][j] = net.getWeights();
                localBestScores[i][j] = 0;
                globalBestScore[j] = 0;
            }
        }

        globalBest = currentWeights[0];


        for (int i = 0; i < rounds; i++)
        {
            if (i % 50 == 0)
            {
                map++;

                // reset global and local best for new map
                for (int k = 0; k < popSize; k++)
                {
                    for (int j = 0; j < 7; j++)
                    {
                        localBestWeights[k][j] = currentWeights[k][j];
                        localBestScores[k][j] = 0;
                        globalBestScore[j] = 0;
                    }
                }

                globalBest = currentWeights[0];
            }

            // first update all of the particles
            for (int j = 0; j < popSize; j++)
            {
                currentWeights[j] = updateParticle(currentWeights[j], localBestWeights[j], globalBest);
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
                            currentFitness[j][l] += results[0][l];
                            currentFitness[k][l] += results[1][l];
                        }
                    }
                }
            }

            // loop over scores and update local and global best as necessary
            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < 7; k++)
                {
                    if ((i % 50) > 0 && globalBestScore[k] != 0)
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
            System.out.println("Finished round " + i + " of PSO");
        }

        return globalBest;
    }

    public double[][] updateParticle(double[][] current, double[][] localBest, double[][] globalBest)
    {
        double[][] newWeights = new double[current.length][current[0].length];

        for (int i = 0; i < newWeights.length; i++)
        {
            for (int j = 0; j < newWeights[i].length; j++)
            {
                newWeights[i][j] = current[i][j] + (((2 * Math.random() * this.randomScale) - this.randomScale) + this.localScale * (localBest[i][j] - current[i][j]) + this.globalScale * (globalBest[i][j] - current[i][j]));
            }
        }

        return newWeights;
    }

}
