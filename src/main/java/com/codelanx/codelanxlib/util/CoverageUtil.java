/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
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

import com.codelanx.codelanxlib.util.number.Single;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link CoverageUtil}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public final class CoverageUtil {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface Coverage {
        int value();
    }

    private final static class PluginMarker {

        private final String name;
        private final Plugin plugin;
        private final Map<String, ClassMarker> classes = new HashMap<>();

        public PluginMarker(Plugin plugin, Class<?>... classes) {
            this.name = plugin.getName();
            this.plugin = plugin;
            for (Class<?> clazz : classes) {
                this.addMarks(new ClassMarker(clazz));
            }
        }

        public PluginMarker(String name, ClassMarker... marks) {
            this.name = name;
            this.plugin = Bukkit.getPluginManager().getPlugin(name);
            this.addMarks(marks);
        }

        public PluginMarker addMarks(ClassMarker... classes) {
            for (ClassMarker clazz : classes) {
                ClassMarker cm = this.classes.get(clazz.getName());
                if (cm == null) {
                    this.classes.put(clazz.getName(), clazz);
                } else {
                    cm.addMarks(clazz.getMethodMarkers().toArray(new MethodMarker[clazz.getMethodMarkers().size()]));
                }
            }
            return this;
        }

        public void addValue(String clazz, String method, int line, boolean hit) {
            ClassMarker cm = this.classes.get(clazz);
            if (cm == null) {
                return;
            }
            cm.addValue(method, line, hit);
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

        public Collection<ClassMarker> getClassMarkers() {
            return Collections.unmodifiableCollection(this.classes.values());
        }
    }

    private final static class ClassMarker {

        private final String name;
        private final Map<String, MethodMarker> methods = new HashMap<>();
        
        public ClassMarker(Class<?> clazz) {
            this.name = clazz.getName();
            for (Method m : clazz.getMethods()) {
                if (m.isAnnotationPresent(Coverage.class)) {
                    this.addMarks(new MethodMarker(m));
                }
            }
            for (Constructor<?> m : clazz.getConstructors()) {
                if (m.isAnnotationPresent(Coverage.class)) {
                    this.addMarks(new MethodMarker(m));
                }
            }
        }

        public ClassMarker(String name, MethodMarker... marks) {
            this.name = name;
            this.addMarks(marks);
        }

        public ClassMarker addMarks(MethodMarker... marks) {
            for (MethodMarker m : marks) {
                MethodMarker mk = this.methods.get(m.getName());
                if (mk == null) {
                    this.methods.put(m.getName(), m);
                } else {
                    mk.addMarks(m.getMarkers().toArray(new Marker[m.getMarkers().size()]));
                }
            }
            return this;
        }

        public void addValue(String method, int line, boolean hit) {
            MethodMarker m = this.methods.get(method);
            if (m == null) {
                return;
            }
            m.addValue(line, hit);
        }

        public String getName() {
            return this.name;
        }

        public Collection<MethodMarker> getMethodMarkers() {
            return Collections.unmodifiableCollection(this.methods.values());
        }
    }

    private final static class MethodMarker {

        private final String name;
        private final int number;
        private final Map<Integer, Marker> markers = new HashMap<>();

        public MethodMarker(Method m) {
            this.name = m.getName();
            Coverage c = m.getDeclaredAnnotation(Coverage.class);
            if (c != null) {
                this.number = c.value();
            } else {
                this.number = 0;
            }
        }

        public MethodMarker(Constructor<?> m) {
            this.name = "<init>";
            Coverage c = m.getDeclaredAnnotation(Coverage.class);
            if (c != null) {
                this.number = c.value();
            } else {
                this.number = 0;
            }
        }

        public MethodMarker(String name, int number, Marker... marks) {
            this.name = name;
            this.number = number;
            this.addMarks(marks);
        }

        public boolean isHit(Marker m) {
            return m.isHit();
        }

        public MethodMarker addMarks(Marker... marks) {
            for (Marker m : marks) {
                Marker mk = this.markers.get(m.getLine());
                if (mk == null) {
                    this.markers.put(m.getLine(), m);
                } else {
                    mk.setHit(m.isHit());
                }
            }
            return this;
        }

        public void addValue(int line, boolean hit) {
            this.markers.put(line, new Marker(line).setHit(hit));
        }

        public String getName() {
            return this.name;
        }

        public int getMissed() {
            return this.getNumber() - this.markers.size();
        }

        public int getNumber() {
            return this.number;
        }

        public MethodMarker mark(int line) {
            this.markers.put(line, new Marker(line).setHit(true));
            return this;
        }

        public Collection<Marker> getMarkers() {
            return Collections.unmodifiableCollection(this.markers.values());
        }
    }

    private final static class Marker {
        private final int line;
        private boolean hit = false;
        
        public Marker(int line) {
            this.line = line;
        }

        public int getLine() {
            return this.line;
        }

        public boolean isHit() {
            return this.hit;
        }

        public Marker setHit(boolean hit) {
            this.hit = hit;
            return this;
        }
    }

    private final static Map<Plugin, PluginMarker> marks = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                CoverageUtil.marks.entrySet().forEach((ent) -> {
                    try {
                        File data = new File(ent.getKey().getDataFolder(), "coverage" + File.separator);
                        data.mkdirs();
                        File log = new File(data, "coverage-latest.log");
                        if (log.exists()) {
                            //rename to creation date
                            Files.move(log, new File(data, "coverage-" + log.lastModified() + ".log"));
                        }
                        try(FileWriter f = new FileWriter(log)) {
                            f.write(CoverageUtil.serialize(ent.getValue()));
                        }
                    } catch (IOException ex) {}
                });
            }

        });
    }

    public static void marker(Plugin p) {
        PluginMarker pm = CoverageUtil.marks.get(p);
        if (pm != null) {
            StackTraceElement elem = Thread.currentThread().getStackTrace()[2];
            pm.addValue(elem.getClassName(), elem.getMethodName(), elem.getLineNumber(), true);
        }
    }

    public static void registerClasses(Plugin p, Class<?>... classes) {
        PluginMarker pm = CoverageUtil.marks.get(p);
        if (pm != null) {
            Collection<ClassMarker> o = new PluginMarker(p, classes).getClassMarkers();
            pm.addMarks(o.toArray(new ClassMarker[o.size()]));
        } else {
            CoverageUtil.marks.put(p, new PluginMarker(p, classes));
        }
    } 

    public static void load(Plugin p) {
        File data = new File(p.getDataFolder(), "coverage" + File.separator);
        File log = new File(data, "coverage-latest.log");
        if (log.exists()) {
            try(FileReader f = new FileReader(log); BufferedReader rd = new BufferedReader(f)) {
                PluginMarker pm = CoverageUtil.deserialize(rd);
                PluginMarker curr = CoverageUtil.marks.get(p);
                if (curr == null) {
                    CoverageUtil.marks.put(p, pm);
                } else {
                    curr.addMarks(pm.getClassMarkers().toArray(new ClassMarker[pm.getClassMarkers().size()]));
                }
                /*CoverageUtil.marks.get(p).getClassMarkers().forEach((c) -> {
                    c.getMethodMarkers()
                });*/
            } catch (IOException ex) {
                DebugUtil.error(String.format("Error reading latest coverage log for plugin '%s'!", p.getName()), ex);
            }
        } else {
            DebugUtil.print(Level.WARNING, "Plugin '%s' called CoverageUtil#load(Plugin), but no logfile was found", p.getName());
        }
    }

    public static void reportAll() {
        DebugUtil.print("Current coverage report:");
        CoverageUtil.marks.values().forEach((pm) -> {
            pm.getClassMarkers().forEach((c) -> {
                c.getMethodMarkers().forEach((m) -> {
                    m.getMarkers().forEach((mk) -> {
                        DebugUtil.print("Plugin: %s\n\tClass: %s\n\tMethod: %s\n\tLine: %d\n\tValue: %B\n", pm.name, c.getName(), m.getName(), mk.getLine(), mk.isHit());
                    });
                });
            });
        });
    }
    
    private static String serialize(PluginMarker mark) {
        StringBuilder sb = new StringBuilder("====================");
        for (int i = mark.getPlugin().getName().length(); i > 0; i--) {
            sb.append('=');
        }
        sb.append('\n');
        sb.append("COVERAGE REPORT FOR ");
        sb.append(mark.getPlugin().getName());
        sb.append('\n');
        sb.append("====================");
        for (int i = mark.getPlugin().getName().length(); i > 0; i--) {
            sb.append('=');
        }
        sb.append("\n\n");
        StringBuilder mb = new StringBuilder();
        mb.append("Hit markers [ class;method;line-number ]:\n");
        Single<Integer> hit = new Single<>(0);
        mark.getClassMarkers().forEach((c) -> {
            c.getMethodMarkers().forEach((m) -> {
                m.getMarkers().stream().filter(m::isHit).forEach((mk) -> {
                    mb.append(String.format("%s;%s;%d\n", c.getName(), m.getName(), mk.getLine()));
                    hit.setValue(hit.getValue() + 1);
                });
            });
        });
        mb.append('\n');
        mb.append("Missed markers [ class;method;missed-amount ]:\n");
        Single<Integer> missed = new Single<>(0);
        mark.getClassMarkers().forEach((c) -> {
            c.getMethodMarkers().forEach((m) -> {
                mb.append(String.format("%s;%s;%d\n", c.getName(), m.getName(), m.getMissed()));
                missed.setValue(missed.getValue() + m.getMissed());
            });
        });
        mb.append('\n');
        mb.append("=== BELOW THIS LINE IS FOR DESERIALIZATION - DO NOT MODIFY ===\n");
        mb.append("Parsables [ plugin;class;method;line;is_hit|missed_count ]:");
        mark.getClassMarkers().forEach((c) -> {
            c.getMethodMarkers().forEach((m) -> {
                mb.append(String.format("\n%s;%s;%s;%d;%d", mark.getPlugin().getName(), c.getName(), m.getName(), -1, m.getNumber()));
                m.getMarkers().stream().forEach((mk) -> {
                    mb.append(String.format("\n%s;%s;%s;%d;%B", mark.getPlugin().getName(), c.getName(), m.getName(), mk.getLine(), mk.isHit()));
                });
            });
        });
        sb.append("Hit markers: ").append(hit.getValue()).append('\n');
        sb.append("Missed markers: ").append(missed.getValue()).append('\n');
        sb.append("Coverage percent: ").append(String.format("%.2f", ((double) hit.getValue())
                / ((double) (hit.getValue() + missed.getValue())) * 100)).append("%\n");
        sb.append('\n');
        sb.append(mb);
        return sb.toString();
    }

    private static PluginMarker deserialize(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null && !line.equalsIgnoreCase("=== BELOW THIS LINE IS FOR DESERIALIZATION - DO NOT MODIFY ===")){}
        if (line == null) {
            return null;
        }
        br.readLine();
        PluginMarker pm = null;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(";");
            if (line.isEmpty() || tokens.length < 5) {
                DebugUtil.print(Level.WARNING, "Bad coverage value found, skipping!");
                continue;
            }
            if (pm == null) {
                pm = new PluginMarker(tokens[0]);
            }
            try {
                int li = Integer.parseInt(tokens[3]);
                if (li <= 0) {
                    pm.addMarks(new ClassMarker(tokens[1]).addMarks(new MethodMarker(tokens[2], Integer.parseInt(tokens[4]))));
                } else {
                    pm.addValue(tokens[1], tokens[2], li, Boolean.valueOf(tokens[4]));
                }
            } catch (NumberFormatException ex) {
                DebugUtil.print(Level.WARNING, "Bad coverage value found, skipping!");
            }
        }
        return pm;
    }

}
