package com.tome25.remotenotifications.network;

import java.net.InetAddress;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.tome25.utils.json.JsonElement;

/**
 * A abstract listener listening to some input and giving a {@link JsonElement}
 * to a {@link Consumer} when it receives something.
 * 
 * @author ToMe25
 *
 */
public abstract class AbstractListener implements Runnable {

	protected final Thread thread;
	protected final BiConsumer<JsonElement, InetAddress> handler;
	private boolean running = true;

	/**
	 * Constructs a new AbstractListener with the given action.
	 * 
	 * @param threadName     the name to use for the thread running this listener.
	 * @param receivehandler the consumer to give the received {@link JsonElement}s
	 *                       and the senders {@link INetAddress} to.
	 */
	public AbstractListener(String threadName, BiConsumer<JsonElement, InetAddress> receivehandler) {
		thread = new Thread(this, threadName);
		thread.setDaemon(true);
		handler = receivehandler;
	}

	/**
	 * Returns the port used by this listener. -1 if not applicable.
	 * 
	 * @return the port used by this listener.
	 */
	public abstract int getPort();

	/**
	 * Closes this listeners input.
	 */
	protected abstract void close();

	/**
	 * Stops this thread, and closes what it is receiving from. Waits for the
	 * listener thread to finsih.
	 */
	public void stop() {
		stop(true);
	}

	/**
	 * Stops this thread, and closes what it is receiving from.
	 * 
	 * @param wait whether the thread calling this should wait for the listener
	 *             thread to stop, or return immediately.
	 */
	public void stop(boolean wait) {
		running = false;
		close();
		if (wait) {
			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks whether this listener is still running.
	 * 
	 * @return whether this listener is still running.
	 */
	protected boolean isRunning() {
		return running;
	}

	/**
	 * Joins this listeners thread
	 * 
	 * @throws InterruptedException if any thread has interrupted the current
	 *                              thread. The interrupted status of the current
	 *                              thread is cleared when this exception is thrown.
	 */
	public void join() throws InterruptedException {
		thread.join();
	}

}
