package com.cat.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cat.R;
import com.cat.adapter.TravelViewPagerAdapter;
import com.cat.entity.BookJson;
import com.cat.entity.Travel;
import com.cat.view.BlurImageview;
import com.qslll.library.ExpandingPagerFactory;
import com.qslll.library.fragments.ExpandingFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.cat.activity.MainActivity.stringToList;

/**
 * Created by 雨晨 on 2017/5/15.
 */

public class BookOptionActivity extends AppCompatActivity implements ExpandingFragment.OnExpandingClickListener{

    ViewPager viewPager;
    ViewGroup back;
    ImageView backPic;
    List<Travel> travels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookoption);
        setupWindowAnimations();
        intiView();
        initData();
        TravelViewPagerAdapter adapter = new TravelViewPagerAdapter(getSupportFragmentManager());
        adapter.addAll(travels);
        viewPager.setAdapter(adapter);


        ExpandingPagerFactory.setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        backPic.setImageBitmap(BlurImageview.getBlurBitmap(BookOptionActivity.this,bitmap,15));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                };

                Picasso.with(BookOptionActivity.this).load(travels.get(position).getImage()).into(target);

                ExpandingFragment expandingFragment = ExpandingPagerFactory.getCurrentFragment(viewPager);
                if(expandingFragment != null && expandingFragment.isOpenend()){
                    expandingFragment.close();
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void intiView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        back = (ViewGroup) findViewById(R.id.back);
        backPic = (ImageView) findViewById(R.id.background);
    }


    private void initData() {
        travels = new ArrayList<>();
        Intent intent = getIntent();
        try {
            JSONObject serverJson = new JSONObject(intent.getStringExtra("serverResponse"));
            List<BookJson> book = stringToList(serverJson.getJSONArray("obj").toString(),BookJson.class);
            for (int i =0;i< book.size();i++){
                Travel t = new Travel();
                BookJson b = book.get(i);
                t.setAuthor(b.getAuthor());
                t.setStoreID(b.getStoreId()+"");
                t.setBookStoreMessage(b.getStoreDescribe());
                t.setBookStoreUserID(b.getUserId()+"");
                t.setHeadPic(b.getHeadPic());
                t.setIsbn(b.getIsbn());
                t.setImage(b.getBookImageURI());
                t.setRating(b.getRating());
                t.setPrice(b.getPrice());
                t.setDoubanURL(b.getDouBanURI());
                t.setPublisher(b.getPublisher());
                t.setPages(b.getPage());
                t.setUserName(b.getUserName());
                t.setBinds(b.getBinding());
                t.setBookid(b.getBookId()+"");
                travels.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(!ExpandingPagerFactory.onBackPressed(viewPager)){
            super.onBackPressed();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Explode slideTransition = new Explode();
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
    }

    @SuppressWarnings("unchecked")
    private void startInfoActivity(View view, Travel travel) {
        Activity activity = this;
        ActivityCompat.startActivity(activity,
                InfoActivity.newInstance(activity, travel),
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        new Pair<>(view, getString(R.string.transition_image)))
                        .toBundle());
    }

    @Override
    public void onExpandingClick(View v) {
        //v is expandingfragment layout
        View view = v.findViewById(R.id.image);
        Travel travel = travels.get(viewPager.getCurrentItem());
        startInfoActivity(view,travel);
    }


}
