package Simulation;

public class GA
{
    private FeedForwardNeuralNetwork net;

    public GA()
    {
        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 5}, ActivationFunction.STEP, ActivationFunction.STEP);
    }

    public double[][] runGA(int popSize, int rounds, double mutationRate, double crossOverRate, double mutationAmount)
    {
        double[][][] population = new double[popSize][7][];

        for (int i = 0; i < popSize; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                net.generateRandomWeights();
                population[i][j] = net.getWeights();
            }
        }


        for (int i = 0; i < rounds; i++)
        {
            population = evolveGeneration(population, mutationRate, crossOverRate, mutationAmount);

            double[][] totalFitness = new double[popSize][7];

            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < popSize; k++)
                {
                    if (j != k)
                    {
                        double[][] results = Main.runFightSimulation(population[j], population[k], 0, 0, false, i);

                        for (int l = 0; l < 7; l++)
                        {
                            totalFitness[j][l] += results[0][l];
                            totalFitness[k][l] += results[1][l];
                        }
                    }
                }
            }

            population = sortPopulation(population, totalFitness);


            for (int j = 0; j < totalFitness.length; j++)
            {
                for (int k = 0; k < 7; k++)
                {
                    totalFitness[j][k] = 0;
                }
            }

            System.out.println("Finished round: " + i + " of the GA");
        }

        return population[0];
    }

    /**
     * This method takes an initial population and runs the GA to evolve a superior poputation
     *
     * Note: this method assumes that the initial population has been sorted from best to worst
     *
     * @param initialPop
     * @param mutationRate
     * @param crossOverRate
     * @param mutationAmount
     * @return
     */
    public static double[][][] evolveGeneration(double[][][] initialPop, double mutationRate, double crossOverRate, double mutationAmount)
    {
        double[][][] evolvedPop = new double[initialPop.length][7][];
        int currentIndex = 0;
        int len = initialPop.length;

        for (int i = 0; i < len; i++) {
            for (int l = 0; l < 7; l++) {
                double[] selected = null;

                while (selected == null)
                {
                    if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                    {
                        selected = initialPop[currentIndex][l];
                    }

                    currentIndex = (currentIndex + 1) % len;
                }

                // Cross Over
                if (Math.random() < crossOverRate)
                {
                    double[] selected2 = null;

                    while (selected2 == null)
                    {
                        if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                        {
                            selected2 = initialPop[currentIndex][l];
                        }

                        currentIndex = (currentIndex + 1) % len;
                    }

                    for (int j = 0; j < selected.length; j++)
                    {
                        int crossOverPoint = (int) (Math.random() * selected.length);
                        for (int k = crossOverPoint; k < selected.length; k++)
                        {
                            selected[j] = selected2[j];
                        }

                    }
                }

                // Mutation
                for (int j = 0; j < selected.length; j++)
                {
                        if (Math.random() < mutationRate)
                        {
                            selected[j] += (Math.random() * 2 * mutationAmount) - mutationAmount;
                        }
                }

                evolvedPop[i][l] = selected;
            }
        }

        return evolvedPop;
    }

    /**
     * This method sorts the population based on their fitness values
     *
     * @param initialPop
     * @param fitness
     * @return
     */
    public static double[][][] sortPopulation(double[][][] initialPop, double[][] fitness)
    {
        double[][][] sortedPop = new double[initialPop.length][][];

        for (int i = 0; i < sortedPop.length; i++)
        {
            sortedPop[i] = initialPop[i];
        }

        // bubble sort FTW!!!!
        for (int i = 0; i < fitness.length; i++)
        {
            for (int j = i+1; j < fitness.length; j++)
            {
                for (int k = 0; k < 7; k++)
                {
                    if (fitness[j][k] > fitness[i][k])
                    {
                        double temp = fitness[j][k];
                        fitness[j][k] = fitness[i][k];
                        fitness[i][k] = temp;
                        double[] temp2 = sortedPop[j][k];
                        sortedPop[j][k] = sortedPop[i][k];
                        sortedPop[i][k] = temp2;
                    }
                }
            }
        }

        return sortedPop;
    }
}
