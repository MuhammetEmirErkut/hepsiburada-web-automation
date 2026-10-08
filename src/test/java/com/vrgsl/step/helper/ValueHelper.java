package com.vrgsl.step.helper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueHelper {

    private static final ConcurrentHashMap<String, String> valueMap = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    static {
        initValueList();
    }

    public static synchronized void initValueList() {
        if (initialized && !valueMap.isEmpty()) {
            return;
        }
        valueMap.clear();
        try {
            List<File> jsonFiles = new ArrayList<>();
            File valueDir = new File("src/test/resources/value-infos");
            if (valueDir.exists() && valueDir.isDirectory()) {
                collectJsonFiles(valueDir, jsonFiles);
            } else {
                URL resource = ValueHelper.class.getClassLoader().getResource("value-infos");
                if (resource != null) {
                    File cpDir = new File(resource.getFile());
                    if (cpDir.exists()) {
                        collectJsonFiles(cpDir, jsonFiles);
                    }
                }
            }

            Gson gson = new Gson();

            for (File file : jsonFiles) {
                try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                    JsonElement root = gson.fromJson(reader, JsonElement.class);
                    if (root != null) {
                        if (root.isJsonArray()) {
                            JsonArray arr = root.getAsJsonArray();
                            for (JsonElement el : arr) {
                                if (el.isJsonObject()) {
                                    JsonObject obj = el.getAsJsonObject();
                                    if (obj.has("key") && obj.has("value")) {
                                        valueMap.put(obj.get("key").getAsString(), obj.get("value").getAsString());
                                    }
                                }
                            }
                        } else if (root.isJsonObject()) {
                            JsonObject obj = root.getAsJsonObject();
                            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                if (entry.getValue().isJsonPrimitive()) {
                                    valueMap.put(entry.getKey(), entry.getValue().getAsString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ValueHelper] Error reading value JSON file [" + file.getName() + "]: " + e.getMessage());
                }
            }
            initialized = true;
        } catch (Exception e) {
            System.err.println("[ValueHelper] Failed to initialize values: " + e.getMessage());
        }
    }

    private static void collectJsonFiles(File dir, List<File> jsonFiles) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collectJsonFiles(f, jsonFiles);
            } else if (f.getName().toLowerCase().endsWith(".json")) {
                jsonFiles.add(f);
            }
        }
    }

    public static String getValue(String key) {
        if (!initialized || valueMap.isEmpty()) {
            initValueList();
        }
        String val = valueMap.get(key);
        // If not found in value-infos, return the key itself so literal strings still work seamlessly
        return val != null ? val : key;
    }

    public static String getValueStrict(String key) {
        if (!initialized || valueMap.isEmpty()) {
            initValueList();
        }
        String val = valueMap.get(key);
        if (val == null) {
            throw new IllegalArgumentException("❌ Value for key [" + key + "] was not found in value-infos JSON files!");
        }
        return val;
    }

    public static boolean hasValue(String key) {
        if (!initialized || valueMap.isEmpty()) {
            initValueList();
        }
        return valueMap.containsKey(key);
    }
}
