package com.example.keeps_000.clearer.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keeps_000.clearer.R;

import java.util.List;

/**
 * Created by keeps_000 on 2018/4/16.
 */

public class ImageAdapter extends ArrayAdapter {
    private final int resourceId;

    public ImageAdapter(Context context, int textViewResourceId, List<Image> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Image image = (Image) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView imageimage = (ImageView) view.findViewById(R.id.image_image);
        TextView imagename = (TextView) view.findViewById(R.id.image_name);
        imageimage.setImageBitmap(image.getBitmap());
        imagename.setText(image.getName());
        return view;
    }
}
