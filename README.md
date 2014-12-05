metrics-spark-receiver
=============

## Apache Spark Streaming receiver for metrics-spark-reporter 

This is a custom receiver of Apache Spark Streaming for the repo metrics-spark-reporter.

## Configuration

You can read the stream of metrics in your Spark app 
by using the method `JavaStreamingContext.receiverStream()` with the custom receiver created :
```
JavaReceiverInputDStream jriDStream = ssc.receiverStream(new MetricsReceiver("localhost", 9999));
```
By default, communications between the reporter and the receiver use the host `localhost` and the port `9999`.

## Test

In order to test this custom receiver, you can :
* Run the [JHipster](http://jhipster.github.io/) sample available
in the [metrics-spark-reporter](https://github.com/ippontech/metrics-spark-reporter) repo.
* Run the class `MetricsToConsole` to display the metrics received from the JHipster sample.

Run the JHipster sample with the Maven command :
```
$ mvn spring-boot:run
```

