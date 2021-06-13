package iftm.identityfunction;

import iftm.identityfunction.cabirch.BIRCH;
import iftm.identityfunction.cabirch.CFEntry;
import iftm.identityfunction.cabirch.CFNode;
import iftm.identityfunction.cabirch.CFTree;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBirchIdentityFunction implements IdentityFunction {

    private final BIRCH model;
    private boolean useOldMethod = false;

    public AbstractBirchIdentityFunction(BIRCH model){
        this.model = model;
    }

    public AbstractBirchIdentityFunction() {
        this.model = new BIRCH();
    }

    @Override
    public double[] predict(double[] values) {
        int cluster = model.cluster(values);
        if(cluster!=-1){
            return new double[values.length];
            //return values;
        }
        if(useOldMethod) {
            return model.getOldClosestBorderPoint(values);
        }
        return model.getVectorFromBorder(values);
        //return domain.getClosestBorderPoint(values);
    }

    @Override
    public void train(double[] values) {
        model.train(values);
    }

    public BIRCH getModel() {
        return model;
    }

    public void useOldMethod(){
        this.useOldMethod = true;
    }

    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("identityfunction.hyperparameter.maxNumClusters", (double) model.getCfTree().getMaxNodeEntries());
        CFTree tree = model.getCfTree();
        CFNode n = tree.getLeafListStart();
        double clusterSize = 0;
        double clusterThresholdSize = 0;
        while (true) {
            if (n.getNextLeaf() == null) {
                break;
            }
            n = n.getNextLeaf();
            for (CFEntry e : n.getEntries()) {
                clusterSize += e.getRadius();
                clusterThresholdSize += e.getRadius() + tree.getThreshold();
            }
        }
        if (n.getEntries().size() > 0) {
            clusterSize /= (double) n.getEntries().size();
            clusterThresholdSize /= (double) n.getEntries().size();
        }
        stats.put("identityfunction.numClusters", (double) model.getCfTree().countLeafEntries());
        stats.put("identityfunction.clusterRadiusSize", clusterSize);
        stats.put("identityfunction.clusterRadiusPlusThresholdSize", clusterThresholdSize);
        return stats;
    }
}
