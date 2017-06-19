package zeal.com.handlerdemo;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private MyHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new MyHandler(this);

        new Thread() {
            @Override
            public void run() {
                super.run();
                //Message 消息在 1000 ms 之后才会被执行
                //在 Message 被执行之前，handler 就一直持有 activity 的引用
                //导致在 finish 之后，activity 的资源都没有被回收，一直保存在对内存中
//                Message msg = Message.obtain();
//                msg.what = 1;
//                handler.sendMessageDelayed(msg, 1000);


                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessageDelayed(msg, 1000);


            }
        }.start();
        //开启线程之后关闭当前 activity
        finish();

    }


//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                //更新 UI
//                Log.e("zeal","开始更新 UI...");
//            }
//        }
//    };



    //使用静态内部类 Handler
    //静态内部类不会持有外部类引用。
    //因为静态内部类不会持有对外部类的引用，所以定义一个静态的Handler，
    //这样Acitivity就不会被泄漏了，同时让Handler持有一个对Activity
    //的弱引用，这样就可以在Handler中调用Activity中的资源或者方法了
    private static class  MyHandler extends Handler {
        private  WeakReference<Activity> activity;
        public MyHandler(Activity activity) {
            this.activity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity myActivity = activity.get();

            if(myActivity!=null) {
                if (msg.what == 1) {
                    //更新 UI
                    Log.e("zeal","开始更新 UI...");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null) {
            // 移除所有消息
            handler.removeCallbacksAndMessages(null);
            // 移除单条消息
            //handler.removeMessages(what);
        }
    }
}
