package com.swapps.buylist.singleton;

import android.content.Context;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.swapps.buylist.R;
import com.swapps.buylist.dto.ChildItem;
import com.swapps.buylist.dto.ParentItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Json {
    public static final String FILE_NAME = "list.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getInstance() {
        return mapper;
    }

    public static List<ParentItem> getList(Context context) {
        List<ParentItem> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);

        if (!file.exists()) {
            list = createSampleList(context);
            saveList(context, list);
            return list;
        }

        try {
            list = mapper.readValue(
                file,
                new TypeReference<>() {}
            );
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.fatal_error_message), Toast.LENGTH_LONG).show();
        }

        return list;
    }

    private static List<ParentItem> createSampleList(Context context) {
        List<ParentItem> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<ChildItem> items = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                items.add(new ChildItem(
                        context.getString(R.string.sample_item) +" " + (i + 1) + "-" + (j + 1),
                        false,
                        (50 * (i + 1)) + (50 * (j + 1)),
                        context.getString(R.string.sample_memo)
                ));
            }

            list.add(new ParentItem(
                    context.getString(R.string.sample_list) + " " + (i + 1),
                    new Date(),
                    items
            ));
        }
        return list;
    }

    public static void saveList(Context context, List<ParentItem> list) {
        File file = new File(context.getFilesDir(), FILE_NAME);

        try {
            mapper.writeValue(file, list);
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.fatal_error_message), Toast.LENGTH_LONG).show();
        }
    }

    private static String readFileAsString(File file) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}