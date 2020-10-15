package in.co.tsmith.miniso;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.browse.MediaBrowser;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//Modified by Pavithra on 10-10-2020
public class SOActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;

    ImageButton ic_search;
    EditText etItemSearchSOActivity;
    AutoCompleteTextView acvItemSearchSOActivity;
    String itemSearchStringLookup = "";
    String DeviceId = "";
    String Identifier1 = "";
    String Identifier2 = "";
    String URL = "";
    String LoggedInSalesPersonName = "";
    String strLoadProductList = "";

    Dialog dialogItemLookup;

    AutoCompleteTextView etItemSearchLookup;
    ImageButton imgBtnItemSearchLookup;
    ListView lvItemsPopup;

    String CustomerType = "";
    int CustomerId;

    int SRSelectedItemId = 0;
    String SelectedItemName = "";
    int SRSelectedBatchId = 0;
    String strGetItemDetailsForSO = "";
    String strSaveSO = "";

    int SelectedFormId = -1;

    Dialog qtydialog;

    List<SalesBillDetailPL> lstProducts;
    ListView lvProductlist;

    String strSalesBill = "";
    int SalesOrderModel = 1;
    int SOSummaryId = 0;

    Button btnSave;
    Button btnNew;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    String CustomerName = "";

    LinearLayout llSOToolbar;
    double llCustInfoToolbarHeight;
    double tsMsgDialogWindowHeight;

    TextView tvSODocNo;
    TextView tvBilltotal;
    ImageButton imgBtnRemarksPrescrptn;
    SharedPreferences prefs;

    String billRemarks = "";
    Dialog dialog;
    double saveDialogWindowHeight;

    ProgressDialog pDialog;
    boolean isSaved = false;

    String listOfItemsAddedStr = "";

    double total_amount = 0;

    int itemQty = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so);

        llSOToolbar = (LinearLayout)findViewById(R.id.llSOToolbar);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        llCustInfoToolbarHeight = (screen_height * 8.75) / 100;  //    56/640
//        llCustInfoBottombarHeight = (screen_height * 21.25) / 100;  //   136/640

        saveDialogWindowHeight = (screen_height * 42) / 100;

        tsMsgDialogWindowHeight = (screen_height * 38) / 100;  //  243/640

        LinearLayout.LayoutParams paramsllHeader = (LinearLayout.LayoutParams) llSOToolbar.getLayoutParams();
        paramsllHeader.height = (int) llCustInfoToolbarHeight;
        paramsllHeader.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llSOToolbar.setLayoutParams(paramsllHeader);


        ic_search = (ImageButton)findViewById(R.id.imgBtnSearchItem);
//        etItemSearchSOActivity = (EditText)findViewById(R.id.etItemSearchSOActivity);
        acvItemSearchSOActivity = (AutoCompleteTextView) findViewById(R.id.acvItemSearchSOActivity);
        lvProductlist = (ListView) findViewById(R.id.lvProductlist);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnNew = (Button)findViewById(R.id.btnNew);

        tvSODocNo = (TextView) findViewById(R.id.tvSODocNo);
        tvBilltotal = (TextView) findViewById(R.id.tvBilltotal);
        imgBtnRemarksPrescrptn = (ImageButton)findViewById(R.id.imgBtnRemarksPrescrptn);

        acvItemSearchSOActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(acvItemSearchSOActivity.getText().toString().length() < 3){
                    acvItemSearchSOActivity.setAdapter(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        lstProducts = new ArrayList<>();
        try {
            String myuniqueID;
            int myversion = Integer.valueOf(android.os.Build.VERSION.SDK);
            if (myversion < 23) {
                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                myuniqueID = info.getMacAddress();
                if (myuniqueID == null) {
                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(SOActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SOActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                    myuniqueID = mngr.getDeviceId();
                }

            } else if (myversion > 23 && myversion < 29) {
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(SOActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SOActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
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
            LoggedInSalesPersonName = String.valueOf(prefs.getString("LoggedSalesPersonName", ""));
            CustomerId = prefs.getInt("CustomerId",0);
            CustomerName = prefs.getString("CustomerName","");

            TextView tvCustomerName = (TextView)findViewById(R.id.tvCustomerName);
            tvCustomerName.setText(""+CustomerName);



            acvItemSearchSOActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    try {

                        String SelectedText = acvItemSearchSOActivity.getText().toString();

                        if (SelectedText.contains("(") && SelectedText.contains(")")) {
                            int Indx = SelectedText.lastIndexOf("(");
                            String str = SelectedText.substring(Indx + 1);
                            str = str.substring(0, str.indexOf(")"));
                            str = str.replace(" ", "");
                            SRSelectedItemId = Integer.valueOf(str);
                            SelectedItemName = SelectedText.substring(0, Indx);
                            new MobSoGetItemDetailsForSOTask().execute();




                        } else {
                            SRSelectedItemId = 0;
                            SelectedItemName = "";
                            acvItemSearchSOActivity.setAdapter(null);
                        }
                    }catch (Exception ex){
                        Toast.makeText(SOActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            imgBtnRemarksPrescrptn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                        billRemarks = prefs.getString("BillRemarksAndroidSO", "");
                        dialog = new Dialog(SOActivity.this);
                        dialog.setContentView(R.layout.add_remarks);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setTitle("Remarks");
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                        ImageButton imgBtnCloseRemarksWindow = (ImageButton) dialog.findViewById(R.id.imgBtnCloseRemarksWindow);
                        Button btnOkRemarks = (Button) dialog.findViewById(R.id.btnOkRemarks_Itemwise);
                        Button btnClearRemarks_Itemwise = (Button) dialog.findViewById(R.id.btnClearRemarks_Itemwise);
                        final EditText etAddRemarks = (EditText) dialog.findViewById(R.id.etAddRemarks_Itemwise);
                        etAddRemarks.setText(billRemarks);


                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = (int) saveDialogWindowHeight;
                        lp.gravity = Gravity.CENTER;
                        dialog.getWindow().setAttributes(lp);


                        imgBtnCloseRemarksWindow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        btnOkRemarks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                billRemarks = etAddRemarks.getText().toString();

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("BillRemarksAndroidSO", billRemarks);
                                editor.commit();

                                if (billRemarks.equalsIgnoreCase("")) {
                                    imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_remarks_item);
                                } else {
                                    imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_remarks_colrchanged);
                                }

                                dialog.dismiss();

                            }
                        });

                        btnClearRemarks_Itemwise.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                etAddRemarks.setText("");
                            }
                        });

                        dialog.show();

                    } catch (Exception ex) {
                        Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                    }

                }
            });


            ic_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

