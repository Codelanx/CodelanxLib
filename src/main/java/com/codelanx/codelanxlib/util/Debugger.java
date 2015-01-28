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
package com.codelanx.codelanxlib.util;

import com.codelanx.codelanxlib.CodelanxLib;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

/**
 * Provides toggleable logging supporting for debug statements and error
 * reporting to a webservice for easy bugfixing. Will find unreported
 * errors as well and submit them
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
        Plugin p = Debugger.getDeclaringPlugin();
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

    private static JavaPlugin getDeclaringPlugin() {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        if (elems.length < 5) {
            throw new UnsupportedOperationException("Must be called from a class loaded from a plugin!");
        }
        JavaPlugin back = JavaPlugin.getProvidingPlugin(elems[4].getClass());
        if (back == null) {
            throw new UnsupportedOperationException("Must be called from a class loaded from a plugin!");
        }
        return back;
    }

    /**
     * Hooks into bukkit's plugin system and adds a handler to all plugin
     * loggers to allow catching unreported exceptions. If already hooked,
     * will do nothing.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws IllegalAccessException If something other than
     *                                {@link CodelanxLib} calls this method
     */
    public static void hookBukkit() throws IllegalAccessException {
        //Check to make sure CodelanxLib is calling it
        if (!ReflectionUtil.accessedFrom(CodelanxLib.class)) {
            throw new IllegalAccessException("Debugger#hookBukkit may only be called by CodelanxLib!");
        }
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

    public static void setReportingURL(String url) {
        DebugOpts opts = Debugger.getOpts();
        if (opts != null) {
            opts.setUrl(url);
        }
    }

    public static void toggleOutput(boolean output) {
        DebugOpts opts = Debugger.getOpts();
        if (opts != null) {
            opts.toggleOutput(output);
        }
    }

    public static void print(Level level, String format, Object... args) {
        DebugOpts opts = Debugger.getOpts();
        if (opts == null || !opts.doOutput()) {
            return;
        }
        Debugger.logger.log(level, String.format("[%s#DEBUG]=> %s",
                opts.getPrefix(), String.format(format, args)));
    }

    public static void print(String format, Object... args) {
        Debugger.print(Level.INFO, format, args);
    }

    public static void error(Throwable error, String message, Object... args) {
        Debugger.logger.log(Level.SEVERE, String.format(message, args), error);
        DebugOpts opts = Debugger.getOpts();
        if (opts == null || opts.getUrl() == null) {
            return;
        }
        //Send JSON payload
        Debugger.report(opts, error, String.format(message, args));
    }

    private static void report(DebugOpts opts, Throwable error, String message) {
        if (opts == null || opts.getUrl() == null) {
            return;
        }
        Scheduler.runAsyncTask(() -> {
            JSONObject out = Debugger.getPayload(opts, error, message);
            try {
                Debugger.send(opts.getUrl(), out);
            } catch (IOException ex) {
                Debugger.print(Level.WARNING, "Unable to report error!");
                //Logger-generated errors should not be re-reported, and
                //no ErrorManager is present for this instance
            }
        }, 0);
    }

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
        back.put("error", Debugger.readableStackTrace(error));
        return back;
    }

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

    private static String readableStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement elem : trace) {
            sb.append("\tat ").append(elem).append('\n');
        }
        if (t.getCause() != null) {
            Debugger.readableStackTraceAsCause(sb, t.getCause(), trace);
        }
        return sb.toString();
    }

    private static void readableStackTraceAsCause(StringBuilder sb, Throwable t, StackTraceElement[] causedTrace) {
        // Compute number of frames in common between previous and caused
        StackTraceElement[] trace = t.getStackTrace();
        int m = trace.length - 1;
        int n = causedTrace.length - 1;
        while (m >= 0 && n >=0 && trace[m].equals(causedTrace[n])) {
            m--; n--;
        }
        int common = trace.length - 1 - m;

        sb.append("Caused by: ").append(t).append('\n');
        for (int i = 0; i <= m; i++) {
            sb.append("\tat ").append(trace[i]).append('\n');
        }
        if (common != 0) {
            sb.append("\t... ").append(common).append(" more\n");
        }
        if (t.getCause() != null) {
            Debugger.readableStackTraceAsCause(sb, t.getCause(), causedTrace);
        }
    }

    private static class DebugOpts {

        private final Plugin plugin;
        private final String prefix;
        private boolean output;
        private String url;

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

        public boolean doOutput() {
            return this.output;
        }

        public void toggleOutput(boolean output) {
            this.output = output;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

    }

    public static class ExceptionHandler extends Handler {

        private final Plugin plugin;

        public ExceptionHandler(Plugin plugin) {
            this.plugin = plugin;
            super.setFilter((LogRecord record) -> record.getLevel() == Level.SEVERE);
        }

        @Override
        public void publish(LogRecord record) {
            if (record.getThrown() != null) {
                //Report exception
                Debugger.report(Debugger.getOpts(this.plugin), record.getThrown(), record.getMessage());
            }
        }

        @Override
        public void flush() {} //not buffered

        @Override
        public void close() throws SecurityException {} //nothing to close

    }

}
