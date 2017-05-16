package com.mccc.bodyw;

import android.content.Context;

import java.util.Set;

/**
 * Created by Coco on 2017/5/14.
 */

public class SharedPreferenceUtils {
    public static final String DATA = "bodyW";
    public static final String KEY_AXIS_CHOICES = "axis_choices";

    public static void setSelectedAxisChoices (Context context, Set<String> stringSet) {
        context.getSharedPreferences(DATA, Context.MODE_PRIVATE)
                .edit()
                .putStringSet(KEY_AXIS_CHOICES, stringSet)
                .apply();
    }

    public static Set<String> getSelectedAxisChoices (Context context) {
        return context.getSharedPreferences(DATA, Context.MODE_PRIVATE)
                .getStringSet(KEY_AXIS_CHOICES, null);
    }
}