//                    if(tsCommonMethods.isNetworkConnected()) {
//                        if (etItemSearchSOActivity.getText().toString().equalsIgnoreCase("")) {
                        if (acvItemSearchSOActivity.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(SOActivity.this, "Please input minimumn 3 characters", Toast.LENGTH_SHORT).show();
                            acvItemSearchSOActivity.setAdapter(null);

                        } else {
//                            if (tsCommonMethods.isNetworkConnected()) {
//                            itemSearchStringLookup = etItemSearchSOActivity.getText().toString();
                            itemSearchStringLookup = acvItemSearchSOActivity.getText().toString();
                            new LoadProductListTask().execute();

//                            } else {
//                                Toast.makeText(SOActivity.this, "No network connectivity", Toast.LENGTH_SHORT).show();
//                            }

                        }
//                    }else{
//                        Toast.makeText(SOActivity.this, "No network connectivity", Toast.LENGTH_SHORT).show();
//                    }
                    } catch (Exception ex) {
                        Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(buttonClick);

                    listOfItemsAddedStr = prefs.getString("ListOfItemsAddedMiniSO", "");
//                    listOfPrescrptnsAddedStr = prefs.getString("ListOfPrescrptnsAddedAndroidSO", "");
                    Gson gson1 = new Gson();
                    if (listOfItemsAddedStr != null && !listOfItemsAddedStr.equals("")) {
//                        itemDetailsPLList = gson1.fromJson(listOfItemsAddedStr, new TypeToken<List<ItemDetails>>() {
//                        }.getType());
                        lstProducts = gson1.fromJson(listOfItemsAddedStr, new TypeToken<List<SalesBillDetailPL>>() {
                        }.getType());
                    }


                    if(lstProducts.size() > 0) {
                        new SaveSOTask().execute();
                    }else{
                        Toast.makeText(SOActivity.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(buttonClick);

//                    view.startAnimation(buttonClick);

                    if (!isSaved) {
                        showAlertMessage();
                    } else {

//                        lvProductlist.setAdapter(null);
//                        tvBilltotal.setText("");
//                        tvNoOfPrescrptnsAdded.setText("0");
//                        prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("ListOfItemsAddedAndroidSO", "");
//                        editor.putString("ListOfPrescrptnsAddedAndroidSO", "");
//                        editor.putString("CustomerInfoJsonStrAndroidSO", "");  //Added by Pavithra on 29-08-2020
//                        editor.putString("BillRemarksAndroidSO", "");
//                        editor.putBoolean("IsSavedAndroidSO", false);
//                        editor.commit();
//                        isSaved = false;

                        btnSave.setEnabled(true);
                        Intent intent = new Intent(SOActivity.this, CustomerInfoActivity.class); //Added by Pavithra on 29-08-2020
                        startActivity(intent);
                        SOActivity.this.finish(); //Added by Pavithra on 10-09-2020
                    }
                }
            });


            imgBtnRemarksPrescrptn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    try {
                        prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                        billRemarks = prefs.getString("BillRemarksAndroidSO", "");
                        dialog = new Dialog(SOActivity.this);
                        dialog.setContentView(R.layout.add_remarks);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setTitle("Remarks");
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        ImageButton imgBtnCloseRemarksWindow = (ImageButton) dialog.findViewById(R.id.imgBtnCloseRemarksWindow);
                        Button btnOkRemarks = (Button) dialog.findViewById(R.id.btnOkRemarks_Itemwise);
                        Button btnClearRemarks_Itemwise = (Button) dialog.findViewById(R.id.btnClearRemarks_Itemwise);
                        final EditText etAddRemarks = (EditText) dialog.findViewById(R.id.etAddRemarks_Itemwise);
                        etAddRemarks.setText(billRemarks);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = (int) saveDialogWindowHeight;
                        lp.gravity = Gravity.CENTER;
                        dialog.getWindow().setAttributes(lp);


                        imgBtnCloseRemarksWindow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        btnOkRemarks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                billRemarks = etAddRemarks.getText().toString();

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("BillRemarksAndroidSO", billRemarks);
                                editor.commit();

                                if (billRemarks.equalsIgnoreCase("")) {
                                    imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_remarks_item);
                                } else {
                                    imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_remarks_colrchanged);
                                }

                                dialog.dismiss();

                            }
                        });

                        btnClearRemarks_Itemwise.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                etAddRemarks.setText("");
                            }
                        });

                        dialog.show();

                    } catch (Exception ex) {
                        Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }catch (Exception ex){
            Toast.makeText(this, ""+ex, Toast.LENGTH_SHORT).show();
        }

    }

    public void showAlertMessage() {

        try {
            AlertDialog.Builder b = new AlertDialog.Builder(SOActivity.this);
            b.setTitle("Confirm Discard");
            b.setMessage("Are you sure to discard this SO ?");  //item name should specify

            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    lvProductlist.setAdapter(null);
//                    tvTotalAmountValue.setText("");
//                    tvNoOfPrescrptnsAdded.setText("0");
//                    prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("ListOfItemsAddedAndroidSO", "");
//                    editor.putString("ListOfPrescrptnsAddedAndroidSO", "");
//                    editor.putString("CustomerInfoJsonStrAndroidSO", "");  //Added by Pavithra on 29-08-2020
//                    editor.putString("BillRemarksAndroidSO", "");
//                    editor.putBoolean("IsSavedAndroidSO",false);
//                    editor.commit();
                    isSaved = false;

                    Intent intent = new Intent(SOActivity.this, CustomerInfoActivity.class); //Added by Pavithra on 29-08-2020
                    startActivity(intent);
                    SOActivity.this.finish(); //Added by Pavithra on 10-09-2020
                }
            });

            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            b.show();
        } catch (Exception ex) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_SHORT).show();
        }
    }


    private class MobSoGetItemDetailsForSOTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            mobSoGetItemDetailsForSO();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strGetItemDetailsForSO.equals("") || strGetItemDetailsForSO == null) {
                Toast.makeText(SOActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {

//                <PRODUCTRATEDETAIL>
//	<MRP>"0"</MRP>
//	<PTR>"0"</PTR>
//	<DISCOUNT>"0"</DISCOUNT>
//	<RATEFROMCUSTPRICEMATRIX>"0"</RATEFROMCUSTPRICEMATRIX>
//	<SOH>"0"</SOH>
//	<FREESOH>"0"</FREESOH>
//</PRODUCTRATEDETAIL>
//<SCHEME>NO Scheme Available for this Item</SCHEME>

                if (strGetItemDetailsForSO.contains("1_Failed") == true) {
                    strGetItemDetailsForSO = strGetItemDetailsForSO.replace("<RESULT>", "");
                    strGetItemDetailsForSO = strGetItemDetailsForSO.replace("</RESULT>", "");
                    Toast.makeText(SOActivity.this, "" + strGetItemDetailsForSO.replace("1_Failed", ""), Toast.LENGTH_SHORT).show();

                    tsMessages("" + strGetItemDetailsForSO.replace("1_Failed", ""));
                    return;
                }

                strGetItemDetailsForSO = strGetItemDetailsForSO.replace("<RESULT>", "");
                strGetItemDetailsForSO = strGetItemDetailsForSO.replace("</RESULT>", "");

                final SalesBillDetailPL clsPL;

                clsPL = new SalesBillDetailPL();


                if (AppConfigSettings.APPFlag.equalsIgnoreCase("SFASO")) {
//                    clsPL.Slno = String.valueOf(lstProducts.size());
                    clsPL.Slno = "1";
                    clsPL.Item = SelectedItemName;
                    clsPL.ItemCode = "";
                    clsPL.Mrp = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<MRP>\"") + 6, strGetItemDetailsForSO.indexOf("\"</MRP>"));

                    clsPL.MRPInclusive = "1";
                    clsPL.TaxOnMRP = "0";
                    clsPL.TaxFlag = "";

                    String PTR = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<PTR>\"") + 6, strGetItemDetailsForSO.indexOf("\"</PTR>"));

                    int t1 = strGetItemDetailsForSO.indexOf("<RATEFROMCUSTPRICEMATRIX>\"");
                    int t2 = strGetItemDetailsForSO.indexOf("\"</RATEFROMCUSTPRICEMATRIX>");

                    String RateFromCPM = strGetItemDetailsForSO.substring(t1 + 27, t2);

                    if (!RateFromCPM.equalsIgnoreCase("")) {
                        if (Double.valueOf(RateFromCPM) > 00) {
                            clsPL.Rate = String.valueOf(RateFromCPM);
                        } else {
                            clsPL.Rate = String.valueOf(PTR);
                        }
                    } else {
                        clsPL.Rate = String.valueOf(PTR);
                    }

                    clsPL.BillingRate = clsPL.Rate;
                    clsPL.DiscPer = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<DISCOUNT>\"") + 11, strGetItemDetailsForSO.indexOf("\"</DISCOUNT>"));
                    clsPL.Soh = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<FREESOH>\"") + 10, strGetItemDetailsForSO.indexOf("\"</FREESOH>"));
                    clsPL.TaxPer = "0";
                    clsPL.TaxId = "0";
                    clsPL.AstPer = "0";
                    clsPL.AstOnFlag = "";
                    clsPL.Uperpack = "1";
                    clsPL.Amount = "0";
                    clsPL.Disc = "0";
                    clsPL.TaxableAmount = "0";
                    clsPL.Tax = "0";
                    clsPL.Ast = "0";
                    clsPL.Qty = "0";
                    clsPL.TotalAmount = "0";
                    clsPL.ItemId = String.valueOf(SRSelectedItemId);
                    clsPL.SohPacks = clsPL.Soh;
