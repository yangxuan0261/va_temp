package com.example.testapp.page;


import com.example.testapp.page.misc.PageTest;

public class PageRegister {

    // 注册所有 layout
    public static void regAll() {
        PageMain.Instance.registerPage(new PageTest());
    }
}