package com.example.keeps_000.clearer.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.teaminfo.Worker;
import com.example.keeps_000.clearer.teaminfo.WorkerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TeamInfoActivity extends AppCompatActivity {

    private Button btn_back;
    private WorkerAdapter workerAdapter;
    private List<Worker> workerList = new ArrayList<Worker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);

        Log.i("test","1");
        initWorkers();
        Log.i("test","2");
        workerAdapter = new WorkerAdapter(TeamInfoActivity.this,R.layout.worker_item,workerList);
        Log.i("test","3");
        ListView listView = (ListView) findViewById(R.id.team_info_list_view);
        Log.i("test","4");
        listView.setAdapter(workerAdapter);
        Log.i("test","5");

        btn_back = findViewById(R.id.btn_back_5);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initWorkers() {
        Bitmap manbitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.man);
        Bitmap womanbitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.woman);
        Worker worker7 = new Worker("MingQiu,指导老师",manbitmap);
        workerList.add(worker7);
        Worker worker8 = new Worker("LitingJing,深度学习算法工程师",womanbitmap);
        workerList.add(worker8);
        Worker worker9 = new Worker("YingWang,深度学习算法工程师",womanbitmap);
        workerList.add(worker9);
        Worker worker = new Worker("Shiney,安卓工程师",manbitmap);
        workerList.add(worker);
        Worker worker1 = new Worker("MengyangYang,服务器工程师",manbitmap);
        workerList.add(worker1);
        Worker worker2 = new Worker("JingcaoHong,服务器工程师",manbitmap);
        workerList.add(worker2);
        Worker worker5 = new Worker("JiehangZeng,深度学习算法工程师",manbitmap);
        workerList.add(worker5);
        Worker worker3 = new Worker("YanggangWang,深度学习算法工程师",manbitmap);
        workerList.add(worker3);
        Worker worker4 = new Worker("RunpuZhang,深度学习算法工程师",manbitmap);
        workerList.add(worker4);
        Worker worker6 = new Worker("YangZheng,深度学习算法工程师",womanbitmap);
        workerList.add(worker6);
        Log.i("list长度",""+workerList.size());
    }
}
