package print.godown_group_order_with_rate.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import print.godown_group_order_with_rate.PrintActivity;
import print.godown_group_order_with_rate.model.GroupOrGodown;

/**
 * Created by Rakesh on 29-Aug-17.
 */
public class GroupOrGodownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<GroupOrGodown> mapList;
    //public static List<GroupOrGodown> listGroup = new ArrayList<>();
    private final int GROUP_LIST = 0;
    public GroupOrGodownAdapter(Context context, List<GroupOrGodown> mapList) {
        this.context=context;
        this.mapList=mapList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == GROUP_LIST)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_all_customer_list, parent, false);
            return new GroupOrGodownHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof GroupOrGodownHolder)
        {
            final GroupOrGodownHolder VHholder = (GroupOrGodownHolder)holder;
            final int pos=position;
            setTableLayout(VHholder.tableLayout,mapList.get(pos));
            VHholder.cardView.setTag(R.id.dataset,mapList.get(pos));
            VHholder.cardView.setTag(R.id.position,pos);
            VHholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupOrGodown dataset = (GroupOrGodown)v.getTag(R.id.dataset);
                    Intent intent = new Intent(context,PrintActivity.class);
                    intent.putExtra("Key",dataset);
                    context.startActivity(intent);
                }
            });
            VHholder.checkBox.setVisibility(View.GONE);
            VHholder.checkBox.setTag(R.id.dataset,mapList.get(pos));
            VHholder.checkBox.setTag(R.id.position,pos);
            VHholder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final GroupOrGodown dataset = (GroupOrGodown) buttonView.getTag(R.id.dataset);
                    final int positionTag = (int) buttonView.getTag(R.id.position);
                    if (isChecked == true){
//                        if (listGroup.contains(dataset)) {
//                            listGroup.remove(dataset);
//                            listGroup.add(dataset);
//                        }else{
//                            listGroup.add(dataset);
//                        }
                    }else{
//                        if (listGroup.contains(dataset)){
//                            listGroup.remove(dataset);
//                        }
                    }
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return GROUP_LIST;
    }
    @Override
    public int getItemCount() {
        return mapList==null?0:mapList.size();
    }
    public Object getItem(int position) {
        return mapList.get(position);
    }
    public class GroupOrGodownHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TableLayout tableLayout,tableLayout2,tableLayout3;
        CheckBox checkBox;
        GroupOrGodownHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_multi_customer);
        }
    }
    //TODO: Set Table Layout
    private void setTableLayout(TableLayout tableLayout,GroupOrGodown dataset){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        if (dataset.getFlag() == 0) {
            //TODO: 1th Row
            View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Godown",dataset.getGroupOrGodownName() == null ? "" : dataset.getGroupOrGodownName()));

            //TODO: 2nd Row
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Godown Type",dataset.getMainGroup() == null ? "" : dataset.getMainGroup()));
        }else if (dataset.getFlag() == 1 || dataset.getFlag() == 2){
            //TODO: 1th Row
            View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Group",dataset.getGroupOrGodownName() == null ? "" : dataset.getGroupOrGodownName()));
            //TODO: 2nd Row
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Main Group",dataset.getMainGroup() == null ? "" : dataset.getMainGroup()));
        }
    }
}
