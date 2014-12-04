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
    private BufferedReader reader;
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
        sparkSocket = null;
        metricsSocket = null;
        reader = null;
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
        close();
    }

    private void receive() {

        try {
            connect();

            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            String input;
            HashMap<String, Object> map;

            while (!isStopped() && (input = reader.readLine()) != null) {
                map = mapper.readValue(input, new TypeReference<HashMap<String, Object>>() {});
                store(map);
            }
            close();

        } catch (UnknownHostException uhe) {
            LOGGER.error("Could not reach Metrics", uhe);
            receive();
        } catch (JsonMappingException jme) {
            LOGGER.error("Error during receiving data", jme);
            receive();
        } catch (JsonParseException jpe) {
            LOGGER.error("Error during parsing data", jpe);
            receive();
        } catch (IOException ioe) {
            LOGGER.error("Could not read data received", ioe);
            receive();
        }
    }

    private void connect() throws IOException {
        if (sparkSocket != null || metricsSocket != null || reader != null) {
            close();
        }
        sparkSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
        metricsSocket = sparkSocket.accept();
        reader = new BufferedReader(new InputStreamReader(metricsSocket.getInputStream()));
    }

    private void close() {
        try {
            reader.close();
            metricsSocket.close();
            sparkSocket.close();
        } catch (IOException ioe) {
            LOGGER.error("Could not disconnect from Metrics", ioe);
        }
        reader = null;
        metricsSocket = null;
        sparkSocket = null;
    }
}
