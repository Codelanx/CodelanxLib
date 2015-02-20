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


##<a name="plugin-file"></a> Plugin Files

Plugin files are the foundation of building interfaces to be applied for
interacting with a flat-file format. As of the current release, there are two
provided interfaces that use this interfacing, which are the [Config](#config)
and [Lang](#lang) interfaces.

Typically, the `PluginFile` interface should be applied to an enum, to allow for
specifying multiple file keys within a single class file. The interface is
completely legal to apply to a class instead, however if it is applied to a
class, then the class must implement the `Iterable` interface from Java 8 in
order for `PluginFile#init(Class<T extends FileDataType)` to work. The easiest
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

* `Config#retrieve(FileDataType, Config)` and `Config#fromOther(FileDataType)`

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
String val = Config.retrieve(json, MyPluginFile.EXAMPLE_STRING);
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
    public Lang getFormat() {
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

Some of you might be familiar with the method:

```java
String myString = /* some string with color codes */;
myString = ChatColor.translateAlternateColorCodes('&', myString);
```

This method is _exhausting_ to write for such a simple operation as swapping
color codes. For this, `Lang#color(String)` is provided as a facade for exactly
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