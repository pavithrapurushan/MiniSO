package in.co.tsmith.miniso;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;

public class CustomerInfoActivity extends AppCompatActivity{

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    AutoCompleteTextView acvCustomerName;
    ImageButton imgBtnCustSearchbyName;

    String DeviceId = "";
    String Identifier1 = "";
    String Identifier2 = "";
    String URL = "";
    String LoggedInSalesPersonName = "";
    String strLoadcustomer = "";
    Button btnCreateSO;

    String CustomerName = "";
    int CustomerId;

    SharedPreferences prefs;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    TextView tvUserName;
    LinearLayout llCustInfoToolbar;
    double llCustInfoToolbarHeight;
    Button btnClear;

    String selected_customer_id = "";

    EditText etCustomerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerinfo);


        llCustInfoToolbar = (LinearLayout)findViewById(R.id.llCustInfoToolbar);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        llCustInfoToolbarHeight = (screen_height * 8.75) / 100;  //    56/640
//        llCustInfoBottombarHeight = (screen_height * 21.25) / 100;  //   136/640

        LinearLayout.LayoutParams paramsllHeader = (LinearLayout.LayoutParams) llCustInfoToolbar.getLayoutParams();
        paramsllHeader.height = (int) llCustInfoToolbarHeight;
        paramsllHeader.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llCustInfoToolbar.setLayoutParams(paramsllHeader);

        btnClear = (Button)findViewById(R.id.btnClear);

        try {
            String myuniqueID;
            int myversion = Integer.valueOf(android.os.Build.VERSION.SDK);
            if (myversion < 23) {
                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                myuniqueID = info.getMacAddress();
                if (myuniqueID == null) {
                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(CustomerInfoActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CustomerInfoActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                    myuniqueID = mngr.getDeviceId();
                }
            } else if (myversion > 23 && myversion < 29) {
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(CustomerInfoActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CustomerInfoActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
                myuniqueID = mngr.getDeviceId();
            } else {
                String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                myuniqueID = androidId;
            }

            DeviceId = myuniqueID;

            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Identifier1 = prefs.getString("MiniSOStore", "");
            Identifier2 = prefs.getString("MiniSOSubStore", "");
            URL = prefs.getString("MiniSOWSUrl", "");
            LoggedInSalesPersonName = String.valueOf( prefs.getString("LoggedSalesPersonName",""));


            acvCustomerName = (AutoCompleteTextView) findViewById(R.id.acvCustomerName);
            etCustomerId = (EditText) findViewById(R.id.etCustomerId);
            imgBtnCustSearchbyName = (ImageButton) findViewById(R.id.imgBtnCustSearchbyName);
            btnCreateSO = (Button) findViewById(R.id.btnCreateSO);

            tvUserName = (TextView)findViewById(R.id.tvUsername);
            tvUserName.setText(""+LoggedInSalesPersonName);

            imgBtnCustSearchbyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new LoadCustomerListTask().execute();

                }
            });

            btnCreateSO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(buttonClick);
                    String selectedCust = acvCustomerName.getText().toString();
                    int Indx = selectedCust.lastIndexOf("(");
                    CustomerName = selectedCust.substring(0, Indx);
                    String str = selectedCust.substring(Indx + 1);
                    str = str.substring(0, str.indexOf(")"));
                    CustomerId = Integer.valueOf(str);
                    prefs = PreferenceManager.getDefaultSharedPreferences(CustomerInfoActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("CustomerId",CustomerId);
                    editor.putString("CustomerName",CustomerName);
                    editor.putString("ListOfItemsAddedMiniSO","");
                    editor.putBoolean("IsSavedMiniSO",false);
//                    IsSavedMiniSO", true); ListOfItemsAddedMiniSO
                    editor.commit();


                    Intent intnt = new Intent(CustomerInfoActivity.this,SOActivity.class);
                    startActivity(intnt);
                    CustomerInfoActivity.this.finish();
                }
            });

            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
//                        etCustCode.setText("");
                        acvCustomerName.setText("");
                        etCustomerId.setText("");
//                        etHUID.setText("");
//                        etCustomerName.setText("");
//                        etCustomerMobile.setText("");
//                        etAdrsLine1.setText("");
//                        etAdrsLine2.setText("");
//                        acvSearchDoctor.setText("");

