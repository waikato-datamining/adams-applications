import adams.ml.dl4j.model.AbstractModelConfiguratorScript as AbstractModelConfiguratorScript 
import org.deeplearning4j.nn.conf.NeuralNetConfiguration as NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.OutputLayer as OutputLayer
import org.deeplearning4j.nn.conf.layers.RBM as RBM
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork as MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit as WeightInit
import org.nd4j.linalg.lossfunctions.LossFunctions as LossFunctions
import org.nd4j.linalg.activations.Activation as Activation
import org.deeplearning4j.nn.conf.layers.DenseLayer as DenseLayer

class SimpleDl4jModel(AbstractModelConfiguratorScript):
    """
    A simple Jython model configurator script for the IRIS dataset.
    
    @author FracPete (fracpete at waikato dot ac dot nz)
    @version $Revision: 6612 $
    """

    def __init__(self):
        """
        Initializes the configurator.
        """

        AbstractModelConfiguratorScript.__init__(self)

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        """

        return "Simple model configuration for the IRIS dataset."
    
    def configureModel(self, numInput, numOutput):
        """
        Returns the configured model.
        
        :param numInput: the number of input nodes 
        :param numOutput: the number of output nodes
        :return: the model
        """
        
        # get parameters
        seed = self.getAdditionalOptions().getInteger("seed", 6)
        numIterations = self.getAdditionalOptions().getInteger("iterations", 1000)
        learningRate = self.getAdditionalOptions().getDouble("learningrate", 0.1)
        useRegularization = self.getAdditionalOptions().getBoolean("regularization", True)
        l2 = self.getAdditionalOptions().getDouble("l2", 1e-4)
        hiddenNodes = self.getAdditionalOptions().getInteger("hiddennodes", 3)
        outputLossFunction = LossFunctions.LossFunction.valueOf(self.getAdditionalOptions().getString("outputlossfunction", LossFunctions.LossFunction.MCXENT.toString()))
        activation = Activation.valueOf(self.getAdditionalOptions().getString("activation", Activation.TANH.toString()));
        outputActivation = Activation.valueOf(self.getAdditionalOptions().getString("outputactivation", Activation.SOFTMAX.toString()));
        weightInit = WeightInit.valueOf(self.getAdditionalOptions().getString("weightinit", WeightInit.XAVIER.toString()));

        # configure network
        conf = NeuralNetConfiguration.Builder() \
                .seed(seed) \
                .iterations(numIterations) \
                .activation(activation) \
                .weightInit(weightInit) \
                .learningRate(learningRate) \
                .regularization(useRegularization) \
                .l2(l2) \
                .list() \
                .layer(0, DenseLayer.Builder() \
                    .nIn(numInput) \
                    .nOut(hiddenNodes) \
                    .build()) \
                .layer(1, DenseLayer.Builder() \
                    .nIn(hiddenNodes) \
                    .nOut(hiddenNodes) \
                    .build()) \
                .layer(2, OutputLayer.Builder(outputLossFunction) \
                .activation(outputActivation) \
                    .nIn(hiddenNodes) \
                    .nOut(numOutput) \
                    .build()) \
                .backprop(True) \
                .pretrain(False) \
                .build()

        return MultiLayerNetwork(conf)
