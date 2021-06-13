package iftm.identityfunction;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.HashMap;
import java.util.Map;

public abstract class DL4JIdentityFunction implements IdentityFunction {

    private MultiLayerNetwork network = null;
    private final int layers;
    private final double learningRate;

    public DL4JIdentityFunction(int layers, double learningRate){
        this.layers = layers;
        this.learningRate = learningRate;
    }

    @Override
    public double[] predict(double[] values) {
        if (network == null) {
            buildNetwork(values.length);
        }
        INDArray data = parse(values);
        INDArray result;
        try {
            result = network.output(data);
        } catch (Exception e) {
            buildNetwork(values.length);
            result = network.output(data);
        }
        return parse(result);
    }

    @Override
    public void train(double[] values) {
        if (network == null) {
            buildNetwork(values.length);
        }
        INDArray data = parse(values);
        try {
            network.fit(data, data);
        } catch (Exception e) {
            buildNetwork(values.length);
            network.fit(data, data);
        }
    }

    abstract MultiLayerConfiguration networkConfig(int numInput);

    private void buildNetwork(int numInput) {
        MultiLayerConfiguration conf = networkConfig(numInput);
        conf.setDataType(DataType.DOUBLE);
        network = new MultiLayerNetwork(conf);
        network.init();
    }

    private double[] parse(INDArray values) {
        double[] array = new double[(int) values.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = values.getDouble(i);
        }
        return array;
    }

    private INDArray parse(double[] values){
        INDArray data = Nd4j.zeros(DataType.DOUBLE, 1, values.length);
        data.addiRowVector(Nd4j.create(new double[][]{values}));
        return data;
    }

    public int getLayers() {
        return layers;
    }

    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("model.hyperparameter.layers", (double) layers);
        stats.put("model.hyperparameter.learningRate", learningRate);
        return stats;
    }
}
