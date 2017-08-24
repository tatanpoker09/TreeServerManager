package com.eilers.tatanpoker09.tsm.server;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.commandmanagement.Command;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandManager;
import net.sf.xenqtt.client.*;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Builds music catalogs from years gone by.
 */
public class MQTTManager implements Manager {

    private static final Logger log = Tree.getLog();
    private static AsyncMqttClient client;

    public static AsyncMqttClient getClient() {
        return client;
    }

    @Override
    public boolean setup() {
        String ip = "127.0.0.1";
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
                return false;
            }
            // Create your subscriptions. In this case we want to build up a catalog of classic rock.
            List<Subscription> subscriptions = new ArrayList<>();
            System.out.println(Tree.getServer().getcManager().getCommands());
            for (Command c : Tree.getServer().getcManager().getCommands()) {
                String topic = c.getTopic();
                subscriptions.add(new Subscription(topic, QoS.AT_LEAST_ONCE));
            }
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
        return true;
    }

    @Override
    public void postSetup() {

    }
}