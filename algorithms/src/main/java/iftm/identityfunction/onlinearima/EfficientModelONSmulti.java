package iftm.identityfunction.onlinearima;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

public class EfficientModelONSmulti extends EfficientModelONS {

    private final int dim;
    private final List<double[][]> grads = new ArrayList<>();

    public EfficientModelONSmulti(int mk, int d, int n) {
        this(mk, d, n, 1, 1);
    }

    public EfficientModelONSmulti(int mk, int d, int n, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(mk, d, initialLearnRate, numSamplesLearningRateAdaption);
        dim = n;
    }

    @Override
    public double[] predict() {
        double[] xT = new double[dim];
        for (int i = 0; i < getMk(); i++) {
            if (grads.size() - i - 1 < 0) {
                break;
            }
            for (int j = 0; j < dim; j++) {
                xT[j] += getLm().get(i, 0) * grads.get(grads.size() - i - 1)[getD()][j];
            }
        }
        for (int i = 0; i < getD(); i++) {
            if (grads.size() - 1 < 0) {
                break;
            } else {
                for (int j = 0; j < dim; j++) {
                    xT[j] += grads.get(grads.size() - 1)[i][j];
                }
            }
        }
        return xT;
    }

    @Override
    public void train(double newVal) {
        throw (new UnsupportedOperationException());
    }

    public void train(double[] newVal) {
        double[] prediction = predict();
        updateModel(prediction, newVal);
        addGrad(newVal);

    }

    private void updateModel(double[] prediction, double[] realData) {
        double[][] gradLoss = new double[getMk()][1];
        for (int i = 0; i < getMk(); i++) {
            double x;
            gradLoss[i][0] = 0;
            if (grads.size() - i - 1 >= 0) {
                for (int j = 0; j < dim; j++) {
                    x = grads.get(grads.size() - i - 1)[getD()][j];
                    gradLoss[i][0] += -2 * (realData[j] - prediction[j]) * x;
                }
            }
        }
        Matrix gl = Matrix.constructWithCopy(gradLoss);
        Matrix glGl = gl.times(gl.transpose());

        Matrix a = getLastInv().times(glGl).times(getLastInv());
        double b = 1 + (gl.transpose()).times(getLastInv()).times(gl).get(0, 0);
        setLastInv(getLastInv().plus(a.times(-1 / b)));

        Matrix midres = getLastInv().times(gl).times(-1.0 / getRate());
        setLm(getLm().plus(midres));
    }

    private void addGrad(double[] newVal) {
        double[][] newGrad = new double[getD() + 1][dim];
        newGrad[0] = newVal;
        int currGradLength = grads.size();

        if (currGradLength > 0) {
            for (int g = 1; g < getD() + 1; g++) {
                for (int n = 0; n < dim; n++) {
                    newGrad[g][n] = newGrad[g - 1][n] - grads.get(currGradLength - 1)[g - 1][n];
                }
            }
        }

        if (currGradLength == getMk()) {
            grads.remove(0);
        }
        grads.add(newGrad);
    }

}
