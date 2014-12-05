metrics-spark-receiver
=============

## Apache Spark Streaming receiver for metrics-spark-reporter 

A java custom receiver of Apache Spark Streaming
for the repo [metrics-spark-reporter] (https://github.com/ippontech/metrics-spark-reporter).

## Configuration

You can read the stream of metrics in your Spark app 
by using the method `JavaStreamingContext.receiverStream()` with the custom receiver created :
```
ssc.receiverStream(new MetricsReceiver("localhost", 9999));
```

## Test

Test sending data with the [JHipster] (http://jhipster.github.io/) sample
available in the [metrics-spark-reporter] (https://github.com/ippontech/metrics-spark-reporter) repo
which report to a Spark Streaming app implementing the java custom receiver MetricsReceiver.

Send data by launching the JHipster sample with the Maven command :
```
$ mvn spring-boot:run
```

Display metrics received by launching one of those two classes :
* `MetricsToConsole` to display data in the console.
* `MetricsToES` to send data to an ElasticSearch server via Spark in order to use Kibana.

