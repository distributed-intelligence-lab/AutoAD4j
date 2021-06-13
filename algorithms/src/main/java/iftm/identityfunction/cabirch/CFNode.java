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
 *  CFNode.java
 *  Copyright (C) 2009 Roberto Perdisci (roberto.perdisci@gmail.com)
 */
package iftm.identityfunction.cabirch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 * @version 0.1
 *
 */
public class CFNode implements Serializable {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private List<CFEntry> entries = null; // stores the CFEntries for this node
    private int maxNodeEntries = 0; // max number of entries per node (parameter B)
    private double distThreshold = 0; // the distance threshold (parameter T), a.k.a. "radius"
    private boolean leafStatus = false; // if true, this is a leaf
    private CFNode nextLeaf = null; // pointer to the next leaf (if not a leaf, pointer will be null)
    private CFNode previousLeaf = null; // pointer to the previous leaf (if not a leaf, pointer will be null)
    private boolean applyMergingRefinement = false; // if true, merging refinement will be applied after every split

    static final Logger LOG = Logger.getLogger(CFNode.class.getName());

    public CFNode(int maxNodeEntries, double distThreshold, boolean applyMergingRefinement, boolean leafStatus) {
        this.maxNodeEntries = maxNodeEntries;
        this.distThreshold = distThreshold;

        this.entries = new ArrayList<>();
        this.leafStatus = leafStatus;
        this.applyMergingRefinement = applyMergingRefinement;
    }

    public CFNode(){}

    /**
     *
     * @return the number of CFEntries in the node
     */
    public int size() {
        return entries.size();
    }

    /**
     *
     * @return true if this is only a place-holder node for maintaining correct pointers in the list of leaves
     */
    public boolean isDummy() {
        return (maxNodeEntries == 0 && distThreshold == 0 && this.size() == 0 && (previousLeaf != null || nextLeaf != null));
    }

    /**
     *
     * @return the max number of entries the node can host (parameter B)
     */
    public int getMaxNodeEntries() {
        return maxNodeEntries;
    }

    /**
     *
     * @return the distance threshold used to decide whether a CFEntry can absorb a new entry
     */
    public double getDistThreshold() {
        return distThreshold;
    }

    public CFNode getNextLeaf() {
        return nextLeaf;
    }

    protected CFNode getPreviousLeaf() {
        return previousLeaf;
    }

    protected void addToEntryList(CFEntry e) {
        this.entries.add(e);
    }

    public List<CFEntry> getEntries() {
        return this.entries;
    }

    /**
     * Retrieves the subcluster id of the closest leaf entry to e
     *
     * @param e the entry to be mapped
     * @return a positive integer, if the leaf entries were enumerated after data insertion is finished, otherwise -1
     */
    public int mapToClosestSubcluster(CFEntry e) {
        CFEntry closest = findClosestEntry(e);
        if (!closest.hasChild()) {
            return closest.getSubclusterID();
        }

        return closest.getChild().mapToClosestSubcluster(e);
    }

    /**
     * Retrieves the subcluster id of the closest leaf entry to e
     *
     * @param point
     * @return a positive integer, if the leaf entries were enumerated after data insertion is finished, otherwise -1
     */
    public CFEntry mapToClosestSubcluster(double[] point) {
        CFEntry closest = findClosestEntry(new CFEntry(point));
        if(closest ==null){
            return null;
        }
        if (!closest.hasChild()) {
            return closest;
        }

        return closest.getChild().mapToClosestSubcluster(point);
    }

    public int cluster(CFEntry e) {
        CFEntry closest = findClosestEntry(e);
        if (closest == null) {
            return -1;
        }
        if (!closest.hasChild()) {
            if (closest.isInside(e)) {
                return closest.getId();
            } else {
                return -1;
            }
        }
        return closest.getChild().cluster(e);
    }
    
    public int cluster(CFEntry e, double threshold) {
        CFEntry closest = findClosestEntry(e);
        if (closest == null) {
            return -1;
        }
        if (!closest.hasChild()) {
            if (closest.isInside(e, threshold)) {
                return closest.getId();
            } else {
                return -1;
            }
        }
        return closest.getChild().cluster(e, threshold);
    }

