package com.silvervine.macnets.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * UserEntity Local Utils
 *
 * @Author: Rakey.Zhao on 16/5/25.
 * @Email: benhare005@126.com
 */
public class UserUtils {

    public final static String FILENAME = "macntes_user";

    /**
     * 获取当前登录的用户
     *
     * @param context
     * @return
     */
    public static String getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String            userJson          = sharedPreferences.getString("user", "");
        return userJson;
    }

    /**
     * 获取密码
     *
     * @param context
     * @return
     */
    public static String getUserPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String            userJson          = sharedPreferences.getString("password", "");
        return userJson;
    }

    /**
     * 更新用户信息,更新成功返回最新的用户信息
     *
     * @param context
     * @return
     */
    public static String updateUser(Context context, String userAccount, String password) {
        SharedPreferences        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor            = sharedPreferences.edit();
        editor.putString("user", userAccount);
        editor.putString("password", password);
        editor.commit();
        return userAccount;
    }


}
