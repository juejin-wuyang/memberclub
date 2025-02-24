package com.memberclub.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FileUtils {
    public static String readFile(String filePath) throws Exception {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URL resource = classLoader.getResource(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), "utf-8"));
        String line;
        String result = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
    }
}
