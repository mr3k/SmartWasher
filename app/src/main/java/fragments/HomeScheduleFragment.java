package fragments;

import com.washer.smart.smartwasher.R;

/**
 * Created by xxkarlue on 2015-04-13.
 */
public class HomeScheduleFragment extends BaseFragment {



    public static BaseFragment create() {
        HomeScheduleFragment fragment = new HomeScheduleFragment();
        fragment.setArguments(createBundle(R.layout.layout_home_content, "HEM"));
        return fragment;
    }

}
