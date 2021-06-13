package iftm.identityfunction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 *
 * @author fschmidt
 */
public class AutoencoderLSTMIdentityFunction extends EndcoderDecoderDL4J {

    public AutoencoderLSTMIdentityFunction(int layers, double learningRate) {
        super(layers, learningRate);
    }

    public AutoencoderLSTMIdentityFunction() {
        this(1, 0.001);
    }

    @Override
    public IdentityFunction newInstance() {
        return new LSTMIdentityFunction(getLayers(), getLearningRate());
    }

    @Override
    protected MultiLayerConfiguration networkConfig(int inputSize) {
        int layernumber = 0;

        calcEncoderDecoder(inputSize);
        int[] encoderLayerSizes = getEncoderLayerSizes();
        int[] decoderLayerSizes = getDecoderLayerSizes();

        NeuralNetConfiguration.ListBuilder listBuilder = getListBuilder();
        listBuilder.layer(layernumber++, new DenseLayer.Builder().nIn(inputSize).nOut(inputSize).activation(Activation.IDENTITY)
                .build());

        for (int i = 0; i < getLayers() - 1; i++) {
            listBuilder.layer(layernumber++, new LSTM.Builder().nIn(encoderLayerSizes[i]).nOut(encoderLayerSizes[i + 1])
                    .activation(Activation.IDENTITY)
                    .build());
        }
        for (int i = 0; i < getLayers() - 1; i++) {
            listBuilder.layer(layernumber++, new LSTM.Builder().nIn(decoderLayerSizes[i]).nOut(decoderLayerSizes[i + 1])
                    .activation(Activation.IDENTITY)
                    .build());
        }

        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE);
        outputLayerBuilder.activation(Activation.IDENTITY);
        outputLayerBuilder.nIn(decoderLayerSizes[getLayers()-1]);
        outputLayerBuilder.nOut(inputSize);
        listBuilder.layer(layernumber, outputLayerBuilder.build());

        return listBuilder.build();
    }
}
