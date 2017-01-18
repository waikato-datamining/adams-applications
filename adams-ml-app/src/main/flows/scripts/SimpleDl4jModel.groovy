/*
 * A simple Groovy model configurator script for the IRIS dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6612 $
 */


import adams.ml.dl4j.model.AbstractModelConfiguratorScript
import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
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
        int seed = getAdditionalOptions().getInteger("seed", 6)
        int numIterations = getAdditionalOptions().getInteger("iterations", 1000)
        float learningRate = getAdditionalOptions().getDouble("learningrate", 0.1).floatValue()
        boolean useRegularization = getAdditionalOptions().getBoolean("regularization", true)
        double l2 = getAdditionalOptions().getDouble("l2", 1e-4)
        int hiddenNodes = getAdditionalOptions().getInteger("hiddennodes", 3)
        LossFunctions.LossFunction outputLossFunction = LossFunctions.LossFunction.valueOf(getAdditionalOptions().getString("outputlossfunction", LossFunctions.LossFunction.MCXENT.toString()))
        Activation activation = Activation.valueOf(getAdditionalOptions().getString("activation", Activation.TANH.toString()));
        Activation outputActivation = Activation.valueOf(getAdditionalOptions().getString("outputactivation", Activation.SOFTMAX.toString()));
        WeightInit weightInit = WeightInit.valueOf(getAdditionalOptions().getString("weightinit", WeightInit.XAVIER.toString()));

        // configure network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(numIterations)
                .activation(activation)
                .weightInit(weightInit)
                .learningRate(learningRate)
                .regularization(useRegularization)
                .l2(l2)
                .list()
                .layer(0, new DenseLayer.Builder()
                    .nIn(numInput)
                    .nOut(hiddenNodes)
                    .build())
                .layer(1, new DenseLayer.Builder()
                    .nIn(hiddenNodes)
                    .nOut(hiddenNodes)
                    .build())
                .layer(2, new OutputLayer.Builder(outputLossFunction)
                .activation(outputActivation)
                    .nIn(hiddenNodes)
                    .nOut(numOutput)
                    .build())
                .backprop(true)
                .pretrain(false)
                .build()

        return new MultiLayerNetwork(conf)
    }
}