//                    clsPL.SohUnits = String.valueOf(TsRound(Float.valueOf(clsPL.Soh) * Float.valueOf(clsPL.Uperpack), 2));
                    clsPL.PackName = "";
                    clsPL.UnitName = "";
//                    clsPL.CanEditRate = String.valueOf(CanEditRate);

                    //Masked And Added By 1165 on 28/07/2016
                    //clsPL.CanBillNonStockkItem = String.valueOf(CanBillNonStockItem);
//                    clsPL.CanBillNonStockkItem = String.valueOf(TsCommonMethods1.GetCanBillNonSohItem());

                    clsPL.SchemeDetails = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<SCHEME>") + 8, strGetItemDetailsForSO.indexOf("</SCHEME>"));
                    if (strGetItemDetailsForSO.contains("<ITEMDETAIL01")) {
                        clsPL.ItemDetail01 = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<ITEMDETAIL01>") + 14, strGetItemDetailsForSO.indexOf("</ITEMDETAIL01>"));
                    }
                    if (strGetItemDetailsForSO.contains("<ITEMDETAIL02")) {
                        clsPL.ItemDetail02 = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<ITEMDETAIL02>") + 14, strGetItemDetailsForSO.indexOf("</ITEMDETAIL02>"));
                    }
                    if (strGetItemDetailsForSO.contains("<ITEMDETAIL03")) {
                        clsPL.ItemDetail03 = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<ITEMDETAIL03>") + 14, strGetItemDetailsForSO.indexOf("</ITEMDETAIL03>"));
                    }

                    // code added by 1165 on26-07-2016

                    if (strGetItemDetailsForSO.contains("<BATCHLIST")) {
                        String strBatchList = strGetItemDetailsForSO.substring(strGetItemDetailsForSO.indexOf("<BATCHLIST>") + 11, strGetItemDetailsForSO.indexOf("</BATCHLIST>"));
                        strBatchList = strBatchList.replace("<BATCH>", "");

                        String[] arrBatch = strBatchList.split("</BATCH>");

//                        clsPL.mrpList = getMrpList(arrBatch);
//                        clsPL.expiryList = getExpiryList(arrBatch);
                    } else {
//                        clsPL.mrpList = getMrpList(new String[]{});
//                        clsPL.expiryList = getExpiryList(new String[]{});
                    }
                }

                qtydialog = new Dialog(SOActivity.this);
                qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                qtydialog.setCanceledOnTouchOutside(false);
                qtydialog.setTitle("Quantity Selection");
                qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                acvItemSearchSOActivity.setAdapter(null);

                ImageButton cancel = (ImageButton) qtydialog.findViewById(R.id.imgBtnCloseQtySelection);
                TextView tvSelectedItemName = (TextView) qtydialog.findViewById(R.id.tvSelectedItemName);
                TextView tvMrp = (TextView) qtydialog.findViewById(R.id.tvMrpInQtySelection);
                TextView tvSOH = (TextView) qtydialog.findViewById(R.id.tvSOHInQtySelection);
                ImageButton btnPlus = (ImageButton) qtydialog.findViewById(R.id.imgBtnPlusPack);
                ImageButton btnMinus = (ImageButton) qtydialog.findViewById(R.id.imgBtnMinusPack);
                Button btnAdd = (Button) qtydialog.findViewById(R.id.btnAddItem_qtySelection);
                final EditText etQty = (EditText) qtydialog.findViewById(R.id.etQty);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(qtydialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                qtydialog.getWindow().setAttributes(lp);

                tvSelectedItemName.setText("" + clsPL.Item);
                tvMrp.setText("MRP : " + clsPL.Mrp);
                tvSOH.setText("SOH : " + clsPL.Soh);

                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        qtydialog.dismiss();
                    }
                });

                btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (etQty.getText().toString().equals("") || etQty.getText().toString() == null) {
                                etQty.setText("0");
                            }
