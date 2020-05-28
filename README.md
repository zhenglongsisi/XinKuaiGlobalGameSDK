# 新快海外版SDK-安卓接入文档

[TOC]

## 1.注意事项
* `targetSdkVersion`必须>=26(Google Play 强制要求),`minSdkVersion`必须>=16;
* `targetSdkVersion`暂不支持29，请设置29以下;
* 游戏包名请与商务提供的包名保持一致;
* 游戏打包请务必使用新快签名文件，该文件在压缩包的签名文件夹下的xinyou.jks，密码在秘钥.txt文件内;
* 请用Android Studio打包，Eclipse不支持;
* 开启androidx依赖;
* 建议游戏主Activity的启动模式为`singleTask`;

## 1.1 关于Android 6.0以上动态权限申请
SDK提供申请必要权限接口，建议在游戏主界面加载完成后调用，示例如下:
```java
public class GameActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //主界面加载完成后，调用SDK请求权限接口
        XKGlobalSDK.requestSDKPermissions(this);
    }
}
```

## 1.2 关于Android P 非HTTPS连接适配
可通过以下方式解决：
```xml
 <application
    android:usesCleartextTraffic="true"/>
```

## 1.3 Android 10 沙盒存储适配
可通过下面两种方式解决不能访问外部存储限制，任选其一。

1.设置`targetSdkVersion` < 29
2.禁用沙盒存储
```xml
 <application
    android:requestLegacyExternalStorage="true"/>
```

## 1.4 迁移至Androidx
在项目根目录的`gradle.properties`文件中配置：
```properties
android.useAndroidX=true
# Automatically convert third-party libraries to use AndroidX
android.enableJetifier=true
```

## 2.导入SDK

### 2.1 配置第三方`Maven`仓库
在项目根目录下的`build.gradle`文件中配置：
```groovy
buildscript {

    repositories {
        google()
        jcenter()
        //第三方Maven仓库
        maven { url 'https://devrepo.kakao.com/nexus/content/groups/public/' }
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.2'
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        //第三方Maven仓库
        maven { url 'https://devrepo.kakao.com/nexus/content/groups/public/' }
    }
}
```


### 2.1 添加SDK依赖
```groovy
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    //新快海外版SDK依赖
    implementation 'com.xinkuai.globalsdk:gamesdk-kr:1.2.0'
}
```

### 2.2 SDK参数配置

在项目的`AndroidManifest.xml`文件中配置相关参数：
```xml
<application>

    <!--新快SDK AppId-->
    <meta-data
        android:name="XK_APP_ID"
        android:value="123456" />

    <!--新快SDK AppKey-->
    <meta-data
        android:name="XK_APP_KEY"
        android:value="xxxxxx" />

    <!--Facebook AppId-->
    <!--TODO appid 前加上'fb'前缀-->
    <meta-data
        android:name="com.facebook.sdk.ApplicationId"
        android:value="fb12345611111" />

    <!--KaKao AppKey-->
    <meta-data
        android:name="com.kakao.sdk.AppKey"
        android:value="xxxxxxx" />

    <!--Naver AppId-->
    <meta-data
        android:name="NAVER_APP_ID"
        android:value="xxxxxx" />
    <!--Naver AppKey-->
    <meta-data
        android:name="NAVER_APP_KEY"
        android:value="xxxxxx" />

    <!--AppsFlyer DevKey-->
    <meta-data
        android:name="AF_DEV_KEY"
        android:value="xxxxxxx" />

</application>
```

## 3. 编码接入

### 3.1 初始化SDK
在自动义`Application`中的`attachBaseContext`和`onCreate`方法内初始化SDK。
示例如下：
```java
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //调用SDK的attachBaseContext方法，必须调用
        XKGlobalSDK.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化新快SDK
        XKGlobalSDK.initialize(this);
        //SDK内部log开关
        XKGlobalSDK.setLoggerEnable(true);
    }
}
```

### 3.2 设置SDK全局事件回调

