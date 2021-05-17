package com.longbei.im_push_service_sdk.app;

import android.Manifest;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.longbei.im_push_service_sdk.common.Factory;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionUtil {


	public static boolean  mPermission = true;

	public static boolean checkAndRequestPermission(String Permission) {

		SoulPermission.getInstance().checkAndRequestPermission(Permission,
				new CheckRequestPermissionListener() {
					@Override
					public void onPermissionOk(Permission permission) {
						mPermission = true;
					}

					@Override
					public void onPermissionDenied(Permission permission) {
						// see CheckPermissionWithRationaleAdapter
						if (permission.shouldRationale()) {
							mPermission =  false;
						} else {
							mPermission = false;
						}
					}
				});

		return mPermission;

	}
}
