package com.example.drivable.utilities;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void testResults(Context context){

        String text = "Test Worked";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void networkError(Context context){

        String text = "Internet Not Found";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void accountCreated(Context context){

        String text = "Account Created";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
