package com.silvervine.macnets.utils;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.peergine.tunnel.android.pgJniTunnel;
import com.peergine.tunnel.android.pgJniTunnel.OutData;
import com.silvervine.macnets.application.RApplication;
import com.silvervine.macnets.consts.EventTag;
import com.silvervine.macnets.consts.LoginStatus;

import org.simple.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: Rickey.Zhao on 2018/8/8.
 * @Email: benhare005@126.com
 */
public class PgTunnel {

    private static PgTunnel instance = new PgTunnel();

    public static PgTunnel getInstance() {
        return instance;
    }

    private PgTunnel() {
    }

    private boolean threadFlag = true;
    private Timer statusChecker;
    private pgJniTunnel.OutStatus accountStatus = new pgJniTunnel.OutStatus();

    /**
     * 停止接收服务端推送
     */
    public void stopPushGet() {
        this.threadFlag = false;
    }

    public int getLoginStatusCode() {
        pgJniTunnel.OutStatus outStatus = new pgJniTunnel.OutStatus();
        do {
            pgJniTunnel.StatusGet(0, outStatus);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (outStatus.iStatus == 16);//16正在请求登录

        return outStatus.iStatus;
    }

    public void statusBackgroundCheckerStart() {
        statusChecker = new Timer();
        statusChecker.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("StatusChecker", accountStatus.iStatus + "");
                pgJniTunnel.StatusGet(0, accountStatus);
                if (accountStatus.iStatus != LoginStatus.PG_TUNNEL_STA_LOGIN_SUCCESS.getCode()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(accountStatus.iStatus);
                            statusChecker.cancel();
                        }
                    });
                }
            }
        }, 0, 5 * 1000);
    }

    public void statusBackgroundCheckerStop() {
        if (statusChecker != null) {
            statusChecker.cancel();
        }
    }


    public int startTunnel(String id, String domain, String password, String macAddress) {
        String sCfgPath = getConfigedFilePath(id, domain, password, macAddress);
        String sSysInfo = "(DevID){" + id
                + "}(MacAddr){" + macAddress + "}(CpuMHz){0}"
                + "(MemSize){0}(BrwVer){}(OSVer){}(OSSpk){}(OSType){Android}";

        Log.d("demoTunnel", "sCfgPath:" + sCfgPath + ", sSysInfo:" + sSysInfo);

        /*Tunnelcfg tunnelcfg = new Tunnelcfg(id + "@" + domain);
        tunnelcfg.comment("mac:" + getMacAddr() + ";client from android");
        tunnelcfg.sandboxPath(RApplication.instance.getFilesDir().getAbsolutePath()+"/");
        tunnelcfg.pass(password);
        pgJniTunnel.SetCfgParam(tunnelcfg.toString());*/
        return pgJniTunnel.Start(sCfgPath, sSysInfo);
    }

    /**
     * @return
     */
    private String getConfigedFilePath(String id, String domain, String password, String macAddress) {
        AssetManager   assetManager   = RApplication.instance.getAssets();
        InputStream    inputStream    = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        OutputStream   outputStream   = null;
        StringBuilder  stringBuilder  = new StringBuilder();
        String         lineData       = "";
        String         dataDirPath    = RApplication.instance.getFilesDir().getAbsolutePath() + "/";
        String         configFileName = "macnets.cfg";
        try {
            inputStream = assetManager.open(configFileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((lineData = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineData);
            }

            String configStr = stringBuilder.toString()
//                    .replace("domain", domain)
                    .replace("user", id + "@" + domain)
                    .replace("pass", password)
                    .replace("macAddr", macAddress);
            outputStream = new FileOutputStream(dataDirPath + configFileName);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(configStr);
            bufferedWriter.flush();
            return dataDirPath + configFileName;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedReader.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    // Get MAC address.
    private String getMacAddr() {

        return MacAddressUtils.getMac(RApplication.instance);

        /*String macAddress = "";

        try {
            WifiManager wm = (WifiManager) RApplication.instance.getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo info = wm.getConnectionInfo();
                if (info != null) {
                    macAddress = info.getMacAddress();
                    macAddress = macAddress.replace(":", "").toUpperCase();
                } else {
                    macAddress = genMacAddr();
                }
            } else {
                macAddress = genMacAddr();
            }
        } catch (Exception ex) {
            macAddress = genMacAddr();
        }
        return macAddress;*/
    }

    // Generate the random MAC address
    private String genMacAddr() {
        try {
            java.util.Random rand  = new java.util.Random();
            byte[]           byMac = new byte[6];
            rand.nextBytes(byMac);
            return String.format("%02X%02X%02X%02X%02X%02X",
                    byMac[0], byMac[1], byMac[2], byMac[3], byMac[4], byMac[5]);
        } catch (Exception ex) {
            return "";
        }
    }


    public void getMsgAndOffline() {
        threadFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
//				System.out.println("getMsgAndOffline =>"+threadFlag);
                while (threadFlag) {
                    try {
                        OutData data = pushGet(1000 * 30);
                        Log.d("PgTunnel", data.sData);
                        switch (data.sData) {
                            case "offline":
                                //下线
                                EventBus.getDefault().post("账号异常,已离线!", EventTag.FORCE_USER_OFFLINE);
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopTunnel() {
        pgJniTunnel.Stop();
    }

    public void stop() {
        pgJniTunnel.Stop();
        stopPushGet();
    }


    /**
     * 接收P2P服务器下发的推送消息* 描述：接收P2P服务器下发的推送消息。
     * 阻塞方式：如果客户端的缓冲区中已经有下推的消息数据，则立即返回缓冲区里的消息数据。
     * 如果客户端的缓冲区中还没有下推的消息数据，则等待P2P服务器下推消息数据。
     *
     * @param timeout：[IN] 等待超时时间（毫秒）
     * @return 接收到服务器的推送消息数据
     */
    public OutData pushGet(int timeout) {
        OutData pushData = new OutData();
        int     iErr     = pgJniTunnel.PushGet(pushData, timeout);
//        checkSuccess(iErr);
        return pushData;
    }

    /**
     * 校验是否成功
     *
     * @param code
     */
    private void checkSuccess(int code) {
        if (TUNNEL_ERROR.PG_TUNNEL_ERROR_OK != code) {
            String msg = getErrorMsg(code);
            EventBus.getDefault().post(msg, EventTag.PUSH_USER_LOGIN_DATA);
        }
    }

    /**
     * 获取错误代码的描述
     *
     * @param code
     * @return
     */
    private String getErrorMsg(int code) {
        switch (code) {
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_SYSTEM:
                return "系统错误";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_BADPARAM:
                return "传递的参数错误";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_BADSTATUS:
                return "状态不正确";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_BADUSER:
                return "用户不存在";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_NETWORK:
                return "网络故障";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_TIMEOUT:
                return "操作超时";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_REJECT:
                return "拒绝操作";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_BUSY:
                return "系统正忙";
            case TUNNEL_ERROR.PG_TUNNEL_ERROR_NOEXIST:
                return "资源不存在";
        }

        return "系统发生【" + code + "】错误";
    }


    /**
     * 错误码定义
     *
     * @author Administrator
     */
    public static final class TUNNEL_ERROR {
        /**
         * 成功
         */
        public static final int PG_TUNNEL_ERROR_OK        = 0;
        /**
         * 系统错误
         */
        public static final int PG_TUNNEL_ERROR_SYSTEM    = -1;
        /**
         * 传递的参数错误
         */
        public static final int PG_TUNNEL_ERROR_BADPARAM  = -2;
        /**
         * 状态不正确
         */
        public static final int PG_TUNNEL_ERROR_BADSTATUS = -6;

        /**
         * 用户不存在
         */
        public static final int PG_TUNNEL_ERROR_BADUSER = -8;
        /**
         * 网络故障
         */
        public static final int PG_TUNNEL_ERROR_NETWORK = -11;
        /**
         * 操作超时
         */
        public static final int PG_TUNNEL_ERROR_TIMEOUT = -12;
        /**
         * 拒绝操作
         */
        public static final int PG_TUNNEL_ERROR_REJECT  = -13;
        /**
         * 系统正忙
         */
        public static final int PG_TUNNEL_ERROR_BUSY    = -14;
        /**
         * 资源不存在
         */
        public static final int PG_TUNNEL_ERROR_NOEXIST = -18;
        /**
         * 该功能没有实现
         */
        public static final int PG_TUNNEL_ERROR_NOIMP   = -127;

    }

}
