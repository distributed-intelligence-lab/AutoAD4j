/*
 *  This file is part of JBIRCH.
 *
 *  JBIRCH is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JBIRCH is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JBIRCH.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

 /*
 *  CFTree.java
 *  Copyright (C) 2009 Roberto Perdisci (roberto.perdisci@gmail.com)
 */
package iftm.identityfunction.cabirch;

import iftm.identityfunction.cabirch.decay.DecayFunction;
import iftm.identityfunction.cabirch.utils.DistanceUtil;
import iftm.identityfunction.cabirch.utils.VectorUtil;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an implementation of the BIRCH clustering algorithm described in:
 *
 * T. Zhang, R. Ramakrishnan, and M. Livny. "BIRCH: A New Data Clustering Algorithm and Its Applications" Data Mining and Knowledge
 * Discovery, 1997.
 *
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 * @version 0.1
 *
 */
public class CFTree implements Serializable {

    static final Logger LOG = Logger.getLogger(VectorUtil.class.getName());

    /**
     * The root node of the CFTree
     */
    private CFNode root;

    /**
     * dummy node that points to the list of leaves. used for fast retrieval of final subclusters
     */
    private CFNode leafListStart = null;

    /**
     * keeps count of the instances inserted into the tree
     */
    private int instanceIndex = 0;

    /**
     * if true, the tree is automatically rebuilt every time the memory limit is reached
     */
    private boolean automaticRebuild = true;

    private long periodicRebuild = 100000;
    private int numDims = 1;
    private boolean useDecay = false;
    private DecayFunction decayType;
    private boolean useRadiusDecay = false;
    private DecayFunction radiusDecay;
    private final int maxNodeEntries;

    public int getInstanceIndex() {
        return instanceIndex;
    }

    public DecayFunction getDecayType() {
        return decayType;
    }

    public DecayFunction getRadiusDecay() {
        return radiusDecay;
    }

    /**
     *
     * @param maxNodeEntries parameter B
     * @param distThreshold parameter T
     * @param applyMergingRefinement if true, activates merging refinement after each node split
     */
    public CFTree(int maxNodeEntries, double distThreshold, boolean applyMergingRefinement) {
        root = new CFNode(maxNodeEntries, distThreshold, applyMergingRefinement, true);
        leafListStart = new CFNode(0, 0, applyMergingRefinement, true); // this is a dummy node that points to the fist leaf
        leafListStart.setNextLeaf(root); // at this point root is the only node and therefore also the only leaf
        this.maxNodeEntries = maxNodeEntries;
    }

    public CFTree(){this.maxNodeEntries = 20;}

    public int getMaxNodeEntries() {
        return maxNodeEntries;
    }

    public CFNode getRoot() {
        return root;
    }

    /**
     * Gets the start of the list of leaf nodes (remember: the first node is a dummy node)
     *
     * @return
     */
    public CFNode getLeafListStart() {
        return this.leafListStart;
    }

    /**
     *
     * @param auto if true, and memory limit is reached, the tree is automatically rebuilt with larger threshold
     */
    public void setAutomaticRebuild(boolean auto) {
        this.automaticRebuild = auto;
    }

    public void useDecay(DecayFunction function) {
        this.useDecay = true;
        this.decayType = function;
    }

    public void useRadiusDecay(DecayFunction function) {
        this.useRadiusDecay = true;
        this.radiusDecay = function;
    }

    public void setPeriodicRebuild(long period) {
        this.periodicRebuild = period;
    }

    /**
     * Inserts a single pattern vector into the CFTree
     *
     * @param x the pattern vector to be inserted in the tree
     * @return true if insertion was successful
     */
    public boolean insertEntry(double[] x) {
        numDims = x.length;
        instanceIndex++;

        if (automaticRebuild && (instanceIndex % periodicRebuild) == 0) {
            rebuild();
        }

        boolean insertReturn = insertEntry(x, instanceIndex);
        if (useDecay) {
            decay();
        }

        return insertReturn;
    }

    /**
     * Insert a pattern vector with a specific associated pattern vector index. This method does not use periodic memory limit checks.
     *
     * @param x the pattern vector to be inserted in the tree
     * @param index a specific index associated to the pattern vector x
     * @return true if insertion was successful
     */
    public boolean insertEntry(double[] x, int index) {
        CFEntry e = new CFEntry(x, index);

        return insertEntry(e);
    }

