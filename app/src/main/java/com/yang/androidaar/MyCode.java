package com.yang.androidaar;

public class MyCode {

    public @interface ECode {
        int Ok = 0;
        int Cancel = 1;
        int LoginError = 2;
        int SupportError = 3;
        int TaskError = 4;
        int JsonError = 6;
        int Unknown = 7;
        int ShareFriendError = 7;
        int ShareTimelineError = 8;
        int UserInfoError = 9;
        int LocInitError = 10;
        int LocPermissionError = 11;
        int LocSericeError = 12;
        int SendMailError = 13;
        int NoIntentError = 14;
        int PermissionDenyError = 15;
        int CopyFileError = 16;

        int WebviewStarted = 20;
        int WebviewOverrideUrl = 21;
        int WebviewReceivedError = 22;
        int WebviewReceivedSslError = 23;
        int WebviewGoback = 24;
        int WebviewOverrideError = 25;

        int PtmTransRes = 30;
        int PtmNetwork = 31;
        int PtmOnErrorProceed = 32;
        int PtmClientIden = 33;
        int PtmUIError = 34;
        int PtmErrorLoading = 35;
        int PtmCancelTrans = 36;
        int PtmOnTransCancel = 37;          //
        int PtmCustomError = 38;            // 自定义的错误，例如，Java代码初始化失败
        int PtmOnActivityResult = 39;       // 调用Paytm APP后的回调
    }
}
