package com.tome25.remotenotifications;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import com.tome25.remotenotifications.network.Receiver;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class ReceiverTest {

	@Test
	public void testReceiver() throws IOException, ParseException, InterruptedException {
		// test tcp receiving
		final JsonElement[] received = new JsonElement[1];
		final ReentrantLock lock = new ReentrantLock();
		final Condition con = lock.newCondition();
		@SuppressWarnings("unused")
		Receiver receiver = new Receiver(4001, 3001, (json, addr) -> {
			received[0] = json;
			lock.lock();
			con.signal();
			lock.unlock();
		});
		String message = "{\"header\":\"Header\",\"message\":\"Test Message\"}";
		Socket tcpSocket = new Socket("localhost", 3001);
		OutputStream tcpOut = tcpSocket.getOutputStream();
		tcpOut.write(message.getBytes());
		tcpOut.flush();
		tcpSocket.close();
		lock.lock();
		con.awaitNanos(2000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString(message), received[0]);
		// test udp receiving
		message = "{\"header\":\"Test Header\",\"message\":\"Message\"}";
		DatagramSocket udpSocket = new DatagramSocket();
		DatagramPacket udpPacket = new DatagramPacket(message.getBytes(), message.getBytes().length,
				InetAddress.getLocalHost(), 4001);
		udpSocket.send(udpPacket);
		udpSocket.close();
		lock.lock();
		con.awaitNanos(2000000000);
		lock.unlock();
		assertEquals(JsonParser.parseString(message), received[0]);
	}

}
