package iftm.anomalydetection;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author fschmidt
 */
public abstract class AnomalyDetection implements Serializable {
    
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private final int id;
    
    public AnomalyDetection() {
        id = COUNT.incrementAndGet();
    }
    
    public abstract DistancePredictionResult predict(double[] values);
    
    public abstract void train(double[] values);
    
    public void saveModel(File file) throws FileNotFoundException {
        saveModel(new FileOutputStream(file));
    }
    
    public abstract void saveModel(OutputStream outputStream);
    
    public void saveModel(String path) throws FileNotFoundException {
        saveModel(new File(path));
    }
    
    public AnomalyDetection loadModel(File file) throws IOException, ClassNotFoundException {
        return loadModel(new FileInputStream(file));
    }
    
    public abstract AnomalyDetection loadModel(InputStream inputStream) throws IOException, ClassNotFoundException;
    
    public AnomalyDetection loadModel(String path) throws IOException, ClassNotFoundException {
        File f = new File(path);
        //Create missing directory if necessary
        File directory = new File(f.getAbsoluteFile().getParentFile().getAbsolutePath());
        directory.mkdirs();
        return loadModel(new File(path));
    }
    
    public int getId() {
        return id;
    }

    public abstract Map<String, Double> getStatistics();

    public static Map<String, Double> mergeStatistics(List<Map<String, Double>> stats) {
        Map<String, DescriptiveStatistics> resultStats = new HashMap<>();
        for (Map<String, Double> stat : stats) {
            for (Map.Entry<String, Double> statEntry : stat.entrySet()) {
                if (resultStats.containsKey(statEntry.getKey())) {
                    resultStats.get(statEntry.getKey()).addValue(statEntry.getValue());
                } else {
                    DescriptiveStatistics aggStats = new DescriptiveStatistics();
                    aggStats.addValue(statEntry.getValue());
                    resultStats.put(statEntry.getKey(), aggStats);
                }
            }
        }
        Map<String, Double> avgStats = new HashMap<>();
        for (Map.Entry<String, DescriptiveStatistics> resultEntry : resultStats.entrySet()) {
            avgStats.put(resultEntry.getKey(), resultEntry.getValue().getMean());
        }
        return avgStats;
    }
}
