package com.rx_mvp.common;

import com.tencent.bugly.crashreport.CrashReport;

import jason.com.rxremvplib.base.BaseApp;

public class RXAPP extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel("myChannel");
        strategy.setAppVersion("2.0.1");
        strategy.setAppPackageName("com.rx_mvp");
        CrashReport.initCrashReport(getApplicationContext(),"67f016bda6",true,strategy);
    }



}