    /**
     * Inserts a new entry to the CFTree
     *
     * @param e the entry to be inserted
     * @return TRUE if the new entry could be inserted without problems, otherwise we need to split the node
     */
    public boolean insertEntry(CFEntry e) {
        if (entries.isEmpty()) { // if the node is empty we can insert the entry directly here
            entries.add(e);
            return true; // insert was successful. no split necessary
        }

        CFEntry closest = findClosestEntry(e);

        boolean dontSplit = false;
        if (closest.hasChild()) { // if closest has a child we go down with a recursive call
            dontSplit = closest.getChild().insertEntry(e);
            if (dontSplit) {
                closest.update(e); // this updates the CF to reflect the additional entry
                return true;
            } else {
                // if the node below /closest/ didn't have enough room to host the new entry
                // we need to split it
                CFEntryPair splitPair = splitEntry(closest);

                // after adding the new entries derived from splitting /closest/ to this node,
                // if we have more than maxEntries we return false, 
                // so that the parent node will be split as well to redistribute the "load"
                if (entries.size() > maxNodeEntries) {
                    return false;
                } else { // splitting stops at this node

                    if (applyMergingRefinement) // performs step 4 of insert process (see BIRCH paper, Section 4.3)
                    {
                        mergingRefinement(splitPair);
                    }

                    return true;
                }
            }
        } else if (closest.isWithinThreshold(e, distThreshold)) {
            // if  dist(closest,e) <= T, /e/ will be "absorbed" by /closest/
            closest.update(e);
            return true; // no split necessary at the parent level
        } else if (entries.size() < maxNodeEntries) {
            // if /closest/ does not have children, and dist(closest,e) > T
            // if there is enough room in this node, we simply add e to it
            entries.add(e);
            return true; // no split necessary at the parent level
        } else { // not enough space on this node
            entries.add(e); // adds it momentarily to this node
            return false;   // returns false so that the parent entry will be split
        }

    }

    /**
     *
     * @param closest the entry to be split
     * @return the new entries derived from splitting
     */
    public CFEntryPair splitEntry(CFEntry closest) {
        // IF there was a child, but we could not insert the new entry without problems THAN
        // split the child of closest entry

        CFNode oldNode = closest.getChild();
        List<CFEntry> oldEntries = closest.getChild().getEntries();
        CFEntryPair p = findFarthestEntryPair(oldEntries);

        CFEntry newEntry1 = new CFEntry();
        CFNode newNode1 = new CFNode(maxNodeEntries, distThreshold, applyMergingRefinement, oldNode.isLeaf());
        newEntry1.setChild(newNode1);

        CFEntry newEntry2 = new CFEntry();
        CFNode newNode2 = new CFNode(maxNodeEntries, distThreshold, applyMergingRefinement, oldNode.isLeaf());
        newEntry2.setChild(newNode2);

        if (oldNode.isLeaf()) { // we do this to preserve the pointers in the leafList 

            CFNode prevL = oldNode.getPreviousLeaf();
            CFNode nextL = oldNode.getNextLeaf();

            if (prevL != null) {
                prevL.setNextLeaf(newNode1);
            }

            if (nextL != null) {
                nextL.setPreviousLeaf(newNode2);
            }

            newNode1.setPreviousLeaf(prevL);
            newNode1.setNextLeaf(newNode2);
            newNode2.setPreviousLeaf(newNode1);
            newNode2.setNextLeaf(nextL);
        }

        redistributeEntries(oldEntries, p, newEntry1, newEntry2);
        // redistributes the entries in n between newEntry1 and newEntry2
        // according to the distance to p.e1 and p.e2

        entries.remove(closest); // this will be substitute by two new entries
        entries.add(newEntry1);
        entries.add(newEntry2);

        return new CFEntryPair(newEntry1, newEntry2);
    }

    /**
     * Called when splitting is necessary
     *
     * @param oldEntries
     * @param farEntries
     * @param newE1
     * @param newE2
     */
    protected void redistributeEntries(List<CFEntry> oldEntries, CFEntryPair farEntries, CFEntry newE1, CFEntry newE2) {
        for (CFEntry e : oldEntries) {
            double dist1 = farEntries.getE1().distance(e);
            double dist2 = farEntries.getE2().distance(e);

            if (dist1 <= dist2) {
                newE1.addToChild(e);
                newE1.update(e);
            } else {
                newE2.addToChild(e);
                newE2.update(e);
            }
        }
    }

