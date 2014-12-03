package fr.ippontech.metrics.spark.receiver;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;

public class MetricsReceiver extends Receiver<HashMap<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsReceiver.class);

    private ServerSocket sparkSocket;
    private Socket metricsSocket;
    private String host;
    private Integer port;
    private StorageLevel storageLevel;

    public MetricsReceiver(String host, Integer port) {
        super(StorageLevel.MEMORY_ONLY());
        this.host = host;
        this.port = port;
        this.storageLevel = StorageLevel.MEMORY_ONLY();
    }

    public MetricsReceiver(String host, Integer port, StorageLevel storageLevel) {
        super(storageLevel);
        this.host = host;
        this.port = port;
        this.storageLevel = storageLevel;
    }

    @Override
    public StorageLevel storageLevel() {
        return storageLevel;
    }


    @Override
    public void onStart() {
        new Thread() {
            @Override
            public void run() {
                receive();
            }
        }.start();
    }

    @Override
    public void onStop() {
        try {
            metricsSocket.close();
            sparkSocket.close();
        } catch (IOException ioe) {
            LOGGER.error("Error stop receiver", ioe);
        }
    }

    private void receive() {

        try {
            sparkSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
            metricsSocket = sparkSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(metricsSocket.getInputStream()));

            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            String input;

            while (!isStopped() && (input = reader.readLine()) != null) {
                store((HashMap<String, Object>)
                    mapper.readValue(input, new TypeReference<HashMap<String, Object>>() {
                    }));
            }
        } catch (UnknownHostException uhe) {
            LOGGER.error("Could not reach Metrics", uhe);
        } catch (JsonMappingException jme) {
            LOGGER.error("Error during receiving data", jme);
        } catch (JsonParseException jpe) {
            LOGGER.error("Error during parsing data", jpe);
        } catch (IOException ioe) {
            LOGGER.error("Could not read data received", ioe);
        }
    }
}
