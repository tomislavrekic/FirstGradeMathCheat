package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.graphics.Bitmap;

public class HelpItem {
    private Bitmap image;
    private String text;

    public HelpItem(Bitmap image, String text) {
        this.image = image;
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
