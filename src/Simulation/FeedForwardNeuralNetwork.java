package Simulation;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Random;

/**
 * Created by joshua on 9/22/15.
 * This class creates a fully connected feedforward neural network
 * with a variable number of hidden layers.
 * It can be imported and exported through the JSON format to make
 * saving the net much easier. There is also a regular constructor
 * which will create the net with random weights.
 * The net can currently use 2 different activation functions,
 * a linear function with a slope of 1 and the logistic function.
 * Each node is connected to a bias node which has a value of 1.
 */
public class FeedForwardNeuralNetwork
{
    //properties of the net
    private double[] weights;
    private int hiddenLayers;
    private int[] sizes;
    private int biggestSize;
    private ActivationFunction hiddenActivationFunction, outputActivationFunction;
    private double biasNum = 1.;
    private double linearSlope = 1.;

    private double[][] weightsToOutput;
    private double[] biasWeightsToOutput;
    private double[][] weightsToHidden;
    private double[] biasWeightsToHidden;

    /**
     * Initialize a new net with the following parameters and random weights
     * @param hiddenLayers Number of hidden layers
     * @param sizes Size of each layer, starting with the input and ending with the output
     * @param hiddenActivationFunction Activation function to use for hidden nodes
     * @param outputActivationFunction Activation function to use for the ouput layer
     */
    public FeedForwardNeuralNetwork(int hiddenLayers, int[] sizes, ActivationFunction hiddenActivationFunction, ActivationFunction outputActivationFunction)
    {
        this.hiddenLayers = hiddenLayers;
        this.sizes = sizes;
        this.hiddenActivationFunction = hiddenActivationFunction;
        this.outputActivationFunction = outputActivationFunction;

        biggestSize = 0;
        for(int k = 0; k < sizes.length; k++)
        {
            if(sizes[k] > biggestSize)
            {
                biggestSize = sizes[k];
            }
        }

        generateRandomWeights();

        this.weightsToOutput = new double[sizes[2]][sizes[1]];
        this.biasWeightsToOutput = new double[sizes[2]];
        this.weightsToHidden = new double[sizes[1]][sizes[0]];
        this.biasWeightsToHidden = new double[sizes[1]];

        setWeightArrays();
    }

    public void setWeightArrays()
    {
        int i, j;

        for (i = this.weightsToOutput.length; --i >= 0; )
        {
            for (j = this.weightsToOutput[i].length; --j >= 0; )
            {
                this.weightsToOutput[i][j] = getWeight(1, i, 2, j);
            }
        }

        for (i = this.weightsToHidden.length; --i >= 0; )
        {
            for (j = this.weightsToHidden[i].length; --j >= 0; )
            {
                this.weightsToHidden[i][j] = getWeight(0, i, 1, j);
            }
        }

        for (i = this.biasWeightsToOutput.length; --i >= 0; )
        {
            this.biasWeightsToOutput[i] = getWeight(-1, 0, 2, i);
        }

        for (i = this.biasWeightsToHidden.length; --i >= 0; )
        {
            this.biasWeightsToHidden[i] = getWeight(-1, 0, 1, i);
        }
    }

    public FeedForwardNeuralNetwork(int hiddenLayers, int[] sizes, ActivationFunction hiddenActivationFunction, ActivationFunction outputActivationFunction, double biasNum, double linearSlope)
    {
        this(hiddenLayers, sizes, hiddenActivationFunction, outputActivationFunction);

        this.biasNum = biasNum;
        this.linearSlope = linearSlope;
    }

    /**
     * Fills all the weights with random numbers between -1 and 1
     */
    public void generateRandomWeights()
    {
        int lowest = -1;
        int highest = 1;

        int totalWeights = 0;
        for(int k = 0; k < sizes.length - 1; k++)
        {
            totalWeights += sizes[k] * sizes[k + 1];
            totalWeights += sizes[k];
        }
        totalWeights += sizes[sizes.length - 1];

        Random rand = new Random();
        weights = new double[totalWeights];

        for(int k = 0; k < totalWeights; k++)
        {
            weights[k] = lowest + ((highest - lowest) * rand.nextDouble());
        }
    }

    /**
     * Compute output nodes based on input
     * @param inputs Values for input layer
     * @return Values of the output layer
     */
    public double[] compute(double[] inputs)
    {
        //if input wrong size, return
        if(inputs.length != sizes[0])
        {
            System.out.println("Invalid number of inputs");
            return null;
        }

        //fill out first layer to temp output
        int lastLayer = sizes[0];
        double[] layerOut = new double[biggestSize];
        for(int k = 0; k < lastLayer; k++)
        {
            layerOut[k] = inputs[k];
        }

        //for each layer after first
        for(int k = 1; k < hiddenLayers + 2; k++)
        {
            //for each node in that layer
            double[] tempOut = new double[sizes[k]];
            for(int a = 0; a < sizes[k]; a++)
            {
                //get sum and apply activation function
                double sum = 0;
                for(int t = 0; t < lastLayer; t++)
                {
                    sum += layerOut[t] * getWeight(k - 1, t, k, a);
                }
                sum += biasNum * getWeight(-1, 0, k, a);
                if(k != hiddenLayers + 1)
                {
                    tempOut[a] = applyActivationFunction(sum, hiddenActivationFunction);
                }
                else
                {
                    tempOut[a] = applyActivationFunction(sum, outputActivationFunction);
                }
            }
            lastLayer = sizes[k];
            //fill out return
            for(int a = 0; a < lastLayer; a++)
            {
                layerOut[a] = tempOut[a];
            }
        }

        double[] toReturn = new double[sizes[sizes.length - 1]];
        for(int k = 0; k < toReturn.length; k++)
        {
            toReturn[k] = layerOut[k];
        }

        return toReturn;
    }

