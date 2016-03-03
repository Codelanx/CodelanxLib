package com.codelanx.codelanxlib.logging;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.listener.ListenerManager;
import com.codelanx.codelanxlib.util.ReflectBukkit;
import com.codelanx.commons.logging.Debugger;
import com.codelanx.commons.logging.Debugger.DebugOpts;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.exception.Exceptions;
import com.codelanx.commons.util.exception.IllegalInvocationException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Rogue on 11/18/2015.
 */
public class PluginDebugOpts extends DebugOpts {

    private final static Map<Plugin, DebugOpts> opts = new HashMap<>();

    protected final Plugin plugin;

    /**
     * Constructor. Determines the logging prefix and initializes fields
     *
     * @version 0.1.0
     * @since 0.1.0
     *
     * @param plugin The {@link Plugin} relevant to this instance
     */
    public PluginDebugOpts(Plugin plugin) {
        super();
        this.plugin = plugin;
        this.prefix = plugin.getDescription().getPrefix() == null
                ? plugin.getName()
                : plugin.getDescription().getPrefix();
        this.output = this.plugin.getClass() == CodelanxLib.class; //false except for CodelanxLib
        if (plugin.getDescription().getMain().startsWith("com.codelanx.")) {
            this.url = "http://blooper.codelanx.com/report"; //Hook specifically for codelanx plugins
        } else {
            this.url = null;
        }
    }

    /**
     * Returns the {@link Plugin} that this {@link DebugOpts} pertains to
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The relevant {@link Plugin} to this instance
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public JSONObject attachInfo() {
        JSONObject back = super.attachInfo();
        back.put("project-type", "bukkit-plugin");
        JSONObject plugin = new JSONObject();
        PluginDescriptionFile pd = this.getPlugin().getDescription();
        plugin.put("name", pd.getName());
        plugin.put("version", pd.getVersion());
        plugin.put("main", pd.getMain());
        plugin.put("prefix", this.getPrefix());
        back.put("plugin", plugin);
        JSONObject server = new JSONObject();
        Server s = Bukkit.getServer();
        server.put("allow-end", s.getAllowEnd());
        server.put("allow-flight", s.getAllowFlight());
        server.put("allow-nether", s.getAllowNether());
        server.put("ambient-spawn-limit", s.getAmbientSpawnLimit());
        server.put("animal-spawn-limit", s.getAnimalSpawnLimit());
        server.put("binding-address", s.getIp());
        server.put("bukkit-version", s.getBukkitVersion());
        server.put("connection-throttle", s.getConnectionThrottle());
        server.put("default-game-mode", s.getDefaultGameMode().name());
        server.put("default-world-type", s.getWorldType());
        server.put("generate-structures", s.getGenerateStructures());
        server.put("idle-timeout", s.getIdleTimeout());
        server.put("players-online", s.getOnlinePlayers().size());
        server.put("max-players", s.getMaxPlayers());
        server.put("monster-spawn-limit", s.getMonsterSpawnLimit());
        server.put("motd", s.getMotd());
        server.put("name", s.getName());
        server.put("online-mode", s.getOnlineMode());
        server.put("port", s.getPort());
        server.put("server-id", s.getServerId());
        server.put("server-name", s.getServerName());
        server.put("spawn-radius", s.getSpawnRadius());
        server.put("ticks-per-animal-spawns", s.getTicksPerAnimalSpawns());
        server.put("ticks-per-monster-spawns", s.getTicksPerMonsterSpawns());
        server.put("version", s.getVersion());
        server.put("view-distance", s.getViewDistance());
        server.put("warning-state", s.getWarningState());
        server.put("water-animal-spawn-limit", s.getWaterAnimalSpawnLimit());
        back.put("server", server);
        return back;
    }

    /**
     * Hooks into Bukkit's plugin system and adds a handler to all plugin
     * loggers to allow catching unreported exceptions. If already hooked, will
     * do nothing. This method will continue to hook new plugins via a listener
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @throws IllegalInvocationException If something other than
     *                                      {@link CodelanxLib} calls this
     *                                      method
     */
    public static void hookBukkit() {
        //Check to make sure CodelanxLib is calling it
        Exceptions.illegalInvocation(Reflections.accessedFrom(CodelanxLib.class),
                "Debugger#hookBukkit may only be called by CodelanxLib");
        Listener l = new BukkitPluginListener();
        if (!ListenerManager.isRegisteredToBukkit(CodelanxLib.get(), l)) {
            Bukkit.getServer().getPluginManager().registerEvents(l, CodelanxLib.get());
        }
        //Hook any current plugins without a handler
        for (Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) { //boo arrays
            ExceptionHandler.apply(p);
        }
    }

