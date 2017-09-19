package com.shuijianbaozi.websocketdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于通过websocket测试远程交互的状况
 * add by shuijianbaozi
 */
public class MainActivity extends Activity implements View.OnClickListener {

    public static final int MESSAGE_SEND_DATA = 10086;
    public static final int MESSAGE_OPEN_WEBSOCKET = 10087;
    public static final String TAG = "MainActivity.class";
    private WebSocketClient mWSC = null;
    public static final String jsessingId = "yze3kq660zyzl635wommry2o";//需要登录获得这个东东

    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_OPEN_WEBSOCKET:

                    connect(jsessingId);
                    break;
                case MESSAGE_SEND_DATA:
                    sendMessageByWebsocket();
                    Message message = mhandler.obtainMessage();
                    message.what = MESSAGE_SEND_DATA;
                    mhandler.sendMessageDelayed(message, 1500);
                    break;

            }
            return false;
        }
    });

    private void sendMessageByWebsocket() {
        if (mWSC != null) {
            try {
                mWSC.send("{\"action\":103,\"from\":\"UID:4020\",\"to\":\"DID:19494\"}");
            } catch (NotYetConnectedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.button)).setOnClickListener(this);
        // TODO: 2017/9/19 1.连接websocket
        mhandler.sendEmptyMessage(MESSAGE_OPEN_WEBSOCKET);
    }

    @Override
    public void onClick(View v) {
        // TODO: 2017/9/19 2.发送103数据
        Message message = mhandler.obtainMessage();
        message.what = MESSAGE_SEND_DATA;
        mhandler.sendMessageDelayed(message, 200);
    }

    public void connect(String jsessionid) {
        Log.d(TAG, "----------------------------link websocket ----------------------------" + jsessionid);
        try {
            Map<String, String> heads = new HashMap<String, String>();

            if (jsessionid == null || "null".equals(jsessionid)) {
                Log.d(TAG, "jsessionid is :" + jsessionid);
                return;
            }

            Log.d(TAG, "jsessionid is not null :" + jsessionid);

            heads.put("Cookie", "JSESSIONID=" + jsessionid + ";");

            URI uri = new URI(ConnectionConfig.WS_SERVER);

            Log.d(TAG, "WebSocketClient.connect : " + uri);

            WebSocketClient wsc = new WebSocketClient(uri, new Draft_17(), heads, 5000) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    Log.d(TAG, "WebSocketClient.onOpen : " + arg0);
                    Log.i(TAG, "onOpen");
                }

                @Override
                public void onMessage(String msg) {
                    Log.i(TAG, "WebSocketClient.onMessage : " + msg);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "WebSocketClient.onError : " + e.toString());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocketClient.onClose : " + code + ", " + reason + ", " + remote);
                }
            };
            try {
                wsc.connectBlocking();
                mWSC = wsc;
            } catch (InterruptedException e) {
                Log.e(TAG, ">>>>>>>>>web socket 错误啦 " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, ">>>>>>>>>web socket 错误啦");
            e.printStackTrace();
        }
    }
}