    /**
     * This function is heavily bytecode optimized to run the compute function
     *
     * @param inputs
     * @return
     */
    public double[] computeFast(double[] inputs)
    {
        int size0 = sizes[0];
        int size1 = sizes[1];
        int size2 = sizes[2];
        double[] hiddenValues = new double[size1];
        double sum;
        double[] results = new double[size2];
        int i, j;

        for (i = size1; --i >= 0; )
        {
            sum = biasNum * biasWeightsToHidden[i];
            for (j = size0; --j>=0; )
            {
                sum += inputs[j] * weightsToHidden[i][j];
            }

            if (sum < 0.5)
            {
                hiddenValues[i] = 0;
            }
            else
            {
                hiddenValues[i] = 1;
            }
//            hiddenValues[i] = applyActivationFunction(sum, hiddenActivationFunction);
        }

        for (i = size2; --i>=0; )
        {
            sum = biasNum * biasWeightsToOutput[i];
            for (j = size1; --j>=0; )
            {
                sum += hiddenValues[j] * weightsToOutput[i][j];
            }
            if (sum < 0.5)
            {
                results[i] = 0;
            }
            else
            {
                results[i] = 1;
            }
//            results[i] = applyActivationFunction(sum, outputActivationFunction);
        }

        return results;
    }

    /**
     * Even more heavily optimized function
     */
    public double[] computeSuperFast(double[] input)
    {
        double[] weights = this.weights;
        double[] middle = {0,0,0,0,0,0,0,0,0,0};
        double[] output = {0,0,0,0,0};

        if(input[0] * weights[0] + input[1] * weights[10] + input[2] * weights[20] + input[3] * weights[30] + input[4] * weights[40] + input[5] * weights[50] > .5)
        {
            middle[0] = 1;
        }

        if(input[0] * weights[1] + input[1] * weights[11] + input[2] * weights[21] + input[3] * weights[31] + input[4] * weights[41] + input[5] * weights[51] > .5)
        {
            middle[1] = 1;
        }

        if(input[0] * weights[2] + input[1] * weights[12] + input[2] * weights[22] + input[3] * weights[32] + input[4] * weights[42] + input[5] * weights[52] > .5)
        {
            middle[2] = 1;
        }

        if(input[0] * weights[3] + input[1] * weights[13] + input[2] * weights[23] + input[3] * weights[33] + input[4] * weights[43] + input[5] * weights[53] > .5)
        {
            middle[3] = 1;
        }

        if(input[0] * weights[4] + input[1] * weights[14] + input[2] * weights[24] + input[3] * weights[34] + input[4] * weights[44] + input[5] * weights[54] > .5)
        {
            middle[4] = 1;
        }

        if(input[0] * weights[5] + input[1] * weights[15] + input[2] * weights[25] + input[3] * weights[35] + input[4] * weights[45] + input[5] * weights[55] > .5)
        {
            middle[5] = 1;
        }

        if(input[0] * weights[6] + input[1] * weights[16] + input[2] * weights[26] + input[3] * weights[36] + input[4] * weights[46] + input[5] * weights[56] > .5)
        {
            middle[6] = 1;
        }

        if(input[0] * weights[7] + input[1] * weights[17] + input[2] * weights[27] + input[3] * weights[37] + input[4] * weights[47] + input[5] * weights[57] > .5)
        {
            middle[7] = 1;
        }

        if(input[0] * weights[8] + input[1] * weights[18] + input[2] * weights[28] + input[3] * weights[38] + input[4] * weights[48] + input[5] * weights[58] > .5)
        {
            middle[8] = 1;
        }

        if(input[0] * weights[9] + input[1] * weights[19] + input[2] * weights[29] + input[3] * weights[39] + input[4] * weights[49] + input[5] * weights[59] > .5)
        {
            middle[9] = 1;
        }

        if(middle[0] * weights[60] + middle[1] * weights[65] + middle[2] * weights[70] + middle[3] * weights[75] + middle[4] * weights[80] + middle[5] * weights[85] + middle[6] * weights[90] + middle[7] * weights[95] + middle[8] * weights[100] + middle[9] * weights[105] > .5)
        {
            output[0] = 1;
        }

        if(middle[0] * weights[61] + middle[1] * weights[66] + middle[2] * weights[71] + middle[3] * weights[76] + middle[4] * weights[81] + middle[5] * weights[86] + middle[6] * weights[91] + middle[7] * weights[96] + middle[8] * weights[101] + middle[9] * weights[106] > .5)
        {
            output[1] = 1;
        }

        if(middle[0] * weights[62] + middle[1] * weights[67] + middle[2] * weights[72] + middle[3] * weights[77] + middle[4] * weights[82] + middle[5] * weights[87] + middle[6] * weights[92] + middle[7] * weights[97] + middle[8] * weights[102] + middle[9] * weights[107] > .5)
        {
            output[2] = 1;
        }

        if(middle[0] * weights[63] + middle[1] * weights[68] + middle[2] * weights[73] + middle[3] * weights[78] + middle[4] * weights[83] + middle[5] * weights[88] + middle[6] * weights[93] + middle[7] * weights[98] + middle[8] * weights[103] + middle[9] * weights[108] > .5)
        {
            output[3] = 1;
        }

        if(middle[0] * weights[64] + middle[1] * weights[69] + middle[2] * weights[74] + middle[3] * weights[79] + middle[4] * weights[84] + middle[5] * weights[89] + middle[6] * weights[94] + middle[7] * weights[99] + middle[8] * weights[104] + middle[9] * weights[109] > .5)
        {
            output[4] = 1;
        }

        return output;
    }