SDK的登录，登出，支付等事件会回调统一接口。
建议在游戏主`Activity`的`onCreate`方法中调用，示例如下：
```java
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
        Log.d(TAG, "onPurchaseFailed: 购买失败：" + debugMessage);
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
```

### 3.3 游戏主Activity生命周期回调
**生命周期方法必须调用，示例如下：**
**只能在主线程调用**
```java
public class GameActivity extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        XKGlobalSDK.onMainActivityCreated(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        XKGlobalSDK.onMainActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        XKGlobalSDK.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}
```

### 3.4 登录授权

#### 3.4.1 登录接口
登录成功或失败回调`XKSDKEventReceiver`的`onLoginSucceed`或`onLoginFailed`方法
调用示例：
```java
//调用SDK登录（只能在主线程调用）
XKGlobalSDK.launchLogin();
```

#### 3.4.2 登出接口
**调用此接口会回调`XKSDKEventReceiver`的`onLogout`方法**
调用示例：
```java
//调用SDK登出，调用此方法会回调onLogout。（只能在主线程调用）
XKGlobalSDK.logout();
```

### 3.5 购买接口

**支付结果请以服务器结果为准**

- 支付成功回调`XKSDKEventReceiver`的`onPurchaseSucceed`方法
- 支付失败回调`XKSDKEventReceiver`的`onPurchaseFailed`方法

调用示例：
```java
//构建支付请求参数
PurchaseParams purchaseParams = new PurchaseParams.Build("diamond_60")  //商品ID，唯一值(必传)
    .setOrderId(String.valueOf(System.currentTimeMillis())) //CP订单ID
    .setProductName("钻石礼包") //商品名称(必传)
    .setServerName("电信一区")  //服务器名称(必传)
    .setPayCallbackUrl("")     //支付回调url(可选) 
    .setPayload("扩展信息")     //扩展信息，服务器支付回调时，原样返回(可选)
    .build();
//调用SDK启动购买（只能在主线程调用）
XKGlobalSDK.launchPurchase(purchaseParams);
```

### 3.6 角色信息上报接口
调用示例：
```java
RoleInfo info = new RoleInfo();
info.setBehavior(4);                 //行为(可选值==>1:进入游戏 2:角色升级 3:进入副本 4:创建角色)(必传)
info.setCpUid("1001");               //CP方用户ID(必传)
info.setRoleId("1001");              //角色ID(必传)
info.setRoleName("无敌大魔王");       //角色名称(必传)
info.setRoleLevel(99);               //角色等级(必传)
info.setServerId("1");               //角色所在服务器ID(必传)
info.setServerName("艾欧里亚");       //角色所在服务器名称(必传)
info.setCoinNum(0);                  //角色剩余(元宝、钻石...)数量(可选)

//调用SDK上报角色信息（只能在主线程调用）
KYGameSdk.reportRoleInfo(info);
```

### 3.7 Facebook分享接口

#### 3.7.1 Facebook分享配置
Facebook分享需在`AndroidManifest.xml`文件中设置`ContentProvider`, 其中 `{APP_ID}`是Facebook AppId：
```xml
<provider
    android:authorities="com.facebook.app.FacebookContentProvider{APP_ID}"
    android:name="com.facebook.FacebookContentProvider"
    android:exported="true"/>
```
#### 3.7.2 开始Facebook分享
```java
XKGlobalSDK.shareToFacebook(new ShareCallback() {
    @Override
    public void onShareSuccess() {
        Log.d(TAG, "onShareSuccess: 分享成功");
    }

    @Override
    public void onShareFailed() {
        Log.d(TAG, "onShareFailed: 分享失败");
    }
});
```

### 3.8 退出游戏
当按下返回键时显示退出游戏指引
```java
//只能在主线程调用
XKGlobalSDK.handleExitGame();
```

### 3.9 其他接口

```java
//返回SDK是否已登录
XKGlobalSDK.isLogged();
//返回SDK当前登录用户信息（只能在主线程调用）
XKGlobalSDK.getLoggedUser();
```

## 4.其他
- 接入过程中不清楚的地方请参考demo工程。
- 遇到任何未知问题请联系我方技术人员。