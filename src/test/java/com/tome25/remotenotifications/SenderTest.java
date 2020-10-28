package com.tome25.remotenotifications;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import com.tome25.remotenotifications.network.Sender;
import com.tome25.remotenotifications.network.TCPListener;
import com.tome25.remotenotifications.network.UDPListener;
import com.tome25.remotenotifications.network.UDPTCPAddress;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class SenderTest {

	@Test
	public void testSender() throws IOException, ParseException, InterruptedException {
		// test tcp sending
		final JsonElement<?>[] received = new JsonElement<?>[1];
		final ReentrantLock lock = new ReentrantLock();
		final Condition con = lock.newCondition();
		Sender sender = new Sender(Arrays.asList(new UDPTCPAddress("localhost", 4000, 3000)));
		TCPListener tcpListener = new TCPListener(3000, (json, addr) -> {
			received[0] = json;
			lock.lock();
			con.signal();
			lock.unlock();
		});
		sender.send("Header", "Test Message");
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		tcpListener.stop();
		assertEquals(JsonParser.parseString("{\"header\":\"Header\",\"message\":\"Test Message\"}"), received[0]);
		// test udp sending
		UDPListener udpListener = new UDPListener(4000, (json, addr) -> {
			received[0] = json;
			lock.lock();
			con.signal();
			lock.unlock();
		});
		sender.send("Test Header", "Message");
		lock.lock();
		con.awaitNanos(1000000000);
		lock.unlock();
		udpListener.stop();
		assertEquals(JsonParser.parseString("{\"header\":\"Test Header\",\"message\":\"Message\"}"), received[0]);
	}

}
