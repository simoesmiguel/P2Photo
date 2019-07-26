package albumCreation;

import androidx.viewpager.widget.ViewPager;

public class AlbumCreationViewPagerListener implements ViewPager.OnPageChangeListener {

    private AlbumCreationViewPagerAdapter adapter;

    public AlbumCreationViewPagerListener(AlbumCreationViewPagerAdapter adapter) {
        super();
        this.adapter = adapter;
    }

    //  viewpager change listener

    @Override
    public void onPageSelected(int position) {
        adapter.addBottomDots(position);
        adapter.setButtonsText(position);

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        //todo complete
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        //todo complete
    }
}

