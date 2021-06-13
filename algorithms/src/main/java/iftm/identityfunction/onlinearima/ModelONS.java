package iftm.identityfunction.onlinearima;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ModelONS extends AbstractModelArima {

    static final Logger LOG = Logger.getLogger(ModelONS.class.getName());

    private Matrix a;

    public ModelONS(int mk, int d) {
        this(mk, d, 1, 1);
    }

    public ModelONS(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(d, new ArrayList<>(), mk, initL(mk), initRate(mk), initialLearnRate, numSamplesLearningRateAdaption);
        double valueD = Math.sqrt(2.0 * mk);
        double epsilon = 1.0 / (Math.pow(getRate(), 2) * Math.pow(valueD, 2));
        a = Matrix.identity(mk, mk).times(epsilon);
    }

    void updateModel(double prediction, double realData) {
        Matrix gl = gradiantLoss(prediction, realData);
        Matrix glGl = gl.times(gl.transpose());
        a = a.plus(glGl);
        Matrix midres = a.inverse().times(gl).times(-1.0 / getRate());
        setLm(getLm().plus(midres));
    }

    Matrix getA() {
        return a;
    }

}