    /**
     * Inserts an entire CFEntry into the tree. Used for tree rebuilding.
     *
     * @param e the CFEntry to insert
     * @return true if insertion happened without problems
     */
    private boolean insertEntry(CFEntry e) {

        boolean dontSplit = root.insertEntry(e);
        if (!dontSplit) {
            // if dontSplit is false, it means there was not enough space to insert the new entry in the tree, 
            // therefore wee need to split the root to make more room
            splitRoot();

            if (automaticRebuild) {
                // rebuilds the tree if we reached or exceeded memory limits (rebuildIfAboveMemLimit())
                rebuild();
            }
        }

        return true; // after root is split, we are sure x was inserted correctly in the tree, and we return true
    }

    /**
     * Every time we split the root, we check whether the memory limit imposed on the tree has been reached. In this case, we automatically
     * increase the distance threshold and rebuild the tree.
     *
     * It is worth noting that since we only check memory consumption only during root split, and not for all node splits (for performance
     * reasons), we cannot guarantee that the memory limit will not be exceeded. The tree may grow significantly between a root split and
     * the next. Furthermore, the computation of memory consumption using the SizeOf class is only approximate.
     *
     * Notice also that if the threshold grows to the point that all the entries fall into one entry of the root (i.e., the root is the only
     * node in the tree, and has only one sub-cluster) the automatic rebuild cannot decrease the memory consumption (because increasing the
     * threshold has not effect on reducing the size of the tree), and if Java runs out of memory the program will terminate.
     *
     * @return true if rebuilt
     */
    private boolean rebuild() {
        LOG.log(Level.FINE, "############## Rebuilding the Tree...");
        LOG.log(Level.FINE, "############## Current Threshold = {0}", root.getDistThreshold());

        double newThreshold = computeNewThreshold(leafListStart, root.getDistThreshold());

        CFTree newTree = this.rebuildTree(root.getMaxNodeEntries(), newThreshold, root.applyMergingRefinement(), true);
        copyTree(newTree);

        return true;
    }

    /**
     * Splits the root to accommodate a new entry. The height of the tree grows by one.
     */
    private void splitRoot() {
        // the split happens by finding the two entries in this node that are the most far apart
        // we then use these two entries as a "pivot" to redistribute the old entries into two new nodes

        CFEntryPair p = root.findFarthestEntryPair(root.getEntries());

        CFEntry newEntry1 = new CFEntry();
        CFNode newNode1 = new CFNode(root.getMaxNodeEntries(), root.getDistThreshold(), root.applyMergingRefinement(), root.isLeaf());
        newEntry1.setChild(newNode1);

        CFEntry newEntry2 = new CFEntry();
        CFNode newNode2 = new CFNode(root.getMaxNodeEntries(), root.getDistThreshold(), root.applyMergingRefinement(), root.isLeaf());
        newEntry2.setChild(newNode2);

        // the new root that hosts the new entries
        CFNode newRoot = new CFNode(root.getMaxNodeEntries(), root.getDistThreshold(), root.applyMergingRefinement(), false);
        newRoot.addToEntryList(newEntry1);
        newRoot.addToEntryList(newEntry2);

        // this updates the pointers to the list of leaves
        if (root.isLeaf()) { // if root was a leaf
            leafListStart.setNextLeaf(newNode1);
            newNode1.setPreviousLeaf(leafListStart);
            newNode1.setNextLeaf(newNode2);
            newNode2.setPreviousLeaf(newNode1);
        }

        // redistributes the entries in the root between newEntry1 and newEntry2
        // according to the distance to p.e1 and p.e2
        root.redistributeEntries(root.getEntries(), p, newEntry1, newEntry2);

        // updates the root
        root = newRoot;
    }

    /**
     * Overwrites the structure of this tree (all nodes, entreis, and leaf list) with the structure of newTree.
     *
     * @param newTree the tree to be copied
     */
    private void copyTree(CFTree newTree) {
        this.root = newTree.root;
        this.leafListStart = newTree.leafListStart;
    }

    /**
     * Computes a new threshold based on the average distance of the closest subclusters in each leaf node
     *
     * @param leafListStart the pointer to the start of the list (the first node is assumed to be a place-holder dummy node)
     * @param currentThreshold
     * @return the new threshold
     */
    public double computeNewThreshold(CFNode leafListStart, double currentThreshold) {
        double avgDist = 0;
        int n = 0;

        CFNode l = leafListStart.getNextLeaf();
        while (l != null) {
            if (!l.isDummy()) {
                CFEntryPair p = l.findClosestEntryPair(l.getEntries());
                if (p != null) {
                    avgDist += p.getE1().distance(p.getE2());
                    n++;

                    /* This is a possible alternative: Overall avg distance between leaf entries
					CFEntry[] v = l.getEntries().toArray(new CFEntry[0]);
					for(int i=0; i < v.length-1; i++) {
						for(int j=i+1; j < v.length; j++) {
							avgDist += v[i].distance(v[j], distFunction);
							n++;
						}
					}*/
                }
            }
            l = l.getNextLeaf();
        }

        double newThreshold = 0;
        if (n > 0) {
            newThreshold = avgDist / (double) n;
        }

        if (newThreshold <= currentThreshold) { // this guarantees that newThreshold always increases compared to currentThreshold
            newThreshold = 2 * currentThreshold;
        }

        return newThreshold;
    }

