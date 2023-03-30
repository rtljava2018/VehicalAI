package com.rtllabs.vehical.ai.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.rtllabs.vehical.ai.BuildConfig;
import com.rtllabs.vehical.ai.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;


public class CommonApi {

    private static CommonApi commonApiInstance = null;
    private final static String TAG = CommonApi.class.getCanonicalName();
    public static Vector<Activity> activityStore = new Vector<Activity>();
    public String errorMsg = "";
    public Activity activity;
    AlertDialog myDialog;
    /**
     * Activity List.
     */
    public List<Activity> activityClass = new ArrayList<Activity>();

    public Dialog mProgressDialog;
    public int hours;
    public int minutes;
    public int seconds;
    public ProgressDialog progressDialog;
    private String mshash="";


    protected CommonApi() {
        // Exists only to defeat instantiation.
    }

    /*public static CommonApi getInstance(Activity activity) {
        if (commonApiInstance == null) {
            commonApiInstance = new CommonApi();
            activityStore.addElement(activity);
        }
        commonApiInstance.activity = activity;
        return commonApiInstance;
    }*/

    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    public synchronized static CommonApi getInstance(Activity activity) {
        if (commonApiInstance == null) {
            commonApiInstance = new CommonApi();
            activityStore.addElement(activity);
        }
        commonApiInstance.activity = activity;
        return commonApiInstance;
    }
    @SuppressLint("MissingPermission")
    public String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
                deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                if (null !=tm.getDeviceId()) {
                    deviceUniqueIdentifier = tm.getDeviceId();
                }
            }
        }


        return deviceUniqueIdentifier;
    }
    public String getOSversion(){
        String CVersion = Build.VERSION.RELEASE;
        return ""+CVersion;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }

    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public void openNewScreen(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(
                commonApiInstance.activity.getApplicationContext(), cls);
        if (bundle != null)
            intent.putExtras(bundle);
        commonApiInstance.activity.startActivity(intent);
        commonApiInstance.activity.overridePendingTransition(
                R.anim.enter, R.anim.exit);
    }

