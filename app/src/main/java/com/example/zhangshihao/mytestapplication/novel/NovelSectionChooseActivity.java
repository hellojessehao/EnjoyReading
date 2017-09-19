package com.example.zhangshihao.mytestapplication.novel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhangshihao.mytestapplication.BaseActivity;
import com.example.zhangshihao.mytestapplication.R;
import com.example.zhangshihao.mytestapplication.novel.model.NovelInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangshihao on 2017/9/13.
 */

public class NovelSectionChooseActivity extends BaseActivity {

    private ImageView ivImg;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvUpdateTime;
    private RecyclerView mRecyclerView;

    private String img;
    private String title;
    private String author;
    private String updateTime;
    private String htmlStr;
    private String baseAddress;

    private List<NovelInfo> infos;

    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_section_choose);
        mLoadingDialog = createLoadingDialog(this);
        mLoadingDialog.show();
        final Intent readIntent = getIntent();
        if(readIntent != null){
            baseAddress = readIntent.getStringExtra("sections_url");
        }
        init();
        getHtmlStrAndParseSectionsHtml();
    }
    //init elements
    private void init(){
        ivImg = (ImageView) findViewById(R.id.iv_section_choose);
        tvTitle = (TextView) findViewById(R.id.tv_section_choose_title);
        tvAuthor = (TextView) findViewById(R.id.tv_section_choose_author);
        tvUpdateTime = (TextView) findViewById(R.id.tv_section_choose_updateTime);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_section_choose);
    }

    private void getHtmlStrAndParseSectionsHtml(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(baseAddress).build();
                Call call = new OkHttpClient().newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        logd(e.getMessage());
                        showShortToast(NovelSectionChooseActivity.this,"网络请求失败，请稍后再试");
                        Looper.prepare();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        htmlStr = new String(response.body().bytes(), Charset.forName("GBK"));
                        parseSectionsHtml();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(NovelSectionChooseActivity.this).load(img).into(ivImg);
                                tvTitle.setText(title);
                                tvAuthor.setText(author);
                                tvUpdateTime.setText(updateTime);
                                mRecyclerView.setLayoutManager(new GridLayoutManager(NovelSectionChooseActivity.this,2));
                                mRecyclerView.addItemDecoration(new DividerItemDecoration(NovelSectionChooseActivity.this,DividerItemDecoration.VERTICAL));
                                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                MyAdapter adapter = new MyAdapter();
                                mRecyclerView.setAdapter(adapter);
                                adapter.setOnClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View itemView, int position) {
                                        Intent readIntent = new Intent(NovelSectionChooseActivity.this, NovelReadActivity.class);
                                        readIntent.putExtra("novel_title",infos.get(position).getTitle());
                                        readIntent.putExtra("section_url",infos.get(position).getUrl());
                                        readIntent.putExtra("base_url",baseAddress);
                                        startActivity(readIntent);
                                    }
                                });
                                mLoadingDialog.cancel();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void parseSectionsHtml(){
        try{
            infos = new ArrayList<NovelInfo>();
            Document document = Jsoup.parse(htmlStr);
            img = "http://www.37zw.net".concat(document.select("div.box_con").select("div#sidebar").select("img[alt]").attr("src"));
            logw("img = "+img);
            title = document.select("div.box_con").select("div#sidebar").select("img[alt]").attr("alt");
            logw("\n title = "+title);
            author = document.select("div.box_con").select("div#maininfo").select("p").get(0).text();
            logw("\n author = "+author);
            updateTime = document.select("div.box_con").select("div#maininfo").select("p").get(2).text();
            logw("\n updateTime = "+updateTime);
            Elements list = document.select("div.box_con").get(1).select("dd");
            Elements links = list.select("a[href]");
            logd(htmlStr+"sections size = "+String.valueOf(links.size()));
            for(Element e : links){
                NovelInfo novel = new NovelInfo();
                novel.url = baseAddress.concat(e.attr("href"));
                novel.title = e.text();
                infos.add(novel);
            }
        }catch (Exception e){
            logd("parse html fail : \n"+e.getMessage());
            showShortToast(this, R.string.html_parse_err);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private OnItemClickListener clickListener;

        public void setOnClickListener(OnItemClickListener listener){
            this.clickListener = listener;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
              R.layout.recycler_section_item,null,false
            );
            MyViewHolder holder = new MyViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tvSection.setText(infos.get(position).getTitle());

            if(clickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        clickListener.onItemClick(holder.itemView,position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tvSection;

            public MyViewHolder(View itemView){
                super(itemView);
                tvSection = (TextView) itemView.findViewById(R.id.tv_section_item);
            }
        }
    }

    private interface OnItemClickListener{
        void onItemClick(View itemView,int position);
    }

}