//                            if (etCountUnits.getText().toString().equals("") || etCountUnits.getText().toString() == null) {
//                                etCountUnits.setText("0");
//                            }

                            itemQty = Integer.parseInt(etQty.getText().toString());
                            itemQty += 1;
                            etQty.setText("" + itemQty);
//                            qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
//                            qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
//                            QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
//                            tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
//                            TotAmt = (Mrp / uper_pack) * QtyInUnits;
//                            tvTotAmt.setText("Approx.Amount: ₹ " + String.format("%.2f", +TotAmt));
                        }catch (Exception ex){
                            Toast.makeText(SOActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!etQty.getText().toString().equals("") && etQty.getText().toString() != null) {
//                                if (!etCountUnits.getText().toString().equals("") && etCountUnits.getText().toString() != null) {

                                    itemQty = Integer.parseInt(etQty.getText().toString());
                                    if (itemQty >= 1)
                                        itemQty -= 1;

                                    etQty.setText("" +  itemQty);
//                                    qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
//                                    qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
//                                    QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
//                                    tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
//
//                                    TotAmt = (Mrp / uper_pack) * QtyInUnits;
//                                    tvTotAmt.setText("Approx.Amount: ₹ " + String.format("%.2f", +TotAmt));
//                                } else {
//                                    Toast.makeText(cntext, "Unit field is empty", Toast.LENGTH_SHORT).show();
//                                }
                            } else {
                                Toast.makeText(SOActivity.this, "Qty field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception ex){
                            Toast.makeText(SOActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //what we need here is product details to add to product list listview
                        if(Integer.parseInt(etQty.getText().toString()) < 1){
                            Toast.makeText(SOActivity.this, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        clsPL.Qty = etQty.getText().toString();

                        Double totAmount = Double.valueOf(clsPL.Qty) * Double.valueOf(clsPL.Mrp);
                        clsPL.TotalAmount = String.valueOf(totAmount);

                        lstProducts.add(clsPL);
                        String[] arrSlNo = new String[lstProducts.size()];

                        for (int i = 0; i < lstProducts.size(); i++) {
                            arrSlNo[i] = lstProducts.get(i).Item;
                        }

//                        double total_amount = 0;
                        total_amount = 0;
                        String[] arr = new String[lstProducts.size()];
                        for (int j = 0; j < lstProducts.size(); j++) {
                            arr[j] = lstProducts.get(j).Item;
//                                    if (j != 0)
                            total_amount = total_amount + Double.valueOf(lstProducts.get(j).TotalAmount);
                        }



//                        ListOfItemsAddedMiniSO

                        Gson gson = new Gson();
                        listOfItemsAddedStr = gson.toJson(lstProducts);

                        prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ListOfItemsAddedMiniSO",listOfItemsAddedStr);
                        editor.putString("BillTotal",""+total_amount);
                        editor.commit();

                        SOActivityAdapter OBJArrayAdSO = new SOActivityAdapter(SOActivity.this, arrSlNo, lstProducts,tvBilltotal);
                        lvProductlist.setAdapter(OBJArrayAdSO);
                        tvBilltotal.setText(""+total_amount);



                        qtydialog.dismiss();
                        acvItemSearchSOActivity.setText(""); //Added by Pavithra on 10-10-2020
                        acvItemSearchSOActivity.setAdapter(null); //added by Pavithra on 13-10-2020

                        //Populate product listview here

//                            }
//                        });
                    }
                });

                //added by Pavithra on 10-10-2020
                etQty.requestFocus();

                etQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                showSoftKeyboard(etQty);
//                InputMethodManager inputMethodManager =
//                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInputFromWindow(
//                        etQty.getApplicationWindowToken(),
//                        InputMethodManager.SHOW_FORCED, 0);
                qtydialog.show();

            }
        }

    }


    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(SOActivity.this);
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


    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private class SaveSOTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Saving..Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strSalesBill = ""; //Added by Pavithra on 13-10-2020
            sfaDocSave();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if(strSaveSO.equals("")||strSaveSO == null){
                Toast.makeText(SOActivity.this, "No result", Toast.LENGTH_SHORT).show();
            }else{
//                Toast.makeText(SOActivity.this, ""+strSaveSO, Toast.LENGTH_SHORT).show();

                if (strSaveSO.contains("1_Failed")) {
                    strSaveSO = strSaveSO.replace("<RESULT>", "");
                    strSaveSO = strSaveSO.replace("</RESULT>", "");
                    strSaveSO = strSaveSO.replace("1_Failed", "");
                    Toast.makeText(SOActivity.this, ""+strSaveSO, Toast.LENGTH_SHORT).show();
                    tsMessages(""+strSaveSO);
//                    tsMessage("Message", str);
                } else {

//                    no_free_soh_items = strNew.substring(strNew.indexOf("<NOFREESOHITEMS>") + 16, strNew.indexOf("</NOFREESOHITEMS>"));
//                    String queue_id = strNew.substring(strNew.indexOf("<SRCQUEUEID>") + 12, strNew.indexOf("</SRCQUEUEID>"));
                    String doc_series = strSaveSO.substring(strSaveSO.indexOf("<SODOCSERIES>") + 13, strSaveSO.indexOf("</SODOCSERIES>"));
                    String doc_no = strSaveSO.substring(strSaveSO.indexOf("<SODOCNO>") + 9, strSaveSO.indexOf("</SODOCNO>"));


                    dialog = new Dialog(SOActivity.this);
                    dialog.setContentView(R.layout.save_dialogwindow);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTitle("Save");
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//                        ImageButton imgBtnCloseSaveWindow = (ImageButton) dialog.findViewById(R.id.imgBtnCloseSaveWindow);
                    Button btnOkSavePopup = (Button) dialog.findViewById(R.id.btnOkSavePopup);
                    TextView tvSaveStatus = (TextView) dialog.findViewById(R.id.tvSaveStatus);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = (int) saveDialogWindowHeight;
                    lp.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(lp);

                    btnOkSavePopup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

//                tvSaveStatus.setText("Saved \n\nToken No: " + tokenNo);
                    tvSaveStatus.setText("Saved \n\nToken No: "+doc_series+" "+doc_no);
                    tvSaveStatus.setMovementMethod(new ScrollingMovementMethod());

                    tvSODocNo.setText(""+doc_series+" "+doc_no);

                    prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                    String bill_tot = prefs.getString("BillTotal","");

                    tvBilltotal.setText(""+bill_tot);
//                    tvBilltotal.setText(""+total_amount);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("IsSavedMiniSO", true);
//                    editor.putString("TokenNoAndroidSO", tokenNo);
                    editor.commit();

                    //Added by Pavithra on 12-10-2020
                    btnSave.setEnabled(false);
                    btnSave.setAlpha(0.4f);
                    imgBtnRemarksPrescrptn.setEnabled(false);
                    imgBtnRemarksPrescrptn.setAlpha(0.4f);
                    acvItemSearchSOActivity.setEnabled(false);
                    ic_search.setEnabled(false);

                    isSaved = true;
                    dialog.show();

                    //Following added by Pavithra on 13-10-2020
                    String[] arr = new String[lstProducts.size()];
                    for(int i = 0 ;i< lstProducts.size(); i++) {
                        arr[i] = lstProducts.get(i).Item;
                    }
                    SOActivityAdapter productListActivityAdapter = new SOActivityAdapter(SOActivity.this, arr, lstProducts, tvBilltotal);
                    lvProductlist.setAdapter(productListActivityAdapter);
                }
            }
        }
    }

    private void sfaDocSave(){

//        strSalesBill += "<SALESBILL>"
//                + "<SUMMARY>"
//                + "<SOMODEL>" + SalesOrderModel + "</SOMODEL>"
//                + "<SOSTOTAMOUNT>" + TsRound(ItemTotal, 2) + "</SOSTOTAMOUNT>" //needed
//                + "<SOSDISCOUNT>" + 0 + "</SOSDISCOUNT>" //needed
//                + "<SOSNETAMOUNT>" + TsRound(BillTotalAfterRounding, 2) + "</SOSNETAMOUNT>" //needed
//                + "<SOSPARTYNAME>" + CleanXml(CustomerName) + "</SOSPARTYNAME>" //needed
//                + "<SOSPARTYACID>" + CustomerId + "</SOSPARTYACID>" //needed
//                + "<SOSBILLAMOUNT>" + TsRound(BillTotalAfterRounding, 2) + "</SOSBILLAMOUNT>" //needed
//                + "<SOSSALESPERSONID>" + String.valueOf(TsCommonMethods1.GetLoggedInSalesPersonId()) + "</SOSSALESPERSONID>"
//                + "<SOSISDOCCOMPLETE>" + SOComplete + "</SOSISDOCCOMPLETE>"
//                + "<SOSID>" + String.valueOf(SOSummaryId) + "</SOSID>" // zero when saving for first time
//                + "</SUMMARY>"
//                + "<DETAIL>";

        strSalesBill += "<SALESBILL>"
                + "<SUMMARY>"
                + "<SOMODEL>" + SalesOrderModel + "</SOMODEL>"
                + "<SOSTOTAMOUNT>" + 150 + "</SOSTOTAMOUNT>" //needed
                + "<SOSDISCOUNT>" + 0 + "</SOSDISCOUNT>" //needed
                + "<SOSNETAMOUNT>" + 200 + "</SOSNETAMOUNT>" //needed
                + "<SOSPARTYNAME>" + "Paras" + "</SOSPARTYNAME>" //needed
                + "<SOSPARTYACID>" + CustomerId + "</SOSPARTYACID>" //needed
                + "<SOSBILLAMOUNT>" + 170 + "</SOSBILLAMOUNT>" //needed
                + "<SOSSALESPERSONID>" + String.valueOf(SalesOrderModel) + "</SOSSALESPERSONID>"
                + "<SOSISDOCCOMPLETE>" + 1 + "</SOSISDOCCOMPLETE>"
                + "<SOSID>" + String.valueOf(SOSummaryId) + "</SOSID>" // zero when saving for first time
                + "</SUMMARY>"
                + "<DETAIL>";

//        for (int i = lstProducts.size(); i > 1; i--) {
        for (int i = 0; i < lstProducts.size(); i++) {

            SalesBillDetailPL PL;
            Log.d("MOBSO", "Index " + (i - 1));
//            PL = lstProducts.get(i - 1);
            PL = lstProducts.get(i);

//            if (Float.valueOf(PL.Qty) >= 0 && Float.valueOf(PL.Rate) > 0) {
//                NoOfItems = NoOfItems + 1;
            int RateTaxFlag = 0;

            strSalesBill += "<DETAILLINE>"
                    + "<SLNO>" + PL.Slno + "</SLNO>"
                    + "<SODITEMID>" + PL.ItemId + "</SODITEMID>"
                    + "<SODMRP>" + PL.Mrp + "</SODMRP>"
//                        + "<SODRATE>" + TsRound(Float.valueOf(PL.Rate), 2) + "</SODRATE>"
                    + "<SODRATE>" + 100 + "</SODRATE>"
                    + "<SODQTY>" + PL.Qty + "</SODQTY>"
                    + "<SODAMOUNT>" + PL.Amount + "</SODAMOUNT>"
                    + "<SODDISCOUNTPER>" + PL.DiscPer + "</SODDISCOUNTPER>"
                    + "<SODDISCOUNTAMOUNT>" + PL.Disc + "</SODDISCOUNTAMOUNT>"
                    + "<SODLINENETAMOUNT>" + PL.TotalAmount + "</SODLINENETAMOUNT>"
                    + "<SODREMARKS>" + PL.Remarks + "</SODREMARKS>"
                    + "<SODBATCHMRP>" + PL.selectedBatchMrp + "</SODBATCHMRP>";

            if (!PL.selectedExpiry.equalsIgnoreCase(""))
                strSalesBill += "<SODBATCHEXPIRY>" + "01/" + PL.selectedExpiry + "</SODBATCHEXPIRY>";
            else
                strSalesBill += "<SODBATCHEXPIRY>" + "" + "</SODBATCHEXPIRY>";

            strSalesBill += "<SODID>" + PL.SODetailLineId + "</SODID>"
                    + "</DETAILLINE>";

        }

        strSalesBill += "</DETAIL>";
        strSalesBill += "</SALESBILL>";

        try {
            String MethodName = "";
            MethodName = "MobSoSalesDocSave";

            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
            request.addProperty("ClientValidator", AppConfigSettings.ClientValidator);
            request.addProperty("Username", LoggedInSalesPersonName);
            request.addProperty("DeviceId", DeviceId);
            request.addProperty("Identifier1", Identifier1);
            request.addProperty("Identifier2", Identifier2);
            request.addProperty("strSalesOrder", strSalesBill);
            request.addProperty("DocSummaryId", SOSummaryId);
            request.addProperty("SalesOrderModel", SalesOrderModel);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, AppConfigSettings.WSTimeOutValueMedium);
            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);

