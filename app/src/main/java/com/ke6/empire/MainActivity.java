package com.ke6.empire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.xinkuai.globalsdk.PurchaseParams;
import com.xinkuai.globalsdk.RoleInfo;
import com.xinkuai.globalsdk.UserToken;
import com.xinkuai.globalsdk.XKGlobalSDK;
import com.xinkuai.globalsdk.XKSDKEventReceiver;
import com.xinkuai.globalsdk.internal.share.ShareCallback;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置SDK-登录、登出、购买等事件接收者
        XKGlobalSDK.registerSDKEventReceiver(new XKSDKEventReceiver() {
            @Override
            public void onLoginSucceed(@NonNull UserToken userToken) {
                Log.d(TAG, "onLoginSucceed: " + userToken.toString());
            }

            @Override
            public void onLoginFailed() {
                Log.d(TAG, "onLoginFailed: 登录失败");
            }

            @Override
            public void onLogout() {
                Log.d(TAG, "onLogout: 退出登录");
            }

            @Override
            public void onPurchaseFailed(String debugMessage) {
                Log.d(TAG, "onPurchaseFailed: " + debugMessage);
            }

            @Override
            public void onPurchaseSucceed() {
                Log.d(TAG, "onPurchaseSucceed: 购买成功");
            }

            @Override
            public void onExitGame() {
                Log.d(TAG, "onExitGame: 退出游戏");
                finish();
                System.exit(0);
            }

        });

        //生命周期
        XKGlobalSDK.onMainActivityCreated(this);
        //XKGlobalSDK申请所需权限
        XKGlobalSDK.requestSDKPermissions(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        XKGlobalSDK.onMainActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        XKGlobalSDK.onMainActivityPaused();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode);
        XKGlobalSDK.onMainActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //调用XKGlobalSDK处理权限请求结果
        XKGlobalSDK.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onBackPressed() {
        //调用XKGlobalSDK处理退出游戏
        XKGlobalSDK.handleExitGame();
    }

    /**
     * 启动登录
     */
    public void startLogin(View view) {
        if (XKGlobalSDK.isLogged()) {
            Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show();
            return;
        }
        XKGlobalSDK.launchLogin();
    }

    /**
     * 退出登录
     */
    public void logout(View view) {
        if (XKGlobalSDK.isLogged()) {
            XKGlobalSDK.logout();
        } else {
            Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Google Play 购买商品
     */
    public void purchase(View view) {
        if (!XKGlobalSDK.isLogged()) {
            Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        PurchaseParams purchaseParams =
                new PurchaseParams.Build("empire.gpdiamond_1") //商品唯一ID
                        .setOrderId(String.valueOf(System.currentTimeMillis())) //CP订单号
                        .setProductName("钻石礼包") //商品名
                        .setServerName("电信一区")  //服务器名
                        .setPayload("扩展信息")     //扩展信息，支付回调时原样返回
                        .build();
        XKGlobalSDK.launchPurchase(purchaseParams);

    }

    /**
     * 上报游戏角色信息
     */
    public void reportRole(View view) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setBehavior(4);            //行为(可选值==>1:进入游戏 2:角色升级 3:进入副本 4:创建角色)(必传)
        roleInfo.setCpUid("1001");          //CP方用户ID(必传)
        roleInfo.setRoleId("1002");         //角色ID(必传)
        roleInfo.setRoleName("无敌大魔王");  //角色名称(必传)
        roleInfo.setRoleLevel(1);           //角色等级(必传)
        roleInfo.setServerId("1001");       //角色所在服务器ID(必传)
        roleInfo.setServerName("艾欧里亚");  //角色所在服务器名称(必传)
        roleInfo.setCoinNum(0);             //角色剩余(元宝、钻石...)数量(可选)

        XKGlobalSDK.reportRoleInfo(roleInfo);
    }

    public void shareToFacebook(View view) {
        XKGlobalSDK.shareToFacebook(new ShareCallback() {
            @Override
            public void onShareSuccess() {
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onShareSuccess: 分享成功");
            }

            @Override
            public void onShareFailed() {
                Log.d(TAG, "onShareFailed: 分享失败");
            }
        });
    }
}