    /**
     * Called when "merging refinement" is attempted but no actual merging can be applied
     *
     * @param oldEntries1
     * @param oldEntries2
     * @param closeEntries
     * @param newE1
     * @param newE2
     */
    protected void redistributeEntries(List<CFEntry> oldEntries1, List<CFEntry> oldEntries2, CFEntryPair closeEntries,
            CFEntry newE1, CFEntry newE2) {
        ArrayList<CFEntry> v = new ArrayList<>();
        v.addAll(oldEntries1);
        v.addAll(oldEntries2);

        for (CFEntry e : v) {
            double dist1 = closeEntries.getE1().distance(e);
            double dist2 = closeEntries.getE2().distance(e);

            if (dist1 <= dist2) {
                if (newE1.getChildSize() < maxNodeEntries) {
                    newE1.addToChild(e);
                    newE1.update(e);
                } else {
                    newE2.addToChild(e);
                    newE2.update(e);
                }
            } else {
                if (newE2.getChildSize() < maxNodeEntries) {
                    newE2.addToChild(e);
                    newE2.update(e);
                } else {
                    newE1.addToChild(e);
                    newE1.update(e);
                }
            }
        }
    }

    /**
     * Called when "merging refinement" is attempted and two entries are actually merged
     *
     * @param oldEntries1
     * @param oldEntries2
     * @param newE
     */
    protected void redistributeEntries(List<CFEntry> oldEntries1, List<CFEntry> oldEntries2, CFEntry newE) {
        ArrayList<CFEntry> v = new ArrayList<>();
        v.addAll(oldEntries1);
        v.addAll(oldEntries2);

        for (CFEntry e : v) {
            newE.addToChild(e);
            newE.update(e);
        }
    }

    /**
     *
     * @param e a CFEntry
     * @return the entry in this node that is closest to e
     */
    protected CFEntry findClosestEntry(CFEntry e) {
        double minDist = Double.MAX_VALUE;
        CFEntry closest = null;
        for (CFEntry c : entries) {
            double d = c.distance(e);
            if (d < minDist) {
                minDist = d;
                closest = c;
            }
        }

        return closest;
    }

