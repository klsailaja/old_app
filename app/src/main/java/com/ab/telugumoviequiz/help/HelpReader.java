package com.ab.telugumoviequiz.help;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HelpReader {
    private static HelpReader instance = null;
    private final Map<String,String> localFileContents = new HashMap<>();
    private final Map<String,String> englishFileContents = new HashMap<>();

    private HelpReader() {
    }

    public void initialize(Context context) {
        loadFileContents(context, "local.txt", 1);
        loadFileContents(context, "english.txt", 2);
    }

    private void loadFileContents(Context context, String fileName, int mode) {
        AssetManager assetManager = context.getAssets();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(assetManager.open(fileName), StandardCharsets.UTF_8))) {
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                mLine = mLine.trim();
                if (mLine.length() == 0) {
                    continue;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(mLine, "=");
                String key = stringTokenizer.nextToken().trim();
                String value = stringTokenizer.nextToken().trim();
                if (mode == 1) {
                    localFileContents.put(key, value);
                } else {
                    englishFileContents.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HelpReader getInstance() {
        if (instance == null) {
            instance = new HelpReader();
        }
        return instance;
    }

    public String getString(String key, int mode) {
        if (mode == 1) {
            return localFileContents.get(key);
        }
        return englishFileContents.get(key);
    }
}
