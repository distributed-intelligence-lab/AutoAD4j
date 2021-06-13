package iftm.automl.thresholdmodel;

import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.thresholdmodel.OneSidedThreshold;

import java.io.Serializable;

public interface ThresholdBreedingPart extends Serializable {
    OneSidedThreshold get(IFTMAnomalyDetection detector, boolean mutate);

    OneSidedThreshold random();
}
