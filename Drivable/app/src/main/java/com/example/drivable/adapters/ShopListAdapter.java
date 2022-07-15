package com.example.drivable.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.drivable.R;
import com.example.drivable.data_objects.Shop;

import java.util.ArrayList;

public class ShopListAdapter extends BaseAdapter {

    private final String TAG = "ShopListAdapter.TAG";

    //base id
    private static final long BASE_ID = 0x1011;

    //ref screen
    private final Context context;

    //ref collection
    private final ArrayList<Shop> shops;

    public ShopListAdapter(Context _context, ArrayList<Shop> _shops){
        context = _context;
        shops = _shops;
    }


    @Override
    public int getCount() {
        if(shops != null){
            return shops.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(shops != null && position >= 0 && position < shops.size()){
            return shops.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        String shopName = shops.get(position).getNickname();
        String shopAddressLine2 = shops.get(position).getAddressLine2();

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.base_shop_list_adapter, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder) convertView.getTag();
        }

        if (shopName != null && shopAddressLine2 != null){
            // *** SET VIEWHOLDER HERE ***

            Log.i(TAG, "getView: " + shopName);
            vh.ba_tv_main.setText(shopName);
            vh.ba_tv_sub.setText(shopAddressLine2);
        }

        return convertView;
    }

    //view holder
    static class ViewHolder{
        final TextView ba_tv_main;
        final TextView ba_tv_sub;

        public ViewHolder(View _layout){
            ba_tv_main = _layout.findViewById(R.id.ba_shop_list_tv_main);
            ba_tv_sub = _layout.findViewById(R.id.ba_shop_list_tv_sub);
        }
    }

}