//                        etCustCode.setEnabled(true);
//                        etHUID.setEnabled(true);
//                        etCustomerName.setEnabled(true);
//                        etCustomerMobile.setEnabled(true);
//                        etAdrsLine1.setEnabled(true);
//                        etAdrsLine2.setEnabled(true);


//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("CustomerInfoJsonStrAndroidSO", "");
//                        editor.putString("ListOfItemsAddedAndroidSO", "");//clearing the already saved items for this customer
//                        editor.commit();
//                        editor.commit();

                    }catch (Exception ex){
                        Toast.makeText(CustomerInfoActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            int Indx = SelectedCust.lastIndexOf("(");
//            CustomerName = SelectedCust.substring(0, Indx);
//            String str = SelectedCust.substring(Indx + 1);
//            str = str.substring(0, str.indexOf(")"));
//            CustomerId = Integer.valueOf(str);



            acvCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {

                        String SelectedText = acvCustomerName.getText().toString();

                        if (SelectedText.contains("(") && SelectedText.contains(")")) {
                            int Indx = SelectedText.lastIndexOf("(");
                            String str = SelectedText.substring(Indx + 1);
                            str = str.substring(0, str.indexOf(")"));
                            str = str.replace(" ", "");
                            selected_customer_id = str;
                            etCustomerId.setText(""+str);
//                            SelectedItemName = SelectedText.substring(0, Indx);
//                            new SOActivity.MobSoGetItemDetailsForSOTask().execute();

                        } else {
                            selected_customer_id = "0";
//                            SelectedItemName = "";
                            acvCustomerName.setAdapter(null);
                        }
                    }catch (Exception ex){
                        Toast.makeText(CustomerInfoActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception ex) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_SHORT).show();
        }

    }

    private class LoadCustomerListTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            loadCustomerList();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(strLoadcustomer == null || strLoadcustomer.equals("")){
                Toast.makeText(CustomerInfoActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            }else {

                String filename = "mysecondfile";
                String outputString = "Hello world!";
                File myDir = getFilesDir();

                try {
                    File secondFile = new File(myDir + "/text/", filename);
                    if (secondFile.getParentFile().mkdirs()) {
                        secondFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(secondFile);

                        fos.write(outputString.getBytes());
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

/////////////////////////////////////////////////////////////////////////////////////////////////////

                strLoadcustomer = strLoadcustomer.replace("1_Failed", "");
                strLoadcustomer = strLoadcustomer.replace("<RESULT>", "");
                strLoadcustomer = strLoadcustomer.replace("</RESULT>", "");
                strLoadcustomer = strLoadcustomer.replace("<FIELD1>", "");

                String[] arrCust = strLoadcustomer.split("</FIELD1>");

                //ArrayAdapter<String> Custadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);

                //Commented by Pavithra on 04-08-2020
//            KArrayAdapter<String> Custadapter = new KArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);
//
//            acvCustomer.setAdapter(Custadapter);
//            acvCustomer.showDropDown();


                //Added by Pavithra on 04-08-2020
                AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(CustomerInfoActivity.this, R.layout.autocomplete_view_row, arrCust);
                acvCustomerName.setAdapter(myAdapter);
                acvCustomerName.showDropDown();
            }
        }
    }

    private  void loadCustomerList(){

        try {
//            if(TsCommonMethods1.isNetworkConnected() == false ) {
//                tsMessage("Message","No Network Connectivity!!!");
//                return;
//            }
//            if(DocumentSaved == true) {
//                tsMessage("Message", "SO Already Saved");
//                return;
//            }

            String MethodName = "MobSoGetCustomerList";
            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
            request.addProperty("ClientValidator", AppConfigSettings.ClientValidator);
            request.addProperty("Username", LoggedInSalesPersonName);
            request.addProperty("DeviceId", DeviceId );
            request.addProperty("Identifier1", Identifier1);
            request.addProperty("Identifier2", Identifier2);
            request.addProperty("Filter", acvCustomerName.getText().toString());
            request.addProperty("Flag", "1");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11 );
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,AppConfigSettings.WSTimeOutValueMedium);
            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);

            Object result = envelope.getResponse();
            String str =  result.toString();
            strLoadcustomer = str;


            /////////////////////////Another experiment////////////////////////////////////////////////////////////
