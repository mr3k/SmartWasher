package fragments;

import android.util.Log;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.washer.smart.smartwasher.R;

import java.util.Calendar;

import dialogs.CustomDialog;
import dialogs.WasherProgramDialog;
import extra.LiveData;
import extra.Storage;
import extra.WasherInfo;
import model.StartStatus;
import sdk.CallBack;
import sdk.WasherError;
import sdk.WasherService;

import model.LiveRecord;
import sdk.CallBack;
import sdk.WasherError;
import sdk.WasherService;

/**
 * Created by xxkarlue on 2015-04-13.
 */
public class HomeSleepFragment extends BaseFragment {
    LinearLayout scheduleButton, startNowButton;
    String programName, degreeName;

    @Override
    protected void init(View view) {
        scheduleButton = (LinearLayout) view.findViewById(R.id.ll_home_sleep_schedule);
        startNowButton = (LinearLayout) view.findViewById(R.id.ll_home_sleep_start);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.saveBoolean(Storage.FROM_START, true);
                MyViewPager.getInstance().setCurrentItem(MyViewPager.START,false);
            }
        });

        startNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNow();


            }
        });
    }





    private void startNow(){
        WasherProgramDialog wpd = new WasherProgramDialog(getActivity());

        wpd.bindOK(new CustomDialog.Callback() {
            @Override
            public void run(int[] ids, String[] tags) {

                long currentTime = 0;
                programName = tags[0];
                degreeName = tags[1];

                Calendar rightNow = Calendar.getInstance();
                currentTime = rightNow.getTimeInMillis();

                Storage.saveInt(Storage.LAST_PROGRAM_ID, ids[0]);
                Storage.saveInt(Storage.LAST_DEGREE_ID, ids[1]);
                int washTime = WasherInfo.getWashTime(programName, degreeName);
                WasherService.startAt(currentTime, washTime, programName, degreeName, new CallBack<StartStatus>() {
                    @Override
                    public void onSuccess(StartStatus startStatus) {
                        LiveData.getLiveRecord().setProgramInfo(startStatus.getProgramInfo());
                        MyViewPager.getInstance().setCurrentItem(MyViewPager.HOME_WASHING, false);
                    }

                    @Override
                    public void onError(WasherError error) {
                        Toast.makeText(getActivity(), "Kunde inte ansluta till servern!", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

        wpd.bindCancel(new Runnable() {
            @Override
            public void run() {

            }
        });

        wpd.show();

        int programIndex = Storage.loadInt(Storage.LAST_PROGRAM_ID, 0);
        int degreeIndex = Storage.loadInt(Storage.LAST_DEGREE_ID, 0);


        wpd.setIds(programIndex, degreeIndex);

        wpd.setRightButton("Starta");
    }




    public static BaseFragment create() {
        HomeSleepFragment fragment = new HomeSleepFragment();
        fragment.setArguments(createBundle(R.layout.layout_home_content, "HEM"));
        return fragment;
    }




}