    /**
     * A {@link Listener} for adding an {@link ExceptionHandler} to a
     * {@link Plugin}'s {@link Logger} upon it being enabled
     *
     * @since 0.1.0
     * @author 1Rogue
     * @version 0.1.0
     */
    private final static class BukkitPluginListener implements Listener {

        /**
         * Appends an {@link ExceptionHandler} to a {@link Plugin}'s
         * {@link Logger}
         *
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param event The relevant {@link PluginEnableEvent} from Bukkit
         */
        @EventHandler
        public void onEnable(PluginEnableEvent event) {
            ExceptionHandler.apply(event.getPlugin());
        }

    }


    /**
     * Attachable {@link Handler} used to catch any exceptions that are logged
     * directly to a plugin's {@link Logger}
     *
     * @since 0.1.0
     * @author 1Rogue
     * @version 0.1.0
     */
    public static class ExceptionHandler extends Handler {

        private final Plugin plugin;

        /**
         * Constructor. Sets the plugin to a field and sets the filter for this
         * {@link Handler} to {@link Level#SEVERE}
         *
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param plugin The relevant {@link Plugin} to the {@link Logger}
         */
        public ExceptionHandler(Plugin plugin) {
            this.plugin = plugin;
            super.setFilter((LogRecord record) -> record.getLevel() == Level.SEVERE);
        }

        /**
         * If {@link LogRecord#getThrown()} does not return {@code null}, then this will call
         * {@link com.codelanx.commons.logging.Debugger.DebugUtil#report(DebugOpts, Throwable, String)}
         * <br><br> {@inheritDoc}
         *
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param record {@inheritDoc}
         */
        @Override
        public void publish(LogRecord record) {
            if (record.getThrown() != null) {
                //Report exception
                Debugger.DebugUtil.report(Debugger.DebugUtil.getOpts(), record.getThrown(), record.getMessage());
            }
        }

        /**
         * Does nothing
         *
         * @since 0.1.0
         * @version 0.1.0
         */
        @Override
        public void flush() {} //not buffered

        /**
         * Does nothing
         *
         * @since 0.1.0
         * @version 0.1.0
         *
         * @throws SecurityException Never happens
         */
        @Override
        public void close() throws SecurityException {} //nothing to close

        /**
         * Applies a new {@link Handler} to the passed {@link Plugin}'s
         * {@link Logger} if it is not already attached to it
         *
         * @since 0.0.1
         * @version 0.0.1
         *
         * @param p The {@link Plugin} with the {@link Logger} to check
         */
        public static void apply(Plugin p) {
            for (Handler h : p.getLogger().getHandlers()) {
                if (h instanceof ExceptionHandler) {
                    return;
                }
            }
            p.getLogger().addHandler(new ExceptionHandler(p));
        }

    }

    public static Debugger.DebugOpts getPluginOpts() {
        Plugin p = ReflectBukkit.getCallingPlugin(2);
        return PluginDebugOpts.getOpts(p);
    }

    private static Debugger.DebugOpts getOpts(Plugin p) {
        return PluginDebugOpts.opts.computeIfAbsent(p, PluginDebugOpts::new);
    }

}
