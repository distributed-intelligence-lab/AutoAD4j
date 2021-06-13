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

import iftm.identityfunction.cabirch.decay.DecayFunction;
import iftm.identityfunction.cabirch.utils.VectorUtil;
import static iftm.identityfunction.cabirch.utils.VectorUtil.divVec;
import static iftm.identityfunction.cabirch.utils.VectorUtil.multVec;
import static iftm.identityfunction.cabirch.utils.VectorUtil.subVec;
import static iftm.identityfunction.cabirch.utils.VectorUtil.sumVec;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 *
 */
public class CFEntry implements Serializable {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private static AtomicInteger uniqueId = new AtomicInteger();
    private final int id;

    private double n = 0; // number of patterns summarized by this entry
    private double[] sumX = null;
    private double[] sumX2 = null;
    private CFNode child = null;
    private List<Integer> indexList = null;
    private int subclusterID = -1; // the unique id the describes a subcluster (valid only for leaf entries)
    private int time = 1;

    static final Logger LOG = Logger.getLogger(CFEntry.class.getName());

    public CFEntry() {
        id = uniqueId.incrementAndGet();
    }

    public CFEntry(double[] x) {
        this(x, 0);
    }

    public CFEntry(double[] x, int index) {
        for (int i = 0; i < x.length; i++) {
            if (Double.isNaN(x[i])) {
                LOG.log(Level.FINE, "Input vector includes NaN. {0}", Arrays.toString(x));
            }
        }
        this.n = 1;

        this.sumX = new double[x.length];
        for (int i = 0; i < sumX.length; i++) {
            sumX[i] = x[i];
        }

        this.sumX2 = new double[x.length];
        for (int i = 0; i < sumX2.length; i++) {
            sumX2[i] = x[i] * x[i];
        }

        indexList = new ArrayList<>();
        indexList.add(index);
        id = uniqueId.incrementAndGet();
    }

    /**
     * This makes a deep copy of the CFEntry e. WARNING: we do not make a deep copy of the child!!!
     *
     * @param e the entry to be cloned
     */
    public CFEntry(CFEntry e) {
        this.n = e.n;
        this.sumX = e.sumX.clone();
        logNaNs(sumX);
        this.sumX2 = e.sumX2.clone();
        this.child = e.child; // WARNING: we do not make a deep copy of the child!!!
        this.indexList = new ArrayList<>();
        for (int i : e.getIndexList()) // this makes sure we get a deep copy of the indexList
        {
            this.indexList.add(i);
        }
        this.id = e.getId();
        this.time = e.getTime();
    }

