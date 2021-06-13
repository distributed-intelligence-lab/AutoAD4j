package iftm.identityfunction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 *
 * @author fschmidt
 */
public class VariationalAutoencoderIdentityFunction extends EndcoderDecoderDL4J {

    /**
     *
     * @param layers number of layers for encoding. Will be also used for decoding.
     * @param learningRate
     */
    public VariationalAutoencoderIdentityFunction(int layers, double learningRate) {
        super(layers, learningRate);
    }

    public VariationalAutoencoderIdentityFunction() {
        this(1, 0.001);
    }

    @Override
    public MultiLayerConfiguration networkConfig(int inputSize) {
        int layernumber = 0;

        calcEncoderDecoder(inputSize);
        int[] encoderLayerSizes = getEncoderLayerSizes();
        int[] decoderLayerSizes = getDecoderLayerSizes();

        NeuralNetConfiguration.ListBuilder listBuilder = getListBuilder();
        listBuilder.layer(layernumber++, new VariationalAutoencoder.Builder()
                .activation(Activation.IDENTITY)
                .encoderLayerSizes(encoderLayerSizes)
                .decoderLayerSizes(decoderLayerSizes)
                .pzxActivationFunction(Activation.IDENTITY) //p(z|data) activation function
                .reconstructionDistribution(new GaussianReconstructionDistribution(Activation.IDENTITY.getActivationFunction()))
                .nIn(inputSize)
                .nOut(inputSize)
                .build());

        listBuilder.layer(layernumber, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(inputSize)
                .nOut(inputSize).build());
        return listBuilder.build();
    }

    @Override
    public IdentityFunction newInstance() {
        return new VariationalAutoencoderIdentityFunction(getLayers(), getLearningRate());
    }

}
