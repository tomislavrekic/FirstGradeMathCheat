package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;

public class FetchImageTask extends AsyncTask<String, Void, Bitmap> {

    public FetchImageResponse mDelegate = null;
    private Context mContext;

    public FetchImageTask(FetchImageResponse delegate, Context context) {
        mDelegate = delegate;
        mContext = context;

    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap temp = GetImage(strings[0]);
        if (temp == null) return null;
        return temp;
    }

    @Override
    protected void onPostExecute(Bitmap buffer) {
        mDelegate.processFinish(buffer);
    }


    private Bitmap GetImage(String imageKey) {
        Bitmap bitmap;

        try {
            bitmap = BitmapFactory.decodeStream(mContext.openFileInput(imageKey));
            //return Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
