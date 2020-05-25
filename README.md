# Remote-Notifications
A simple Program/Library to create popup notifications on one device from another device.

![Maven Build and Publish](https://github.com/ToMe25/Remote-Notifications/workflows/Maven%20Build%20and%20Publish/badge.svg)

## Usage
Remote-Notifications can either be used in the command line by starting a client with
`java -jar Remote-Notifications-VERSION.jar`
and sending a notifications with
`java -jar Remote-Notifications-VERSION.jar -server -header=HEADER -message=MESSAGE`
or by using it as a library in another project.
If you want to send notifications from inside another application you will need [ToMe25s-Java-Utilities](https://github.com/ToMe25/ToMe25s-Java-Utilities) as Remote-Notifications is based on that library.
After you have sorted out all the dependency stuff, you just need to invoke
```java
RemoteNotifications.initServer();
```
to initialize the Notifications server, which will read its config file, and initialize some stuff, to use it you need to then invoke
```java
RemoteNotifications.sender.send(HEADER, MESSAGE);
```
to send a notification to the client set in the config file.
If you for some reason want to change the client from code, you can do that with
```java
RemoteNotifications.config.clientAddress = ADDRESS;
```
this wont change the config file tho.
