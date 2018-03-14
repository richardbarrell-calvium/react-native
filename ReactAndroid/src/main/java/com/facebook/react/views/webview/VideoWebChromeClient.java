package com.facebook.react.views.webview;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.webkit.GeolocationPermissions;

import static android.view.ViewGroup.LayoutParams;
import com.facebook.react.common.build.ReactBuildConfig;

/**
 * Provides support for full-screen video on Android
 */
public class VideoWebChromeClient extends WebChromeClient {

  private final FrameLayout.LayoutParams FULLSCREEN_LAYOUT_PARAMS = new FrameLayout.LayoutParams(
    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);

  private WebChromeClient.CustomViewCallback mCustomViewCallback;

  private Activity mActivity;
  private View mWebView;
  private View mVideoView;

  public VideoWebChromeClient(Activity activity, WebView webView) {
    mWebView = webView;
    mActivity = activity;
  }

  @Override
  public boolean onConsoleMessage(ConsoleMessage message) {
    if (ReactBuildConfig.DEBUG) {
      return super.onConsoleMessage(message);
    }
    // Ignore console logs in non debug builds.
    return true;
  }

  @Override
  public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
    callback.invoke(origin, true, false);
  }

  @Override
  public void onShowCustomView(View view, CustomViewCallback callback) {
    if (mVideoView != null) {
      callback.onCustomViewHidden();
      return;
    }

    // Store the view and its callback for later, so we can dispose of them correctly
    mVideoView = view;
    mCustomViewCallback = callback;

    view.setBackgroundColor(Color.BLACK);

    getRootView().addView(view, FULLSCREEN_LAYOUT_PARAMS);

    mWebView.setVisibility(View.GONE);
  }

  @Override
  public void onHideCustomView() {
    if (mVideoView == null) {
      return;
    }

    mVideoView.setVisibility(View.GONE);

    // Remove the custom view from its container.
    getRootView().removeView(mVideoView);
    mVideoView = null;
    mCustomViewCallback.onCustomViewHidden();

    mWebView.setVisibility(View.VISIBLE);
  }

  private ViewGroup getRootView() {
    return ((ViewGroup) mActivity.findViewById(android.R.id.content));
  }
}
