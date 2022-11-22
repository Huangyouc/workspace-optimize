package com.example.module_base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * 对APP中所有的Activity进行管理，统一放在Stack中处理。
 * @author hyc
 */
public class AppManager {
    private final String TAG = "AppManager";

    private List<FrontBackCallback> frontBackCallbacks = new ArrayList<>();
    private int activityStartCount = 0;
    private boolean front = true;
    private Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
        activityStack = new Stack<>();
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public void init(Application application){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityCreated");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityStarted");
                activityStartCount++;
                //activityStartCount>0  说明应用处在可见状态，也就是前台
                //!front 之前是不是在后台
                if (!front && activityStartCount > 0) {
                    front = true;
                    onFrontBackChanged(front);
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityResumed");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityPaused");
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityStopped");
                activityStartCount--;
                if (activityStartCount <= 0 && front) {
                    front = false;
                    onFrontBackChanged(front);
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Logger.e(activity.getClass().getSimpleName() + "  onActivityDestroyed");
            }
        });
    }

    //通知是否处于前台
    private void onFrontBackChanged(boolean front) {
        for (FrontBackCallback callback:frontBackCallbacks) {
            callback.onChanged(front);
        }
    }

    public void addFrontBackCallback(FrontBackCallback callback) {
        if (!frontBackCallbacks.contains(callback)) {
            frontBackCallbacks.add(callback);
        }
    }

    public void  removeFrontBackCallback(FrontBackCallback callback) {
        frontBackCallbacks.remove(callback);
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        Iterator iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @SuppressWarnings("WeakerAccess")
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            Log.e(TAG, "退出App异常:" + e.getMessage());
        }
    }

    /**
     * 结束指定的Activity;该方法不要在activity的finish方法中调用，因为activity销毁，不一样定会走finish，但是一定会走onDestroy
     * 比如：使用任务栈底部弹出activity的方式，被弹出的activity就不会执行finish方法。
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 得到指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }


    public interface FrontBackCallback {
        void onChanged(boolean front);
    }
}
