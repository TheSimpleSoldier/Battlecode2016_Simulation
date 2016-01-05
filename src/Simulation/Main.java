package Simulation;

import java.io.*;

public class Main
{
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

        double[][] psoWeights = {
                {
                        -2.1827712928375355E22, -2.714187787861116E22, -1.207086283308601E21, -4.790309190646043E22, -1.189212048641437E22, 4.577805222615349E22, 2.7575394348305524E22, -2.995882939051523E21, -4.878666615818758E22, -1.6553875273622435E22, 1.7282773086762856E22, -2.706649202191671E22, 7.574693533357408E22, -5.302825499703793E22, -4.465951549828271E22, 1.6485049836608292E22, 3.568554825005976E22, 1.571022971802199E22, -3.86952217223574E22, -1.2712761093817746E21, 5.726083082778657E22, -3.6223064756128087E22, 1.5030561734869285E22, 2.2916281308768945E21, 4.002685657069959E22, -1.1323748033029504E22, -1.1411974995959494E22, 1.258110443586057E22, -8.706537956421874E20, 9.878372655387E21, 1.0034972432466042E22, 3.2002172046935996E22, -3.2682875190229795E22, -2.765921220466826E22, 1.1572051795992316E22, -1.248815236666122E22, 3.935724118301547E22, 3.8281542295094E22, -2.7813594683854733E22, 5.2396356970419705E22, -4.799308029295299E22, 4.380378753255938E22, 3.950618743101621E22, -5.769852113311448E21, 2.0145844620345844E22, -3.5061381611368628E22, 3.02878725641617E22, -2.3047836285817718E22, -9.757064723112294E21, 2.3910050943393625E22, 1.3476996480888694E22, 2.727167374823269E22, 3.2779734841076123E22, -4.504455037291222E22, 5.284007624796552E22, -5.181137408291421E22, 1.0554544843725503E22, 1.1886867781305244E22, -1.7548340609186924E22, 9.394552981321278E21, -6.204198583138991E22, -6.222818923334049E22, -5.393342407031911E22, 5.266606885258269E22, 4.79558639629185E22, -1.9492501020000748E22, 3.1282169051968034E22, -4.3806463400741844E22, -5.441701111139432E22, -1.241752992021045E21, -3.339184970776361E22, 8.974828659143569E21, 3.531370114348279E22, -3.3137113188776358E22, -6.234729279516411E22, -2.149669349949392E22, 3.4660715236376596E22, 4.0470286506113244E22, -3.785755235101155E22, -2.819794587102644E22, 5.278541177222249E22, -2.5721144775538387E22, 2.2037893112343424E22, -1.6646917425395164E22, 4.793166995280244E22, -5.228751599492334E22, -5.914917981315366E22, 3.862400142016055E22, -1.961260669847712E22, -6.336890959381438E22, -5.375572829819432E21, 4.437430524022195E22, 4.413295799219516E22, -6.275570966454809E21, 3.3929836660783093E22, -1.1215519935757766E22, -1.5786115209256262E22, 5.961312018875625E22, 1.4930528260997492E22, 5.870239218761649E22, 3.0268745951703116E22, 6.178075568787418E20, 2.407744251553906E22, 2.1543957001824476E22, -1.8715732014401073E22, -7.524354162422337E20, 5.2590245668675205E22, 3.3235847011748284E22, 1.7285821831610236E22, 1.7398648116587147E22, 4.349555787786289E22, -4.359817195087774E22, -6.765731574824099E22, -2.681408366982542E22, -3.5799154983626986E22, -9.72015614134916E21, -4.369554069234374E21, 1.1834080587642883E21, -3.902177132565063E22, -4.790287746892177E22, -1.0536274431723595E22, -3.378975333879484E22, 9.709289492217363E21, 1.4820428266348132E22, -3.446498578148536E21, -3.4718247489424685E22, -2.913237167137639E21, 2.5187083629853057E22, -1.6200577040746612E22, 4.155486929425266E22, 8.205512713632733E21,
                }
        };

