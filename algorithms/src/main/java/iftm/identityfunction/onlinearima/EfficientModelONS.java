package iftm.identityfunction.onlinearima;

import Jama.Matrix;

public class EfficientModelONS extends ModelONS {

    private Matrix lastInv;

    public EfficientModelONS(int mk, int d) {
        this(mk, d, 1, 1);
    }

    public EfficientModelONS(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(mk, d, initialLearnRate, numSamplesLearningRateAdaption);
        lastInv = getA().inverse();
    }

    @Override
    void updateModel(double prediction, double realData) {
        Matrix gl = gradiantLoss(prediction, realData);
        Matrix glGl = gl.times(gl.transpose());

        Matrix a = lastInv.times(glGl).times(lastInv);
        double b = 1 + (gl.transpose()).times(lastInv).times(gl).get(0, 0);
        lastInv = lastInv.plus(a.times(-1 / b));

        Matrix midres = lastInv.times(gl).times(-1.0 / getRate());
        setLm(getLm().plus(midres));
    }

    Matrix getLastInv() {
        return lastInv;
    }

    void setLastInv(Matrix lastInv) {
        this.lastInv = lastInv;
    }

}
