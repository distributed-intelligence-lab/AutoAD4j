package iftm.identityfunction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

/**
 *
 * @author fschmidt
 */
public class AutoencoderIdentityFunction extends EndcoderDecoderDL4J {

    /**
     *
     * @param layers number of layers for encoding. Will be also used for decoding.
     * @param learningRate
     */
    public AutoencoderIdentityFunction(int layers, double learningRate) {
        super(layers, learningRate);
    }

    public AutoencoderIdentityFunction() {
        this(2, 0.01);
    }

    @Override
    public MultiLayerConfiguration networkConfig(int inputSize) {
        int layernumber = 0;
        calcEncoderDecoder(inputSize);
        int[] encoderLayerSizes = getEncoderLayerSizes();
        int[] decoderLayerSizes = getDecoderLayerSizes();

        NeuralNetConfiguration.ListBuilder listBuilder = getListBuilder();

        for (int i = 0; i < getLayers() - 1; i++) {
            listBuilder.layer(layernumber++, new DenseLayer.Builder().nIn(encoderLayerSizes[i]).nOut(encoderLayerSizes[i + 1])
                    .activation(Activation.IDENTITY)
                    .build());
        }
        for (int i = 0; i < getLayers() - 1; i++) {
            listBuilder.layer(layernumber++, new DenseLayer.Builder().nIn(decoderLayerSizes[i]).nOut(decoderLayerSizes[i + 1])
                    .activation(Activation.IDENTITY)
                    .build());
        }
        listBuilder.layer(layernumber, new OutputLayer.Builder(LossFunction.MSE).activation(Activation.IDENTITY)
                .nIn(decoderLayerSizes[getLayers()-1]).nOut(inputSize).build())
                .build();
        return listBuilder.build();
    }

    @Override
    public IdentityFunction newInstance() {
        return new AutoencoderIdentityFunction(getLayers(), getLearningRate());
    }

}
