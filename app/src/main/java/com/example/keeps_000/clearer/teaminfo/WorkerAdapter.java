package com.example.keeps_000.clearer.teaminfo;

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
 * Created by keeps_000 on 2018/4/17.
 */

public class WorkerAdapter extends ArrayAdapter {
    private final int resourceId;


    public WorkerAdapter(Context context, int textViewResourceId, List<Worker> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Worker worker = (Worker) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView workerimage = (ImageView) view.findViewById(R.id.worker_image);
        TextView workername = (TextView) view.findViewById(R.id.worker_name);
        workerimage.setImageBitmap(worker.getBitmap());
        workername.setText(worker.getName());
        return view;
    }
}
