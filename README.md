# ExperimentalCore

A minimal Paper plugin: it shows a welcome message when a player joins.

This is a learning project, built up one feature at a time so you can see what
each piece does. The furniture chair fix used to live here; it now has its own
project at `..\FurnitureChairs`, so that experimenting here cannot break
seating on the server.

## Build it

In IntelliJ, or from this folder:

```
mvn package
```

The jar lands at `target/ExperimentalCore-1.0.0.jar`. Upload that to the
Raspberry Pi's `plugins/` folder and restart the server.

> **Careful:** the jar currently running on the Pi is the older, fuller version
> - it also provides `/fly`, `/patch` and the furniture chairs. Uploading this
> build over it would remove all three. Upload FurnitureChairs first if you want
> seating to keep working.

## Java version: must be 25 or newer

The Paper API jar is compiled to class file version 69, which is Java 25. You
cannot build against it with an older JDK, and you cannot work around it by
lowering `maven.compiler.release` in pom.xml - javac would then refuse the
dependency with "class file has wrong version 69.0".

**In IntelliJ:** `File > Project Structure > Project > SDK` must be JDK 25+.
If it is set to 24 the build fails with `release version 25 not supported`,
because Maven's runner defaults to the project SDK.

**On the command line**, point Maven at the right JDK first:

```
$env:JAVA_HOME = "D:\Program Files\Java\jdk-27"
```

## What is in here

```
pom.xml                         how to build: one dependency, Java version, output name
src/main/resources/plugin.yml   what the server reads: name, main class
src/main/resources/config.yml   default settings, copied out on first run
src/main/java/rockar/experimentalcore/
    ExperimentalCore.java       main class - onEnable wires everything together
    listeners/JoinListener.java the welcome message
```

## The three ideas worth knowing

**1. A plugin is a jar with a `plugin.yml`.** That file names the main class.
Bukkit loads the jar, finds the class, and calls `onEnable()`. No `plugin.yml`,
no plugin.

**2. Listeners need registering.** Writing `@EventHandler` is not enough -
`registerEvents(listener, plugin)` is what actually hooks it up. An unregistered
listener fails silently, with no error to tell you why nothing happened.

**3. Commands need two halves.** Declared in `plugin.yml`, handled in code via
`getCommand("name").setExecutor(...)`. Miss either half and the command silently
does nothing. This plugin has no commands yet - adding one is the exercise below.

## How the join message works

`PlayerJoinEvent` fires when someone joins. `JoinListener` has a method marked
`@EventHandler` that takes it as a parameter - and **the parameter type is what
decides which event you are listening to**, not the method name. Bukkit finds
the method by reflection and calls it.

The message text lives in `config.yml` as a list, one entry per line. Config is
read at runtime, so changing the text and restarting is enough; only Java
changes need a rebuild.

`&` colour codes work because `ExperimentalCore.format()` converts them into an
Adventure `Component`. Sending the raw string would print `&6Welcome` literally.

## Things to try, easiest first

**Change the message.** Edit `join.message` in the server's
`plugins/ExperimentalCore/config.yml` and restart. No rebuild at all.

**Add a leave message.** `PlayerQuitEvent` is the same shape as the join handler
with one word changed. Put it in `JoinListener` - it needs no new registration,
because registration is per-class, not per-method.

**Hide the default join broadcast.** `event.joinMessage(null)` removes the
yellow `X joined the game` line, so only your own message shows.

**Add a command.** `/ping` is a good first one - `player.getPing()` makes the
body nearly one line, so the lesson is the three-step pattern: declare it in
`plugin.yml`, write a class implementing `CommandExecutor`, register it in
`onEnable()`.

## Reference

- Paper plugin docs: https://docs.papermc.io/paper/dev/getting-started/paper-plugins
- Event list: https://jd.papermc.io/paper/