//                sample params = <SALESBILL><SUMMARY><SOMODEL>2</SOMODEL><SOSTOTAMOUNT>0.0</SOSTOTAMOUNT><SOSDISCOUNT>0</SOSDISCOUNT><SOSNETAMOUNT>0.0</SOSNETAMOUNT>
//	<SOSPARTYNAME>GEEKAY DRUG HOUSE         ...</SOSPARTYNAME><SOSPARTYACID>510</SOSPARTYACID><SOSBILLAMOUNT>0.0</SOSBILLAMOUNT><SOSSALESPERSONID>2678</SOSSALESPERSONID>
//	<SOSISDOCCOMPLETE>0</SOSISDOCCOMPLETE><SOSID>0</SOSID></SUMMARY><DETAIL><DETAILLINE><SLNO>1</SLNO><SODITEMID>5470</SODITEMID><SODMRP>76.16</SODMRP><SODRATE>65.65</SODRATE>
//	<SODQTY>0</SODQTY><SODAMOUNT>0</SODAMOUNT><SODDISCOUNTPER>0</SODDISCOUNTPER><SODDISCOUNTAMOUNT>0</SODDISCOUNTAMOUNT><SODLINENETAMOUNT>0</SODLINENETAMOUNT><SODREMARKS></SODREMARKS>
//	<SODBATCHMRP></SODBATCHMRP><SODBATCHEXPIRY></SODBATCHEXPIRY><SODID>0</SODID></DETAILLINE></DETAIL></SALESBILL>

            Object result = envelope.getResponse();
            String str = result.toString();
            strSaveSO = str;