    /**
     * This implementation of the rebuilding algorithm is different from the one described in Section 4.5 of the paper. However the effect
     * is practically the same. Namely, given a tree t_i build using threshold T_i, if we set a new threshold T_(i+1) and call rebuildTree
     * (assuming maxEntries stays the same) we will obtain a more compact tree.
     *
     * Since the CFTree is sensitive to the order of the data, there may be cases in which, if we set the T_(i+1) so that non of the
     * sub-clusters (i.e., the leaf entries) can be merged (e.g., T_(i+1)=-1) we might actually obtain a new tree t_(i+1) containing more
     * nodes than t_i. However, the obtained sub-clusters in t_(i+1) will be identical to the sub-clusters in t_i.
     *
     * In practice, though, if T_(i+1) > T_(i), the tree t_(i+1) will usually be smaller than t_i. Although the Reducibility Theorem in
     * Section 4.5 may not hold anymore, in practice this will not be a big problem, since even in those cases in which t_(i+1)>t_i, the
     * growth should be very small.
     *
     * The advantage is that relaxing the constraint that the size of t_(i+1) must be less than t_i makes the implementation of the
     * rebuilding algorithm much easier.
     *
     * @param newMaxEntries the new number of entries per node
     * @param newThreshold the new threshold
     * @param applyMergingRefinement if true, merging refinement will be applied after every split
     * @param discardOldTree if true, the old tree will be discarded (to free memory)
     *
     * @return the new (usually more compact) CFTree
     */
    public CFTree rebuildTree(int newMaxEntries, double newThreshold, boolean applyMergingRefinement, boolean discardOldTree) {
        CFTree newTree = new CFTree(newMaxEntries, newThreshold, applyMergingRefinement);
        newTree.instanceIndex = this.instanceIndex;
//        newTree.memLimit = this.memLimit;

        CFNode oldLeavesList = this.leafListStart.getNextLeaf(); // remember: the node this.leafListStart is a dummy node (place holder for beginning of leaf list)

        if (discardOldTree) {
            this.root = null;
        }

        CFNode leaf = oldLeavesList;
        while (leaf != null) {
            if (!leaf.isDummy()) {
                for (CFEntry e : leaf.getEntries()) {
                    CFEntry newE = e;
                    if (!discardOldTree) // we need to make a deep copy of e
                    {
                        newE = new CFEntry(e);
                    }

                    newTree.insertEntry(newE);
                }
            }

            leaf = leaf.getNextLeaf();
        }

        if (discardOldTree) {
            this.leafListStart = null;
        }

        return newTree;
    }

    /**
     *
     * @return a list of subcluster, and for each subcluster a list of pattern vector indexes that belong to it
     */
    public List<List<Integer>> getSubclusterMembers() {
        List<List<Integer>> membersList = new ArrayList<>();

        CFNode l = leafListStart.getNextLeaf(); // the first leaf is dummy!
        while (l != null) {
            if (!l.isDummy()) {
                for (CFEntry e : l.getEntries()) {
                    membersList.add(e.getIndexList());
                }
            }
            l = l.getNextLeaf();
        }

        return membersList;
    }

    /**
     * Signals the fact that we finished inserting data. The obtained subclusters will be assigned a positive, unique ID number
     */
    public void finishedInsertingData() {
        CFNode l = leafListStart.getNextLeaf(); // the first leaf is dummy!

        int id = 0;
        while (l != null) {
            if (!l.isDummy()) {
                for (CFEntry e : l.getEntries()) {
                    id++;
                    e.setSubclusterID(id);
                }
            }
            l = l.getNextLeaf();
        }
    }

    /**
     * Retrieves the subcluster id of the closest leaf entry to e
     *
     * @param x the entry to be mapped
     * @return a positive integer, if the leaf entries were enumerated using finishedInsertingData(), otherwise -1
     */
    public int mapToClosestSubcluster(double[] x) {
        CFEntry e = new CFEntry(x);
        return root.mapToClosestSubcluster(e);
    }

