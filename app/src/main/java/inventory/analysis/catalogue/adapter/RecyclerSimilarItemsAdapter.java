package inventory.analysis.catalogue.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.DividerItemDecoration;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import inventory.analysis.catalogue.MainGroupOrGroupActivity;
import inventory.analysis.catalogue.Utils;
import inventory.analysis.catalogue.dataset.RecyclerSimilarItemsDataSet;
import inventory.analysis.catalogue.responsedataset.ResponseAddOrRemoveDataset;
import inventory.analysis.catalogue.responsedataset.ResponseColorOptionDataset;
import inventory.analysis.catalogue.responsedataset.ResponseSimilarColorDataset;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.barcode_search.OrderBookingActivity;
import orderbooking.barcode_search.SubItemActivity;
import orderbooking.barcode_search.WithoutSubItemActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerSimilarItemsAdapter extends RecyclerView.Adapter<RecyclerSimilarItemsAdapter.RecyclerSimilarItemsHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerSimilarItemsDataSet> mapList;
    int width,height;
    private CloseOrBookDataset closeOrBookDataset;
    RecyclerView recyclerView;
    ProgressDialog spotsDialog;
    private static String TAG = RecyclerSimilarItemsAdapter.class.getSimpleName();
    public RecyclerSimilarItemsAdapter(Context context, List<RecyclerSimilarItemsDataSet> mapList, CloseOrBookDataset closeOrBookDataset) {
        this.context=context;
        this.mapList=mapList;
        this.closeOrBookDataset=closeOrBookDataset;
        this.inflater = LayoutInflater.from(context);
        DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
        float screenWidth  = StaticValues.mViewWidth / dm.xdpi;
        if(screenWidth > 1.5) {
            int w = (int)(1.5*dm.xdpi);
            this.width = w;
            this.height = (int)(w*1.366);
        }else if(screenWidth < 1.5){
            this.width = StaticValues.mViewWidth;
            this.height = StaticValues.mViewHeight;
        }
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerSimilarItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_item_design, parent, false);
        RecyclerSimilarItemsHolder viewHolder=new RecyclerSimilarItemsHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerSimilarItemsHolder holder, final int position) {

        final int pos=position;
        holder.cardView.getLayoutParams().width = width;
        holder.cardView.getLayoutParams().height = height;
        holder.txtViewItemCode.setText(mapList.get(position).getItemCode());
        int rate=Integer.valueOf(mapList.get(position).getRate());
        String Rate=(StaticValues.rateFlag==0)?"":((rate<0)?"Not available":"₹"+ rate);
        if(Rate==""){
            holder.txtViewPrice.setVisibility(View.GONE);
            holder.txtViewPrice.setText("" + Rate);
        }
        else{
            holder.txtViewPrice.setText("" + Rate);
        }
        holder.txtViewColor.setText(""+mapList.get(position).getTotalColor());
        int stock=Integer.valueOf(mapList.get(position).getItemStock());
        String ItemStock=(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnit():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnit()));
        holder.txtViewStock.setText(""+ItemStock);
        Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        holder.cardView.setTag(mapList.get(position).getItemCode());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null)
                        BarcodeSearchViewPagerActivity.Barcode = v.getTag().toString();
                    StaticValues.OrderViewIntentFlag = 1;
                    StaticValues.CatalogueFlag = 1;
                    CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], BarcodeSearchViewPagerActivity.Barcode, closeOrBookDataset.getOrderID());
                } else {
                    MessageDialog.MessageDialog(context, "", status);
                }
            }
        });
        holder.checkboxWishlist.setChecked((mapList.get(position).isSelectedWishlist()==0)?false:true);
        holder.checkboxWishlist.setTag(mapList.get(position));
        holder.checkboxWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                RecyclerSimilarItemsDataSet recyclerItemDataset = (RecyclerSimilarItemsDataSet) cb.getTag();
                recyclerItemDataset.setSelectedWishlist((cb.isChecked()==true)?1:0);
                mapList.get(pos).setSelectedWishlist((cb.isChecked()==true)?1:0);
                String status = NetworkUtils.getConnectivityStatusString(context);
                if(cb.isChecked()){
                    if (!status.contentEquals("No Internet Connection")) {
                        if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !mapList.get(pos).getItemID().isEmpty()) {
                            CallRetrofitAddRemoveWishlist(CommanStatic.DeviceID, CommanStatic.DevicePassword, mapList.get(pos).getItemID(), "1");
                        }
                    }else{
                        Snackbar.make(cb,status,Snackbar.LENGTH_LONG).show();
                        cb.setChecked(false);
                    }
                }
                else {
                    if (!status.contentEquals("No Internet Connection")) {
                        if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !mapList.get(pos).getItemID().isEmpty()) {
                            CallRetrofitAddRemoveWishlist(CommanStatic.DeviceID, CommanStatic.DevicePassword, mapList.get(pos).getItemID(), "0");
                        }
                    }else{
                        Snackbar.make(cb,status,Snackbar.LENGTH_LONG).show();
                        cb.setChecked(true);
                    }
                }
            }
        });
        holder.txtViewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemName(),mapList.get(position).getItemCode(),mapList.get(position).getRate(),0);
            }
        });
        holder.imageViewSimilarItem.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public class RecyclerSimilarItemsHolder extends RecyclerView.ViewHolder {

        TextView txtViewPrice,txtViewStock,txtViewItemCode,txtViewColor;
        ImageView imageView,imageViewSimilarItem;
        CheckBox  checkboxWishlist;
        CardView cardView;
        public RecyclerSimilarItemsHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            checkboxWishlist = (CheckBox) itemView.findViewById(R.id.checkbox_wishlist);
            imageViewSimilarItem = (ImageView) itemView.findViewById(R.id.imageViewSimilarItems);
            txtViewItemCode = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewStock = (TextView) itemView.findViewById(R.id.textView_item_stock);
            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_title_price);
            txtViewColor = (TextView) itemView.findViewById(R.id.textView_Color);
        }
    }
    private void CustomDialog(String ItemID,String ItemName,String Itemcode,String rate,int flag){
        Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_color_similar);
        android.view.Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) (display.getHeight() * 55 / 100);
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(flag==0) {
            dialog.setTitle("Color Option");
            dialog.show();
            TextView txtViewItemCode = (TextView) dialog.findViewById(R.id.txtView_Itemcode);
            txtViewItemCode.setText("Itemcode: " + Itemcode + "\t\tPrice:" + rate);
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));
            //TODO: ColorOption List Execute
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, ItemName, CommanStatic.PriceListID,0);
                }
            }else{
                MessageDialog.MessageDialog(context,"",status);
            }
        }
        else if(flag==1){
            dialog.setTitle("Similar Items");
            dialog.show();
            TextView txtViewItemCode = (TextView) dialog.findViewById(R.id.txtView_Itemcode);
            txtViewItemCode.setVisibility(View.GONE);
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));
            //TODO: ColorOption List Execute
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, ItemName, CommanStatic.PriceListID,1);
                }
            }else{
                MessageDialog.MessageDialog(context,"",status);
            }
        }
    }
    private void CallRetrofit(String AKey, String Password,String ItemID,String ItemName, String PriceListID,int flag){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("PriceListID", PriceListID);
        if(flag==0) {
            params.put("ItemID", ItemID);
            Call<ResponseColorOptionDataset> call = apiService.getInventoryColorOptionList(params);
            call.enqueue(new Callback<ResponseColorOptionDataset>() {
                @Override
                public void onResponse(Call<ResponseColorOptionDataset> call, retrofit2.Response<ResponseColorOptionDataset> response) {
                    try {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            List<RecyclerColorOptionDataSet> DatasetList = response.body().getResult();
                            if(DatasetList!=null) {
                                RecyclerColorOptionAdapter recyclerColorOptionAdapter = new RecyclerColorOptionAdapter(context, DatasetList,closeOrBookDataset);
                                recyclerView.setAdapter(recyclerColorOptionAdapter);
                            }
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                        //}
                    } catch (Exception e) {
                        Log.e(TAG, "Color Option Exception:" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseColorOptionDataset> call, Throwable t) {
                    Log.e(TAG, "Failure:" + t.toString());
                    Toast.makeText(context, "Color Option Failure", Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }else if(flag==1){
            params.put("ItemName", ItemName);
            Call<ResponseSimilarColorDataset> call = apiService.getInventorySimilarItemList(params);
            call.enqueue(new Callback<ResponseSimilarColorDataset>() {
                @Override
                public void onResponse(Call<ResponseSimilarColorDataset> call, retrofit2.Response<ResponseSimilarColorDataset> response) {
                    try {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            List<RecyclerSimilarItemsDataSet> DatasetList = response.body().getResult();
                            if(DatasetList!=null) {
                                if(DatasetList.isEmpty()){
                                    Toast.makeText(context, "No Similar Items!!!", Toast.LENGTH_LONG).show();
                                }else {
                                    RecyclerSimilarItemsAdapter recyclerSimilarItemsAdapter = new RecyclerSimilarItemsAdapter(context, DatasetList,closeOrBookDataset);
                                    recyclerView.setAdapter(recyclerSimilarItemsAdapter);
                                }
                            }
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                        //}
                    } catch (Exception e) {
                        Log.e(TAG, "Color Option Exception:" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseSimilarColorDataset> call, Throwable t) {
                    Log.e(TAG, "Failure:" + t.toString());
                    Toast.makeText(context, "Color Option Failure", Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });

        }
    }
    private void CallRetrofitAddRemoveWishlist(String AKey, String Password, final String ItemID, final String WishlistFlag){
        //showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("ItemID", ItemID);
        params.put("WishlistFlag", WishlistFlag);
        Call<ResponseAddOrRemoveDataset> call = apiService.getInventoryStatusForWishListAddOrDelete(params);
        call.enqueue(new Callback<ResponseAddOrRemoveDataset>() {
            @Override
            public void onResponse(Call<ResponseAddOrRemoveDataset> call, retrofit2.Response<ResponseAddOrRemoveDataset> response) {
                try {
                    int status = response.body().getStatus();
                    String msg = response.body().getMsg();
                    if (status == 1) {
                        if(WishlistFlag.equals("1")){
                            Toast.makeText(context, "Item add to wishlist", Toast.LENGTH_SHORT).show();
                            StaticValues.totalWishlistCount++;
                            Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                        }else if(WishlistFlag.equals("0")){
                            Toast.makeText(context, "Item remove to wishlist", Toast.LENGTH_SHORT).show();
                            StaticValues.totalWishlistCount--;
                            Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                        }
                    } else {
                        Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                    }
                    //}
                } catch (Exception e) {
                    Log.e(TAG, "Wishlist Add Remove Exception:" + e.getMessage());
                }
                //hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseAddOrRemoveDataset> call, Throwable t) {
                Log.e(TAG, "Wishlist Add Remove Failure:" + t.toString());
                Toast.makeText(context, "Wishlist Add Remove Failure", Toast.LENGTH_LONG).show();
                //hidepDialog();
            }
        });
    }
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }

    private void CallVolleyStyleOrBarcodeSearch(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String Barcode,final String OrderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemDetailsForBooking", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        StaticValues.ColorWise = 0;
                        List<Map<String,String>> mapList = new ArrayList<>();
                        List<Map<String,String>> mapListSizeSet = new ArrayList<>();
                        JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
                        String MainGroup = jsonObjectResult.getString("MainGroup");
                        String MainGroupID = jsonObjectResult.getString("MainGroupID");
                        String GroupName = jsonObjectResult.getString("GroupName");
                        String GroupID = jsonObjectResult.getString("GroupID");
                        String SubGroup = jsonObjectResult.getString("SubGroup");
                        String SubGroupID = jsonObjectResult.getString("SubGroupID");
                        String ItemID = jsonObjectResult.getString("ItemID");
                        String ItemName = jsonObjectResult.getString("ItemName");
                        String ItemCode = jsonObjectResult.getString("ItemCode");
                        int SubItemApplicable = jsonObjectResult.getInt("SubItemApplicable");
                        int MdApplicable = jsonObjectResult.getInt("MdApplicable");
                        StaticValues.MDApplicable = MdApplicable;
                        StaticValues.SubItemApplicable = SubItemApplicable;
                        StaticValues.StockCheck = jsonObjectResult.getInt("StockCheck");
                        if (MdApplicable == 1){
                            //TODO: Multi Details only
                            //TODO SizeSet Array
                            JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                            //System.out.println("SizeSet:"+jsonArraySizeSet);
                            //TODO Details Array
                            JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                            //System.out.println("Details:"+jsonArrayDetails);
                            for (int i=0; i< jsonArrayDetails.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                map.put("MainGroup", MainGroup);
                                map.put("MainGroupID", MainGroupID);
                                map.put("GroupName", GroupName);
                                map.put("GroupID", GroupID);
                                map.put("SubGroup", SubGroup);
                                map.put("SubGroupID", SubGroupID);
                                map.put("ItemID", ItemID);
                                map.put("ItemName", ItemName);
                                map.put("ItemCode", ItemCode);
                                map.put("SubItemApplicable", String.valueOf(SubItemApplicable));
                                map.put("MdApplicable", String.valueOf(MdApplicable));
                                map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
                                map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
                                map.put("ColorCode", jsonArrayDetails.getJSONObject(i).getString("ColorCode"));
                                map.put("Size", jsonArrayDetails.getJSONObject(i).getString("Size"));
                                map.put("SizeSequence", jsonArrayDetails.getJSONObject(i).getString("SizeSequence"));
                                map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
                                map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
                                map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                    map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                    map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                    map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                    map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                    map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                    map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                }
                                mapList.add(map);
                            }
                            //TODO: Size set
                            for (int i=0; i<jsonArraySizeSet.length(); i++){
                                Map<String,String> map = new HashMap<>();
                                map.put("MainGroupID", MainGroupID);
                                map.put("GroupID", GroupID);
                                map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                mapListSizeSet.add(map);
                            }
                            context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                            DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                            DBOrder.deleteTablesData();
                            DBOrder.deleteOutOfStockTablesData();
                            DBOrder.insertOrderBookingTable(mapList);
                            DBOrder.insertSizeSetTable(mapListSizeSet);
                            hidepDialog();

                            Intent intent = new Intent(context,OrderBookingActivity.class);
                            context.startActivity(intent);
//                            getActivity().finish();
                        }else {
                            //TODO: SubItem Only
                            if (SubItemApplicable == 1){
                                //TODO SizeSet Array
                                JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                                //System.out.println("SizeSet:"+jsonArraySizeSet);
                                //TODO Details Array
                                JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                                //System.out.println("Details:"+jsonArrayDetails);
                                for (int i=0; i< jsonArrayDetails.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("MainGroup", MainGroup);
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupName", GroupName);
                                    map.put("GroupID", GroupID);
                                    map.put("SubGroup", SubGroup);
                                    map.put("SubGroupID", SubGroupID);
                                    map.put("ItemID", ItemID);
                                    map.put("ItemName", ItemName);
                                    map.put("ItemCode", ItemCode);
                                    map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                    map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                    map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                    map.put("SubItemID", jsonArrayDetails.getJSONObject(i).getString("SubItemID"));
                                    map.put("SubItemName", jsonArrayDetails.getJSONObject(i).getString("SubItemName"));
                                    map.put("SubItemCode", jsonArrayDetails.getJSONObject(i).getString("SubItemCode"));
                                    map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                    map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                    map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                    map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                    map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                        map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                        map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                        map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                        map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                        map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                        map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                    }
                                    mapList.add(map);
                                }
                                //TODO: Size set
                                for (int i=0; i<jsonArraySizeSet.length(); i++){
                                    Map<String,String> map = new HashMap<>();
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupID", GroupID);
                                    map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                    map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                    mapListSizeSet.add(map);
                                }
                                context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                                DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                                DBOrder.deleteTablesData();
                                DBOrder.insertOrderBookingSubItemTable(mapList);
                                DBOrder.insertSizeSetTable(mapListSizeSet);
                                hidepDialog();

                                Intent intent = new Intent(context,SubItemActivity.class);
                                context.startActivity(intent);
                                //getActivity().finish();
                            }else{
                                //TODO: Without SubItem Or Item only
                                //TODO SizeSet Array
                                JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                                //System.out.println("SizeSet:"+jsonArraySizeSet);
                                //TODO Details Array
                                JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                                //System.out.println("Details:"+jsonArrayDetails);
                                for (int i=0; i< jsonArrayDetails.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("MainGroup", MainGroup);
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupName", GroupName);
                                    map.put("GroupID", GroupID);
                                    map.put("SubGroup", SubGroup);
                                    map.put("SubGroupID", SubGroupID);
                                    map.put("ItemID", ItemID);
                                    map.put("ItemName", ItemName);
                                    map.put("ItemCode", ItemCode);
                                    map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                    map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                    map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                    map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                    map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                    map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                    map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                    map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                        map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                        map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                        map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                        map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                        map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                        map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                    }
                                    mapList.add(map);
                                }
                                //TODO: Size set
                                for (int i=0; i<jsonArraySizeSet.length(); i++){
                                    Map<String,String> map = new HashMap<>();
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupID", GroupID);
                                    map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                    map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                    mapListSizeSet.add(map);
                                }
                                context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                                DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                                DBOrder.deleteTablesData();
                                DBOrder.insertOrderBookingWithoutSubItemTable(mapList);
                                DBOrder.insertSizeSetTable(mapListSizeSet);
                                hidepDialog();

                                Intent intent = new Intent(context,WithoutSubItemActivity.class);
                                context.startActivity(intent);
                                //getActivity().finish();
                            }
                        }
                        //System.out.println("List:"+mapList.toString());
                        //System.out.println("ListSizeSet:"+mapListSizeSet.toString());
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        hidepDialog();
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    hidepDialog();
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("Barcode", Barcode);
                params.put("OrderID", OrderID);
                Log.d(TAG,"Search barcode or style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
