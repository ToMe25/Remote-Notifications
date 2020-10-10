package com.tome25.remotenotifications;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import com.tome25.remotenotifications.network.Sender;
import com.tome25.remotenotifications.network.TCPListener;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class SenderTest {

	private JsonElement received;
	private TCPListener listener;

	@Test
	public void testSender() throws IOException, ParseException, InterruptedException {
		Sender sender = new Sender("localhost", 0, 3333);
		listener = new TCPListener(3333, json -> {
			received = json;
			listener.stop(false);
		});
		sender.send("Header", "Test Message");
		listener.join();
		assertEquals(JsonParser.parseString("{\"header\":\"Header\",\"message\":\"Test Message\"}"), received);
	}

}
