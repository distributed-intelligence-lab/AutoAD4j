# Unsupervised Anomaly Detection for High-frequent Data-Streams

This repository provides a framework to capture the IFTM modelling approach for anomaly detection.
IFTM offers a variety of unsupervised anomaly detection algorithms for online data stream processing. It captures various known techniques from clustering, forecasting to deep learning, which capture complex normal models in an unsupervised manner. 
The basic idea is, that you can model automatically the normal behaviour of any multivariate signal. 
Assumed, that most data is normal, we consider constantly learning the signal and provide a reconstruction error, indicating the difference to the normal behaviour of the current appearing data. 
Thus, a dynamic threshold is learned in order to distinguish between reconstruction errors, which are too high indicating an abnormal behaviour.

There exists a variaty of different functions, which can be used within this framework. All of those can be combined as you like to model your own unsupervised online anomaly detection algorithm.

## Quick start
## Install
```
git clone https://github.com/flohannes/IFTM-Anomaly-Detection.git
cd IFTM-Anomaly-Detection
mvn install
```
Add to Maven project:
```
<dependency>
    <groupId>fschmidt</groupId>
    <artifactId>IFTM-anomalydetection-models</artifactId>
    <version>0.0.1</version>
</dependency>
```
## Usage
```java
IdentityFunction identityFunction = new OnlineArimaMulti(10,3,3);
ErrorFunction errorFunction = new EuclideanError();
AvgStdThresholdModel thresholdModel = new CompleteHistoryAvgStd(2);
AnomalyDetection model = new AnomalyDetection(identityFunction, errorFunction, thresholdModel);

double[] datapoint1 = new double[]{0.0,0.3,2.0};
model.train(datapoint);

double[] datapoint2 = new double[]{0.0,100.0,2.0};
DistancePredictionResult result = model.predict(datapoint2);
System.out.println("Is Anomaly: "+result.isAnomaly());
```

## Existing identity functions
* LSTM
* Variational Autoencoder
* Autoencoder using LSTM cells
* Online Arima
* Concept Adapting BIRCH
* Streaming HSTrees

## Existing reconstruction error functions
* Absolute error
* Euclidean distance
* Mean squared error
* Root mean squared error
* Manhatten distance
* Canberra distance
* Chebyshev distance
* Earth movers distance

## Existing threshold models
* Sliding window threshold
* Complete history threshold
* Exponential moving average threshold
* Double exponential moving average threshold
* Triple exponential moving average threshold

## Existing aggregation functions  
TBA soon!

## Reference
### Please reference this paper:
> Schmidt, F., Gulenko, A., Wallschläger, M., Acker, A., Hennig, V., Liu, F. and Kao, O., 2018, July. IFTM-Unsupervised Anomaly Detection for Virtualized Network Function Services. In 2018 IEEE International Conference on Web Services (ICWS) (pp. 187-194). IEEE.
https://ieeexplore.ieee.org/document/8456348

### Further papers and research:
https://www.researchgate.net/profile/Florian_Schmidt13/research
