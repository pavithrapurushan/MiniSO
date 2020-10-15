package in.co.tsmith.miniso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    String strUsername = "";
    String strPassword = "";
    String Identifier1 = "";
    String Identifier2 = "";
    String URL = "";
    String DeviceId = "";
    String strCheckLogin = "";

    ImageButton imgBtnSettings;

    ProgressDialog pDialog;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);
    double tsMsgDialogWindowHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String myuniqueID;
            int myversion = Integer.valueOf(android.os.Build.VERSION.SDK);
            if (myversion < 23) {
                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                myuniqueID = info.getMacAddress();
                if (myuniqueID == null) {
                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                    myuniqueID = mngr.getDeviceId();
                }
            } else if (myversion > 23 && myversion < 29) {
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
                myuniqueID = mngr.getDeviceId();
            } else {
                String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                myuniqueID = androidId;
            }

            DeviceId = myuniqueID;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screen_height = displayMetrics.heightPixels;
            int screen_width = displayMetrics.widthPixels;

            tsMsgDialogWindowHeight = (screen_height * 38) / 100;  //  243/640

            etUsername = (EditText) findViewById(R.id.etUsername);
            etPassword = (EditText) findViewById(R.id.etPassword);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            imgBtnSettings = (ImageButton)findViewById(R.id.imgBtnSettings);

            imgBtnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intnt = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intnt);
                }
            });

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Identifier1 = prefs.getString("MiniSOStore", "");
            Identifier2 = prefs.getString("MiniSOSubStore", "");
            URL = prefs.getString("MiniSOWSUrl", "");

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(buttonClick);
                    if (etUsername.getText().toString().equals("") && etPassword.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "fields can not be empty", Toast.LENGTH_SHORT).show();

                    } else {
                        strUsername = etUsername.getText().toString();
                        strPassword = etPassword.getText().toString();
//                    Call API
                        new CheckLoginTask().execute();
                    }

                }
            });

        } catch (Exception ex){
            Toast.makeText(this, ""+ex, Toast.LENGTH_SHORT).show();
        }
    }

    private class CheckLoginTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in..Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            checkLogin();
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            if(strCheckLogin.equals("")||strCheckLogin == null){
                Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT).show();
            }else{
                if(strCheckLogin.contains("0_Success")){
                    String sUsername = strCheckLogin.replace("<RESULT>0_Success_","").replace("</RESULT>", "");
                    String LoggedUser ="";
                    LoggedUser = sUsername;
                    String SalesPersonId = "0";
                    if(AppConfigSettings.APPFlag.equalsIgnoreCase("SFASO")){
                        SalesPersonId = LoggedUser.substring(0,LoggedUser.indexOf("_"));
                        LoggedUser = LoggedUser.substring(LoggedUser.indexOf("_")+1);
                    }

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor edtr =  sharedPrefs.edit();
                    edtr.putString("LoggedSalesPersonId",SalesPersonId);
                    edtr.putString("LoggedSalesPersonName",LoggedUser);
                    boolean Res = edtr.commit();

                    if(Res == true) {
                        //Do Nothing.
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Saving LoggedSalesPersonId Preference Failed", Toast.LENGTH_SHORT).show();
//                    tsMessage("Message","Saving LoggedSalesPersonId Preference Failed");
                    }

                    Intent intent = new Intent(MainActivity.this, CustomerInfoActivity.class);
                    intent.putExtra("LoggedUser",LoggedUser);
                    intent.putExtra("SalesPersonId",SalesPersonId);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                } else if (strCheckLogin.contains("1_Failed")) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor edtr =  sharedPrefs.edit();
                    edtr.putString("LoggedSalesPersonId","0");
                    edtr.putString("LoggedSalesPersonName","");
                    boolean Res = edtr.commit();

                    strCheckLogin = strCheckLogin.replace("<RESULT>","");
                    strCheckLogin = strCheckLogin.replace("</RESULT>","");
                    strCheckLogin = strCheckLogin.replace("1_Failed","");
//                    Toast.makeText(MainActivity.this, ""+strCheckLogin, Toast.LENGTH_SHORT).show();
                    tsMessages(strCheckLogin);

//                tsMessage("Message",str);

                } else {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor edtr =  sharedPrefs.edit();
                    edtr.putString("LoggedSalesPersonId","0");
                    edtr.putString("LoggedSalesPersonName","");
                    boolean Res = edtr.commit();
                    tsMessages(""+strCheckLogin.replace("1_Failed", ""));
//                    Toast.makeText(MainActivity.this, ""+strCheckLogin.replace("1_Failed", ""), Toast.LENGTH_SHORT).show();
//                tsMessage("Message",str.replace("1_Failed", ""));
                }

            }
        }
    }

    private void checkLogin(){
        try {
            String MethodName = "MobSoLoginCheck";
            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
            request.addProperty("ClientValidator", AppConfigSettings.ClientValidator);
            request.addProperty("Username", strUsername);
            request.addProperty("DeviceId", DeviceId);
            request.addProperty("Identifier1", Identifier1);
            request.addProperty("Identifier2", Identifier2);
            request.addProperty("Password", strPassword);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, AppConfigSettings.WSTimeOutValueVerySmall);

            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);






            Object result = envelope.getResponse();


//            envelope.s




//            int responsecode = envelope.getResponse();
////            try {
//            if(responsecode == 200) {
//                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder sb = new StringBuilder();
//                String inputLine = "";
//
//                while ((inputLine = reader.readLine()) != null) {
//                    sb.append(inputLine);
//                    break;
//                }
//
//                reader.close();
//                strSaveMiniSO = sb.toString();
//
//            }else{
//                strErrorMsg = connection.getResponseMessage();
//                strSaveMiniSO="httperror";
//            }


            String str = result.toString();
            strCheckLogin = str;

        }
        catch(Exception e) {
            Log.d("MA",""+e);
        }
    }


    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.tsmessage_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Save");
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ImageButton imgBtnCloseSaveWindow = (ImageButton) dialog.findViewById(R.id.imgBtnClosetsMsgWindow);
            TextView tvSaveStatus = (TextView) dialog.findViewById(R.id.tvTsMessageDisplay);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = (int) tsMsgDialogWindowHeight;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            imgBtnCloseSaveWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
//                    if (dialogItemLookup != null)
//                        dialogItemLookup.dismiss();
                }
            });

//            tvSaveStatus.setText("" + msg);
            tvSaveStatus.setText("" + msg);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_SHORT).show();
        }
    }
}