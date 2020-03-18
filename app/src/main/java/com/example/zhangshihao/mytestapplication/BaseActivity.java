package com.example.zhangshihao.mytestapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhangshihao on 2017/9/13.
 */

public class BaseActivity extends Activity {
    public static final String TAG = "zsh";
    public static final boolean DEBUG = false;
    public static final String SP_NAME = "novel_sp";

    //Toast...@{
    public static void showShortToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(Context context,int resourceId){
        Toast.makeText(context,context.getResources().getString(resourceId),Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, int resourceId){
        Toast.makeText(context,context.getResources().getString(resourceId),Toast.LENGTH_LONG).show();
    }
    //@}

    //Log...@{
    public static void logd(String msg){
        if(DEBUG) Log.d(TAG,msg);
    }

    public static void loge(String msg){
        Log.e(TAG,msg);
    }

    public static void logw(String msg){
         Log.w(TAG,msg);
    }

    public static void logi(String msg){
         Log.i(TAG,msg);
    }
    //@}

    /**
     * 得到自定义的progressDialog
     * @param context
     * @return
     */
    public Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null,false);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout_dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        /*
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();*/
        //loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }
}