//        <RESULT><STATUS>SUCCESS</STATUS><SRCQUEUEID>0</SRCQUEUEID><SODOCSERIES>5001/20/WSO</SODOCSERIES><SODOCNO>22</SODOCNO><NOFREESOHITEMS></NOFREESOHITEMS></RESULT>

        }catch (Exception ex){
            Log.d("SOA",""+ex);
        }

    }




    private void mobSoGetItemDetailsForSO(){

        try {

//            if (TsCommonMethods1.isNetworkConnected() == false) {
//                Toast.makeText(SalesOrderModel2.this, "No Network Connectivity!!!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            acvProduct.setEnabled(true);
//            acvBatch.setEnabled(true);
//            acvProduct.setKeyListener(KLProd);
//            acvBatch.setKeyListener(KLBatch);

            String MethodName = "MobSoGetItemDetailsForSO";
            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
            request.addProperty("ClientValidator", AppConfigSettings.ClientValidator);
            request.addProperty("Username", LoggedInSalesPersonName);
            request.addProperty("DeviceId", DeviceId);
            request.addProperty("Identifier1", Identifier1);
            request.addProperty("Identifier2", Identifier2);
            request.addProperty("CustomerId", CustomerId);
            request.addProperty("CustomerType", CustomerType);
            request.addProperty("ItemId", SRSelectedItemId);
            request.addProperty("Item", SelectedItemName);
            request.addProperty("FormId", SelectedFormId);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, AppConfigSettings.WSTimeOutValueMedium);
            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);

            Object result = envelope.getResponse();
            String str = result.toString();
            strGetItemDetailsForSO = str;

        }catch (Exception ex) {
            Log.d("SOA", "" + ex);
        }

    }

    private class LoadProductListTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            loadProductList();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if (strLoadProductList == null || strLoadProductList.equals("")) {
                    Toast.makeText(SOActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
                } else {

                    strLoadProductList = strLoadProductList.replace("1_Failed", "");
                    strLoadProductList = strLoadProductList.replace("<RESULT>", "");
                    strLoadProductList = strLoadProductList.replace("</RESULT>", "");
                    strLoadProductList = strLoadProductList.replace("<FIELD1>", "");

                    String[] arrProducts = strLoadProductList.split("</FIELD1>");

                    AutoCompleteProductListCustomAdapter myAdapter = new AutoCompleteProductListCustomAdapter(SOActivity.this, R.layout.autocomplete_view_row, arrProducts);
                    acvItemSearchSOActivity.setAdapter(myAdapter);
                    acvItemSearchSOActivity.showDropDown();

                }

            }catch (Exception ex){
                Toast.makeText(SOActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductList() {

        try {
            String MethodName = "MobSoGetItemList";
            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
            request.addProperty("ClientValidator", AppConfigSettings.ClientValidator);
            request.addProperty("Username", LoggedInSalesPersonName);
            request.addProperty("DeviceId", DeviceId);
            request.addProperty("Identifier1", Identifier1);
            request.addProperty("Identifier2", Identifier2);
            request.addProperty("CustomerId", CustomerId);
            CustomerType = "RETAIL";
            request.addProperty("CustomerType", CustomerType);
//            request.addProperty("Filter", etItemSearchSOActivity.getText().toString());
            request.addProperty("Filter", acvItemSearchSOActivity.getText().toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, AppConfigSettings.WSTimeOutValueMedium);
            //androidHttpTransport.setTimeout(100000);
            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);

            Object result = envelope.getResponse();
            String str = result.toString();
            strLoadProductList = str;

//            str = str.replace("1_Failed", "");
//            str = str.replace("<RESULT>", "");
//            str = str.replace("</RESULT>", "");
//            str = str.replace("<FIELD1>", "");
//
//            String[] arrCust = str.split("</FIELD1>");
//
//            //Added by Pavithra on 04-08-2020
//            AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(this, R.layout.autocomplte_view_row, arrCust);
//            acvProduct.setAdapter(myAdapter);


        } catch (SocketTimeoutException ex) {
            Log.d("SOA", "" + ex);
//            tsMessage("Message","Server Taking Too Much Time. Please Try Again ");
        } catch (Exception ex) {
            Log.d("SOA", "" + ex);
//            if(ex.getMessage().contains("ECONNRESET")&&wsCallCount<3){
//                LoadProductList();
//                wsCallCount += 1;
//            }else
//                tsMessage("Message", "Unexpected Error While Loading ProductList " + (ex.getMessage() == null ? "" :  ex.getMessage()));
        }

    }

//    private void LoadProductList() {
//        try {
//            if(TsCommonMethods1.isNetworkConnected() == false ) {
//                Toast.makeText(SalesOrderModel2.this,"No Network Connectivity!!!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            if(DocumentSaved == true) {
//                tsMessage("Message","SO Already Saved");
//                return;
//            }
//
//            if(CustomerId == -1) {
//                tsMessage("Message","Document Not Initialised!!!");
//                return;
//            }
//            if(!radioRetailer.isChecked() && !radioStockist.isChecked()) {
//                tsMessage("Message","Select Either Retailer Or Stockist");
//                acvCustomer.setText("");
//                return;
//            }
//
//            if(acvCustomer.getText().length() == 0) {
//                tsMessage("Message","Provide Customer");
//                return;
//            }
//
//            if(acvCustomer.getText().length() > 0 && CustomerId < 0) {
//                tsMessage("Message","Confirm Customer Clicking OK Button");
//                return;
//            }
//
//            if(CustomerId <= 0 ) {
//                tsMessage("Message","Select Customer From Lookup");
//                acvCustomer.setText("");
//                return;
//            }
//            String MethodName = "MobSoGetItemList";
//            SoapObject request = new SoapObject(AppConfigSettings.WSNAMESPACE, MethodName);
//            request.addProperty("ClientValidator", ClientValidator);
//            request.addProperty("Username", TsCommonMethods1.GetLoggedInSalesPersonName());
//            request.addProperty("DeviceId", DeviceUniqueId);
//            request.addProperty("Identifier1", Identifier1);
//            request.addProperty("Identifier2", Identifier2);
//            request.addProperty("CustomerId", CustomerId);
//            request.addProperty("CustomerType", CustomerType);
//            request.addProperty("Filter", acvProduct.getText().toString());
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//            envelope.dotNet = true;
//            envelope.setOutputSoapObject(request);
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, AppConfigSettings.WSTimeOutValueMedium);
//            //androidHttpTransport.setTimeout(100000);
//            androidHttpTransport.call(AppConfigSettings.WSNAMESPACE + MethodName, envelope);
//
//            Object result = envelope.getResponse();
//            String str = result.toString();
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
////                KArrayAdapter<String> Custadapter = new KArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrCust);
////                acvProduct.setAdapter(Custadapter);
////                acvProduct.showDropDown();
//
//            //Added by Pavithra on 04-08-2020
//            AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(this, R.layout.autocomplte_view_row, arrCust);
//            acvProduct.setAdapter(myAdapter);
//
//
//        } catch(SocketTimeoutException ex){
//            tsMessage("Message","Server Taking Too Much Time. Please Try Again ");
//        } catch(Exception ex) {
//            if(ex.getMessage().contains("ECONNRESET")&&wsCallCount<3){
//                LoadProductList();
//                wsCallCount += 1;
//            }else
//                tsMessage("Message", "Unexpected Error While Loading ProductList " + (ex.getMessage() == null ? "" :  ex.getMessage()));
//        }
//    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
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