    /**
     * Gets weight between 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @return The value of the weight
     */
    public double getWeight(int layerStart, int start, int layerEnd, int end)
    {
        int index = getIndex(layerStart, start, layerEnd, end);
        return weights[index];
    }

    /**
     * Sets a new weight between 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @param newWeight New weight between the nodes
     */
    public void setWeight(int layerStart, int start, int layerEnd, int end, double newWeight)
    {
        int index = getIndex(layerStart, start, layerEnd, end);
        weights[index] = newWeight;
    }

    /**
     * Gets the index of the 1-d array that represents the weight between the 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @return The index where the weight is
     */
    public int getIndex(int layerStart, int start, int layerEnd, int end)
    {
        if(layerStart != -1)
        {
            int index = 0;
            for(int k = 0; k < layerStart; k++)
            {
                index += sizes[k] * sizes[k + 1];
            }

            index += start * sizes[layerEnd];
            index += end;

            return index;
        }
        else
        {
            int index = 0;
            for(int k = 0; k < hiddenLayers + 1; k++)
            {
                index += sizes[k] * sizes[k + 1];
            }

            for(int k = 0; k < layerEnd; k++)
            {
                index += sizes[k];
            }

            index += end;

            return index;
        }
    }

    /**
     * Applies the relevant activation function to the sum of a node
     * @param sum The value to plug into the function
     * @return The value returned by applying the function to the value
     */
    public double applyActivationFunction(double sum, ActivationFunction activationFunction)
    {
        switch(activationFunction)
        {
            case LINEAR:
                return linearSlope * sum;
            case LOGISTIC:
                return 1.0 / (1.0 + Math.pow(Math.E, sum * -1.0));
            case STEP:
                if(sum < .5)
                {
                    return 0;
                }
                return 1;
        }

        System.out.println("Failed to apply activation function");
        return -9999;
    }

    /**
     * similar to applyActivationFunction, but applies the derivative for training
     * @param sum the value to plug into the function
     * @return The value returned by applying the function to the value
     */
    public double applyActivationFunctionDerivative(double sum, ActivationFunction activationFunction)
    {
        switch(activationFunction)
        {
            case LINEAR:
                return linearSlope;
            case LOGISTIC:
                return (Math.pow(Math.E, sum) / Math.pow(Math.pow(Math.E, sum) + 1, 2));
            case STEP:
                if(sum < .5)
                {
                    return 0;
                }
                return 1;
        }

        System.out.println("Failed to apply activation function");
        return -9999;
    }

    /**
     * Gets an array of all the for the net
     * @return double array of weights
     */
    public double[] getWeights()
    {
        return weights;
    }

    /**
     * Gets the array of sizes of each layer
     * @return double array with layer sizes with input being at index 0
     */
    public int[] getSizes()
    {
        return sizes;
    }

    /**
     * Gets the magnitude of the bias value
     * @return a double that is the bias value
     */
    public double getBiasNum()
    {
        return biasNum;
    }

    /**
     * Gets the activation function used by hidden layers
     * @return an ActivationFunction enum
     */
    public ActivationFunction getHiddenActivationFunction()
    {
        return hiddenActivationFunction;
    }

    /**
     * Gets the activation function used by output layer
     * @return an ActivationFunction enum
     */
    public ActivationFunction getOutputActivationFunction()
    {
        return outputActivationFunction;
    }

    /**
     * sets the weights for the net
     * @param weights array of doubles that has weight values
     */
    public void setWeights(double[] weights)
    {
        this.weights = weights;
        setWeightArrays();
    }
}
