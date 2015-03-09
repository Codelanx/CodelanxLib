# CodelanxLib <a href="https://travis-ci.org/CodeLanx/CodelanxLib/builds"><img src="https://api.travis-ci.org/CodeLanx/CodelanxLib.svg" \></a>
Library for Codelanx plugins. Public use is allowed, but must be credited.

## Table of contents

* __[Commands](#commands)__
* __[Plugin Files](#plugin-file)__
  * __[Config System](#config)__
  * __[Lang Files](#lang)__
* __[Data Types](#data)__
  * __[FileDataType](#filedatatype)__
  * __[SQLDataType](#sqldatatype)__
  * __[Concrete Implementations](#data-impl)__
* __[Economy](#economy)__
* __[Implementers](#implementers)__
* __[Inventory](#inventory)__
  * __[Interfaces](#interfaces)__
* __[Listeners](#listeners)__
* __[Logging](#logging)__
* __[Permissions](#permissions)__
* __[Serializable classes](#serialize)__
* __[Utility Classes](#util)__
  * __[Auth](#auth)__
  * __~~Coverage~~__ - Deprecated, will be remade in version 0.3.0
  * __[Exceptions](#exceptions)__
  * __[Inventory](#inventory)__
  * __[Number](#number)__
  * __[Time](#time)__
  * __[Utility Classes](#util-classes)__
    * __[Blocks](#u-blocks)__
    * __[Cache](#u-cache)__
    * __[Databases](#u-databases)__
    * __[Paginator](#u-paginator)__
    * __[Players](#u-players)__
    * __[Protections](#u-players)__
    * __[RNG (Random number generators)](#u-rng)__
    * __[Reflections](#u-reflections)__
    * __[Scheduler](#u-scheduler)__
* __[Outline](#outline)__
  * __[Events](#events)__
  * __[Internal Classes](#internal)__
  * __[Legal](#legal)__


##<a name="commands"></a> Commands

__Note__: This section is now obsolete with the introduction of the new
command system being introduced in the 0.1.0 release

Overall, you will only be dealing with three classes when making commands, and
most/nearly all of the implementation for this is up to you as the developer.
Those classes are:

* `CommandHandler` - [Documentation](http://docs.codelanx.com/CodelanxLib/0.1.0/com/codelanx/codelanxlib/command/CommandHandler.html)
* `CommandStatus` - [Documentation](http://docs.codelanx.com/CodelanxLib/0.1.0/com/codelanx/codelanxlib/command/CommandStatus.html)
* `SubCommand` - [Documentation](http://docs.codelanx.com/CodelanxLib/0.1.0/com/codelanx/codelanxlib/command/SubCommand.html)

A `CommandHandler` is an object that will handle a specific command in your
plugin. For instance, let's say we had a channel plugin for chat, we could make
our "channel" `CommandHandler` like so:

```java
Plugin p = /* our main plugin instance */;
CommandHandler channel = new CommandHandler(p, "channel", true);
```

Great! That starts us off with a basic CommandHandler. This seems relatively
simple, but under the hood a lot just happened. For starters, there are two
reserved `SubCommand` classes that universally fill a specific purpose:

* `HelpCommand` - [Documentation](http://docs.codelanx.com/CodelanxLib/0.1.0/com/codelanx/codelanxlib/command/HelpCommand.html)
* `ReloadCommand` - [Documentation](http://docs.codelanx.com/CodelanxLib/0.1.0/com/codelanx/codelanxlib/command/ReloadCommand.html)

What these commands do (if it isn't immediately obvious) is covered in the
documentation for both of the classes. If you wish to unregister these commands
from your handler so as to not allow reloading or help output from that specific
command, you can use `CommandHandler#unregister(String)`. Continuing
off our channel example:

```java
channel.unregister("help"); //Note: 'channel' is a CommandHandler
channel.unregister("reload");
```

Additionally, upon our original construction of the `CommandHandler`, we can
specify that we do not want these classes automatically registered via the
`boolean` that is passed:

```java
CommandHandler channel = new CommandHandler(p, "channel", false); //false, will not register
```

Now that we have our CommandHandler, we should add our own SubCommands! Say we
want to have a command that allows a person to join a chat channel. The syntax,
for our purposes, will be something like `/channel join <channel>`. To start,
let's define our SubCommand:

```java
public class JoinCommand extends SubCommand<MyChannelPlugin> {

    public JoinCommand(MyChannelPlugin plugin, CommandHandler handler) {
        super(plugin, handler);
    }

}
```

Firstly, let's fill out the two simplest methods first for our command:

```java
    @Override
    public String getName() {
        return "join"; //This is the first argument for "channel", our subcommand's specific name
    }

    @Override
    public Lang info() {
        return Lang.createLang("Allows players to join a channel"); //We get to this later in the plugin file section!
    }
```

If you're confused about the `Lang` part here, don't fret! It's completely
covered under the [Lang section of this readme](#lang). Note that you wouldn't
want to have a call to `Lang#createLang(String)` in a production environment
for this method, but for now it helps convey the purpose.

Something that the `SubCommand` class also has is a method for usage, however
this isn't something you always have to override. By default, the usage printed
will be `/<main-command> <subcommand-name>`, however if you remember we want
to add a new argument for channel name, per `/channel join <channel>`, so let's
do that now:

```java
    @Override
    public String getUsage() {
        return super.getUsage() + " <channel>";
    }
```

And with that, it will now always return with the extra ` <channel>` on the end
of the usage for our subcommand!

Now we're left with the real meat of what your `SubCommand` class will be doing.
For the purposes of this example, our class will have a `Set<String> channels`
field which specifies which channels we can join.

For starters, we should fill in the tab completion so that players have an
easier time tab-completing our commands. That, and it's just cool to have!

```java
    @Override
    public List<String> tabComplete(CommandSender sender, String... args) {
        //From here, we need to return a list based on what has already been typed
        //The main command and the subcommand name are not included in our arguments array!
        if (args.length < 1) { //Empty arguments
            //Return our available channels
            return new ArrayList<>(this.channels);
        } else if (args.length == 1) { //potentially auto-completing a word
            //only return channels that start with the argument
            return this.channels.stream().filter(c -> c.startsWith(args[0])).collect(Collectors.toList());
        }
        return new ArrayList<>(); //catch-all, we don't have anything to provide
    }
```

Great! Now our command will auto-complete for anyone who attempts to tab a
channel name. All this leaves us now is to implement the actual command
execution. Remember that the concept of returning a boolean no longer exists
in `SubCommand` objects, we use the `CommandStatus` enum to specify how the
plugin will react to your command execution.

```java
    @Override
    public CommandStatus execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return CommandStatus.PLAYER_ONLY; //Only players use channels
        }
        //The main command and the subcommand name are not included in our arguments array!
        if (args.length < 1) { //Empty arguments
            return CommandStatus.BAD_ARGS;
        }
        if (!this.channels.contains(args[0])) { //Make sure channel exists
            sender.sendMessage("That channel does not exist!");
            return CommandStatus.SUCCESS; //Success? That seems odd... Explained below!
        }
        //At this point, we've confirmed the channel exists, and the sender is a player
        if (!sender.hasPermission("myplugin.channels." + args[0])) {
            //Hey! They can't join that channel!
            return CommandStatus.NO_PERMISSION;
        }
        //Let's presume the main plugin has a #joinChannel(Player, String) method
        this.plugin.joinChannel((Player) sender, args[0]);
        return CommandStatus.SUCCESS;
    }
```

And there you have it! You now have a complete `SubCommand` (to which I've
placed at the bottom of this section). All you need to do now is register your
`SubCommand` when you make your `CommandHandler`:

```java
Plugin p = /* our main plugin instance */;
CommandHandler channel = new CommandHandler(p, "channel", true);
channel.register(new JoinCommand<>(p, channel)); //Can pass multiple new SubCommand objects
```

> But why did you return `CommandStatus#SUCCESS` when the channel didn't exist?

The thing about `CommandStatus#SUCCESS` and `CommandStatus#FAILED` is that it
has nothing to do with the actual context of the command itself. When you
reached the point where your command recognized the channel didn't exist, and
sent a message to the player in response to that, you successfully handled that
situation. If you were to have a totally different situation, say doing file
I/O:

```java
File f = /* some file */;
try {
    f.createNewFile();
} catch (Exception ex) {
    //The file can't be made, we have no way to recover from this exception :(
    return CommandStatus.FAILED;
}
```

Notice here that our problem isn't that the user provided bad input, but rather
that our code ran into a problem while attempting to execute, and we no longer
have a reasonable way to continue. This is almost exclusively the case with
exceptions, but it may come up in other contexts.

And lastly, here is our completed SubCommand!

```java
public class JoinCommand extends SubCommand<MyChannelPlugin> {

    private final Set<String> channels = new HashSet<>(); //Our channels

    public JoinCommand(MyChannelPlugin plugin, CommandHandler handler) {
        super(plugin, handler);
        this.channels.addAll(plugin.getChannelNames()); //Pretend method
    }

    @Override
    public CommandStatus execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            return CommandStatus.PLAYER_ONLY; //Only players use channels
        }
        //The main command and the subcommand name are not included in our arguments array!
        if (args.length < 1) { //Empty arguments
            return CommandStatus.BAD_ARGS;
        }
        if (!this.channels.contains(args[0])) { //Make sure channel exists
            sender.sendMessage("That channel does not exist!");
            return CommandStatus.SUCCESS; //You thought I copy/pasted every line didn't you?
        }
        //At this point, we've confirmed the channel exists, and the sender is a player
        if (!sender.hasPermission("myplugin.channels." + args[0])) {
            //Hey! They can't join that channel!
            return CommandStatus.NO_PERMISSION;
        }
        //Let's presume the main plugin has a #joinChannel(Player, String) method
        this.plugin.joinChannel((Player) sender, args[0]);
        return CommandStatus.SUCCESS;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String... args) {
        //From here, we need to return a list based on what has already been typed
        //The main command and the subcommand name are not included in our arguments array!
        if (args.length < 1) { //Empty arguments
            //Return our available channels
            return new ArrayList<>(this.channels);
        } else if (args.length == 1) { //potentially auto-completing a word
            //only return channels that start with the argument
            return this.channels.stream().filter(c -> c.startsWith(args[0])).collect(Collectors.toList());
        }
        return new ArrayList<>(); //catch-all, we don't have anything to provide
    }

    @Override
    public String getUsage() {
        return super.getUsage() + " <channel>";
    }

    @Override
    public String getName() {
        return "join"; //This is the first argument for "channel", our subcommand's specific name
    }

    @Override
    public Lang info() {
        return Lang.createLang("Allows players to join a channel"); //We get to this later in the plugin file section!
    }

}
```


##<a name="plugin-file"></a> Plugin Files

Plugin files are the foundation of building interfaces to be applied for
interacting with a flat-file format. As of the current release, there are two
provided interfaces that use this interfacing, which are the [Config](#config)
and [Lang](#lang) interfaces.

Typically, the `PluginFile` interface should be applied to an enum, to allow for
specifying multiple file keys within a single class file. The interface is
completely legal to apply to a class instead, however if it is applied to a
class, then the class must implement the `Iterable` interface from Java 8 in
order for `PluginFile#init(Class<T extends FileDataType>)` to work. The easiest
way to think about it is that a `PluginFile` in reality specifies only a single
value of a file, which is why a multi-instance class such as an enum helps to
specify everything at once.

Lastly, a `PluginFile` needs to have two class-level annotations: `PluginClass`
and `RelativePath`. The `PluginClass` annotation simply needs a reference to
your main class, and `RelativePath` specifies the name and location of the file
that the implementing `PluginFile` uses.

At the heart of an implementation for `PluginFile`, what you as a user will be
dealing with is something like this:

```java
@PluginClass(MyPlugin.class) //Pass your main class
@RelativePath("some-file.yml") //The location of your file in the plugin folder
public enum MyPluginFile implements PluginFile {

    EXAMPLE_STRING("example.string", "Hello world!"),
    EXAMPLE_INT("example.int", 42),
    EXAMPLE_DOUBLE("example.double", 3.14),
    EXAMPLE_LIST("example.list", new ArrayList<>());

    private static Yaml yaml; //FileDataType, you'll see this later!
    private final String path;
    private final Object def;

    /**
     * Enum Constructor, stores the keys and default values for the PluginFile
     */
    private MyPluginFile(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Object getDefault() {
        return this.def;
    }

    @Override
    public Yaml getConfig() {
        if (MyPluginFile.yaml == null) {
            MyPluginFile.yaml = this.init(Yaml.class); //Lazy initialization
        }
        return MyPluginFile.yaml;
    }

}
```

With this, you have defined the basic structure of all `PluginFile` objects.
However, you rarely apply the `PluginFile` interface directly to an enum, as
this does not carry much functionality at all. This is where the
[Config](#config) and [Lang](#lang) interfaces that are provided come into play.

###<a name="config"></a> Configuration Files (Config)

As you saw in the [PluginFile](#plugin-file) specifications, `PluginFile` can be
extended into other interfaces to add functionality to the file. `Config` is one
of these interfaces, and to specify an enum as a Config you merely have to
change the class declaration from:

```java
public enum MyPluginFile implements PluginFile {
```

To:

```java
public enum MyPluginFile implements Config {
```

And like magic, your plugin file now has all the methods it needs to be used as
a config file. With this, some new methods are automatically added to your
class:

* `Config#as(Class<T>)`

This is the main method that you will be dealing with when using the `Config`
interface. If we look at our earlier example for `MyPluginFile`, we see that
we're able to reference multiple `Config` values by specifying which enum
constant we want to use. So if we wanted to retrieve some values:

```java
double answer = MyPluginFile.EXAMPLE_DOUBLE.as(double.class); //Retrieve a double
String hello = MyPluginFile.EXAMPLE_STRING.as(String.class); //Retrieve a String
```

With this, we have added a fundamental separation of config management and
data retrieval. Config values usually need to be retrieved often and continually
chaining up or down a reference chain to get from your current context to your
plugin and back to a config manager turns into an unmanageable hassle.

* `Config#get()` and `Config#set(Object)`

On a lower level, these methods are for directly interfacing with the underlying
`FileDataType` for the `Config` being referred to. In general, you won't use
`Config#get()` directly, as this returns a raw `Object` that requires casting
which can already be done through the `as` method (which has additional safety
checks in place). As for `set(Object)`, it's fairly self-explanatory:

```java
String example = "Mashed Potatoes";
Config val = MyPluginFile.EXAMPLE_STRING;
val.set(example);
System.out.println(val.as(String.class)); //Prints "Mashed Potatoes"

//Simplified
MyPluginFile.EXAMPLE_STRING.set("Mashed Potatoes");
```

Something to note is that information that is inserted via `#set(Object)`
will not change the file that the Config is based off of, due to the way that
underlying implementations of `FileDataType` work. In order to save set values
to the file, you will need to call the `PluginFile#save()` method on the config.
In this area, the call for this is slightly awkward when utilizing an enum, as
you need to have an instance to refer to rather than a static call (this is
a limitation of java):

```java
MyPluginFile.EXAMPLE_STRING.save();
```

While `EXAMPLE_STRING` was used, the entire file is actually saved in this call.

* `Config#retrieve(FileDataType, Config)` and `Config#retrieve(FileDataType)`

Sometimes, a `Config` should actually relate to more than one file. For this,
methods are provided to retrieve an anonymous/dynamic `Config` value which uses
the passed data type with the paths and defaults of the relevant `Config` value
that was already in use. What's nice about the abstraction with `FileDataType`
here is that you are able to apply `Config` values across different types of
files. For example:

```java
Json json = /* a JSON file we need data from */;
//Retrieve the EXAMPLE_STRING from the json file
String val = MyPluginFile.EXAMPLE_STRING.retrieve(json).as(String.class);
```

The `Config#retrieve(FileDataType)` actually just calls the static method
`Config#retrieve(FileDataType, Config)` with the current config context, which
returns an anonymous `Config` class containing the relevant information for
`Config` to internally handle retrieving values. So in essence, the above
example can easily be written as:

```java
String val = Config.retrieve(json, MyPluginFile.EXAMPLE_STRING).as(String.class);
```

This is useful for when you have a method which accepts a `Config` parameter and
don't wish to call upon the specific config value directly. This is a bit more
advanced, and won't actually be applicable until Java 9 is released (due to
type erasure being removed), but if you want to cast to the appropriate type,
and can assume that the default supplied by the unknown `Config` value isn't
null, you can automatically retrieve the value like so:

```java
public void doSomething(Config value) {
    //Because we don't know the type
    SomeObject val = value.as(value.getDefault().getClass());
}
```

At which point in time a `Config#cast()` method might be added, however this is
much more into the realm of theory than actual implementations yet.

###<a name="lang"></a> Language Files (Lang)

We've now seen both the `PluginFile` interface, as well as one of its extending
interfaces (`Config`). However CodelanxLib provides a second interface that
extends `PluginFile` specifically for the purpose of string externalization and
`CommandSender` messaging. To implement, simply swap the `PluginFile` interface
with the `Lang` interface, just like you did before with configs:

```java
public enum MyPluginFile implements Lang {
```

You now have a Lang enum, which adds some slightly different functionality to
your `PluginFile`. `PluginFile#getDefault()` is now overridden, as Lang files
only map strings to other strings, therefore the default value should always
be a string. In this specific scenario, I'm going to redefine some things from
the previous `MyPluginFile` example:

```java
@PluginClass(MyPlugin.class) //Pass your main class
@RelativePath("some-file.yml") //The location of your file in the plugin folder
public enum MyPluginFile implements Lang {

    EXAMPLE_STRING("example.string", "Hello world!"),
    EXAMPLE_ARGS("example.with-args", "This is a %s"),
    EXAMPLE_MONEY("example.money", "Your balance is $%.2f"),
    /**
     * By contract, a format should only have a single '%s' token, which is
     * where all messages that are sent will be placed
     */
    FORMAT("format", "[&9MyAwesomePlugin&f] %s");

    private static Yaml yaml; //Note you can use other FileDataTypes
    private final String path;
    private final String def;

    /**
     * Enum Constructor, stores the keys and default values for the PluginFile
     */
    private MyPluginFile(String path, String def) {
        this.path = path;
        this.def = def;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDefault() { //Notice the return value has changed to String
        return this.def;
    }

    @Override
    public Lang getFormat() { //A new method we need to override
        return MyPluginFile.FORMAT; //Returning our format for output
    }

    @Override
    public Yaml getConfig() {
        if (MyPluginFile.yaml == null) {
            MyPluginFile.yaml = this.init(Yaml.class); //Lazy initialization
        }
        return MyPluginFile.yaml;
    }

}
```

`Lang` takes `Formatter` arguments, which are synonymous with C/C++'s `printf`
tokens. To see these in action:

```java
CommandSender sender = /* our CommandSender (or player, etc) */;
Lang.sendMessage(sender, MyPluginFile.EXAMPLE_STRING); //Message sent!

//Replacing the %s token with a string
Lang.sendMessage(sender, MyPluginFile.EXAMPLE_ARGS, "test"); //Sends "This is a test"
Lang.sendMessage(sender, MyPluginFile.EXAMPLE_ARGS, "unicorn enchilada"); //Sends "This is a unicorn enchilada"

//Formatting money (useful with Vault!)
double money = 42D;
Lang.sendMessage(sender, MyPluginFile.EXAMPLE_MONEY, money); //Sends "Your balance is $42.00"
```

Note that because `Formatter`/`printf` are incredibly unforgiving in mismatched
or missing tokens/arguments, you should be very careful that you always have the
correct amount of both. In a single-plugin scenario, this is fairly easy, but
since you're allowing people to modify the lang by using this, you're opening
them to the possibility of messing up the plugin. If you want to proactively
monitor the values of all your `Lang` strings, then you can override the
`Lang#value()` method:

Re-implementing `Lang#value()`:
```java
@Override
public String value() {
    String format = Lang.super.value(); //Retrieve what we will work with
    //Verify/Modify the contents of "format" here (or throw an exception), and then
    return format; //Return the appropriate string
}
```

In reality, you won't end up using `Lang` for much more than what is described
above. However, for methods not shown above:

* `Lang#color(String)`

You might be familiar with the method:

```java
String myString = /* some string with color codes */;
myString = ChatColor.translateAlternateColorCodes('&', myString);
```

This method is _exhausting_ to write for such a simple operation as swapping
color codes. For this, `Lang#color(String)` is provided as a façade for exactly
that method:

```java
public static String color(String color) {
    return ChatColor.translateAlternateColorCodes('&', color);
}
```

Seeing as most of the community is already familiar with the `&` symbol being
used for color, the method automatically assumes that is what you're
translating. For all other purposes that need a different character, `ChatColor`
should be used.

* `Lang#createLang(String)`

As we saw with the `Config` interface, a way to retrieve an anonymous value
of the interface helps a lot in situations where you need to dynamically create
values, for which enums are not purposed to do. This is an extremely simple
wrapping operation: The string you put in is the same value as the `Lang` that
is returned:

```java
CommandSender target = /* our message recipient */;
String value = "MazenMC lost his pet T-Rex";
Lang out = Lang.createLang(value);
Lang.sendMessage(target, out); //Sends "MazenMC lost his pet T-Rex"
```

This is relevant farther down the line when you begin to use
[Implementers](#implementers), such as `Economics` to enable the use of
`CEconomy`. A lot of implementers extend the implementer interface `Formatted`,
meaning that they need to be able to retrieve a `Lang` object to use as a format
for your messages, as those classes don't actually have any knowledge of your
specific plugin! For more info on that, see the `Formatted` interface under the
[Implementers](#implementers) section.

* `Lang#sendMessage(CommandSender, Lang, Lang, Object...)`

This is a variation of the original `Lang#sendMessage(CommandSender, Lang, Object...)`
that you saw earlier in the examples. However, instead of using the default
format provided by the `Lang` that you pass in, it will use the first `Lang`
argument as the actual format. So in practice:

```java
CommandSender sender = /* our message recipient */;
Lang ourNewFormat = Lang.createLang("[&4Tacos&f] %s");
Lang.sendMessage(sender, MyPluginFile.EXAMPLE_STRING); //Encodes and prints "[&9MyAwesomePlugin&f] Hello World!"
Lang.sendMessage(sender, ourNewFormat, MyPluginFile.EXAMPLE_STRING); //Encodes and prints "[&4Tacos&f] Hello World!"
```

This is much more useful for when you have plugin inheritence, and one plugin
is essentially extending the usage. If you want to be additionally sneaky, you
can use another `Lang` class's format as your own:

```java
@Override
public Lang getFormat() {
    return SomeOtherLang.getFormat();
}
```

##<a name="outline"></a> Outline

###<a name="legal"></a> Legal

Code copyright is a giant headache, which most people don't want to even think
about (I sure don't). So, to make things simple, here's a brief summary of what
you can, cannot, and must do under CodelanxLib's library:

<div style="text-align: center;"><img src="http://i.imgur.com/WMwzhEa.png" /></div>

The only discrepency here, however, is that you cannot modify <i>and</i> distribute
simultaneously. That is to say, if you modify the library, and proceed to distribute
your modifications, you must label/publicize these modifications as your own, and not as
the original library.