        double[][] gaWeights = {
                {
                        2.6452357365289894, -0.09025621045906956, -0.8543510316421756, -0.8414153944258509, 1.0454422590919064, 1.264909835784719, -0.12056368029392944, 3.094754800331854, -3.314233193448041, 0.7534812485116745, -0.3420898838346022, -1.3660511242144593, -1.2109125231152897, 1.8877651008468317, 0.7913175045590684, -1.0262603203619136, -1.4434880162836592, 3.1555439375887344, -0.9808578486765884, -0.47535029008201923, -2.1084228876143563, -0.9416727080295342, 0.26753347903565794, 1.5030411678416227, 0.8074872730747162, -2.53508838739887, -3.009732902807281, 0.07440922153898324, 1.9666087550882818, -1.4046506178458, 0.43345307523955934, -0.5783494186828598, -3.3806215798763737, -0.17707739256538896, -1.0089519409591434, 0.1469329421374537, -1.648262117921228, -2.7374790946611025, -2.5092785202692425, -2.0777309196767417, 1.1292152467065502, 1.706870690214936, 0.8677502232837234, -1.3374052448187514, -0.008587010273129768, -0.7328424250076399, -0.18867375329716748, -1.5773008963710202, -0.9095969772640625, 1.0398186266374891, 1.1620431090122196, -0.21297723009912634, -2.0930927208028804, -2.766882804324655, -0.08461762193061317, 0.9885162019370769, 2.901887880544107, 0.6479149644566458, 0.23916517571262638, 0.4569930145803106, -1.5574527314238615, -1.8867179363765691, -1.995945594117752, -0.0861495650745468, -1.2998349230256698, -0.3244801380849597, -2.3851635526964854, -0.5641240530793385, 2.0891763776063956, -0.2379495733825552, -3.431455593814721, 1.183531755935145, 0.07117934181953098, 0.7269911900010992, 0.4835235641457547, -0.04305602363341489, -0.3873159177306166, 0.4817323124455559, -1.0504156162802953, 1.393968694632964, 1.0120351008509956, -1.0689717703767383, -1.9886479126743397, -0.4562452584956771, 0.9873391586721312, -1.9392178194149188, 1.3417051054568663, 0.8240655357708864, -2.6254261145241196, -2.3444410965796734, 1.235634143518301, -1.4462203814888286, 0.6027186304051646, 1.109042226747029, -1.1611746684342763, 0.8020612145528141, -0.04835830925965502, 2.1078532255386215, -0.7369075178082333, 1.5123624088538155, 2.41173902695852, 0.284934951317797, 1.4551291862018654, 2.7657824480893725, 0.761223079090517, -0.6674763398094508, 2.7628194929907797, -4.320169635288309, 0.5115055128708346, 0.8810451548659901, -2.192246018791522, -0.2112073309833989, -0.7329680531969693, -0.6712748710164083, 1.4495281091512937, -1.433844109149669, -0.9544585884778768, 1.9533452809839291, -4.340767718940253, -0.35061423345110115,
                }
        };

//        runFightSimulation(psoWeights, gaWeights, 0, 0, true, 1);

        //runFightSimulation(inputs, inputs);

//        System.out.println("Basic vs. Advanced");
//        runFightSimulation(null, null, 1, 2, true, 2);
//        System.out.println("Advanced vs. Basic");
//        runFightSimulation(null, null, 2, 1, true, 2);

        double[][] idealWeights = getIdealWeights(10, 200, mutationRate, crossOverRate, mutationAmount);

        PSO pso = new PSO(globalScale, localScale, randomScale);
        double[][] idealWeights2 = pso.getBestWeights(200, 10);

        long startTime = System.currentTimeMillis();

        System.out.println();

        for (int i = 0; i < idealWeights2.length; i++)
        {
            for (int j = 0; j < idealWeights2[i].length; j++)
            {
                System.out.print(idealWeights2[i][j] + ", ");
            }
            System.out.println();
        }

        System.out.println("GA: ");
        for (int i = 0; i < idealWeights.length; i++)
        {
            for (int j = 0; j < idealWeights[i].length; j++)
            {
                System.out.print(idealWeights[i][j] + ", ");
            }
            System.out.println();
        }

        verbose = true;

        for (int i = 0; i < 2; i++)
        {
            System.out.println("Net is Red:");
            runFightSimulation(idealWeights, idealWeights2, 0, 0, verbose, 1);
            System.out.println("Net is blue");
            runFightSimulation(idealWeights2, idealWeights, 0, 0, verbose, 1);
            System.out.println("Net vs. basic");
            runFightSimulation(idealWeights, idealWeights, 0, 1, verbose, 1);
            System.out.println("basic vs. Net");
            runFightSimulation(idealWeights, idealWeights, 1, 0, verbose, 1);
            System.out.println("PSO vs. basic");
            runFightSimulation(idealWeights2, idealWeights2, 0, 1, verbose, 1);
            System.out.println("basic vs. PSO");
            runFightSimulation(idealWeights2, idealWeights2, 1, 0, verbose, 1);
        }

