package com.example.zhangshihao.mytestapplication.novel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zhangshihao.mytestapplication.BaseActivity;
import com.example.zhangshihao.mytestapplication.Constant;
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
 * Created by zhangshihao on 2017/9/7.
 */

public class NovelResultActivity extends BaseActivity {

    private RecyclerView novelRecycler;

    private List<NovelInfo> infos;
    private int clickPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_result);
        init();
        final Intent novelIntent = getIntent();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (novelIntent == null) {
                    return;
                }
                parseHtml(novelIntent.getStringExtra("novelBody"));
                novelRecycler.setLayoutManager(new LinearLayoutManager(NovelResultActivity.this));
                novelRecycler.addItemDecoration(new DividerItemDecoration(NovelResultActivity.this,DividerItemDecoration.VERTICAL));
                novelRecycler.setItemAnimator(new DefaultItemAnimator());
                MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter();
                novelRecycler.setAdapter(myRecyclerAdapter);
                myRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        String url = infos.get(position).getUrl();
                        /*
                        NovelResultActivity.this.clickPosition = position;
                        requestNovelSections(url);*/
                        Intent readIntent = new Intent(NovelResultActivity.this,NovelSectionChooseActivity.class);
                        readIntent.putExtra("sections_url",url);
                        startActivity(readIntent);
                        logw("result activity : url = "+url);
                    }
                });

            }
        });

    }
    //request for novel sections
    private void requestNovelSections(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                Call call = new OkHttpClient().newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        showShortToast(NovelResultActivity.this,"网络请求失败，请稍后再试");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String html = new String(response.body().bytes(),Charset.forName("GBK"));
                        Intent readIntent = new Intent(NovelResultActivity.this,NovelSectionChooseActivity.class);
                        NovelInfo info = infos.get(clickPosition);
                        readIntent.putExtra("sections_html",html);
                        readIntent.putExtra("sections_img",info.getImg());
                        readIntent.putExtra("sections_title",info.getTitle());
                        readIntent.putExtra("sections_author",info.getAuthor());
                        readIntent.putExtra("sections_updateTime",info.getUpdateTime());
                        readIntent.putExtra("sections_url",info.getUrl());
                        logd("position = "+String.valueOf(clickPosition)+
                        "\n img = "+info.getImg()+"\n title = "+info.getTitle()+
                        "\n author = "+info.getAuthor()+"\n updateTime = "+info.updateTime);
                        startActivity(readIntent);
                    }
                });
            }
        }).start();
    }

    //init widget&elements
    private void init() {
        novelRecycler = (RecyclerView) findViewById(R.id.novel_result_recycler);
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>{

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mOnItemClickListener = listener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(NovelResultActivity.this)
                    .inflate(R.layout.recycler_novel_item,null,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            if(!TextUtils.isEmpty(infos.get(position).getImg())){
                setImageByNetAddress(holder.img,infos.get(position).getImg());
            }
            holder.title.setText(infos.get(position).getTitle());
            holder.intro.setText(infos.get(position).getIntro());
            holder.author.setText(infos.get(position).getAuthor());
            holder.category.setText(infos.get(position).getCategory());
            holder.updateTime.setText(infos.get(position).getUpdateTime());
            //set onclicklistener @{
            if(mOnItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView,position);
                    }
                });
            }//@}
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private ImageView img;
            private TextView title;
            private TextView intro;
            private TextView author;
            private TextView category;
            private TextView updateTime;

            public MyViewHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.img_novel);
                title = (TextView) itemView.findViewById(R.id.tv_title);
                intro = (TextView) itemView.findViewById(R.id.tv_intro);
                author = (TextView) itemView.findViewById(R.id.tv_author);
                category = (TextView) itemView.findViewById(R.id.tv_category);
                updateTime = (TextView) itemView.findViewById(R.id.tv_updateTime);
            }
        }
    }

    //for watch recyclerview click event
    private interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    private void setImageByNetAddress(ImageView iv,String url){
        Glide.with(this).load(url).into(iv);
    }

    //parse search result html
    private void parseHtml(String htmlStr) {
        try {
            infos = new ArrayList<NovelInfo>();
            Document js = Jsoup.parse(htmlStr);
            Elements novelHtmls = js.select("div.novellist").select("li");
            logd(" list size = "+novelHtmls.size());
            for (Element e : novelHtmls) {
                NovelInfo novel = new NovelInfo();
                novel.url = Constant.BASE_URL.concat(e.select("a[href]").attr("href"));
                novel.novelId = Math.abs(novel.url.hashCode());
                novel.title = e.select("a[href]").text();
                novel.author = e.text();
                infos.add(novel);
                logd(novel.toString());
            }
        } catch (Exception e) {
            logd("html parse err : \n"+e.getMessage());
            showShortToast(this,R.string.html_parse_err);
        }
    }

}
