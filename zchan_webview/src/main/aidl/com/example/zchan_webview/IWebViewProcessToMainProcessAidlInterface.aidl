// IWebViewProcessToMainProcessAidlInterface.aidl
package com.example.zchan_webview;

// Declare any non-default types here with import statements

interface IWebViewProcessToMainProcessAidlInterface {
    void handleWebAction(String action, String params);
}