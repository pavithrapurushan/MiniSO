package in.co.tsmith.miniso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

public class SOActivityAdapter extends ArrayAdapter<String> {

    List<SalesBillDetailPL> lstSalesBillDetailPL;
    Context cntext;
    String[] values;

    SharedPreferences prefs;
    TextView tvTotAmount;
    Dialog dialog;

    double remarksDialogWindowHeight;
    Dialog qtydialog;

    String item_name = "";
    String mrp = "";
    String soh = "";
    String selected_item_id = "";

    String CurrentQty = "";

    boolean isSaved = false;
    String listOfItemsAddedStr = "";
    int itemQty = 0;

    public SOActivityAdapter(Context context, String[] v1, List<SalesBillDetailPL> salesBillDetailPLList,TextView tvBillTotal) {

        super(context, R.layout.activity_so, v1);
        cntext = context;
        values = v1;
        lstSalesBillDetailPL = salesBillDetailPLList;
        tvTotAmount = tvBillTotal;

        prefs = PreferenceManager.getDefaultSharedPreferences(cntext);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
        DisplayMetrics displayMetrics = cntext.getResources().getDisplayMetrics();
        int screen_height = displayMetrics.heightPixels;

        remarksDialogWindowHeight = (screen_height * 38) / 100;  //  243/640

    }

    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) cntext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_lv_salesorder, parent, false);

        TextView tvSlNo = (TextView) rowView.findViewById(R.id.tvSlNo);
        TextView tvItem = (TextView) rowView.findViewById(R.id.tvProductName);
        TextView tvMrp = (TextView) rowView.findViewById(R.id.tvMRP);
        TextView tvQty = (TextView) rowView.findViewById(R.id.tvQty); //Commented by Pavithra on 03-09-2020
//        EditText tvQty = (EditText) rowView.findViewById(R.id.tvQty);   //added by Pavithra on 03-09-2020
        TextView tvTotal = (TextView) rowView.findViewById(R.id.tvTotal);
        final ImageButton imgBtnRemarksItem = (ImageButton) rowView.findViewById(R.id.imgBtnRemarksItem);
        ImageButton btnDelete = (ImageButton) rowView.findViewById(R.id.btnDeleteItem);

        SalesBillDetailPL itemDetailsPLObj = lstSalesBillDetailPL.get(position);
        tvSlNo.setText(String.valueOf(position + 1));
        tvItem.setText(itemDetailsPLObj.Item);
        tvMrp.setText(String.valueOf(itemDetailsPLObj.Mrp));
        tvMrp.setGravity(Gravity.LEFT);

        tvQty.setText(itemDetailsPLObj.Qty);
        tvTotal.setText(itemDetailsPLObj.TotalAmount);


        isSaved = prefs.getBoolean("IsSavedMiniSO",false);
        if(isSaved){
            imgBtnRemarksItem.setEnabled(false);
            btnDelete.setEnabled(false);
            tvQty.setEnabled(false);
        }else {
            imgBtnRemarksItem.setEnabled(true);
            btnDelete.setEnabled(true);
            tvQty.setEnabled(true);
        }


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeletePopUP(position);
            }
        });

        imgBtnRemarksItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    dialog = new Dialog(cntext);
                    dialog.setContentView(R.layout.add_remarks);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTitle("Remarks");
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    ImageButton imgBtnCloseRemarksWindow = (ImageButton) dialog.findViewById(R.id.imgBtnCloseRemarksWindow);
                    Button btnOkRemarks_Itemwise = (Button) dialog.findViewById(R.id.btnOkRemarks_Itemwise);
                    Button btnClearRemarks_Itemwise = (Button) dialog.findViewById(R.id.btnClearRemarks_Itemwise);
                    final EditText etAddRemarks_Itemwise = (EditText) dialog.findViewById(R.id.etAddRemarks_Itemwise);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = (int) remarksDialogWindowHeight;
                    lp.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(lp);

//                    etAddRemarks_Itemwise.setText(lstSalesBillDetailPL.get(position).Remarks);

                    imgBtnCloseRemarksWindow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnOkRemarks_Itemwise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                            listItemDetails.get(position).Remarks = etAddRemarks_Itemwise.getText().toString();
//
//                            Gson gson = new Gson();
//                            String ListOfItemsAddedStr = gson.toJson(listItemDetails);
//                            prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
//                            SharedPreferences.Editor editor = prefs.edit();
//                            editor.putString("ListOfItemsAddedAndroidSO", ListOfItemsAddedStr);
//                            editor.commit();

