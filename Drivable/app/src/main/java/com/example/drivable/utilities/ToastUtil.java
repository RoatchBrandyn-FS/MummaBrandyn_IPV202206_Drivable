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

    public static void accountUpdated(Context context){

        String text = "Account Updated";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void accountSignedOut(Context context){

        String text = "Signed Out of Account";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void vehicleAdded(Context context){

        String text = "Vehicle Added";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void vehicleUpdated(Context context){

        String text = "Vehicle Updated";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void shopAdded(Context context){

        String text = "Shop Added";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void shopUpdated(Context context){

        String text = "Shop Updated";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void logAdded(Context context){

        String text = "Maintenance Log Added";

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