    private void logNaNs(double[] array){
        for (int i = 0; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                LOG.log(Level.FINE, "SumX vector includes NaN. {0}", Arrays.toString(sumX));
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    protected List<Integer> getIndexList() {
        return indexList;
    }

    protected boolean hasChild() {
        return (child != null);
    }

    protected CFNode getChild() {
        return child;
    }

    protected int getChildSize() {
        return child.getEntries().size();
    }

    protected void setChild(CFNode n) {
        child = n;
        indexList = null; // we don't keep this if this becomes a non-leaf entry
    }

    protected void setSubclusterID(int id) {
        subclusterID = id;
    }

    protected int getSubclusterID() {
        return subclusterID;
    }

    protected void update(CFEntry e) {
        this.n += e.n;

        if (this.sumX == null) {
            this.sumX = e.sumX.clone();
        } else {
            for (int i = 0; i < sumX.length; i++) {
                this.sumX[i] += e.sumX[i];
            }
        }

        logNaNs(sumX);

        if (this.sumX2 == null) {
            this.sumX2 = e.sumX2.clone();
        } else {
            for (int i = 0; i < sumX2.length; i++) {
                this.sumX2[i] += e.sumX2[i];
            }
        }

        if (!this.hasChild()) { // we keep indexList only if we are at a leaf
            if (this.indexList != null && e.indexList != null) {
                this.indexList.addAll(e.indexList);
            } else if (this.indexList == null && e.indexList != null) {
                this.indexList = new ArrayList<>(e.indexList);
            }
        }
    }

    protected void addToChild(CFEntry e) {
        // adds directly to the child node
        child.getEntries().add(e);
    }

    protected boolean isWithinThreshold(CFEntry e, double threshold) {
        double dist = distance(e);
        // read the comments in function d0() about differences with implementation in R
        return dist == 0 || dist <= threshold;
    }

    /**
     *
     * @param e
     * @return the distance between this entry and e
     */
    protected double distance(CFEntry e) {
        return d0(this, e);
    }

    private double d0(CFEntry e1, CFEntry e2) {
        double dist = 0;
        for (int i = 0; i < e1.sumX.length; i++) {
            double diff = e1.sumX[i] / e1.n - e2.sumX[i] / e2.n;
            dist += diff * diff;
        }

        if (dist < 0) {
            LOG.log(Level.FINE, "d0 < 0. {0}", dist);
        }

        // notice here that in the R implementation of BIRCH (package birch)
        // 
        // the radius parameter is based on the squared distance /dist/
        // this causes a difference in results.
        // if we change the line below into (return dist)
        // the results produced by the R implementation and this Java implementation
        // will match perfectly (notice that in the R implementation maxEntries = 100
        // and merging refinement is not implemented)
        return Math.sqrt(dist);
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || this.getClass() != o.getClass())
            return false;

        CFEntry e = (CFEntry) o;

        if (this.n != e.n) {
            return false;
        }

        if (this.child != null && e.child == null) {
            return false;
        }

        if (this.child == null && e.child != null) {
            return false;
        }

        if (this.child != null && !this.child.equals(e.child)) {
            return false;
        }

        if (this.indexList == null && e.indexList != null) {
            return false;
        }

        if (this.indexList != null && e.indexList == null) {
            return false;
        }

        if (!Arrays.equals(this.sumX, e.sumX) || !Arrays.equals(this.sumX2, e.sumX2)) {
            return false;
        }

        return this.indexList != null && !this.indexList.equals(e.indexList);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append(" ");
        for (int i = 0; i < sumX.length; i++) {
            buff.append(sumX[i] / n).append(" ");
        }

        if (this.indexList != null) {
            buff.append("( ");
            for (int i : indexList) {
                buff.append(i).append(" ");
            }
            buff.append(")");
        }
        if (this.hasChild()) {
            buff.append(LINE_SEP);
            buff.append("||").append(LINE_SEP);
            buff.append("||").append(LINE_SEP);
            buff.append(this.getChild());
        }

        return buff.toString();
    }

    public double[] getCentroid() {
        double[] centroid = new double[sumX.length];
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] = sumX[i] / n;
        }
        return centroid;
    }

    public double[] getRadiusVector() {
        double[] a = multVec(multVec(getCentroid(), getCentroid()), n);
        double[] b = subVec(sumX2, multVec(multVec(getCentroid(), 2.0), sumX));
        double[] c = divVec(sumVec(b, a), n);
        for (int i = 0; i < c.length; i++) {
            if (c[i] < 0) {
                if (c[i] < -0.001) {
                    LOG.log(Level.FINE, "Negative radius. {0}", c);
                }
                c[i] = 0;
            }
        }
        double[] d = VectorUtil.sqrtVec(c);
        logNaNs(d);
        return d;
    }

    private double radiusDecayValue = 0;

    public double getRadius() {
        return VectorUtil.lengthVec(getRadiusVector()) - radiusDecayValue;
    }

    /**
     * Decays a cluster by removing its centroid multiple times, thus decreasing the density.
     *
     * @param type
     * @param parameters
     */
    public void decay(DecayFunction type, double... parameters) {

        double decayFactor = calculateDecay(type, parameters);
        double radius = getRadius();

        double nNew = n - decayFactor;
        double[] sumXNew = subVec(sumX, multVec(divVec(sumX, n), decayFactor));
        double[] sumX2New = subVec(sumX2, multVec(divVec(sumX2, n), decayFactor));

        n = nNew;
        sumX = sumXNew;
        sumX2 = sumX2New;
        if (Math.abs(radius - getRadius()) > 0.00001) {
            LOG.log(Level.FINE, "Radius changed after decay significantly. Diff: {0}, Old radius: {1}, new radius: {2}", new Object[]{Math
                .abs(radius - getRadius()), radius, getRadius()});
        }
    }

    public void decayRadius(DecayFunction type, double... parameters) {
        radiusDecayValue += calculateRadiusDecay(type, parameters);
    }

    private double calculateDecay(DecayFunction type, double... parameters) {
        return type.getValue(time, parameters);
    }

    private double calculateRadiusDecay(DecayFunction type, double... parameters) {
        if(time > 100) {
            return type.getValue(time-100, parameters);
        }else{
            return 0;
        }
    }

    public boolean isInside(CFEntry e) {
        return distance(e) <= this.getRadius();
    }
    
    public boolean isInside(CFEntry e, double threshold) {
        return distance(e) < this.getRadius()+threshold;
    }

    public void increaseTime() {
        time++;
    }

    public double getN() {
        return n;
    }

    public enum DecayType {
        STATIC, EXPONENTIAL, LOGISTIC;
    }

    public double[] getSumX() {
        return sumX;
    }

    public double[] getSumX2() {
        return sumX2;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, n, child, indexList, subclusterID, time, radiusDecayValue);
        result = 31 * result + Arrays.hashCode(sumX);
        result = 31 * result + Arrays.hashCode(sumX2);
        return result;
    }
}