//    public void openNewScreenWithFadeEffect(Class<?> cls, Bundle bundle) {
//        Intent intent = new Intent(
//                commonApiInstance.activity.getApplicationContext(), cls);
//        if (bundle != null)
//            intent.putExtras(bundle);
//        commonApiInstance.activity.startActivity(intent);
//        commonApiInstance.activity.overridePendingTransition(
//                R.anim.fade_in, R.anim.fade_out);
//    }

    public void openNewScreen(Class<?> cls) {
        Intent intent = new Intent(
                commonApiInstance.activity.getApplicationContext(), cls);
        commonApiInstance.activity.startActivity(intent);
       /* commonApiInstance.activity.overridePendingTransition(
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);*/
    }

    public void openNewScreentransationenter(Class<?> cls) {
        Intent intent = new Intent(
                commonApiInstance.activity.getApplicationContext(), cls);
        commonApiInstance.activity.startActivity(intent);
        commonApiInstance.activity.overridePendingTransition(
                R.anim.enter, R.anim.exit);
    }
    public String getJulianDateTimeNow() {

        String dateString = "";

        try {

            Date time = Calendar.getInstance().getTime();

            SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            outputFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateString = outputFmt.format(time);
            String gmtDateTimeString = outputFmt.format(time);
            Date gmtDate = outputFmt.parse(gmtDateTimeString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(gmtDate);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int milliSecond =  calendar.get(Calendar.MILLISECOND);

            String yearString = String.valueOf(year);
            String dayString = String.format("%03d", day);
            String hourString = String.format("%02d", hour);
            String minuteString = String.format("%02d", minute);
            String secondString = String.format("%02d", second);
            String millisecondString = String.format("%03d", milliSecond);


            dateString = yearString + dayString + hourString + minuteString + secondString + millisecondString;


        } catch (ParseException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }

        return dateString;

    }

    public void showToastDefaultMsg(String msg) {
        Toast toast = new Toast(commonApiInstance.activity.getApplicationContext());
        toast.setText(msg);
        toast.show();
    }

    public void backButton() {
        commonApiInstance.activity.finish();
    }

    public void makeWindowFullScreen() {
        commonApiInstance.activity
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void makeSCREEN_ORIENTATION_PORTRAIT() {
        commonApiInstance.activity
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    public void makeSCREEN_ORIENTATION_LANDSCAPE() {
        commonApiInstance.activity
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }


        public boolean checkEmailValidation(String text) {
        if (!(text.length() > 0)) {
            // showToastMsg("Please enter your email Id");
            return false;
        } else {
            boolean result = Patterns.EMAIL_ADDRESS.matcher(text).matches();
            if (!result) {
                //   showToastMsg("Please enter valid email id");
            }
            return result;
        }
    }


    /*public boolean checkMobileNumber(String text) {
        if (!(text.length() > 0)) {
            // showToastMsg("Please enter your Phone number");
            return false;
        } else {
            boolean result = Patterns.PHONE.matcher(text).matches();
            if (!result) {
                //     showToastMsg("Please enter valid mobile number");
            }
            return result;
        }
    }

    public void finishActivity(Activity actvity) {
        actvity.finish();
    }

    public void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        commonApiInstance.activity.startActivity(launchBrowser);
    }

    public void finishAllActivity() {
        for (int i = 0; i < activityClass.size(); i++) {
            activityClass.get(i).finish();
        }
    }

    public void clearAllActivity() {
        activityClass.clear();
    }

    public String dateFormatChanger(String date_time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(date_time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    public String dateFormatChangerFormat(String date_time) {
        String dtStart = date_time;
        String dt = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dtStart);
            dt = format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    public String getCurrentDateTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }





    public String dateFormatChangerNew(String date_time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd.MM.yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(date_time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }


//    public void dismissProgress() {
//        mProgressDialog.dismiss();
//    }

    public String secConverter(String second) {
        hours = Integer.parseInt(second) / 3600;
        minutes = (Integer.parseInt(second) % 3600) / 60;
        seconds = Integer.parseInt(second) % 60;
        String duration_s = ((hours < 10 ? "0" : "") + hours +
                ":" + (minutes < 10 ? "0" : "") + minutes +
                ":" + (seconds < 10 ? "0" : "") + seconds);

        return duration_s;
    }*/


    /*public boolean isInternetAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }*/

    public static void registerActivity(Activity context) {
        activityStore.addElement(context);
    }

    public static Vector<Activity> getAllAcivities() {
        return activityStore;
    }

   /* @SuppressWarnings("deprecation")
    public static void removeAllActivities() {
        for (int ii = 0; ii < activityStore.size(); ii++) {
            (activityStore.elementAt(ii)).finish();
        }

        System.runFinalizersOnExit(true);
        activityStore.removeAllElements();
    }*/


    public void showSnackBar(View root_layout, String message, int length) {
        try {
            if (root_layout instanceof ViewGroup) {
                if (length == 0) {
                    Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
                    snackbar.show();
                } else if (length == 1) {
                    Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_LONG);

                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.purple_200));
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
                    snackbar.show();
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



   /* public void checkStorage()
    {
        final Snackbar customSnackBar = Snackbar.make(coordinatorLayout, "", SNACKBAR_DURATION);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) customSnackBar.getView();

        View customsnackView = getLayoutInflater().inflate(R.layout.storage_check, null);

        TextView tvConnection = (TextView) customsnackView.findViewById(R.id.tv_connection);
        ImageView ivConnection = (ImageView) customsnackView.findViewById(R.id.iv_connection);

        // We can also customize the above controls

        layout.setPadding(0,0,0,0);
        layout.addView(customsnackView, 0);

        customSnackBar.show();
    }*/

    public void formatUrlWithHttp(final EditText mEditText) {
        mEditText.setText("http://");
        Selection.setSelection(mEditText.getText(), mEditText.getText().length());


        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("http://")) {
                    mEditText.setText("http://");
                    Selection.setSelection(mEditText.getText(), mEditText.getText().length());

                }

            }
        });
    }

    public String checkRequiredFields(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                checkRequiredFields((ViewGroup) view);

            else if (view instanceof EditText) {
                EditText edittext = (EditText) view;
                if (edittext.getText().toString().trim().equals("")) {
//                    android.support.design.widget.TextInputLayout parent = (android.support.design.widget.TextInputLayout) edittext.getParent();
                    if (errorMsg.equals(""))
                        errorMsg = "" + edittext.getTag();
                }
            }
        }
        return errorMsg;
    }

    public String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.d("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.d("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            if (BuildConfig.DEBUG)
                Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            if (BuildConfig.DEBUG)
                Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.e("Exception", e.toString());
        }

        return key;
    }

    public static String splitString(String myName) {
        if (myName.equals("")) {
            return "";
        }
        myName = myName.toUpperCase();
        String temp = "";
        String[] myNameArray = myName.split(" ");
        for (int i = 0; i < myNameArray.length; i++) {
            temp = temp + myNameArray[i].charAt(0);
        }
        return (temp + ".");
    }

    public static String toSentenceCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        boolean terminalCharacterEncountered = false;
        char[] terminalCharacters = {'.', '?', '!'};
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (terminalCharacterEncountered) {
                if (currentChar == ' ') {
                    result = result + currentChar;
                } else {
                    char currentCharToUpperCase = Character.toUpperCase(currentChar);
                    result = result + currentCharToUpperCase;
                    terminalCharacterEncountered = false;
                }
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
            for (int j = 0; j < terminalCharacters.length; j++) {
                if (currentChar == terminalCharacters[j]) {
                    terminalCharacterEncountered = true;
                    break;
                }
            }
        }
        return result;
    }

    public static int dpToPx(Activity myActivity, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, myActivity.getResources().getDisplayMetrics());
    }

    public static int pxToDp(Activity myActivity, int px) {
        DisplayMetrics displayMetrics = myActivity.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertPxToDp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

//    public void showProgressbarAsDialog(int icon, String title, String message) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.dialog_layout, null);
//        CircularProgressBar circular = (CircularProgressBar) view.findViewById(R.id.progress_circular);
//        SmoothProgressBar horizontal = (SmoothProgressBar) view.findViewById(R.id.progress_horizontal);
//        TextView anotherView = (TextView) view.findViewById(R.id.tv_dialogMessage);
//        circular.setVisibility(View.GONE);
//        anotherView.setVisibility(View.GONE);
//        anotherView.setText("Loading...");
//        dialogBuilder.setView(view);
//        dialogBuilder.setTitle(title);
//        dialogBuilder.setMessage(message);
//        dialogBuilder.setCancelable(false);
//        dialogBuilder.setIcon(icon);
//        myDialog = dialogBuilder.create();
//        myDialog.show();
//    }

//    public void cancelDialog() {
//        try {
//            myDialog.cancel();
//        } catch (Exception e) {
//        }
//    }


    public void checkPermissions(String... permissions) {
        int flagPermission = 1;
        for (int i = 0; i < permissions.length; i++) {
            if ((ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED)) {
                flagPermission = 0;
            }
        }
        if (flagPermission == 0) {
            ActivityCompat.requestPermissions(activity, permissions, 199);
        }
    }

    public String errorresponse(String string) {
        String str = string;
        try {
            JSONObject jsonObject = new JSONObject(str);
            String responsedata = jsonObject.getString("message");
            //Toast.makeText(getApplicationContext(), "" + responsedata, Toast.LENGTH_SHORT).show();

            return responsedata;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Internal Server Error";
    }
    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }
    /*public ArrayList<SymtomModel> getListOdSymtom(){
        ArrayList<SymtomModel> list=new ArrayList<>();
        list.add(new SymtomModel("Fever",R.mipmap.ic_fever));
        list.add(new SymtomModel("Cough",R.mipmap.cough));
        list.add(new SymtomModel("Persistent Cough",R.mipmap.group_86));
        list.add(new SymtomModel("Fatigue",R.mipmap.group_87));
        list.add(new SymtomModel("Shortness of breath",R.mipmap.group_88));
        list.add(new SymtomModel("Loss of smell or taste",R.mipmap.group_89));
        list.add(new SymtomModel("Chest pain",R.mipmap.group_90));
        list.add(new SymtomModel("Abdominal pain",R.mipmap.group_91));
        list.add(new SymtomModel("Diarrhoea",R.mipmap.group_dia));
        list.add(new SymtomModel("Drowsiness",R.mipmap.ic_group_94));
        list.add(new SymtomModel("Lack of appetite",R.mipmap.ic_group_93));
        list.add(new SymtomModel("Others",R.mipmap.others));
        return list;

    }*/
    /*public ArrayList<ModelSymptomGVT> getListOdSymtomByGVT(){
        ArrayList<ModelSymptomGVT> list=new ArrayList<>();
        list.add(new ModelSymptomGVT("1","Fever"));
        list.add(new ModelSymptomGVT("2","Diabetes"));
        list.add(new ModelSymptomGVT("3","Cough/Sore Throat"));
        list.add(new ModelSymptomGVT("9","Respiratory Distress"));

       // list.add(new ModelSymptomGVT("18","Others"));
      *//*  list.add(new SymtomModel("Diarrhoea",R.mipmap.group_dia));
        list.add(new SymtomModel("Drowsiness",R.mipmap.ic_group_94));
        list.add(new SymtomModel("Lack of appetite",R.mipmap.ic_group_93));
        list.add(new SymtomModel("Others",R.mipmap.others));*//*
        return list;

    }*/
    /*public ArrayList<ModelSymptomGVT> getListOdSymtomByGVTMedication(){
        ArrayList<ModelSymptomGVT> list=new ArrayList<>();


        list.add(new ModelSymptomGVT("6","Hypertension"));
        list.add(new ModelSymptomGVT("7","Heart Problem"));
        list.add(new ModelSymptomGVT("10","Diabetes Mellitus"));
        list.add(new ModelSymptomGVT("11","Asthma or any Lung Disease"));
        list.add(new ModelSymptomGVT("12","Cancer"));
        list.add(new ModelSymptomGVT("13","Liver Disease"));
        list.add(new ModelSymptomGVT("14","Kidney Disease"));
        list.add(new ModelSymptomGVT("15","Tuberculosis"));
        list.add(new ModelSymptomGVT("16","On immuno-suppressants"));
        list.add(new ModelSymptomGVT("17","Organ Transplantation"));
        list.add(new ModelSymptomGVT("18","Others"));
      *//*  list.add(new SymtomModel("Diarrhoea",R.mipmap.group_dia));
        list.add(new SymtomModel("Drowsiness",R.mipmap.ic_group_94));
        list.add(new SymtomModel("Lack of appetite",R.mipmap.ic_group_93));
        list.add(new SymtomModel("Others",R.mipmap.others));*//*
        return list;

    }*/
    public boolean isPackageExisted(Context context,String targetPackage){
        PackageManager pm=context.getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /*public String getBBMPZONEName(Context mcontext,String talukid,String zoneid){
        String jsonFileString = getJsonFromAssets(mcontext, "bbmp_zone.json");
        // Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<ModelBBMPZone>>() {
        }.getType();
        List<ModelBBMPZone> listOfTaluk = new ArrayList<>();
        listOfTaluk = gson.fromJson(jsonFileString, listUserType);

        for (ModelBBMPZone model:listOfTaluk){
            if (model.getBbmp_zone_no_ksrsac().equalsIgnoreCase(talukid)){
                return model.getZone_name();
            }
        }
        return "";
    }*/

    /*public String getBBMPWARDName(Context mcontext,String talukid,String zoneid){
        String jsonFileString = getJsonFromAssets(mcontext, "bbmp_ward.json");
        // Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<ModelBBMPWard>>() {
        }.getType();
        List<ModelBBMPWard> listOfTaluk = new ArrayList<>();
        listOfTaluk = gson.fromJson(jsonFileString, listUserType);

        for (ModelBBMPWard model:listOfTaluk){
            if (model.getWard_no_ksrac().equalsIgnoreCase(talukid)){
                return model.getWard_name();
            }
        }
        return "";
    }*/

    /*public String getTalukName(Context mcontext,String talukid,String distid){
        String jsonFileString = getJsonFromAssets(mcontext, "blockjson_new.json");
       // Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<BlockModel>>() {
        }.getType();
        List<BlockModel> listOfTaluk = new ArrayList<>();
        listOfTaluk = gson.fromJson(jsonFileString, listUserType);

        for (BlockModel model:listOfTaluk){
            if (model.getBlockCode().equalsIgnoreCase(talukid)){
                return model.getBlockName();
            }
        }
        return "";
    }*/

    /*public String getPanchyateName(Context mcontext,String talukid){
        String jsonFileString = getJsonFromAssets(mcontext, "panchayatjson.json");
        //Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<PanchyateModel>>() {
        }.getType();
        List<PanchyateModel> listOfTaluk = new ArrayList<>();
        listOfTaluk = gson.fromJson(jsonFileString, listUserType);

        for (PanchyateModel model:listOfTaluk){
            if (model.getPanchayatCode().equalsIgnoreCase(talukid)){
                return model.getPanchayatName();
            }
        }
        return "";
    }*/

    /*public List<ResStaticMasterDistricDB> getDistrictUrban(Context mcontext) {
        String jsonFileString = getJsonFromAssets(mcontext, "urbanareamaster_new.json");
        Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<ResStaticMasterDistricDB>>() {
        }.getType();
        List<ResStaticMasterDistricDB>listOFdistrict = new ArrayList<>();
        listOFdistrict = gson.fromJson(jsonFileString, listUserType);

        return listOFdistrict;

    }*/

   /* public List<VillageModel> getVillageList(Context mcontext) {
        String jsonFileString = getJsonFromAssets(mcontext, "villagefile1.json");
        String jsonFileString2 = getJsonFromAssets(mcontext, "villagefile2.json");
       // Log.i("data", jsonFileString);

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<VillageModel>>() {
        }.getType();
        List<VillageModel>listOFdistrict = new ArrayList<>();
        List<VillageModel>listOFdistrict2 = new ArrayList<>();
        listOFdistrict = gson.fromJson(jsonFileString, listUserType);
        Log.e("Listsize1",listOFdistrict.size()+"");
        listOFdistrict2 = gson.fromJson(jsonFileString2, listUserType);
        Log.e("Listsize1",listOFdistrict2.size()+"");
        listOFdistrict.addAll(listOFdistrict2);
        return listOFdistrict;

    }*/
/*
    // Function to remove duplicates from an ArrayList
    public ArrayList<ResStaticMasterDistric> removeDuplicates(ArrayList<ResStaticMasterDistric> list)
    {

        // Create a new ArrayList
        ArrayList<ResStaticMasterDistric> newList = new ArrayList<ResStaticMasterDistric>();

        // Traverse through the first list
        for (ResStaticMasterDistric element : list) {

            // If this element is not present in newList
            // then add it
            if (newList.size()>0){
                for (ResStaticMasterDistric item : newList){
                    if (!item.getDistName().equalsIgnoreCase(element.getDistName())) {

                        newList.add(element);
                    }
                }
            }else {
                newList.add(element);
            }

        }

        // return the new list
        return newList;
    }*/

}