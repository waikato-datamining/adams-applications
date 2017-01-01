/*
 * A simple Groovy model configurator script for the IRIS dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6612 $
 */

import adams.ml.dl4j.model.AbstractModelConfiguratorScript
import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.conf.layers.RBM
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.lossfunctions.LossFunctions

class SimpleDl4jModel
        extends AbstractModelConfiguratorScript {

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    public String globalInfo() {
        return "Simple model configuration for the IRIS dataset."
    }

    /**
     * Returns the configured model.
     *
     * @param numInput  the number of input nodes
     * @param numOutput the number of output nodes
     * @return          the model
     */
    public Model configureModel(int numInput, int numOutput) {
        // get parameters
        int seed = getAdditionalOptions().getInteger("seed", 1)
        int numIterations = getAdditionalOptions().getInteger("iterations", 1000)
        float learningRate = getAdditionalOptions().getDouble("learningrate", 1e-6).floatValue()
        OptimizationAlgorithm optimization = OptimizationAlgorithm.valueOf(getAdditionalOptions().getString("optiimization", OptimizationAlgorithm.CONJUGATE_GRADIENT.toString()))
        double l1 = getAdditionalOptions().getDouble("l1", 1e-1)
        boolean useRegularization = getAdditionalOptions().getBoolean("regularization", true)
        double l2 = getAdditionalOptions().getDouble("l2", 2e-4)
        boolean useDropConnect = getAdditionalOptions().getBoolean("dropconnect", true)
        int hiddenNodes = getAdditionalOptions().getInteger("hiddennodes", 3)
        WeightInit hiddenWeightInit = WeightInit.valueOf(getAdditionalOptions().getString("hiddenweightinit", WeightInit.XAVIER.toString()))
        String hiddenActivation = getAdditionalOptions().getString("hiddenactivation", "relu")
        LossFunctions.LossFunction hiddenLossFunction = LossFunctions.LossFunction.valueOf(getAdditionalOptions().getString("hiddenlossfunction", LossFunctions.LossFunction.RMSE_XENT.toString()))
        Updater hiddenUpdater = Updater.valueOf(getAdditionalOptions().getString("hiddenupdater", Updater.ADAGRAD.toString()))
        double hiddenDropOut = getAdditionalOptions().getDouble("hiddendropout", 0.5)
        LossFunctions.LossFunction outputLossFunction = LossFunctions.LossFunction.valueOf(getAdditionalOptions().getString("outputlossfunction", LossFunctions.LossFunction.MCXENT.toString()))
        String outputActivation = getAdditionalOptions().getString("outputactivation", "softmax")

        // configure network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed) // Locks in weight initialization for tuning
                .iterations(numIterations) // # training iterations predict/classify & backprop
                .learningRate(learningRate) // Optimization step size
                .optimizationAlgo(optimization) // Backprop to calculate gradients
                .l1(l1)
                .regularization(useRegularization)
                .l2(l2)
                .useDropConnect(useDropConnect)
                .list()
                .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
                    .nIn(numInput) // # input nodes
                    .nOut(hiddenNodes) // # fully connected hidden layer nodes. Add list if multiple layers.
                    .weightInit(hiddenWeightInit) // Weight initialization
                    .k(1) // # contrastive divergence iterations
                    .activation(hiddenActivation) // Activation function type
                    .lossFunction(hiddenLossFunction) // Loss function type
                    .updater(hiddenUpdater)
                    .dropOut(hiddenDropOut)
                    .build()
                ) // NN layer type
                .layer(1, new OutputLayer.Builder(outputLossFunction)
                    .nIn(hiddenNodes) // # input nodes
                    .nOut(numOutput) // # output nodes
                    .activation(outputActivation)
                    .build()
                ) // NN layer type
                .build();

        return new MultiLayerNetwork(conf);
    }
}