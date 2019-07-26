package albumCreation;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import data.Album;
import data.AppDatabase;
import pt.ulisboa.tecnico.cmov.p2photo.R;
import userListRecyclerView.UserListController;

public class AlbumCreationViewPagerAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    private int[] layouts;
    Activity parentActivity;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button buttonBack, buttonNext;
    private TextView[] dots;
    private View userListsContainer;

    private UserListController userListController;
    private EditText viewAlbumTitle;
    private Album album;

    public AlbumCreationViewPagerAdapter(Activity parentActivity, final ViewPager viewPager, Album album) {
        super();
        this.parentActivity = parentActivity;
        this.viewPager = viewPager;
        this.album = album;

        buttonBack = (Button) parentActivity.findViewById(R.id.btn_back);
        buttonNext = (Button) parentActivity.findViewById(R.id.btn_next);
        dotsLayout = (LinearLayout) parentActivity.findViewById(R.id.layoutDots);

        // layouts of all album creation fragments
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.album_creation_title_fragment,
                //TODO put photo adding layout
        };
        setButtonsListeners(parentActivity.getApplicationContext());

        addBottomDots(0);
        //TODO get info for album creation

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) parentActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View view = layoutInflater.inflate(layouts[position], container, false);


        if (position == 0)
            setViewsTitleSetting(view);

        if (position == 1)
            setUserAddingView(view);


        container.addView(view);


        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;

        if (position == 0)
            album.setTitle(viewAlbumTitle.getText().toString());

        container.removeView(view);
    }

    protected void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        Context context = parentActivity.getApplicationContext();

        int activeColor = context.getResources().getColor(R.color.dot_active);
        int inactiveColor = context.getResources().getColor(R.color.dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(inactiveColor);
            dotsLayout.addView(dots[i]);
        }

        dots[currentPage].setTextColor(activeColor);
    }

    private void setButtonsListeners(final Context context) {

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for first page
                // if first page main screen will be launched
                int current = getItem(-1);
                if (current >= 0){
                    // move to previous screen
                    viewPager.setCurrentItem(current);
                } else {
                    parentActivity.finish();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    //Conclusion of album creation

                    if (viewAlbumTitle != null)
                        album.setTitle(viewAlbumTitle.getText().toString());


                    Album.addAlbumToDb(AppDatabase.getAppDatabase(context), album, context);
                    parentActivity.finish();
                }
            }
        });

    }

    void setButtonsText(int position){
        Context context = parentActivity.getApplicationContext();

        //BackButton
        // First Page
        if (position == 0){
           buttonBack.setText(context.getString(R.string.cancel_step));
        }else{
            buttonBack.setText(context.getString(R.string.back_step));
        }

        //NextButton
        // Last Page
         if (position == layouts.length - 1) {
            buttonNext.setText(context.getString(R.string.confirm_setp));
        } else {
            buttonNext.setText(context.getString(R.string.next_step));
        }

    }

    void setViewsTitleSetting(View view){
        viewAlbumTitle = view.findViewById(R.id.album_title);
    }

    void setUserAddingView(View view){
            userListsContainer = view.findViewById(R.id.container_user_adding);
            viewAlbumTitle = view.findViewById(R.id.album_title);
            userListController = new UserListController(userListsContainer, album);
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}