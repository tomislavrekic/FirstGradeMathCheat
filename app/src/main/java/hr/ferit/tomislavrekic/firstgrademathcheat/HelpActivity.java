package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static hr.ferit.tomislavrekic.firstgrademathcheat.Constants.TAG;

public class HelpActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HelpAdapter adapter;

    private Resources res;
    private AssetManager assetManager;

    private static int helpPagesNum = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        res = getResources();

        adapter = new HelpAdapter(getSupportFragmentManager());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateAdapter(getData());
    }


    private void updateAdapter(List<HelpItem> data) {
        adapter.setmData(data);

        viewPager=findViewById(R.id.vpPager);
        tabLayout = findViewById(R.id.tlTabDots);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager, true);
    }

    private List<HelpItem> getData(){
        List<HelpItem> data = new ArrayList<>();
        String[] helpTexts = res.getStringArray(R.array.HelpTexts);
        assetManager = res.getAssets();
        List<Bitmap> images = getImages();

        for (int i=0;i<helpPagesNum;i++){
            data.add(new HelpItem(images.get(i),helpTexts[i]));
        }

        return data;
    }

    private List<Bitmap> getImages(){
        List<Bitmap> images = new ArrayList<>();

        for (int i =0; i<helpPagesNum; i++){
            InputStream stream = null;
            try{
                stream = assetManager.open(Constants.HELP_IMAGES[i]);
            }
            catch (IOException e){
                Log.e(TAG, "getImages: " + e.toString());
            }

            if(stream == null){
                images.add(null);
            }
            else {
                Bitmap temp = BitmapFactory.decodeStream(stream);
                Bitmap scaled = Bitmap.createScaledBitmap(temp, temp.getWidth()/2,temp.getHeight()/2,true);
                images.add(scaled);
            }
        }
        return images;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
