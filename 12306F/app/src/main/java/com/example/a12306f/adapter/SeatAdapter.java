//package com.example.a12306f.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.example.a12306f.R;
//import com.example.a12306f.ticket.YuDing02;
//import com.example.a12306f.ticket.YuDing03;
//
//import java.util.List;
//import java.util.Map;
//
//public class SeatAdapter extends BaseAdapter {
//
//    private Context context;
//    private List<Map<String,Object>> list02;
//    private LayoutInflater listContaniner;
//
//    public class ViewHolder{
//        public TextView textView_leiixng,textView_num0201,textView_price;
//        public Button button_yuding02;
//    }
//
//    public SeatAdapter(Context context, List<Map<String,Object>>list02){
//        listContaniner = LayoutInflater.from(context);
//        this.context = context;
//        this.list02 = list02;
//    }
//
//    @Override
//    public int getCount() {
//        return list02.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return list02.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (convertView == null){
//            holder = new ViewHolder();
//
//            convertView = listContaniner.inflate(R.layout.item_yuding02,null);
//            holder.textView_leiixng = convertView.findViewById(R.id.textView_leixing02);
//            holder.textView_num0201 = convertView.findViewById(R.id.textView_num0201);
//            holder.textView_price = convertView.findViewById(R.id.textView_price02);
//            holder.button_yuding02 = convertView.findViewById(R.id.button_yuding);
//            convertView.setTag(holder);
//        }else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.textView_leiixng.setText(list02.get(position).get("leixing").toString());
//        holder.textView_num0201.setText(list02.get(position).get("nums").toString());
//        holder.textView_price.setText(list02.get(position).get("price").toString());
//        final ViewHolder finalHolder = holder;
//        holder.button_yuding02.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, YuDing03.class);
////                intent.putExtra("StationName",.getText().toString());
////                intent.putExtra("TicketNo",TicketName.getText().toString());
////                intent.putExtra("TicketDate",DateTitle.getText().toString());
////                intent.putExtra("day",day);
////                intent.putExtra("FromToTime",TicketFromToTime.getText().toString());
////                intent.putExtra("Seat", finalHolder.tvSeatName.getText().toString()+"("+finalHolder.tvSeatNum.getText().toString()+")");
////                intent.putExtra("SeatPrice",finalHolder.tvSeatPrice.getText().toString());
////                startActivity(intent);
////                context.startActivity(intent02);
//            }
//        });
//        return convertView;
//    }
//}