    public int cluster(double[] x) {
        CFEntry e = new CFEntry(x);
        return root.cluster(e);
    }
    
    public int cluster(double[] x, double threshold) {
        CFEntry e = new CFEntry(x);
        return root.cluster(e, threshold);
    }

    /**
     * Computes an estimate of the cost of running an O(n^2) algorithm to split each subcluster in more fine-grained clusters
     *
     * @return sqrt(sum_i[(n_i)^2]), where n_i is the number of members of the i-th subcluster
     */
    public double computeSumLambdaSquared() {
        double lambdaSS = 0;

        CFNode l = leafListStart.getNextLeaf();
        while (l != null) {
            if (!l.isDummy()) {
                for (CFEntry e : l.getEntries()) {
                    lambdaSS += Math.pow(e.getIndexList().size(), 2);
                }
            }
            l = l.getNextLeaf();
        }

        return Math.sqrt(lambdaSS);
    }

    /**
     * prints the CFTree
     */
    public void printCFTree() {
        System.err.println(root);
    }

    /**
     * Counts the nodes of the tree (including leaves)
     *
     * @return the number of nodes in the tree
     */
    public int countNodes() {
        int n = 1; // at least root has to be present
        n += root.countChildrenNodes();

        return n;
    }

    /**
     * Counts the number of CFEntries in the tree
     *
     * @return the number of entries in the tree
     */
    public int countEntries() {
        int n = root.size(); // at least root has to be present
        n += root.countEntriesInChildrenNodes();

        return n;
    }

    /**
     * Counts the number of leaf entries (i.e., the number of sub-clusters in the tree)
     *
     * @return the number of leaf entries (i.e., the number of sub-clusters)
     */
    public int countLeafEntries() {
        int i = 0;
        CFNode l = leafListStart.getNextLeaf();
        while (l != null) {
            if (!l.isDummy()) {
                i += l.size();
            }

            l = l.getNextLeaf();
        }

        return i;
    }

    /**
     * Prints the index of all the pattern vectors that fall into the leaf nodes. This is only useful for debugging purposes.
     */
    public void printLeafIndexes() {
        List<Integer> indexes = new ArrayList<>();

        CFNode l = leafListStart.getNextLeaf();
        while (l != null) {
            if (!l.isDummy()) {
                System.err.println(l);
                for (CFEntry e : l.getEntries()) {
                    indexes.addAll(e.getIndexList());
                }
            }
            l = l.getNextLeaf();
        }

        Integer[] v = indexes.toArray(new Integer[0]);
        Arrays.sort(v);
        System.err.println("Num of Indexes = " + v.length);
        System.err.println(Arrays.toString(v));
    }

    /**
     * Prints the index of the pattern vectors in each leaf entry (i.e., each subcluster)
     */
    public void printLeafEntries() {
        int i = 0;
        CFNode l = leafListStart.getNextLeaf();
        while (l != null) {
            if (!l.isDummy()) {
                for (CFEntry e : l.getEntries()) {
                    System.err.println("[[" + (++i) + "]]");
                    Integer[] v = e.getIndexList().toArray(new Integer[0]);
                    Arrays.sort(v);
                    System.err.println(Arrays.toString(v));
                }
            }

            l = l.getNextLeaf();
        }
    }

    /**
     * Gets the distance to a given point from the Node's sphere
     *
     * @param point point to compute the distance to
     * @return distance from point to sphere
     */
    public double[] getDistance(double[] point) {
        CFEntry entry = root.mapToClosestSubcluster(point);
        if (DistanceUtil.euclidean(entry.getCentroid(), point) - entry.getRadius() <= 0) {
            return new double[point.length];
        }

        return VectorUtil.absVec(VectorUtil.subVec(VectorUtil.absVec(VectorUtil.subVec(entry.getCentroid(), point)), VectorUtil.absVec(entry
                .getRadiusVector())));
    }

