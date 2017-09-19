package com.example.zhangshihao.mytestapplication.novel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhangshihao.mytestapplication.BaseActivity;
import com.example.zhangshihao.mytestapplication.R;
import com.example.zhangshihao.mytestapplication.novel.model.NovelOpenHelper;
import com.example.zhangshihao.mytestapplication.novel.model.SectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshihao on 2017/9/15.
 */

public class BookMarkActivity extends BaseActivity {

    private TextView tvBookmarkNull;
    private RecyclerView mRecyclerView;

    private List<SectionInfo> sectionInfos;
    private Cursor cursorAllData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        init();
        cursorAllData = NovelOpenHelper.getInstance(this).selectAllData();
        if(cursorAllData != null && cursorAllData.getCount()!=0){
            tvBookmarkNull.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            while(cursorAllData.moveToNext()){
                SectionInfo info = new SectionInfo();
                info.novelName = cursorAllData.getString(1);
                info.name = cursorAllData.getString(2);
                info.url = cursorAllData.getString(3);
                logd("novelName = "+info.novelName+"\n name = "+info.name+
                "\n url = "+info.url);
                sectionInfos.add(info);
            }
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            final MyAdapter mAdapter = new MyAdapter();
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    Intent readIntent = new Intent(BookMarkActivity.this,NovelReadActivity.class);
                    readIntent.putExtra("section_url",sectionInfos.get(position).url);
                    logw("readintent : url = "+sectionInfos.get(position).url);
                    startActivity(readIntent);
                    finish();
                }

                @Override
                public void onItemLongClick(View itemView, final int position) {
                    AlertDialog.Builder removeDialog = new AlertDialog.Builder(BookMarkActivity.this)
                            .setMessage("确定删除该书签吗？")
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NovelOpenHelper.getInstance(BookMarkActivity.this).deleteData(sectionInfos.get(position).url);
                                    sectionInfos.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    if(sectionInfos.size() == 0){
                                        tvBookmarkNull.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.GONE);
                                    }
                                }
                            });
                    removeDialog.show();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private void init(){
        sectionInfos = new ArrayList<SectionInfo>();
        tvBookmarkNull = (TextView) findViewById(R.id.tv_bookmark_null);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_bookmark);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener clickListener){
            this.onItemClickListener = clickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(BookMarkActivity.this)
            .inflate(R.layout.recycler_bookmark_item,null,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.novelName.setText(sectionInfos.get(position).novelName);
            holder.sectionName.setText(sectionInfos.get(position).name);
            if(onItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView,position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView,position);
                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return sectionInfos.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView novelName;
            private TextView sectionName;

            public MyViewHolder(View itemView) {
                super(itemView);
                novelName = (TextView) itemView.findViewById(R.id.tv_bookmark_item_novelname);
                sectionName = (TextView) itemView.findViewById(R.id.tv_bookmark_item_sectionname);
            }
        }
    }

    private interface OnItemClickListener{
        void onItemClick(View itemView,int position);
        void onItemLongClick(View itemView,int position);
    }

}
