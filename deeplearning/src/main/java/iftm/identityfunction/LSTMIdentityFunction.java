package iftm.identityfunction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 *
 * @author fschmidt
 */
public class LSTMIdentityFunction extends DL4JIdentityFunction {

    public LSTMIdentityFunction(int layers, double learningRate) {
        super(layers, learningRate);
    }

    public LSTMIdentityFunction() {
        this(1, 0.001);
    }

    @Override
    protected MultiLayerConfiguration networkConfig(int inputSize) {
        int layernumber = 0;
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.seed(123);
        builder.biasInit(0);
        builder.miniBatch(false);
        builder.updater(new RmsProp(getLearningRate()));
        builder.weightInit(WeightInit.XAVIER);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();
        listBuilder.layer(layernumber++, new DenseLayer.Builder().nIn(inputSize).nOut(inputSize).activation(Activation.IDENTITY)
                .build());
        for(int i = 0; i < getLayers();i++){
            LSTM.Builder hiddenLayerBuilder = new LSTM.Builder();
            hiddenLayerBuilder.nIn(inputSize);
            hiddenLayerBuilder.nOut(inputSize);
            hiddenLayerBuilder.activation(Activation.IDENTITY);
            listBuilder.layer(layernumber++, hiddenLayerBuilder.build());
        }

        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE);
        outputLayerBuilder.activation(Activation.IDENTITY);
        outputLayerBuilder.nIn(inputSize);
        outputLayerBuilder.nOut(inputSize);
        listBuilder.layer(layernumber, outputLayerBuilder.build());

        return listBuilder.build();
    }

    @Override
    public IdentityFunction newInstance() {
        return new LSTMIdentityFunction(getLayers(), getLearningRate());
    }

}