//                            imgBtnRemarksItem.setColorFilter(cntext.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);


                            // locally store remarks
                            if(etAddRemarks_Itemwise.getText().toString().equalsIgnoreCase("")){
                                imgBtnRemarksItem.setImageResource(R.drawable.ic_remarks_item);
                            }else {
                                imgBtnRemarksItem.setImageResource(R.drawable.ic_remarks_colrchanged);
                            }

                            dialog.dismiss();

                        }
                    });
                    btnClearRemarks_Itemwise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etAddRemarks_Itemwise.setText("");

                        }
                    });

                    dialog.show();
                }catch (Exception ex){
                    Toast.makeText(cntext, ""+ex, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                qtydialog = new Dialog(cntext);
                qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                qtydialog.setCanceledOnTouchOutside(false);
                qtydialog.setTitle("Quantity Selection");
                qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


//                tvSelectedItemName = (TextView) qtydialog.findViewById(R.id.tvSelectedItemName); //setting product name
//                tvMrpInQtySelection = (TextView) qtydialog.findViewById(R.id.tvMrpInQtySelection);
//                tvUperpackInQtySelection = (TextView) qtydialog.findViewById(R.id.tvUperpackInQtySelection);
//
//                tvSelectedItemName.setText("" + itemname);
//                tvMrpInQtySelection.setText("MRP: " + mrp);
//                tvUperpackInQtySelection.setText("UPerPack: " + uperpack);

                ImageButton cancel = (ImageButton) qtydialog.findViewById(R.id.imgBtnCloseQtySelection);

                TextView tvSelectedItemName = (TextView) qtydialog.findViewById(R.id.tvSelectedItemName);
                TextView tvMrp = (TextView) qtydialog.findViewById(R.id.tvMrpInQtySelection);
                TextView tvSOH = (TextView) qtydialog.findViewById(R.id.tvSOHInQtySelection);
                Button btnAdd = (Button) qtydialog.findViewById(R.id.btnAddItem_qtySelection);
                ImageButton btnPlus = (ImageButton) qtydialog.findViewById(R.id.imgBtnPlusPack);
                ImageButton btnMinus = (ImageButton) qtydialog.findViewById(R.id.imgBtnMinusPack);
                final EditText etQty = (EditText) qtydialog.findViewById(R.id.etQty);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(qtydialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                qtydialog.getWindow().setAttributes(lp);

                item_name = lstSalesBillDetailPL.get(position).Item;
                mrp = lstSalesBillDetailPL.get(position).Mrp;
                soh = lstSalesBillDetailPL.get(position).Soh;
                selected_item_id = lstSalesBillDetailPL.get(position).ItemId;
                CurrentQty = lstSalesBillDetailPL.get(position).Qty;


                tvSelectedItemName.setText("" + item_name);
                tvMrp.setText("MRP : " + mrp);
                tvSOH.setText("SOH : " + soh);
                etQty.setText(""+CurrentQty);

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
                            Toast.makeText(cntext, ""+ex, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(cntext, "Qty field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception ex){
                            Toast.makeText(cntext, ""+ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(Integer.parseInt(etQty.getText().toString()) < 1){
                            Toast.makeText(cntext, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        for (int i = 0; i < lstSalesBillDetailPL.size(); i++) {
                            if (selected_item_id == lstSalesBillDetailPL.get(i).ItemId) {

                                lstSalesBillDetailPL.get(i).ItemId = selected_item_id;
//                                        itemDetailsPLList.get(i).Qty = String.valueOf(QtyInUnits); //Commented by Pavithra on 11-09-2020
                                lstSalesBillDetailPL.get(i).Qty = etQty.getText().toString();    //Added by Pavithra on 11-09-2020

                                int qty = Integer.parseInt(lstSalesBillDetailPL.get(i).Qty);
                                Double mrp = Double.valueOf(lstSalesBillDetailPL.get(i).Mrp);

                                Double totAmount = qty * mrp;
                                lstSalesBillDetailPL.get(i).TotalAmount = String.format("%.2f", totAmount);
//                                    itemDetailsPLList.get(i).Amount = String.format("%.2f", totAmount);

                            }
                        }

                        Double totamnt = 0d;

                        String[] arr = new String[lstSalesBillDetailPL.size()];
                        for (int j = 0; j < lstSalesBillDetailPL.size(); j++) {
                            arr[j] = lstSalesBillDetailPL.get(j).Item;
//                                if (j != 0)
                            totamnt = totamnt + Double.valueOf(lstSalesBillDetailPL.get(j).TotalAmount);
                        }

                        Gson gson = new Gson();
                        listOfItemsAddedStr = gson.toJson(lstSalesBillDetailPL);

                        prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ListOfItemsAddedMiniSO",listOfItemsAddedStr);
                        editor.putString("BillTotal",""+totamnt);
                        editor.commit();


                        SOActivityAdapter productListActivityAdapter = new SOActivityAdapter(cntext, arr, lstSalesBillDetailPL, ((SOActivity) cntext).tvBilltotal);
                        ((SOActivity) cntext).lvProductlist.setAdapter(productListActivityAdapter);
//                                ((SOActivity) cntext).tvTotalAmountValue.setText("Total Amount : " + String.format("%.2f", totamnt));
                        ((SOActivity) cntext).tvBilltotal.setText("" + String.format("%.2f", totamnt));
                        ((SOActivity) cntext).tvBilltotal.setVisibility(View.VISIBLE);

//
//                        clsPL.Qty = etQty.getText().toString();
//
//                        Double totAmount = Double.valueOf(clsPL.Qty) * Double.valueOf(clsPL.Mrp);
//                        clsPL.TotalAmount = String.valueOf(totAmount);
//
//                        lstProducts.add(clsPL);
//                        String[] arrSlNo = new String[lstProducts.size()];
//
//                        for (int i = 0; i < lstProducts.size(); i++) {
//                            arrSlNo[i] = lstProducts.get(i).Item;
//                        }
//
//                        double total_amount = 0;
//                        String[] arr = new String[lstProducts.size()];
//                        for (int j = 0; j < lstProducts.size(); j++) {
//                            arr[j] = lstProducts.get(j).Item;
////                                    if (j != 0)
//                            total_amount = total_amount + Double.valueOf(lstProducts.get(j).TotalAmount);
//                        }
//                        SOActivityAdapter OBJArrayAdSO = new SOActivityAdapter(SOActivity.this, arrSlNo, lstProducts,tvBilltotal);
//                        lvProductlist.setAdapter(OBJArrayAdSO);
//                        tvBilltotal.setText(""+total_amount);



                        qtydialog.dismiss();
                        ((SOActivity) cntext).acvItemSearchSOActivity.setText("");
//                        acvItemSearchSOActivity.setText(""); //Added by Pavithra on 10-10-2020

                    }
                });

                qtydialog.show();

            }
        });

        return rowView;
    }

    @Override
    public int getCount() {
        return lstSalesBillDetailPL.size();
    }

    public void showDeletePopUP(final int position) {

        AlertDialog.Builder b = new AlertDialog.Builder(cntext);
        b.setTitle("Confirm Delete");
        b.setMessage("Are you sure to delete " + lstSalesBillDetailPL.get(position).Item);  //item name should specify

        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
                Toast.makeText(cntext, "Item Deleted successfully", Toast.LENGTH_LONG).show();
            }
        });

        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();
    }



    public void deleteItem(int position) {
        try {
            this.lstSalesBillDetailPL.remove(position);
            this.notifyDataSetChanged();

            Double totamnt = 0d;
            if (lstSalesBillDetailPL.size() > 0) {

                for (int j = 0; j < lstSalesBillDetailPL.size(); j++) {
                    totamnt = totamnt + Double.valueOf(lstSalesBillDetailPL.get(j).TotalAmount);
                }

                tvTotAmount.setText("" + String.format("%.2f", totamnt));
                tvTotAmount.setVisibility(View.VISIBLE);

            } else {
                tvTotAmount.setVisibility(View.GONE);
//            this.listItemDetails.remove(0);
                this.notifyDataSetChanged();
            }

            //Following added by Pavithra on 26-08-2020
            Gson gson = new Gson();
            String ListOfItemsAddedStr = gson.toJson(lstSalesBillDetailPL);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ListOfItemsAddedMiniSO", ListOfItemsAddedStr);
            editor.putString("BillTotal",""+totamnt);
            editor.commit();
        } catch (Exception ex) {
            Toast.makeText(cntext, "" + ex, Toast.LENGTH_SHORT).show();
        }

    }

}
