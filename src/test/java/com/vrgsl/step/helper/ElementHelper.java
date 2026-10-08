package com.vrgsl.step.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrgsl.step.model.ElementInfo;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ElementHelper {

    private static final ConcurrentHashMap<String, ElementInfo> elementMapList = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    static {
        initElementList();
    }

    public static synchronized void initElementList() {
        if (initialized && !elementMapList.isEmpty()) {
            return;
        }
        elementMapList.clear();
        try {
            List<File> jsonFiles = new ArrayList<>();
            File elementDir = new File("src/test/resources/element-infos");
            if (elementDir.exists() && elementDir.isDirectory()) {
                collectJsonFiles(elementDir, jsonFiles);
            } else {
                URL resource = ElementHelper.class.getClassLoader().getResource("element-infos");
                if (resource != null) {
                    File cpDir = new File(resource.getFile());
                    if (cpDir.exists()) {
                        collectJsonFiles(cpDir, jsonFiles);
                    }
                }
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ElementInfo>>() {}.getType();

            for (File file : jsonFiles) {
                try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                    List<ElementInfo> elements = gson.fromJson(reader, listType);
                    if (elements != null) {
                        for (ElementInfo el : elements) {
                            if (el.getKey() != null) {
                                elementMapList.put(el.getKey(), el);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ElementHelper] Error reading element JSON file [" + file.getName() + "]: " + e.getMessage());
                }
            }
            initialized = true;
        } catch (Exception e) {
            System.err.println("[ElementHelper] Failed to initialize elements: " + e.getMessage());
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

    public static ElementInfo findElementInfoByKey(String key) {
        if (!initialized || elementMapList.isEmpty()) {
            initElementList();
        }
        ElementInfo info = elementMapList.get(key);
        if (info == null) {
            throw new IllegalArgumentException("❌ Element with key [" + key + "] was not found in any JSON under src/test/resources/element-infos/");
        }
        return info;
    }

    public static By getBy(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        return constructBy(key, elementInfo.getType(), elementInfo.getValue());
    }

    /**
     * Parameterized locator resolver.
     * Replaces %s or %d placeholders in the JSON value with passed arguments.
     */
    public static By getBy(String key, Object... params) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        String formattedValue = String.format(elementInfo.getValue(), params);
        return constructBy(key, elementInfo.getType(), formattedValue);
    }

    private static By constructBy(String key, String type, String value) {
        String cleanType = type.toLowerCase().trim();
        switch (cleanType) {
            case "id":
                return By.id(value);
            case "xpath":
                return By.xpath(value);
            case "css":
            case "cssselector":
                return By.cssSelector(value);
            case "name":
                return By.name(value);
            case "class":
            case "classname":
                return By.className(value);
            case "linktext":
                return By.linkText(value);
            case "partiallinktext":
                return By.partialLinkText(value);
            case "tag":
            case "tagname":
                return By.tagName(value);
            default:
                throw new IllegalArgumentException("❌ Unsupported locator type [" + type + "] for element [" + key + "]");
        }
    }
}
