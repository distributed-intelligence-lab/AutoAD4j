# Unsupervised Anomaly Detection with AutoML for High-frequent Data-Streams 

This repository provides a framework to capture the IFTM modelling approach for unsupervised anomaly detection.
IFTM offers a variety of unsupervised anomaly detection algorithms for online data stream processing. It captures various known techniques from clustering, forecasting to deep learning, which capture complex normal models in an unsupervised manner. 
The basic idea is, that you can model automatically the normal behaviour of any multivariate signal. 
Assumed, that most data are normal, we consider constantly learning the signal and provide a reconstruction error, indicating the difference to the normal behaviour of the current appearing data. 
Thus, a dynamic threshold is learned in order to distinguish between reconstruction errors, which are too high indicating an abnormal behaviour.

There exists a variaty of different functions, which can be used within this framework. All of those can be combined as you like to model your own unsupervised online anomaly detection algorithm.

Additionally, the AutoML approach can be also used when avoiding timewaidting own hyperparamter optimization.
AutoAD provides now this AutoML feature, so that you can through your data directly on your chosen IFTM algorithm and get a best performing model and results.
It includes a novel optimization strategy to model best performing parameter selection for this unsupervised setting.

## Quick start 
## Install

Add to Maven projects pom.xml:
```
<dependencies>
    <dependency>
        <groupId>com.github.distributed-intelligence-lab</groupId>
        <artifactId>AutoAD4j</artifactId>
        <version>v0.0.1</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

## Simple Usage
```java
AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection();

double[] p1 = new double[]{0.0,0.3,2.0};
DistancePredictionResult result = detector.trainAndPredict(p1);

double[] p2 = new double[]{0.0,100.0,2.0};
DistancePredictionResult result = detector.trainAndPredict(p2);
```

## Advanced Usage
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


