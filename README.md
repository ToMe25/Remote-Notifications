# Remote-Notifications
A simple Program/Library to create popup notifications on one device from another device.

![Maven Build and Publish](https://github.com/ToMe25/Remote-Notifications/workflows/Maven%20Build%20and%20Publish/badge.svg)

## Usage
### Commandline
Remote-Notifications can be used in the command line by starting a client with

`java -jar Remote-Notifications.jar`

and sending a notifications with

`java -jar Remote-Notifications.jar -server -header=HEADER -message=MESSAGE -addresses='[{"addr": "RECEIVER_ADDRESS", "udp": RECEIVER_UDP_PORT, "tcp": RECEIVER_TCP_PORT}]'`


`-tcp=TCP_PORT` or `-udp=UDP_PORT` can be added to the client start command to set the tcp/udp port to listen for notifications on.

To change config options, you can either change them in the config file in the "Remote-Notifications-Config" directory next to the jar file,

or using the Config Window that can be accessed by selecting the config option in the popup that appears when right-clicking the Tray Icon, or a Dialog style notification.

### Library
If you want to send notifications from inside another application you will also need [ToMe25s-Java-Utilities](https://github.com/ToMe25/ToMe25s-Java-Utilities) as Remote-Notifications is based on that library.

After you have sorted out all the dependency stuff, you just need to create a new instance of `com.tome25.remotenotifications.client.Client` to start a client,

or a new instance of `com.tome25.remotenotifications.server.Server` to start a server.
Both of them will automatically read their config file, and initialize everything they need.

You can use the server like this
```java
Server server = new Server();
server.getSender().send("HEADER", "MESSAGE");
```
to send a notification to the client set in the config file.

If you for want to change the config with code, you can do that like this
```java
Client client = new Client();
client.getConfig().setConfig("PROPERTY", "VALUE");
```
for the client and like this
```java
Server server = new Server();
server.getConfig().setConfig("PROPERTY", "VALUE");
```
for the server.
Both of them will automatically write that to the config file.

If you want to get config properties you can do that like this
```java
Client client = new Client();
Object value = client.getConfig().getConfig("PROPERTY");
```
for the client and like this
```java
Server server = new Server();
Object value = server.getConfig().getConfig("PROPERTY");
```
for the server.

#### Client config properties:
 * notification-style
 * notification-time
 * udp-port
 * tcp-port
 * confirm-exit
 * servers

These can also be found as constants in `com.tome25.remotenotifications.client.config.ClientConfig`.

#### Server config properties:
 * clients
 * udp-port
 * tcp-port

These can also be found as constants in `com.tome25.remotenotifications.server.config.ServerConfig`.
