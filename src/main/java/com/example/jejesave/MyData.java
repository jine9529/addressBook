package com.example.jejesave;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.widget.EditText;

import java.util.Locale;

public class MyData {
    private String name;
    private String fone;
    private String nick;

    public MyData(String name, String fone, String nick) {
        this.name = name;
        this.fone = fone;
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public String getFone() {
        //하이픈 넣기


//        fone = PhoneNumberUtils.formatNumber(fone);
//      fone = PhoneNumberUtils.formatNumber(fone, Locale.getDefault().getCountry());

        return fone;
    }

    public String getNick() {
        return nick;
    }
}
