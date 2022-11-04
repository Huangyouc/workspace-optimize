package com.example.module_base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.example.module_base.fileutil.MD5Util;

/**
 * 可以用来校验当前apk是否被修改了 签名
 *
 * if (Assembly.supportCheckSignKey && !Assembly.debugMode) {
 *       // 检验签名信息是否被篡改
 *       String sign = ApkSignUtil.getSign(this);
 *       if (sign != null && !Constant.SIGN_KEY_FILE_MD5_VALUE.equals(sign)) {
 *         finish();
 *         return;
 *       }
 *     }
 *
 *     public static final String SIGN_KEY_FILE_MD5_VALUE = "56a9eac1638e50c05f4eff203c6a89d9";
 *     SIGN_KEY_FILE_MD5_VALUE   这个值需要在签名文件使用时，用ApkSignUtil 工具生成，保存到代码里
 */
public class ApkSignUtil {

	//may return null
	public static String getSign(Context context) {
		String packageName = context.getPackageName();
		Signature[] signs = getRawSignature(context, packageName);
		if ((signs == null) || (signs.length == 0)) {
			return null;
		} else {
			Signature sign = signs[0];
			String signMd5 = MD5Util.md5(sign.toByteArray());
			return signMd5;
		}
	}

	public static Signature[] getRawSignature(Context context,
			String packageName) {
		if ((packageName == null) || (packageName.length() == 0)) {
			return null;
		}
		PackageManager pkgMgr = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pkgMgr.getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
		if (info == null) {
			return null;
		}
		return info.signatures;
	}
}