//            String filename = "mysecondfile";
//            String outputString = "Hello world!";
//            File myDir = getFilesDir();
//
//            try {
//                File secondFile = new File(myDir + "/text/", filename);
//                if (secondFile.getParentFile().mkdirs()) {
//                    secondFile.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(secondFile);
//
//                    fos.write(outputString.getBytes());
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
///////////////////////////////////////////////////////////////////////////////////////////////////////
//
//            str = str.replace("1_Failed", "");
//            str = str.replace("<RESULT>", "");
//            str = str.replace("</RESULT>", "");
//            str = str.replace("<FIELD1>", "");
//
//            String[] arrCust = str.split("</FIELD1>");
//
//            //ArrayAdapter<String> Custadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);
//
//            //Commented by Pavithra on 04-08-2020
////            KArrayAdapter<String> Custadapter = new KArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);
////
////            acvCustomer.setAdapter(Custadapter);
////            acvCustomer.showDropDown();
//
//
//            //Added by Pavithra on 04-08-2020
//            AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(this, R.layout.autocomplete_view_row, arrCust);
//            acvCustomerName.setAdapter(myAdapter);


        } catch(SocketTimeoutException ex){
            Log.d("CI",""+ex);
//            Toast.makeText(this, ""+ex, Toast.LENGTH_SHORT).show();
//            tsMessage("Message","Server Taking Too Much Time. Please Try Again");
        }
        catch(Exception ex) {
            Log.d("CI",""+ex);
//            if(ex.getMessage().contains("ECONNRESET")&&wsCallCount<3){
//                LoadCustomerList();
//                wsCallCount += 1;
//            }else
//                tsMessage("Message", "Unexpected Error Occured " + (ex.getMessage() == null ? "" :  ex.getMessage()));
        }


    }

//    private void LoadCustomerList() {
//        try {
//            if(TsCommonMethods1.isNetworkConnected() == false ) {
//                tsMessage("Message","No Network Connectivity!!!");
//                return;
//            }
//            if(DocumentSaved == true) {
//                tsMessage("Message", "SO Already Saved");
//                return;
//            }
//
//            String MethodName = "MobSoGetCustomerList";
//            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
//            request.addProperty("ClientValidator", ClientValidator);
//            request.addProperty("Username", TsCommonMethods1.GetLoggedInSalesPersonName());
//            request.addProperty("DeviceId", DeviceUniqueId);
//            request.addProperty("Identifier1", Identifier1);
//            request.addProperty("Identifier2", Identifier2);
//            request.addProperty("Filter", acvCustomer.getText().toString());
//            request.addProperty("Flag", "1");
//
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11 );
//            envelope.dotNet=true;
//            envelope.setOutputSoapObject(request);
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,AppConfigSettings.WSTimeOutValueMedium);
//            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);
//
//            Object result = envelope.getResponse();
//            String str =  result.toString();
//
//
//            /////////////////////////Another experiment////////////////////////////////////////////////////////////
//            String filename = "mysecondfile";
//            String outputString = "Hello world!";
//            File myDir = getFilesDir();
//
//            try {
//                File secondFile = new File(myDir + "/text/", filename);
//                if (secondFile.getParentFile().mkdirs()) {
//                    secondFile.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(secondFile);
//
//                    fos.write(outputString.getBytes());
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
///////////////////////////////////////////////////////////////////////////////////////////////////////
//
//            str = str.replace("1_Failed", "");
//            str = str.replace("<RESULT>", "");
//            str = str.replace("</RESULT>", "");
//            str = str.replace("<FIELD1>", "");
//
//            String[] arrCust = str.split("</FIELD1>");
//
//            //ArrayAdapter<String> Custadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);
//
//            //Commented by Pavithra on 04-08-2020
////            KArrayAdapter<String> Custadapter = new KArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrCust);
////
////            acvCustomer.setAdapter(Custadapter);
////            acvCustomer.showDropDown();
//
//
//            //Added by Pavithra on 04-08-2020
//            AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(this, R.layout.autocomplte_view_row, arrCust);
//            acvCustomer.setAdapter(myAdapter);
//
//
//        } catch(SocketTimeoutException ex){
//            tsMessage("Message","Server Taking Too Much Time. Please Try Again");
//        }
//        catch(Exception ex) {
//            if(ex.getMessage().contains("ECONNRESET")&&wsCallCount<3){
//                LoadCustomerList();
//                wsCallCount += 1;
//            }else
//                tsMessage("Message", "Unexpected Error Occured " + (ex.getMessage() == null ? "" :  ex.getMessage()));
//        }
//
//    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerInfoActivity.this);
        alertDialogBuilder.setMessage("Do you want to exit the application?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                System.exit(0);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
