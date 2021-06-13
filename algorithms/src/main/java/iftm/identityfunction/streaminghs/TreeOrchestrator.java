package iftm.identityfunction.streaminghs;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class TreeOrchestrator implements Serializable{

    private final Node[] roots; // saves the roots of the half-space trees
    private final int nrOfTrees;
    private final int nrOfDimensions;
    private final int maxDepth;  //NOTE a tree with a maxDepth of n has n+1 levels!!! - root is depth ZERO.
    private final double[] min;
    private final double[] max;
    private final int windowSize;
    private int windowCounter; // keeps track of the number of samples that have been inserted.
    private final int sizeLimit; // the minimum number of instances in the reference counter of a node, at which the anomalyScore is calculated.

    public TreeOrchestrator(int nrOfTrees, int maxDepth, int windowSize, int nrOfDimensions, double[] minArray, double[] maxArray, int sizeLimit) {
        this.nrOfTrees = nrOfTrees;
        this.maxDepth = maxDepth;
        this.windowSize = windowSize;
        this.nrOfDimensions = nrOfDimensions;
        this.min = minArray.clone();
        this.max = maxArray.clone();
        this.sizeLimit = sizeLimit;
        this.windowCounter = 0;
        this.roots = new Node[nrOfTrees];
        createTrees();
    }

    private void createTrees() {

        for(int i = 0; i < nrOfTrees; i++) {
            double[] newMin = min.clone();
            double[] newMax = max.clone();

            // Random Perturbation of the datastream's domain in order to generate a more diverse family of Halfspace Trees
            for(int j = 0; j < nrOfDimensions; j++){
                double distance = newMax[j] - newMin[j];
                double randomPoint = ThreadLocalRandom.current().nextDouble();

                if(randomPoint < 0.5) {
                    newMax[j] = newMin[j] + (randomPoint*distance) + 2.0*((1.0-randomPoint) * distance);
                    newMin[j] = newMin[j] + (randomPoint*distance) - 2.0*((1.0-randomPoint) * distance);
                } else {
                    newMax[j] = newMin[j] + (randomPoint*distance) + 2.0*(randomPoint * distance);
                    newMin[j] = newMin[j] + (randomPoint*distance) - 2.0*(randomPoint * distance);
                }
            }
            roots[i] = new Node(0, maxDepth, nrOfDimensions, newMin, newMax, sizeLimit, null);
        }
    }

    public int predictSample(double[] newSample) {
        // if the latest window is full, replace the reference mass with the latest mass in all trees.
        int anomalyScore = 0;
        for (int i = 0; i < nrOfTrees; i++) {
            anomalyScore = anomalyScore + roots[i].predictSample(newSample, false, 0);
        }
        return anomalyScore;
    }
    
    public int insertSample(double[] newSample) {
        // if the latest window is full, replace the reference mass with the latest mass in all trees.
        windowCounter++;
        if (windowCounter >= windowSize) {
            for (int i = 0; i < nrOfTrees; i++) {
                roots[i].updateReference();
            }
            windowCounter = 0;
        }

        int anomalyScore = 0;
        for (int i = 0; i < nrOfTrees; i++) {
            anomalyScore = anomalyScore + roots[i].insertSample(newSample, false, 0);
        }
        return anomalyScore;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < nrOfTrees; i++) {
            builder.append("\n" + "\n" + "\n" + "Tree Nr. " + i + "\n" + singleTreeToString(i));
        }

        return builder.toString();
    }

    public String singleTreeToString(int tree) {
        StringBuilder builder = new StringBuilder();
        int maxWidth = (int) Math.pow(2, maxDepth);
        String[][] treeString = new String[maxDepth + 1][maxWidth + 1];
        Node activeNode = roots[tree];

        int nrOfNodesInTree = 0; // calculates the total number of nodes in the tree
        for (int i = 0; i <= maxDepth; i++) {
            nrOfNodesInTree = nrOfNodesInTree + (int) Math.pow(2, i);
        }

        int depth = 0;
        int width = 0;
        treeString[depth][width] = activeNode.toString(); //adds the root to the string

        // Traverses the tree and saves nodes' values.
        while (width < maxWidth - 1) {
            if (activeNode.isAmLeaf()) {
                if (activeNode == activeNode.getParent().getLeftChild()) {
                    activeNode = activeNode.getParent().getRightChild();
                    width++;
                } else {
                    while (!activeNode.isAmRoot() && activeNode == activeNode.getParent().getRightChild()) {
                        activeNode = activeNode.getParent();
                        depth--;
                        width = width / 2;
                    }
                    activeNode = activeNode.getParent().getRightChild();
                    width++;
                }
            } else {
                activeNode = activeNode.getLeftChild();
                depth++;
                width = 2 * width;
            }
            treeString[depth][width] = activeNode.toString();
        }

        // This is where the output is constructed to resemble a tree.
        for (int i = 0; i <= maxDepth; i++) {
            int nodesThisDepth = (int) Math.pow(2, i);
            int seperatorsThisDepth = (int) (maxWidth / (double)(nodesThisDepth + 1));

            for (int j = 0; j < nodesThisDepth; j++) {
                for (int seps = 1; seps <= seperatorsThisDepth; seps++) {
                    builder.append("\t" + "\t" + "\t" + "\t");
                }
                builder.append(" " + treeString[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
