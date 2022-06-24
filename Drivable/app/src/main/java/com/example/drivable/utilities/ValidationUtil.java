package com.example.drivable.utilities;

import android.util.Patterns;

import io.grpc.okhttp.internal.Util;

public class ValidationUtil {

    public static boolean isEmail(CharSequence inputEmail){

        return Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches();
    }

}
