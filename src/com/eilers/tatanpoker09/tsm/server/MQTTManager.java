package com.eilers.tatanpoker09.tsm.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import net.sf.xenqtt.client.AsyncClientListener;
import net.sf.xenqtt.client.AsyncMqttClient;
import net.sf.xenqtt.client.MqttClient;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

/**
 * Builds music catalogs from years gone by.
 */
public class MQTTManager {

    private static final Logger log = Tree.getLog();
    private static AsyncMqttClient client;

    public void start() {
        String ip = "192.168.1.4";
        String port = "7727";

        final CountDownLatch connectLatch = new CountDownLatch(1);
        final AtomicReference<ConnectReturnCode> connectReturnCode = new AtomicReference<ConnectReturnCode>();

        AsyncClientListener listener = new AsyncClientListener() {
            public void publishReceived(MqttClient client, PublishMessage message) {
                String topic = message.getTopic();
                String payload = message.getPayloadString();
                CommandManager cm = Tree.getServer().getcManager();
                cm.parseAndRun(topic, payload);
                message.ack();
            }
            public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
                if (cause != null) {
                    log.severe("Disconnected from the broker due to an exception: "+cause);
                } else {
                    log.info("Disconnecting from the broker.");
                }
                if (reconnecting) {
                    log.info("Attempting to reconnect to the broker.");
                }
            }
            public void connected(MqttClient client, ConnectReturnCode returnCode) {
                log.info("Connected with return code: "+returnCode);
                connectReturnCode.set(returnCode);
                connectLatch.countDown();
            }
            public void published(MqttClient client, PublishMessage message) {
                // We do not publish so this should never be called, in theory ;).
            }
            public void subscribed(MqttClient client, Subscription[] requestedSubscriptions, Subscription[] grantedSubscriptions, boolean requestsGranted) {
                if (!requestsGranted) {
                    log.severe("Unable to subscribe to the following subscriptions: " + Arrays.toString(requestedSubscriptions));
                }
                log.info("Granted subscriptions: " + Arrays.toString(grantedSubscriptions));
            }
            public void unsubscribed(MqttClient client, String[] topics) {
                log.info("Unsubscribed from the following topics: " + Arrays.toString(topics));
            }
        };

        this.client = new AsyncMqttClient("tcp://" + ip + ":" + port, listener, 5);
        try {
            // Connect to the broker with a specific client ID. Only if the broker accepted the connection shall we proceed.
            getClient().connect("localAdmin", true);
            Thread.sleep(500);
            ConnectReturnCode returnCode = connectReturnCode.get();
            if (returnCode == null || returnCode != ConnectReturnCode.ACCEPTED) {
                log.severe("Unable to connect to the MQTT broker. Reason: " + returnCode);
                return;
            }
            // Create your subscriptions. In this case we want to build up a catalog of classic rock.
            List<Subscription> subscriptions = new ArrayList<Subscription>();
            subscriptions.add(new Subscription("main/tatanroom/lights", QoS.AT_LEAST_ONCE));
            subscriptions.add(new Subscription("$SYS/#", QoS.AT_LEAST_ONCE));
            getClient().subscribe(subscriptions);
            while (!getClient().isClosed()) {
                Thread.sleep(3000);
            }

        } catch (Exception ex) {
            log.severe("An unexpected exception has occurred: " + ex);
        } finally {
            if (!getClient().isClosed()) {
                getClient().disconnect();
            }
        }
    }

    public static AsyncMqttClient getClient() {
        return client;
    }
}