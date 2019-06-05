package hr.ferit.tomislavrekic.firstgrademathcheat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HelpAdapter extends FragmentPagerAdapter {

    private List<HelpItem> mData;

    public void setmData(List<HelpItem> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public HelpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return HelpFragment.newInstance(mData.get(i));
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
