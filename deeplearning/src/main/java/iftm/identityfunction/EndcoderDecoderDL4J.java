package iftm.identityfunction;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.RmsProp;

public abstract class EndcoderDecoderDL4J extends DL4JIdentityFunction{

    private int[] encoderLayerSizes;
    private int[] decoderLayerSizes;

    public EndcoderDecoderDL4J(int layers, double learningRate) {
        super(layers, learningRate);
    }

    void calcEncoderDecoder(int inputSize){
        encoderLayerSizes = new int[getLayers()];
        decoderLayerSizes = new int[getLayers()];
        for (int i = 0; i < getLayers(); i++) {
            int layer = (int) Math.ceil((double) (inputSize * (i + 1)) / (double) (getLayers()));
            if (layer <= 0) {
                layer = 1;
            }
            encoderLayerSizes[getLayers() - i - 1] = layer;
            decoderLayerSizes[i] = layer;
        }
    }

    public int[] getEncoderLayerSizes() {
        return encoderLayerSizes;
    }

    public int[] getDecoderLayerSizes() {
        return decoderLayerSizes;
    }

    NeuralNetConfiguration.ListBuilder getListBuilder(){
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.seed(123);
        builder.biasInit(0);
        builder.miniBatch(false);
        builder.updater(new RmsProp(getLearningRate()));
        builder.weightInit(WeightInit.XAVIER);
        return builder.list();
    }
}
