import adams.ml.dl4j.model.AbstractModelConfiguratorScript as AbstractModelConfiguratorScript 
import org.deeplearning4j.nn.api.Model as Model
import org.deeplearning4j.nn.api.OptimizationAlgorithm as OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration as MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration as NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater as Updater
import org.deeplearning4j.nn.conf.layers.OutputLayer as OutputLayer
import org.deeplearning4j.nn.conf.layers.RBM as RBM
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork as MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit as WeightInit
import org.nd4j.linalg.lossfunctions.LossFunctions as LossFunctions

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
        seed = self.getAdditionalOptions().getInteger("seed", 1)
        numIterations = self.getAdditionalOptions().getInteger("iterations", 1000)
        learningRate = self.getAdditionalOptions().getDouble("learningrate", 1e-6)
        optimization = OptimizationAlgorithm.valueOf(self.getAdditionalOptions().getString("optimization", OptimizationAlgorithm.CONJUGATE_GRADIENT.toString()))
        l1 = self.getAdditionalOptions().getDouble("l1", 1e-1)
        useRegularization = self.getAdditionalOptions().getBoolean("regularization", True)
        l2 = self.getAdditionalOptions().getDouble("l2", 2e-4)
        useDropConnect = self.getAdditionalOptions().getBoolean("dropconnect", True)
        hiddenNodes = self.getAdditionalOptions().getInteger("hiddennodes", 3)
        hiddenWeightInit = WeightInit.valueOf(self.getAdditionalOptions().getString("hiddenweightinit", WeightInit.XAVIER.toString()))
        hiddenActivation = self.getAdditionalOptions().getString("hiddenactivation", "relu")
        hiddenLossFunction = LossFunctions.LossFunction.valueOf(self.getAdditionalOptions().getString("hiddenlossfunction", LossFunctions.LossFunction.RMSE_XENT.toString()))
        hiddenUpdater = Updater.valueOf(self.getAdditionalOptions().getString("hiddenupdater", Updater.ADAGRAD.toString()))
        hiddenDropOut = self.getAdditionalOptions().getDouble("hiddendropout", 0.5)
        outputLossFunction = LossFunctions.LossFunction.valueOf(self.getAdditionalOptions().getString("outputlossfunction", LossFunctions.LossFunction.MCXENT.toString()))
        outputActivation = self.getAdditionalOptions().getString("outputactivation", "softmax")

        # configure network
        conf = NeuralNetConfiguration.Builder() \
            .seed(seed) \
            .iterations(numIterations) \
                .learningRate(learningRate) \
                .optimizationAlgo(optimization) \
                .l1(l1) \
                .regularization(useRegularization) \
                .l2(l2) \
                .useDropConnect(useDropConnect) \
                .list() \
                .layer(0, RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN) \
                    .nIn(numInput) \
                    .nOut(hiddenNodes) \
                    .weightInit(hiddenWeightInit) \
                    .k(1) \
                    .activation(hiddenActivation) \
                    .lossFunction(hiddenLossFunction) \
                    .updater(hiddenUpdater) \
                    .dropOut(hiddenDropOut) \
                    .build()
                ) \
                .layer(1, OutputLayer.Builder(outputLossFunction) \
                    .nIn(hiddenNodes) \
                    .nOut(numOutput) \
                    .activation(outputActivation) \
                    .build()
                ) \
                .build()

        return MultiLayerNetwork(conf)
