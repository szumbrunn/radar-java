# radar-java
Java Implementation of [Radar: Residual Analysis for Anomaly Detection in Attributed Networks acc. to J.Li (2017)](http://www.public.asu.edu/~jundongl/paper/IJCAI17_Radar.pdf)

## How to use
Add the OjAlgo dependecy for linear algebra to your gradle.buile file
```groovy
	compile group: 'org.ojalgo', name: 'ojalgo', version: '31.0'
```
Add RadarImpl.java to your project then use scoreFromRadar() to get the node anomaly score
