package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static hr.ferit.tomislavrekic.firstgrademathcheat.Constants.*;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;

    private BroadcastReceiver receiver;
    private IntentFilter filter;

    private Intent intent;

    private ImageView imageView;
    private TextView textView;
    private Button switchToCamera;

    ActionBarDrawerToggle toggle;

    //private String input = "eighty three plus seven minus three equals";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        initViews();
        initBtnListeners();
        initIntents();

        initNavBar();

        configureReceiver();
    }

    private void initNavBar() {
        DrawerLayout drawerLayout = findViewById(R.id.dlDrawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.NavOpen, R.string.NavClose);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nvNav);

        ImageView image = navigationView.getHeaderView(0).findViewById(R.id.ivNavImage);

        InputStream stream = null;
        AssetManager assetManager = getAssets();

        try{
            stream = assetManager.open(Constants.APP_LOGO);
        }
        catch (IOException e){
            Log.e(TAG, "getImages: " + e.toString());
        }

        if(stream != null){
            Bitmap temp = BitmapFactory.decodeStream(stream);
            Bitmap scaled = Bitmap.createScaledBitmap(temp, temp.getWidth()/2,temp.getHeight()/2,true);
            image.setImageBitmap(scaled);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch(id)
                {
                    case R.id.Inav1:
                        Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent1, 0);
                        break;
                    case R.id.Inav2:
                        Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent2, 0);
                        break;
                    case R.id.Inav3:
                        Toast.makeText(MainActivity.this, "Tres", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        return true;
                }


                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void startOCR(){
        displayLoading();

        new FetchImageTask(new FetchImageResponse() {
            @Override
            public void processFinish(Bitmap output) {
                displayImage(output);
                toFireBase(output);
                Log.d(TAG, "processFinish: imageRES" + output.getWidth() + "x" + output.getHeight());
            }
        }, this).execute(TEMP_IMAGE_KEY);

    }

    private void toFireBase(Bitmap output) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(output);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result = detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String resultStr = firebaseVisionText.getText();

                displayResults(resultStr);

                int result = 0;
                try{
                    result = WordEquationParser.parser(resultStr);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                textView.append("\n\n"+ result);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        displayResults(e.toString());

                    }
                });
    }

    private void configureReceiver() {
        filter = new IntentFilter();
        filter.addAction(BROADCAST_KEY1);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startOCR();
            }
        };
        receiverReg();
    }

    public void receiverReg(){
        registerReceiver(receiver,filter);
    }
    public void receiverUnreg(){
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void initIntents() {
        intent = new Intent(this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    private void initViews() {
        imageView = findViewById(R.id.ivPreview);
        textView = findViewById(R.id.tvGuess);
        switchToCamera = findViewById(R.id.btnSwitch);
        switchToCamera.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: check");

        switchToCamera.setEnabled(true);
    }

    private void initBtnListeners() {
        switchToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    switchToCamera.setEnabled(false);
                    startActivityIfNeeded(intent,0);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverUnreg();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new DeleteImageTask().execute();
        Log.d(TAG, "onStop: check");
    }

    class DeleteImageTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            File dir = getFilesDir();
            File file = new File(dir,Constants.TEMP_IMAGE_KEY);
            if (!file.exists()){
                return null;
            }
            file.delete();
            return null;
        }
    }

    private void displayImage(Bitmap image) {
        imageView.setImageBitmap(image);
    }

    private void displayLoading() {
        textView.setText("Reading...");
    }


    private void displayResults(String result) {
        textView.setText(result);
    }

    private Bitmap GetImage() {
        Bitmap bitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(this.openFileInput(Constants.TEMP_IMAGE_KEY));
            //TODO: could scale the image before saving it
            return Bitmap.createScaledBitmap(bitmap,Constants.DIM_IMG_SIZE_X,Constants.DIM_IMG_SIZE_Y, true);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