        System.out.println("Run Time: " + (System.currentTimeMillis() - startTime));
    }

    public static double[][] getIdealWeights(int popSize, int rounds, double mutationRate, double crossOverRate, double mutationAmount)
    {
        FeedForwardNeuralNetwork net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 4}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);

        double[][][] population = new double[popSize][][];

        for (int i = 0; i < popSize; i++)
        {
            net.generateRandomWeights();
            population[i] = new double[1][];
            population[i][0] = net.getWeights();
//            System.out.println("net length:" + net.getWeights().length);
        }


        for (int i = 0; i < rounds; i++)
        {
            population = runTheGA(population, mutationRate, crossOverRate, mutationAmount);

            double[] totalFitness = new double[popSize];

            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < popSize; k++)
                {
                    if (j != k)
                    {
                        double[][] results = runFightSimulation(population[j], population[k], 0, 0, false, i);
                        totalFitness[j] += results[0][0];
                        totalFitness[k] += results[1][0];

//                        System.out.println("Score for Red: " + results[0][0]);
//                        System.out.println("Score for Blue: " + results[1][0]);
                    }
                }
            }

            population = sortPopulation(population, totalFitness);


            for (int j = 0; j < totalFitness.length; j++)
            {
                totalFitness[j] = 0;
            }

            System.out.println("Finished round: " + i + " of the GA");
        }

        return population[0];
    }

    /**
     * This method sorts the population based on their fitness values
     *
     * @param initialPop
     * @param fitness
     * @return
     */
    public static double[][][] sortPopulation(double[][][] initialPop, double[] fitness)
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
                if (fitness[j] > fitness[i])
                {
                    double temp = fitness[j];
                    fitness[j] = fitness[i];
                    fitness[i] = temp;
                    double[][] temp2 = sortedPop[j];
                    sortedPop[j] = sortedPop[i];
                    sortedPop[i] = temp2;
                }
            }
        }

        return sortedPop;
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
    public static double[][][] runTheGA(double[][][] initialPop, double mutationRate, double crossOverRate, double mutationAmount)
    {
        double[][][] evolvedPop = new double[initialPop.length][][];
        int currentIndex = 0;
        int len = initialPop.length;

        for (int i = 0; i < len; i++)
        {
            double[][] selected = null;

            while (selected == null)
            {
                if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                {
                    selected = initialPop[currentIndex];
                }

                currentIndex = (currentIndex + 1) % len;
            }

            // Cross Over
            if (Math.random() < crossOverRate)
            {
                double[][] selected2 = null;

                while (selected2 == null)
                {
                    if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                    {
                        selected2 = initialPop[currentIndex];
                    }

                    currentIndex = (currentIndex + 1) % len;
                }

                for (int j = 0; j < selected.length; j++)
                {
                    int crossOverPoint = (int) (Math.random() * selected[j].length);
                    for (int k = crossOverPoint; k < selected[j].length; k++)
                    {
                        selected[j] = selected2[j];
                    }

                }
            }

            // Mutation
            for (int j = 0; j < selected.length; j++)
            {
                for (int k = 0; k < selected[j].length; k++)
                {
                    if (Math.random() < mutationRate)
                    {
                        selected[j][k] += (Math.random() * 2 * mutationAmount) - mutationAmount;
                    }
                }
            }

            evolvedPop[i] = selected;
        }

        return evolvedPop;
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

        Game game = new Game(team1Inputs, team2Inputs, verbose);

        // Battlecode2016_Simulation/src/Simulation
        String map = "/Users/fred/Desktop/battlecode-scaffold-master/Battlecode2016_Simulation/src/Simulation/maps/SoldierTest1.xml";


        if (round % 4 == 1)
        {
//            map = "FightMicroGA/Simulation/simulationMaps/barren.xml";
        }
        else if (round % 4 == 2)
        {
//            map = "FightMicroGA/Simulation/simulationMaps/frontlines.xml";
        }
//        else if (round % 4 == 3)
//        {
//            map = "FightMicroGA/Simulation/simulationMaps/noeffort.xml";
//        }

        game.runMatch(map, teamA, teamB);

        double[][] results = new double[2][];

        results[0] = game.getTeamResults(0);
        results[1] = game.getTeamResults(1);

        return results;
    }
}
