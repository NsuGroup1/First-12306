package com.example.a12306f.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> data;
    private LayoutInflater inflater;

    public OrderAdapter(Context context, List<Map<String,Object>> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (holder == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_ticket_order,null);
            holder.tvOrderId = view.findViewById(R.id.tv_order);
            holder.tvOrderStatus = view.findViewById(R.id.tv_order_status);
            holder.tvOrderTrainNo = view.findViewById(R.id.tv_order_train_num);
            holder.tvOrderDateFrom = view.findViewById(R.id.tv_order_date);
            holder.tvOrderStation = view.findViewById(R.id.tv_order_station);
            holder.tvOrderPrice = view.findViewById(R.id.tv_order_price);
            holder.imgOrderFlag = view.findViewById(R.id.img_order_flag);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvOrderId.setText(data.get(i).get("orderId").toString());
        holder.tvOrderStatus.setText(data.get(i).get("orderStatus").toString());
        if ("未支付".equals(data.get(i).get("orderStatus").toString())){
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
        }else if ("已支付".equals(data.get(i).get("orderStatus").toString())){
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        holder.tvOrderTrainNo.setText(data.get(i).get("orderTrainNo").toString());
        holder.tvOrderDateFrom.setText(data.get(i).get("orderDateFrom").toString());
        holder.tvOrderStation.setText(data.get(i).get("orderStationFrom").toString());
        holder.tvOrderPrice.setText(data.get(i).get("orderPrice").toString());

        //TODO 判断图片的显示
        Integer resId = (Integer) data.get(i).get("orderFlag");
        if (resId == null){
            holder.imgOrderFlag.setImageDrawable(null);
        }else {
            holder.imgOrderFlag.setImageDrawable(context.getResources().getDrawable(resId));
        }
        return view;
    }
    class ViewHolder{
        TextView tvOrderId;
        TextView tvOrderStatus;
        TextView tvOrderTrainNo;
        TextView tvOrderDateFrom;
        TextView tvOrderStation;
        TextView tvOrderPrice;
        ImageView imgOrderFlag;
    }
}
