metrics-spark-receiver
=============

## Apache Spark Streaming receiver for metrics-spark-reporter

A java custom receiver of Apache Spark Streaming
for the repo [metrics-spark-reporter] (https://github.com/ippontech/metrics-spark-reporter).

## Configuration

You can read the stream of metrics in your Spark app
by using the method `JavaStreamingContext.receiverStream()` with the custom receiver created :
```
ssc.receiverStream(new MetricsReceiver(9999));
```

## Test

There is two ways to test this Receiver :

* With a sample [metrics-spark] (https://github.com/ahars/metrics-spark)
* With Docker (in sample/).

A [JHipster] (http://jhipster.github.io/) app
is available in the [metrics-spark-reporter] (https://github.com/ippontech/metrics-spark-reporter) repo
which report to the metrics-spark app.

Send data by launching the JHipster sample with the Maven command :
```
$ mvn spring-boot:run
```

Display metrics received by launching one of those two classes of metrics-spark :
* `MetricsToConsole` to display data in the console.
* `MetricsToES` to send data to an ElasticSearch server via Spark in order to use Kibana.
