# ForestRedisAPI
![badge](https://img.shields.io/github/v/release/ForestTechMC/ForestRedisAPI)
[![badge](https://jitpack.io/v/ForestTechMC/ForestRedisAPI.svg)](https://jitpack.io/#ForestTechMC/ForestRedisAPI)
![badge](https://img.shields.io/github/downloads/ForestTechMC/ForestRedisAPI/total)
![badge](https://img.shields.io/github/last-commit/ForestTechMC/ForestRedisAPI)
![badge](https://img.shields.io/badge/platform-spigot%20%7C%20bungeecord-lightgrey)
![badge](https://img.shields.io/discord/896466173166747650?label=discord)

Simple Spigot&Bungee Redis API based on Jedis library. ForestRedisAPI allows developers to comfortably maintain
communication between servers using simple API calls and Events. **Supports both BungeeCord and Spigot servers.**

## Getting started

Make sure the server has ForestRedisAPI plugin installed. Otherwise, look at **[Standalone Usage](https://github.com/ForestTechMC/ForestRedisAPI#standalone-usage-without-forestredisapi-installed)**.

### Add ForestRedisAPI to your project 

[![badge](https://jitpack.io/v/ForestTechMC/ForestRedisAPI.svg)](https://jitpack.io/#ForestTechMC/ForestRedisAPI)

First, you need to setup the dependency on the ForestRedisAPI. Replace **VERSION** with the version of the release.

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ForestTechMC</groupId>
        <artifactId>ForestRedisAPI</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

#### Gradle

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.ForestTechMC:ForestRedisAPI:VERSION'
}
```

### Plugin configuration

You need to (soft)depend on ForestRedisAPI in order to work properly. Choose depend(s) for mandatory usage of the
ForestRedisAPI or softdepend(s) for optional usage.

for **plugin.yml**: `depend: [ForestRedisAPI]` or `softdepend: [ForestRedisAPI]`

for **bungee.yml**: `depends: [ForestRedisAPI]` or `softDepends: [ForestRedisAPI]`

### Standalone usage (without ForestRedisAPI installed)

You can use the ForestRedisAPI as standalone library. Then you need to initialize RedisManager and provide him with
required data.

```java
/**
 * Use this ONLY if ForestRedisAPI plugin is not present and
 * for some reason you don't want to install it.
 */
public void setupRedis() {
    // Construct RedisConfiguration object
    RedisConfiguration redisConfiguration = new RedisConfiguration(
            "localhost", //hostname
            6379, //port
            null, //username (null if not any)
            null, //password (null if not any)
            false //ssl
    );

    // Initialize RedisManager instance (singleton)
    new RedisManager(this, serverIdentifier, redisConfiguration).setup();
    
    // Now you can use #getAPI() call to get singleton instance
    RedisManager.getAPI().subscribe("MyChannel1");
}
```

## Subscribing to channel

To receive data from Redis server, you need to subscribe to selected channels. You can do it simply just by calling:

```java
// You can check if the channel is subscribed or not
if(RedisManager.getAPI().isSubscribed("MyChannel")){
        this.log().warning("Channel 'MyChannel' is already subscribed!");
        return;
}

// You can subscribe as many channels as you want. 
// Already subscribed channels will be skipped.
RedisManager.getAPI().subscribe("MyChannel1","MyChannel2","MyChannel3");
```

## Publishing messages / objects

You can easily publish messages and objects to Redis server. It is not required to subscribe channel you want to send
data in.

```java
// For simple messages in String format use #publishMessage method.
RedisManager.getAPI().publishMessage("MyChannel1","Hello, how are you?");

// You can also publish any object. They'll be serialized using JSON.
RedisManager.getAPI().publishObject("MyChannel1",new MyObject());
```

## Events & Incoming messages

Using ForestRedisAPI you can retrieve data from Redis using bukkit's (bungee's) Listeners. **But make sure the correct
Event is chosen as the names are same for Bungee and Spigot!**

```java
// Use bungee event import for BungeeCord!!!

import cz.foresttech.forestredis.spigot.events.RedisMessageReceivedEvent;

public class MyListener implements Listener {

    @EventHandler
    public void onRedisMessageReceived(RedisMessageReceivedEvent event) {

        // Whether the message was sent by this server or not.
        // Uses the serverIdentifier from ForestRedisAPI config.yml
        boolean isSelfMessage = event.isSelfSender();

        // Name of the channel. Must be subscribed first.
        String channel = event.getChannel();

        // Identifier of the sender server.
        String senderServerId = event.getSenderIdentifier();

        // Text of the message received.
        String messageText = event.getMessage();

        // Parses any object from JSON. Can be used instead of #getMessage()
        // Returns 'null' if it couldn't be parsed.
        MyObject myObject = event.getMessageObject(MyObject.class);

    }

}
```

## License
ForestRedisAPI is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/ForestTechMC/ForestRedisAPI/blob/master/LICENSE.txt) for more information.