package com.tome25.remotenotifications;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tome25.remotenotifications.client.Client;
import com.tome25.remotenotifications.client.config.ClientConfig;
import com.tome25.remotenotifications.client.notification.INotification;
import com.tome25.remotenotifications.client.notification.LogNotification;
import com.tome25.remotenotifications.client.notification.NotificationHandler;
import com.tome25.remotenotifications.network.UDPTCPAddress;
import com.tome25.remotenotifications.server.Server;
import com.tome25.remotenotifications.server.config.ServerConfig;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;
import com.tome25.utils.json.JsonParser;

public class ClientServerTest {

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	@Test
	public void testClient() throws IOException, InterruptedException, ParseException {
		// test receiving notifications via tcp
		final ReentrantLock lock = new ReentrantLock();
		final Condition con = lock.newCondition();
		final File testDir = tempDir.newFolder("testClient");
		final JsonObject notif = new JsonObject();
		final Client client = new Client(testDir, 4003, 3003);
		NotificationHandler.setNotification(new TestNotification((h, m) -> {
			notif.put("header", h);
			notif.put("message", m);
			lock.lock();
			con.signal();
			lock.unlock();
		}));
		Socket tcpSocket = new Socket(InetAddress.getLocalHost(), 3003);
		OutputStream tcpOut = tcpSocket.getOutputStream();
		String notification = "{\"header\": \"Header\", \"message\": \"Test Message\"}";
		tcpOut.write(notification.getBytes());
		tcpOut.flush();
		tcpSocket.close();
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString(notification), notif);
		// test receiving notifications via udp
		notification = "{\"header\": \"Test Header\", \"message\": \"Message\"}";
		DatagramSocket udpSocket = new DatagramSocket();
		DatagramPacket udpPacket = new DatagramPacket(notification.getBytes(), notification.getBytes().length,
				InetAddress.getLocalHost(), 4003);
		udpSocket.send(udpPacket);
		udpSocket.close();
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString(notification), notif);
		// test messages in dummy mode
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		NotificationHandler.setNotification("Dummy");
		LogNotification.LOGGER.addHandler(new TestHandler(bOut));
		notification = "{\"header\": \"Test Header\", \"message\": \"Test Message\"}";
		tcpSocket = new Socket(InetAddress.getLocalHost(), 3003);
		tcpOut = tcpSocket.getOutputStream();
		tcpOut.write(notification.getBytes());
		tcpOut.flush();
		tcpSocket.close();
		final int MAX_WAIT_TIME = 2000;
		int wait = 0;
		while (bOut.size() == 0 && wait < MAX_WAIT_TIME) {
			Thread.sleep(10);
			wait += 10;
		}
		String log = "   Test Header   =================Test Message";
		assertEquals(log, bOut.toString().replaceAll(System.lineSeparator(), ""));
		// delete test folder
		client.getConfig().deleteConfig();
		testDir.delete();
	}

	@Test
	public void testServer() throws UnknownHostException, IOException, InterruptedException, ParseException {
		// test receiving notification requests via tcp
		final ReentrantLock lock = new ReentrantLock();
		final Condition con = lock.newCondition();
		final File testDir = tempDir.newFolder("testServer");
		final JsonArray conf = new JsonArray();
		final Server server = new Server(testDir);
		server.getConfig().setConfig(ServerConfig.UDP_PORT, 3002);
		server.getConfig().setConfig(ServerConfig.TCP_PORT, 4002);
		server.getConfig().registerUpdateHandler(cfg -> {
			JsonArray clients = cfg.getConfig(ServerConfig.CLIENTS);
			conf.clear();
			conf.addAll(clients);
			if (!clients.isEmpty()) {
				lock.lock();
				con.signal();
				lock.unlock();
			}
		});
		server.clearClients();
		Thread.sleep(50);
		assertEquals(new JsonArray(), conf);
		Socket tcpSocket = new Socket(InetAddress.getLocalHost(), 4002);
		OutputStream tcpOut = tcpSocket.getOutputStream();
		String request = "{\"tcp\":4002,\"udp\"3002}";
		tcpOut.write(request.getBytes());
		tcpOut.flush();
		tcpSocket.close();
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		String clientsConfig = String.format("[{\"addr\":\"%s\",\"tcp\":4002,\"udp\":3002}]",
				InetAddress.getByName("localhost").getCanonicalHostName());// for some reason getLocalHost returns a
																			// different object, that can't be used
																			// here.
		assertEquals(JsonParser.parseString(clientsConfig), conf);
		// test receiving notification requests via udp
		server.clearClients();
		Thread.sleep(50);
		assertEquals(new JsonArray(), conf);
		DatagramSocket udpSocket = new DatagramSocket();
		DatagramPacket udpPacket = new DatagramPacket(request.getBytes(), request.getBytes().length,
				InetAddress.getLocalHost(), 3002);
		udpSocket.send(udpPacket);
		udpSocket.close();
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString(clientsConfig), conf);
		// delete test folder
		server.getConfig().deleteConfig();
		testDir.delete();
	}

	@Test(timeout = 20000)
	public void testCombination() throws IOException, InterruptedException, ParseException {
		// test tcp notification request
		final ReentrantLock lock = new ReentrantLock();
		final Condition con = lock.newCondition();
		final File testDir = tempDir.newFolder("testCombination");
		final JsonArray conf = new JsonArray();
		final Server server = new Server(testDir);
		server.getConfig().setConfig(ServerConfig.TCP_PORT, 3004);
		server.getConfig().setConfig(ServerConfig.UDP_PORT, 4004);
		server.getConfig().registerUpdateHandler(cfg -> {
			JsonArray clients = cfg.getConfig(ServerConfig.CLIENTS);
			conf.clear();
			conf.addAll(clients);
			if (!clients.isEmpty()) {
				lock.lock();
				con.signal();
				lock.unlock();
			}
		});
		final JsonObject notif = new JsonObject();
		final Client client = new Client(testDir, 4005, 3005, true);
		client.clearServers();
		server.clearClients();
		Thread.sleep(50);
		assertEquals(new JsonArray(), conf);
		client.addServer(new UDPTCPAddress("localhost", 0, 3004));
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString("[{\"addr\": \"" + InetAddress.getByName("localhost").getCanonicalHostName()
				+ "\", \"tcp\": 3005, \"udp\": 4005}]"), conf);// for some reason getLocalHost returns a different
																// object, that can't be used here.
		// test udp notification request
		server.clearClients();
		Thread.sleep(50);
		assertEquals(new JsonArray(), conf);
		client.addServer(new UDPTCPAddress("localhost", 4004, 0));
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		// test tcp notification
		client.clearServers();
		client.getConfig().setConfig(ClientConfig.UDP_PORT, 0);
		final TestNotification testNotification = new TestNotification((h, m) -> {
			notif.put("header", h);
			notif.put("message", m);
			lock.lock();
			con.signal();
			lock.unlock();
		});
		NotificationHandler.setNotification(testNotification);
		server.getSender().send("Header", "Test Message");
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString("{\"header\": \"Header\", \"message\": \"Test Message\"}"), notif);
		// test udp notification
		notif.clear();
		client.getConfig().setConfig(ClientConfig.TCP_PORT, 0);
		client.getConfig().setConfig(ClientConfig.UDP_PORT, 4005);
		NotificationHandler.setNotification(testNotification);
		server.getSender().send("Test Header", "Message");
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString("{\"header\": \"Test Header\", \"message\": \"Message\"}"), notif);
		// delete test folder
		server.getConfig().deleteConfig();
		client.getConfig().deleteConfig();
		testDir.delete();
	}

	private class TestNotification implements INotification {

		private final BiConsumer<String, String> consumer;

		/**
		 * Creates a new Test Notification
		 * 
		 * @param consumer the consumer handling the header and the message.
		 */
		private TestNotification(BiConsumer<String, String> consumer) {
			this.consumer = consumer;
		}

		@Override
		public void display(String header, String message) {
			consumer.accept(header, message);
		}

	}

	private class TestHandler extends Handler {

		private final OutputStream out;

		/**
		 * Creates a new Handler printing all the {@link LogRecord}s to the given
		 * {@link OutputStream}.
		 * 
		 * @param out the {@link OutputStream} to print to.
		 */
		private TestHandler(OutputStream out) {
			this.out = out;
			setFormatter(new SimpleFormatter());
		}

		@Override
		public void publish(LogRecord record) {
			try {
				out.write((getFormatter().formatMessage(record) + System.lineSeparator()).getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void flush() {
			try {
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void close() throws SecurityException {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
