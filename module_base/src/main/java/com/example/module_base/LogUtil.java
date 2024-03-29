package com.example.module_base;

import android.text.TextUtils;
import android.util.Log;

public class LogUtil {

    private static final String LOG_TAG = "LogUtil";
    private static final boolean IF_LOG = BuildConfig.DEBUG;

    public static void setLog(boolean enable){
        //        IF_LOG = enable;
    }

    public static void i(String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.i(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void d(String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.d(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void v(String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.v(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void w(String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.w(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void e(String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.e(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void e(String msg, Throwable tr, Object... args) {
        if (IF_LOG && msg != null) {
            Log.e(LOG_TAG, args.length > 0 ? String.format(msg, args) : msg, tr);
        }
    }

    public static void i(Tag tag, String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.i(tag.toString(), args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void d(Tag tag, String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.d(tag.toString(), args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void v(Tag tag, String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.v(tag.toString(), args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void w(Tag tag, String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.w(tag.toString(), args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static void e(Tag tag, String msg, Object... args) {
        if (IF_LOG && msg != null) {
            Log.e(tag.toString(), args.length > 0 ? String.format(msg, args) : msg);
        }
    }

    public static class Tag {
        String tag;

        public Tag(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            if (TextUtils.isEmpty(tag)) {
                return LOG_TAG;
            }
            return tag;
        }
    }

    public static final Tag LOCAL = new Tag("local");
    public static final Tag TEMP = new Tag("temp");
}
