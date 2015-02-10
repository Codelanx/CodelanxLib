/*
 * Copyright (C) 2015 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2015 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.logging;

import com.codelanx.codelanxlib.util.exception.Exceptions;
import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.util.Reflections;
import com.codelanx.codelanxlib.util.Scheduler;
import com.codelanx.codelanxlib.util.exception.IllegalPluginAccessException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.JSONObject;

/**
 * Provides toggleable logging supporting for debug statements and error
 * reporting to a webservice for easy bugfixing. Will find unreported errors as
 * well and submit them. Note that this class is provided as a utility, it
 * should not be used as a substitute for general logging statements that are
 * <i>always</i> available. Use the {@link Logging} class instead.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Debugger {

    private final static Map<Plugin, DebugOpts> opts = new HashMap<>();
    private final static Logger logger = Logger.getLogger(Debugger.class.getName());
    private static boolean listenerHooked = false;

    private Debugger() {
    }

    private static DebugOpts getOpts() {
        Plugin p = Reflections.getCallingPlugin(1);
        return Debugger.getOpts(p);
    }

    private static DebugOpts getOpts(Plugin p) {
        DebugOpts back = Debugger.opts.get(p);
        if (back == null) {
            back = new DebugOpts(p);
            Debugger.opts.put(p, back);
        }
        return back;
    }

    /**
     * Hooks into Bukkit's plugin system and adds a handler to all plugin
     * loggers to allow catching unreported exceptions. If already hooked, will
     * do nothing.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @throws IllegalPluginAccessException If something other than
     * {@link CodelanxLib} calls this method
     */
    public static void hookBukkit() {
        //Check to make sure CodelanxLib is calling it
        Exceptions.illegalPluginAccess(Reflections.accessedFrom(CodelanxLib.class),
                "Debugger#hookBukkit may only be called by CodelanxLib!");
        //end check - add hook
        if (!Debugger.listenerHooked) {
            //Add listener hook for new, incoming plugins
            Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onEnable(PluginEnableEvent event) {
                    for (Handler h : event.getPlugin().getLogger().getHandlers()) {
                        if (h instanceof ExceptionHandler) {
                            return;
                        }
                    }
                    event.getPlugin().getLogger().addHandler(new ExceptionHandler(event.getPlugin()));
                }

            }, CodelanxLib.get());
        }
        Debugger.listenerHooked = true; //potential failure point on reload?
        //Hook any current plugins without a handler
        pluginLoop:
        for (Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) {
            for (Handler h : p.getLogger().getHandlers()) {
                if (h instanceof ExceptionHandler) {
                    continue pluginLoop;
                }
            }
            p.getLogger().addHandler(new ExceptionHandler(p));
        }
    }

    /**
     * Sets the URL to send a JSON payload of server information to as well as
     * any other relevant information for when a stack trace occurs. This allows
     * for a simple way of setting up error reporting. The default value for
     * this is {@code null}, and as a result will not send any information upon
     * errors occurring unless a target URL is set.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param url The URL to send JSON payloads to
     */
    public static void setReportingURL(String url) {
        DebugOpts opts = Debugger.getOpts();
        if (opts != null) {
            opts.setUrl(url);
        }
    }

    /**
     * Sets whether or not to actually output any calls from your plugin to the
     * Debugger. This defaults to {@code false}.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param output {@code true} if calls to the Debugger should print out
     */
    public static void toggleOutput(boolean output) {
        DebugOpts opts = Debugger.getOpts();
        if (opts != null) {
            opts.toggleOutput(output);
        }
    }

    /**
     * Prints to the Debugging {@link Logger} if
     * {@link Debugger#toggleOutput(boolean)} is set to {@code true}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param level The {@link Level} to print at
     * @param format The formatting string
     * @param args The printf arguments
     */
    public static void print(Level level, String format, Object... args) {
        DebugOpts opts = Debugger.getOpts();
        if (opts == null || !opts.doOutput()) {
            return;
        }
        Debugger.logger.log(level, String.format("[%s]=> %s",
                opts.getPrefix(), String.format(format, args)));
    }

    /**
     * Prints to the Debugging {@link Logger} at {@link Level#INFO} if
     * {@link Debugger#toggleOutput(boolean)} is set to {@code true}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param format The formatting string
     * @param args The printf arguments
     */
    public static void print(String format, Object... args) {
        Debugger.print(Level.INFO, format, args);
    }

    /**
     * Prints to the Debugging {@link Logger} at {@link Level#SEVERE} if
     * {@link Debugger#toggleOutput(boolean)} is set to {@code true}. It will
     * also report the error with the URL set via
     * {@link Debugger#setReportingURL(String)}. If that is {@code null},
     * nothing will be reported
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param error The {@link Throwable} to be printed
     * @param message The formatting string
     * @param args The formatting arguments
     */
    public static void error(Throwable error, String message, Object... args) {
        DebugOpts opts = Debugger.getOpts();
        if (opts == null) {
            return;
        }
        if (opts.doOutput()) {
            Debugger.logger.log(Level.SEVERE, String.format(message, args), error);
        }
        //Send JSON payload
        Debugger.report(opts, error, String.format(message, args));
    }

    /**
     * Reports an error to a specific reporting URL
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param opts The {@link DebugOpts} relevant to the current plugin context
     * @param error The {@link Throwable} to report
     * @param message The message relevant to the error
     */
    private static void report(DebugOpts opts, Throwable error, String message) {
        if (opts == null || opts.getUrl() == null) {
            return;
        }
        Scheduler.runAsyncTask(() -> {
            JSONObject out = Debugger.getPayload(opts, error, message);
            try {
                Debugger.send(opts.getUrl(), out);
            } catch (IOException ex) {
                Debugger.logger.log(Level.WARNING, "Unable to report error!");
                //Logger-generated errors should not be re-reported, and
                //no ErrorManager is present for this instance
            }
        }, 0);
    }

    /**
     * Returns a JSON payload containing as much relevant server information as
     * possible (barring anything identifiable) and the error itself
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param opts The {@link DebugOpts} relevant to the current plugin context
     * @param error The {@link Throwable} to report
     * @param message The message relevant to the error
     * @return A new {@link JSONObject} payload
     */
    private static JSONObject getPayload(DebugOpts opts, Throwable error, String message) {
        JSONObject back = new JSONObject();
        back.put("project-type", "bukkit-plugin");
        JSONObject plugin = new JSONObject();
        PluginDescriptionFile pd = opts.getPlugin().getDescription();
        plugin.put("name", pd.getName());
        plugin.put("version", pd.getVersion());
        plugin.put("main", pd.getMain());
        plugin.put("prefix", opts.getPrefix());
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
        JSONObject system = new JSONObject();
        system.put("name", System.getProperty("os.name"));
        system.put("version", System.getProperty("os.version"));
        system.put("arch", System.getProperty("os.arch"));
        back.put("system", system);
        JSONObject java = new JSONObject();
        java.put("version", System.getProperty("java.version"));
        java.put("vendor", System.getProperty("java.vendor"));
        java.put("vendor-url", System.getProperty("java.vendor.url"));
        java.put("bit", System.getProperty("sun.arch.data.model"));
        back.put("java", java);
        back.put("message", message);
        back.put("error", Exceptions.readableStackTrace(error));
        return back;
    }

    /**
     * Sends a JSON payload to a URL specified by the string parameter
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param url The URL to report to
     * @param payload The JSON payload to send via POST
     * @throws IOException If the sending failed
     */
    private static void send(String url, JSONObject payload) throws IOException {
        URL loc = new URL(url);
        HttpURLConnection http = (HttpURLConnection) loc.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/json");
        http.setUseCaches(false);
        http.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.writeBytes(payload.toJSONString());
            wr.flush();
        }
    }

    /**
     * Represents internally stored debugging options for specific plugins
     * 
     * @since 0.1.0
     * @author 1Rogue
     * @version 0.1.0
     */
    private static class DebugOpts {

        private final Plugin plugin;
        private final String prefix;
        private boolean output;
        private String url;

        /**
         * Constructor. Determines the logging prefix and initializes fields
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param plugin The {@link Plugin} relevant to this instance
         */
        public DebugOpts(Plugin plugin) {
            this.plugin = plugin;
            this.prefix = plugin.getDescription().getPrefix() == null
                    ? plugin.getName()
                    : plugin.getDescription().getPrefix();
            this.output = true;
            if (plugin.getDescription().getMain().startsWith("com.codelanx.")) {
                this.url = "http://blooper.codelanx.com/report"; //Hook specifically for codelanx plugins
            } else {
                this.url = null;
            }
        }

        /**
         * Returns {@code true} if output is printed to the debug {@link Logger}
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return {@code true} if output is printed
         */
        public boolean doOutput() {
            return this.output;
        }

        /**
         * Toggles whether or not to print information to the debugger
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param output {@code true} to enable output
         */
        public void toggleOutput(boolean output) {
            this.output = output;
        }

        /**
         * Returns the URL that errors are reported to
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The error reporting URL
         */
        public String getUrl() {
            return this.url;
        }

        /**
         * Sets the URL to report errors to
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param url The error reporting URL
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Returns the logging prefix used for debug output. This is typically
         * the plugin's name unless a prefix is specified in the plugin's
         * {@code plugin.yml} file.
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The prefix used for debug output
         */
        public String getPrefix() {
            return this.prefix;
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
         * If {@link LogRecord#getThrown()} does not return {@code null}, then
         * this will call {@link Debugger#report(DebugOpts, Throwable, String)}
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
                Debugger.report(Debugger.getOpts(this.plugin), record.getThrown(), record.getMessage());
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

    }

}