    /**
     * Gets the closest border point on the sphere between centroid and given point.
     *
     * @param point point to compute the closest border point
     * @return border point on sphere from point to sphere
     */
    public double[] getBorderPoint(double[] point) {
        CFEntry entry = root.mapToClosestSubcluster(point);
        if(entry==null){
            double[] dirVec = new double[point.length];
            for(int i = 0; i < dirVec.length;i++){
                dirVec[i] = Double.MAX_VALUE;
            }
            return dirVec;
        }else {
            double[] dirVec = VectorUtil.subVec(point, entry.getCentroid());
            double distDirVec = 0.0;
            for (double dirVecEntry : dirVec) {
                distDirVec += (dirVecEntry * dirVecEntry);
            }
            if (distDirVec == 0.0) {
                return dirVec;
            }
            distDirVec = Math.sqrt(distDirVec);
            double changeDir = entry.getRadius() / distDirVec;
            for (int i = 0; i < dirVec.length; i++) {
                //Add the direction-vector to the centroid
                dirVec[i] = entry.getCentroid()[i] + dirVec[i] * changeDir ;
            }

            return dirVec;
        }
    }
//    public double[] getBorderPoint(double[] point) {
//        CFEntry entry = root.mapToClosestSubcluster(point);
//        if(entry==null){
//            double[] dirVec = new double[point.length];
//            for(int i = 0; i < dirVec.length;i++){
//                dirVec[i] = Double.MAX_VALUE;
//            }
//            return dirVec;
//        }else {
//            double[] dirVec = VectorUtil.subVec(point, entry.getCentroid());
//            double distDirVec = 0.0;
//            for (double dirVecEntry : dirVec) {
//                distDirVec += (dirVecEntry * dirVecEntry);
//            }
//            if (distDirVec == 0.0) {
//                return dirVec;
//            }
//            distDirVec = Math.sqrt(distDirVec);
//            double changeDir = entry.getRadius() / distDirVec;
//            for (int i = 0; i < dirVec.length; i++) {
//                dirVec[i] = dirVec[i] * changeDir;
//            }
//            return dirVec;
//        }
//    }

    public double[] getOldClosestBorderPoint(double[] point) {
        CFEntry entry = root.mapToClosestSubcluster(point);
        if(entry==null){
            double[] dirVec = new double[point.length];
            for(int i = 0; i < dirVec.length;i++){
                dirVec[i] = Double.MAX_VALUE;
            }
            return dirVec;
        }else {
            double[] dirVec = VectorUtil.subVec(point, entry.getCentroid());
            double distDirVec = 0.0;
            for (double dirVecEntry : dirVec) {
                distDirVec += (dirVecEntry * dirVecEntry);
            }
            if (distDirVec == 0.0) {
                return dirVec;
            }
            distDirVec = Math.sqrt(distDirVec);
            double changeDir = entry.getRadius() / distDirVec;
            for (int i = 0; i < dirVec.length; i++) {
                dirVec[i] = dirVec[i] * changeDir;
            }

            return dirVec;
        }
    }


    /**
     * Gets the closest border point on the sphere between centroid and given point
     * and returns the vector from the border point to the given point.
     *
     * @param point point to compute the closest border point
     * @return border point on sphere from point to sphere
     */
    public double[] getVectorFromBorder(double[] point) {
        CFEntry entry = root.mapToClosestSubcluster(point);
        if(entry==null){
            double[] dirVec = new double[point.length];
            for(int i = 0; i < dirVec.length;i++){
                dirVec[i] = Double.MAX_VALUE;
            }
            return dirVec;
        }else {
            double[] dirVec = VectorUtil.subVec(point, entry.getCentroid());
            double distDirVec = 0.0;
            for (double dirVecEntry : dirVec) {
                distDirVec += (dirVecEntry * dirVecEntry);
            }
            if (distDirVec == 0.0) {
                return dirVec;
            }
            distDirVec = Math.sqrt(distDirVec);
            double changeDir = (distDirVec - entry.getRadius()) / distDirVec;
            for (int i = 0; i < dirVec.length; i++) {
                dirVec[i] = dirVec[i] * changeDir;
            }
            return dirVec;
        }
    }

    public void decay() {
        //increase time in all leaf nodes
        CFNode l = leafListStart.getNextLeaf();
        double countLeafEntries = 1.0 / (double) countLeafEntries();
        while (l != null) {
            if (!l.isDummy()) {
                List<CFEntry> goodEntries = new ArrayList<>();
                for (CFEntry e : l.getEntries()) {
                    e.increaseTime();
                    e.decay(decayType, countLeafEntries);
                    if (useRadiusDecay) {
                        e.decayRadius(radiusDecay, countLeafEntries);
                    }
                    if(e.getN() > 0 && e.getRadius() >= 0){
                        goodEntries.add(e);
                    }
                }

                l.getEntries().clear();
                l.getEntries().addAll(goodEntries);
                if (l.getEntries().isEmpty()) {
                    //remove leaf and adapt leaf path. Propagate to upper nodes to check if they need to be removed
                    rebuild();
                }
            }
            l = l.getNextLeaf();
        }
    }

    public double getThreshold() {
        if (root == null) {
            return 0;
        }
        return root.getDistThreshold();
    }
}
