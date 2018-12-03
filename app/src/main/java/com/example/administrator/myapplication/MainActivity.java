package com.example.administrator.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class MainActivity extends AppCompatActivity {
	WebView webView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar != null){
			actionBar.hide();//隐藏系统标题栏
		}


		webView= (WebView)findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new MyChromeClient(this));
		webView.loadUrl("http://baidu.com/");
	}
	//使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
		if(keyCode==KeyEvent.KEYCODE_BACK&&webView.canGoBack()){
			webView.goBack();
			return true;
		}else {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * webview上传文件的载体
	 */
	public ValueCallback<Uri> mUploadMsg;
	public ValueCallback<Uri[]> mUploadMsgList;

	/**
	 * 如果调用拍照，图片保存路径
	 */
	public static String mCameraFilePath = "";

	public static String getmCameraFilePath() {
		return mCameraFilePath;
	}

	public static void setmCameraFilePath(String mCameraFilePath) {
		MainActivity.mCameraFilePath = mCameraFilePath;
	}

	public ValueCallback<Uri> getmUploadMsg() {
		return mUploadMsg;
	}

	public void setmUploadMsg(ValueCallback<Uri> mUploadMsg) {
		this.mUploadMsg = mUploadMsg;
	}

	public ValueCallback<Uri[]> getmUploadMsgList() {
		return mUploadMsgList;
	}

	public void setmUploadMsgList(ValueCallback<Uri[]> mUploadMsgList) {
		this.mUploadMsgList = mUploadMsgList;
	}

// .......

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

			case MyChromeClient.FILECHOOSER_RESULTCODE:

				if (null == mUploadMsg && null == mUploadMsgList) {
					Log.i("webview activity", "UploadMsg =null");
					return;
				}

				Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
				if (result != null || (!TextUtils.isEmpty(mCameraFilePath))) {
					if (null == result) {
						result = Uri.fromFile(new File(mCameraFilePath));
					}
					if (null != mUploadMsg) {
						mUploadMsg.onReceiveValue(result);
					}
					if (null != mUploadMsgList) {
						mUploadMsgList.onReceiveValue(new Uri[] { result });
					}
				} else {
					Log.i("webview activity", "result =null");
					setResultNull();
				}

				break;

			default:
				break;
		}

	}

	private void setResultNull() {
		if (null != mUploadMsg) {
			mUploadMsg.onReceiveValue(null);
		}
		if (null != mUploadMsgList) {
			mUploadMsgList.onReceiveValue(null);
		}
	}

}
