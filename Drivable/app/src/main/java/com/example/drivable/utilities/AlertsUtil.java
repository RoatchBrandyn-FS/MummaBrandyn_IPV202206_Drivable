package com.example.drivable.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertsUtil {

    public static void signInError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Field(s) Empty");
        builder.setMessage("Please fill in all needed information to Sign In to the account.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emailValidateError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Account Not Found");
        builder.setMessage("Email provided was not found assigned to an account");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void passwordSignInError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Password Doesn't Match");
        builder.setMessage("Password doesn't match whats on file with this email.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void signupImageError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Image Missing");
        builder.setMessage("Please either choose an image or take an image for the account.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void signupEmptyError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Field(s) Empty");
        builder.setMessage("Please fill in all needed information to Signup for an account.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emailError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Email Format Wrong");
        builder.setMessage("Please double check the email is inputted correctly (Ex: email@gmail.com");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void passwordMatchError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Password Doesn't Match");
        builder.setMessage("Please make sure Password and Confirm Password matches.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void passwordMinError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Password Too Short");
        builder.setMessage("For your safety, please make sure your password is at least 6 characters long");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emailDuplicateError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Email in Use");
        builder.setMessage("Email in use, try going to Forgot Password? to retrieve the password");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emptyEmailsError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Field(s) Empty");
        builder.setMessage("Please fill in all needed information to Update Email.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emailMatchError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Original Email Doesn't Match");
        builder.setMessage("Please make sure you are in the right account, or double check the Email for the account before updating.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void updateEmailMatchError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Email Match");
        builder.setMessage("Your New Email Matches the Original Email. Please use different email to update.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void updateEmailDuplicateError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Email in Use");
        builder.setMessage("Email in use, try a different email or login with that email.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void emptyPasswordsError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Field(s) Empty");
        builder.setMessage("Please fill in all needed information to Update Password.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void editProfileEmptyError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Field(s) Empty");
        builder.setMessage("Please fill in all needed information to Edit Profile.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public static void editProfileNoChangeError(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Profile Unchanged");
        builder.setMessage("Nothing changed from Profile. If make sure at least one of the fields or image is changed to update Profile.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

}
