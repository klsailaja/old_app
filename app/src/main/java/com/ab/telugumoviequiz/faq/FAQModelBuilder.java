package com.ab.telugumoviequiz.faq;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FAQModelBuilder {
    private final String fileName;
    private final List<FAQEntry> fileContents = new ArrayList<>();

    public FAQModelBuilder(String fileName, Context context) {
        this.fileName = fileName;
        initialize(context);
    }

    private void initialize(Context context) {
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

                FAQEntry faqEntry = new FAQEntry();
                faqEntry.setQuestion(key);
                faqEntry.setAnswer(value);
                fileContents.add(faqEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<FAQEntry> getFileContents() {
        return fileContents;
    }
}
