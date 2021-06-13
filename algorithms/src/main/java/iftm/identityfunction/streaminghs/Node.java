package iftm.identityfunction.streaminghs;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/*
A class that represents a node in an HS-Tree
 */
public class Node implements Serializable {

    private final int myDepth; //depth of this node. See Node.k in paper
    private final int maxDepth; //depth of a leaf in this tree
    private Node leftChild;  //left Child of this Node (instances smaller than halfPoint go here)
    private Node rightChild; //right Child of this Node (instances greater OR EQUAL than halfPoint go here)
    private Node parent;   //parent Node of this Node
    private double[] myMax; //array of maximum values in all dimensions this node contains
    private double[] myMin; //array of minimum values in all dims this node contains
    private int halvingDim; //dimension by which this node's domain will be halved by this Node
    private double halfPoint; //point that halves the domain. NOTE: < goes to left child, >= goes to right child
    private boolean amLeaf; // true if this node is a leaf
    private boolean amRoot; // true if this node is the root of a tree
    private final int sizeLimit; // the minimum number of instances in the reference counter of a node, at which the anomalyScore is calculated.
    private int referenceMass; // saves mass of this node in the reference window
    private int latestMass; // saves mass of this node in the latest window

    public Node(int depth, int maxD, int nrOfDims, double[] min, double[] max, int sizeLimit, Node parent) {
        this.myDepth = depth;
        this.parent = parent;
        this.myMin = min;
        this.myMax = max;
        this.maxDepth = maxD;
        this.sizeLimit = sizeLimit;

        referenceMass = 0;
        latestMass = 0;

        if(myDepth == 0){ amRoot = true; } else { amRoot = false; }

        if(myDepth == maxDepth){
            amLeaf = true;
        }else{ // create Children
            amLeaf = false;

            halvingDim = ThreadLocalRandom.current().nextInt(0, nrOfDims);
            halfPoint = (min[halvingDim]+max[halvingDim])/2;

            // create left Child
            double[] tempMax = myMax.clone();
            tempMax[halvingDim] = halfPoint;
            leftChild = new Node(myDepth+1, maxDepth, nrOfDims, myMin.clone(), tempMax, sizeLimit, this);

            //create right Child
            double[] tempMin = myMin.clone();
            tempMin[halvingDim] = halfPoint;
            rightChild = new Node(myDepth+1, maxDepth, nrOfDims, tempMin, myMax.clone(), sizeLimit, this);
        }

    }

    // TODO Mass only needs to be kept in Leaves if we ignore SizeLimit

    /*
     * inserts an instance into the tree and returns its anomaly score.
     */
    public int insertSample(double[] instance, boolean scoreCreated, int returnScore) {
        latestMass++;
        if (!scoreCreated && referenceMass <= sizeLimit) {
            returnScore = referenceMass * (int) Math.pow(2, myDepth);
            scoreCreated = true;
        } else if (!scoreCreated && amLeaf) {
            return referenceMass * (int) Math.pow(2, myDepth);
        }

        if (!amLeaf && instance[halvingDim] < halfPoint) {
            return leftChild.insertSample(instance, scoreCreated, returnScore);
        } else if (!amLeaf && instance[halvingDim] >= halfPoint) {
            return rightChild.insertSample(instance, scoreCreated, returnScore);
        }

        return returnScore;
    }

    public int predictSample(double[] instance, boolean scoreCreated, int returnScore) {
        if (!scoreCreated && referenceMass <= sizeLimit) {
            returnScore = referenceMass * (int) Math.pow(2, myDepth);
            scoreCreated = true;
        } else if (!scoreCreated && amLeaf) {
            return referenceMass * (int) Math.pow(2, myDepth);
        }

        if (!amLeaf && instance[halvingDim] < halfPoint) {
            return leftChild.predictSample(instance, scoreCreated, returnScore);
        } else if (!amLeaf && instance[halvingDim] >= halfPoint) {
            return rightChild.predictSample(instance, scoreCreated, returnScore);
        }

        return returnScore;
    }

    public void updateReference() {
        // TODO the paper says to replace referenceMass with latestMass only if latestMass or referenceMass are non-zero.
        // figure out if that is important.

        referenceMass = latestMass;
        latestMass = 0;

        if (!amLeaf) {
            leftChild.updateReference();
            rightChild.updateReference();
        }
    }

    @Override
    public String toString() {
        String myValues;

        if (amRoot) {
            myValues = "HalvingDim =" + halvingDim;
        } else {
            myValues = "Dim=" + parent.halvingDim + " (" + myMin[parent.halvingDim] + ", " + myMax[parent.halvingDim] + ")";
        }

        return myValues;
    }

    public boolean isAmLeaf() {
        return amLeaf;
    }

    public Node getParent() {
        return parent;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public boolean isAmRoot() {
        return amRoot;
    }
}
