package iftm.identityfunction.onlinearima.learningoptimizer;

import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

import java.lang.reflect.InvocationTargetException;

/**
 * @author kevinstyp
 */
public enum LearningRateOptimizers {
    SIMPLE(SimpleLearningRateOptimizer.class),
    MOMENTUM(MomentumLearningRateOptimizer.class),
    NESTEROV(NesterovLearningRateOptimizer.class),
    ADAGRAD(AdagradLearningRateOptimizer.class),
    RMSPROP(RmspropLearningRateOptimizer.class),
    ADAM(AdamLearningRateOptimizer.class),
    AMSGRAD(AmsgradLearningRateOptimizer.class),
    ;

    final private Class learningRateOptimizer;

    LearningRateOptimizers(Class learningRateOptimizer) {
        this.learningRateOptimizer = learningRateOptimizer;
    }

    public AbstractLearningRateOptimizer get(LearningRateAdaptizer learningRateAdaptizer, double... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        return (AbstractLearningRateOptimizer) learningRateOptimizer.getDeclaredConstructor(LearningRateAdaptizer.class, double[].class).newInstance(learningRateAdaptizer, args);
    }

}
