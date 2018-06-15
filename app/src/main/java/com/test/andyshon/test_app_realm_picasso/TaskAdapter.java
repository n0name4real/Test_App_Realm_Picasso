package com.test.andyshon.test_app_realm_picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by andyshon on 12.05.18.
 */

public class TaskAdapter extends RealmBaseAdapter<Item> implements ListAdapter {
    private OrderedRealmCollection<Item> tasks;

    private static class ViewHolder {
        TextView taskName;
        ImageView ivCover;
    }

    TaskAdapter(OrderedRealmCollection<Item> data) {
        super(data);
        this.tasks = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.ivCover = convertView.findViewById(R.id.ivCover);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Item item = adapterData.get(position);
            viewHolder.taskName.setText(item.getName());
            Bitmap bitmap = BitmapFactory.decodeByteArray(item.getBitmap(), 0, item.getBitmap().length);
            if (bitmap == null){
                viewHolder.ivCover.setImageResource(R.drawable.ic_launcher_foreground);
            }
            else {
                viewHolder.ivCover.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }
}
