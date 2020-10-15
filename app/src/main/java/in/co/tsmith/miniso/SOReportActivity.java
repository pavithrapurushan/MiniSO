package in.co.tsmith.miniso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

//Created on 09-10-2020
public class SOReportActivity extends AppCompatActivity {

    Button btnLoadReport;
    EditText etDate;
    String filterParam = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soreport);

        btnLoadReport = (Button )findViewById(R.id.btnLoadReport);
        etDate = (EditText ) findViewById(R.id.etDate);

        btnLoadReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(String.valueOf(etDate.getText()).trim().length() > 0) {

                    filterParam = String.valueOf(etDate.getText()).trim();
                    new LoadSOSummaryReportTask().execute();
                }

            }
        });
    }

    private class LoadSOSummaryReportTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
//            loadSOSummaryReport();
            return null;
        }
    }

//    private void loadSOSummaryReport(){
//
//
//        String MethodName = "MobSoGetSoSummaryReport";
//        SoapObject request = new SoapObject(NAMESPACE, MethodName);
//        request.addProperty("ClientValidator",AppConfigSettings.ClientValidator);
//        request.addProperty("Username", SalesBi);
//        request.addProperty("DeviceId", DeviceUniqueId);
//        request.addProperty("Identifier1", Identifier1);
//        request.addProperty("Identifier2", Identifier2);
//
//        if(filterParam.length()> 0){
//            request.addProperty("FilterParam1", filterParam);
//        }
//
//        request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
//
////        if(WSMethodName.equalsIgnoreCase("MobSoGetCustomerOutStanding")) {
////            request.addProperty("CustomerId", CustomerId);
////        }
////        if(WSMethodName.equalsIgnoreCase("MobSoGetReceivables")) {
////            request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
////        }
////        if(WSMethodName.equalsIgnoreCase("MobSoGetCollectionSummaryReport")) {
////            request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
////        }
////        if(WSMethodName.equalsIgnoreCase("MobSoGetSoSummaryReport")) {
////            request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
////        }
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11 );
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,WSTimeOutValueInMillis);
//        androidHttpTransport.call(NAMESPACE+MethodName, envelope);
//
//        Object result = envelope.getResponse();
//        String str =  result.toString();
//        ResultFromWebService = str;
//
//    }



//    private void GetListToDisplayAsynk() {
//        try {
//            if(TsCommonMethods1.isNetworkConnected() == false ) {
//                Toast.makeText(ReportActivity.this,"No Network Connectivity!!!", Toast.LENGTH_LONG).show();
//            }
//            if(String.valueOf(txtReportFilter.getText()).trim().length() > 0) {
//
//                FilterParam1 = String.valueOf(txtReportFilter.getText()).trim();
//            }
//
//            RequestTime = System.currentTimeMillis();
//
//            String MethodName = WSMethodName;
//            SoapObject request = new SoapObject(NAMESPACE, MethodName);
//            request.addProperty("ClientValidator", ClientValidator);
//            request.addProperty("Username", LoggedUser);
//            request.addProperty("DeviceId", DeviceUniqueId);
//            request.addProperty("Identifier1", Identifier1);
//            request.addProperty("Identifier2", Identifier2);
//
//            if(FilterParam1.length()> 0){
//                request.addProperty("FilterParam1", FilterParam1);
//            }
//            if(WSMethodName.equalsIgnoreCase("MobSoGetCustomerOutStanding")) {
//                request.addProperty("CustomerId", CustomerId);
//            }
//            if(WSMethodName.equalsIgnoreCase("MobSoGetReceivables")) {
//                request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
//            }
//            if(WSMethodName.equalsIgnoreCase("MobSoGetCollectionSummaryReport")) {
//                request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
//            }
//            if(WSMethodName.equalsIgnoreCase("MobSoGetSoSummaryReport")) {
//                request.addProperty("SalesPersonId", TsCommonMethods1.GetLoggedInSalesPersonId());
//            }
//
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11 );
//            envelope.dotNet = true;
//            envelope.setOutputSoapObject(request);
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,WSTimeOutValueInMillis);
//            androidHttpTransport.call(NAMESPACE+MethodName, envelope);
//
//            Object result = envelope.getResponse();
//            String str =  result.toString();
//            ResultFromWebService = str;
//
//            if(androidHttpTransport != null) {
//                androidHttpTransport.getServiceConnection().disconnect();
//                Log.d(TAG,"Connection Closed");
//            }
//
//        } catch(Exception ex) {
//            if(ex.getMessage().contains("ECONNRESET")){
//                NoOfAttemptsTogetData = NoOfAttemptsTogetData+1;
//                if(NoOfAttemptsTogetData <= 3) {
//                    Toast.makeText(ReportActivity.this,"Connection Attempt " + String.valueOf(NoOfAttemptsTogetData) + " of 3" , Toast.LENGTH_SHORT).show();
//                    this.finish();
//                    Intent intent = new Intent(ReportActivity.this, ReportActivity.class);
//                    intent.putExtra("URL",URL);
//                    intent.putExtra("LoggedUser",LoggedUser);
//                    intent.putExtra("SalesPersonId",TsCommonMethods1.GetLoggedInSalesPersonId());
//                    intent.putExtra("MainTag",MainTAG);
//                    intent.putExtra("SubTag",WSMethodName);
//                    intent.putExtra("ReportName",ReportName);
//                    intent.putExtra("FilterHint", FilterHint);
//                    ReportActivity.this.startActivity(intent);
//                } else{
//                    Toast.makeText(ReportActivity.this,"Connection Error. Please try Later" , Toast.LENGTH_LONG).show();
//                }
//            } else{
//                Toast.makeText(ReportActivity.this,ex.getMessage() , Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
