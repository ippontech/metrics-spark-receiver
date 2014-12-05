package streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ippontech.metrics.spark.receiver.MetricsReceiver;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;

public class MetricsToES {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
            .setAppName("MetricsToES")
            .setMaster("local[2]")
            .set("es.nodes", "localhost:9200")
            .set("es.index.auto.create", "true");

        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(5000));
        ObjectMapper mapper = new ObjectMapper();

        ssc.receiverStream(new MetricsReceiver("localhost", 9999))
            .map(s -> mapper.writeValueAsString(s))
            .foreachRDD(rdd -> {
                JavaEsSpark.saveJsonToEs(rdd, "jhipster/metrics");
                return null;
            });

        ssc.start();
        ssc.awaitTermination();
    }
}