    protected CFEntryPair findFarthestEntryPair(List<CFEntry> entries) {
        if (entries.size() < 2) {
            return null;
        }

        double maxDist = -1;
        CFEntryPair p = new CFEntryPair();

        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                CFEntry e1 = entries.get(i);
                CFEntry e2 = entries.get(j);

                double dist = e1.distance(e2);
                if (dist > maxDist) {
                    p.setE1(e1);
                    p.setE2(e2);
                    maxDist = dist;
                }
            }
        }

        return p;
    }

    protected CFEntryPair findClosestEntryPair(List<CFEntry> entries) {
        if (entries.size() < 2) {
            return null; // not possible to find a valid pair
        }
        double minDist = Double.MAX_VALUE;
        CFEntryPair p = new CFEntryPair();

        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                CFEntry e1 = entries.get(i);
                CFEntry e2 = entries.get(j);

                double dist = e1.distance(e2);
                if (dist < minDist) {
                    p.setE1(e1);
                    p.setE2(e2);
                    minDist = dist;
                }
            }
        }

        return p;
    }

    /**
     * Used during merging refinement
     *
     * @param p
     * @param newE1
     * @param newE2
     */
    private void replaceClosestPairWithNewEntries(CFEntryPair p, CFEntry newE1, CFEntry newE2) {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).equals(p.getE1())) {
                this.entries.set(i, newE1);
            } else if (this.entries.get(i).equals(p.getE2())) {
                this.entries.set(i, newE2);
            }
        }
    }

    /**
     * Used during merging refinement
     *
     * @param p
     * @param newE
     */
    private void replaceClosestPairWithNewMergedEntry(CFEntryPair p, CFEntry newE) {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).equals(p.getE1())) {
                this.entries.set(i, newE);
            } else if (this.entries.get(i).equals(p.getE2())) {
                this.entries.remove(i);
            }
        }
    }

    /**
     *
     * @param splitEntries the entry that got split
     *
     */
    public void mergingRefinement(CFEntryPair splitEntries) {
        List<CFEntry> nodeEntries = this.entries;
        CFEntryPair p = findClosestEntryPair(nodeEntries);

        if (p == null || p.equals(splitEntries)) {
            return; // if the closet pair is the one that was just split, we terminate
        }
        CFNode oldNode1 = p.getE1().getChild();
        CFNode oldNode2 = p.getE2().getChild();

        List<CFEntry> oldNode1Entries = oldNode1.getEntries();
        List<CFEntry> oldNode2Entries = oldNode2.getEntries();

        if (oldNode1.isLeaf() != oldNode2.isLeaf()) { // just to make sure everything is going ok
            LOG.log(Level.SEVERE, "ERROR: Nodes at the same level must have same leaf status");
            System.exit(2);
        }

        if ((oldNode1Entries.size() + oldNode2Entries.size()) > maxNodeEntries) {
            // the two nodes cannot be merged into one (they will not fit)
            // in this case we simply redistribute them between p.e1 and p.e2

            CFEntry newEntry1 = new CFEntry();
            // note: in the CFNode construction below the last parameter is false 
            // because a split cannot happen at the leaf level 
            // (the only exception is when the root is first split, but that's treated separately)
            CFNode newNode1 = oldNode1;
            newNode1.resetEntries();
            newEntry1.setChild(newNode1);

            CFEntry newEntry2 = new CFEntry();
            CFNode newNode2 = oldNode2;
            newNode2.resetEntries();
            newEntry2.setChild(newNode2);

            redistributeEntries(oldNode1Entries, oldNode2Entries, p, newEntry1, newEntry2);
            replaceClosestPairWithNewEntries(p, newEntry1, newEntry2);

        } else {
            // if the the two closest entries can actually be merged into one single entry

            CFEntry newEntry = new CFEntry();
            // note: in the CFNode construction below the last parameter is false 
            // because a split cannot happen at the leaf level 
            // (the only exception is when the root is first split, but that's treated separately)
            CFNode newNode = new CFNode(maxNodeEntries, distThreshold, applyMergingRefinement, oldNode1.isLeaf());
            newEntry.setChild(newNode);

            redistributeEntries(oldNode1Entries, oldNode2Entries, newEntry);

            if (oldNode1.isLeaf() && oldNode2.isLeaf()) { // this is done to maintain proper links in the leafList
                if (oldNode1.getPreviousLeaf() != null) {
                    oldNode1.getPreviousLeaf().setNextLeaf(newNode);
                }
                if (oldNode1.getNextLeaf() != null) {
                    oldNode1.getNextLeaf().setPreviousLeaf(newNode);
                }
                newNode.setPreviousLeaf(oldNode1.getPreviousLeaf());
                newNode.setNextLeaf(oldNode1.getNextLeaf());

                // this is a dummy node that is only used to maintain proper links in the leafList
                // no CFEntry will ever point to this leaf
                CFNode dummy = new CFNode(0, 0, false, true);
                if (oldNode2.getPreviousLeaf() != null) {
                    oldNode2.getPreviousLeaf().setNextLeaf(dummy);
                }
                if (oldNode2.getNextLeaf() != null) {
                    oldNode2.getNextLeaf().setPreviousLeaf(dummy);
                }
                dummy.setPreviousLeaf(oldNode2.getPreviousLeaf());
                dummy.setNextLeaf(oldNode2.getNextLeaf());
            }

            replaceClosestPairWithNewMergedEntry(p, newEntry);
        }

        // merging refinement is done
    }

    private void resetEntries() {
        this.entries = new ArrayList<>();
    }

    public boolean isLeaf() {
        return this.leafStatus;
    }

    /**
     *
     * @return true if merging refinement is enabled
     */
    public boolean applyMergingRefinement() {
        return this.applyMergingRefinement;
    }

    protected void setLeafStatus(boolean status) {
        this.leafStatus = status;
    }

    protected void setNextLeaf(CFNode l) {
        this.nextLeaf = l;
    }

    protected void setPreviousLeaf(CFNode l) {
        this.previousLeaf = l;
    }

    protected int countChildrenNodes() {
        int n = 0;
        for (CFEntry e : this.entries) {
            if (e.hasChild()) {
                n++;
                n += e.getChild().countChildrenNodes();
            }
        }

        return n;
    }

    protected int countEntriesInChildrenNodes() {
        int n = 0;
        for (CFEntry e : this.entries) {
            if (e.hasChild()) {
                n += e.getChild().size();
                n += e.getChild().countChildrenNodes();
            }
        }

        return n;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();

        buff.append("==============================================").append(LINE_SEP);
        if (this.isLeaf()) {
            buff.append(">>> THIS IS A LEAF ").append(LINE_SEP);
        }
        buff.append("Num of Entries = ").append(entries.size()).append(LINE_SEP);
        buff.append("{");
        for (CFEntry e : entries) {
            buff.append("[").append(e).append("]");
        }
        buff.append("}").append(LINE_SEP);
        buff.append("==============================================").append(LINE_SEP);

        return buff.toString();
    }
}
