package com.example.module_base;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.github.gzuliyujiang.oaid.DeviceIdentifier;

/***
 * 获取android设备的唯一识别码
 * 参考链接：
 * https://blog.csdn.net/weixin_42600398/article/details/117984064?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-117984064-blog-125539211.pc_relevant_recovery_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-117984064-blog-125539211.pc_relevant_recovery_v2&utm_relevant_index=2
 * https://github.com/gzu-liyujiang/Android_CN_OAID
 AndroidID什么时候会改变？
 恢复出厂设置
 root/恢复root
 三清
 刷机
 系统更新
 软件修改（一般是模拟器，xposed，root）

 *不要获取MAC地址，因为android 10 以后，返回的MAC地址是随机的，无法拿到真正的MAC
 *
 * UUID是跟着app走的，app卸载后，重新安卓获得的UUID不同
 *
 * 
 */
public class DeviceUtil {
    public static boolean privacyPolicyAgreed=true;
    // 在`Application#onCreate`里初始化，注意APP合规性，若最终用户未同意隐私政策则不要调用
    public static void init(Application application){
        if (privacyPolicyAgreed) {
            DeviceIdentifier.register(application);
        }
    }

//    // 获取IMEI，只支持Android 10之前的系统，需要READ_PHONE_STATE权限，可能为空
//        DeviceIdentifier.getIMEI(this);
//    // 获取安卓ID，可能为空
//        DeviceIdentifier.getAndroidID(this);
//    // 获取数字版权管理ID，可能为空
//        DeviceIdentifier.getWidevineID();
//    // 获取伪造ID，根据硬件信息生成，不会为空，有大概率会重复
//        DeviceIdentifier.getPseudoID()；
//                // 获取GUID，随机生成，不会为空
//                DeviceIdentifier.getGUID(this);
//    // 是否支持OAID/AAID
//        DeviceID.supportedOAID(this);
//    // 获取OAID/AAID，同步调用
//        DeviceIdentifier.getOAID(this);
//    // 获取OAID/AAID，异步回调
//        DeviceID.getOAID(this, new IGetter() {
//        @Override
//        public void onOAIDGetComplete(String result) {
//            // 不同厂商的OAID/AAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
//        }
//
//        @Override
//        public void onOAIDGetError(Exception error) {
//            // 获取OAID/AAID失败
//        }
//    });

//    混淆规则
//    -keep class repeackage.com.uodis.opendevice.aidl.** { *; }
//-keep interface repeackage.com.uodis.opendevice.aidl.** { *; }
//-keep class repeackage.com.asus.msa.SupplementaryDID.** { *; }
//-keep interface repeackage.com.asus.msa.SupplementaryDID.** { *; }
//-keep class repeackage.com.bun.lib.** { *; }
//-keep interface repeackage.com.bun.lib.** { *; }
//-keep class repeackage.com.heytap.openid.** { *; }
//-keep interface repeackage.com.heytap.openid.** { *; }
//-keep class repeackage.com.samsung.android.deviceidservice.** { *; }
//-keep interface repeackage.com.samsung.android.deviceidservice.** { *; }
//-keep class repeackage.com.zui.deviceidservice.** { *; }
//-keep interface repeackage.com.zui.deviceidservice.** { *; }
//-keep class repeackage.com.coolpad.deviceidsupport.** { *; }
//-keep interface repeackage.com.coolpad.deviceidsupport.** { *; }
//-keep class repeackage.com.android.creator.** { *; }
//-keep interface repeackage.com.android.creator.** { *; }
//-keep class repeackage.com.google.android.gms.ads.identifier.internal.** { *; }
//-keep interface repeackage.com.google.android.gms.ads.identifier.internal.* { *; }

    /**
     * 因传统的移动终端设备标识如国际移动设备识别码（IMEI）等已被部分国家认定为用户隐私的一部分， 并存在被篡改和冒用的风险，所以在Android 10及后续版本中非厂商系统应用将无法获取IMEI、MAC等设备信息。
     *
     *  无法获取IMEI会在用户行为统计过程中对设备识别产生一定影响。 移动安全联盟针对该问题联合国内手机厂商推出补充设备标准体系方案， 选择OAID字段作为IMEI等的替代字段。
     *
     * OAID字段是由中国信通院联合华为、小米、OPPO、VIVO等厂商共同推出的设备识别字段，具有一定的权威性，可满足用户行为统计的使用场景。
     * @param context
     * @return
     */
    private static String getImei(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return DeviceIdentifier.getIMEI(context);
        } else
            return DeviceIdentifier.getOAID(context);
    }
}
