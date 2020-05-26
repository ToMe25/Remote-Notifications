# Remote-Notifications
A simple Program/Library to create popup notifications on one device from another device.

![Maven Build and Publish](https://github.com/ToMe25/Remote-Notifications/workflows/Maven%20Build%20and%20Publish/badge.svg)

## Usage
### Commandline
Remote-Notifications can be used in the command line by starting a client with

`java -jar Remote-Notifications.jar`

and sending a notifications with

`java -jar Remote-Notifications.jar -server -header=HEADER -message=MESSAGE -address=ADDRESS`


### Library
If you want to send notifications from inside another application you will also need [ToMe25s-Java-Utilities](https://github.com/ToMe25/ToMe25s-Java-Utilities) as Remote-Notifications is based on that library.

After you have sorted out all the dependency stuff, you just need to invoke
```java
RemoteNotifications.initClient();
```
to start the client, or
```java
RemoteNotifications.initServer();
```
to start the Notifications server, which will read its config file, and initialize some stuff.

To use the server you need to then invoke
```java
RemoteNotifications.sender.send(HEADER, MESSAGE);
```
to send a notification to the client set in the config file.
If you for some reason want to change the client from code, you can do that with
```java
RemoteNotifications.config.setConfig("client-address", ADDRESS);
```
for the address,
```java
RemoteNotifications.config.setConfig("client-udp-port", PORT);
```
for the client udp port and
```java
RemoteNotifications.config.setConfig("client-tcp-port", PORT);
```
for the client tcp port.
This will be written to the config file.
