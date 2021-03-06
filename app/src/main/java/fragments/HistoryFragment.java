package fragments;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.washer.smart.smartwasher.NavigationBar;
import com.washer.smart.smartwasher.R;

import java.util.ArrayList;
import java.util.List;

import customviews.Border;
import customviews.EnergyGraph;
import customviews.GraphContainer;
import customviews.RecordLabel;
import extra.GraphData;
import extra.Storage;
import model.WashRecord;
import model.WashRecords;
import sdk.CallBack;
import sdk.WasherError;
import sdk.WasherService;

/**
 * Created by xxkarlue on 2015-04-13.
 */
public class HistoryFragment extends BaseFragment {

    NavigationBar topTabBar, graphDataBar;
    LinearLayout scrollContent, graphContent;
    TextView totalEnergy, totalPrice;
    ScrollView sv;
    boolean showGraphs = false;

    private final long DAY_MILLIS = 1000*60*60*24;
    private final long WEEK_MILLIS = DAY_MILLIS*6;
    private final long MONTH_MILLIS = DAY_MILLIS*29;
    private final long YEAR_MILLIS = DAY_MILLIS*364;

    private List<WashRecord> weekRecords;
    private List<WashRecord> monthRecords;
    private List<WashRecord> yearRecords;
    GraphData data, weekData, monthData, yearData;



    private long startTime;



    protected void init(View v){
        topTabBar = NavigationBar.greenWhiteToggle(v);
        scrollContent = (LinearLayout) v.findViewById(R.id.ll_history_scroll_content);
        graphContent = (LinearLayout) v.findViewById(R.id.ll_history_graph_content);
        totalEnergy = (TextView) v.findViewById(R.id.tv_history_total_watt);
        totalPrice = (TextView) v.findViewById(R.id.tv_history_total_cost);

        sv = (ScrollView) v.findViewById(R.id.sv_history);



        topTabBar.addView(v.findViewById(R.id.tv_history_week), new Runnable() {
            @Override
            public void run() {
                if(weekRecords == null){
                    loadRecordsSince(WEEK_MILLIS, 0);
                }else{
                    updateViews(weekRecords, 0);
                }
            }
        });

        topTabBar.addView(v.findViewById(R.id.tv_history_month), new Runnable() {
            @Override
            public void run() {
                if(monthRecords == null){
                    loadRecordsSince(MONTH_MILLIS, 1);
                }else{
                    updateViews(monthRecords, 1);
                }
            }
        });

        topTabBar.addView(v.findViewById(R.id.tv_history_year), new Runnable() {
            @Override
            public void run() {
                if(yearRecords == null){
                    loadRecordsSince(YEAR_MILLIS, 2);
                }else{
                    updateViews(yearRecords, 2);
                }
            }
        });

        graphDataBar = NavigationBar.greenWhiteToggle(v);

        graphDataBar.addView(v.findViewById(R.id.tv_history_graph), new Runnable() {
            @Override
            public void run() {
                sv.setVisibility(View.GONE);
                graphContent.setVisibility(View.VISIBLE);
                showGraphs = true;
            }
        });

        graphDataBar.addView(v.findViewById(R.id.tv_history_data), new Runnable() {
            @Override
            public void run() {
                graphContent.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);
                showGraphs = false;
            }
        });
    }

    private void loadRecordsSince(long time,final int id){
        WasherService.getHistoryByTime(startTime = (System.currentTimeMillis() - time), new CallBack<WashRecords>() {
            @Override
            public void onSuccess(WashRecords washRecords) {
                switch (id){
                    case 0: weekRecords = washRecords.getRecords(); break;
                    case 1: monthRecords = washRecords.getRecords(); break;
                    case 2: yearRecords = washRecords.getRecords(); break;
                }

               updateViews(washRecords.getRecords(), id);

            }

            @Override
            public void onError(WasherError error) {
                Log.d("Error", error.getReason());
            }
        });
    }

    private void updateViews(List<WashRecord> records, int id){

        switch (id){
            case 0: data = (weekData == null) ? weekData = GraphData.weekData(records, startTime) : weekData; break;
            case 1: data = (monthData == null) ? monthData = GraphData.monthData(records, startTime) : monthData; break;
            default : data = (yearData == null) ? yearData = GraphData.yearData(records, startTime) : yearData; break;
        }

        createGraphs();
        createLabels(records);

        totalEnergy.setText(String.format("%.3f",data.getTotalEnergy())+" kWh");
        totalPrice.setText(String.format("%.3f",data.getTotalPrice())+" öre");

    }

    private void createGraphs(){
        graphContent.removeAllViews();
        graphContent.addView(new GraphContainer(getActivity(), data));
    }

    private void createLabels(List<WashRecord> records){
        scrollContent.removeAllViews();
        for(int i = 0; i < records.size(); ++i){
            if(i != 0)
                scrollContent.addView(new Border(getActivity(), getResources().getColor(R.color.tieto_green), 10, 0));

            scrollContent.addView(new RecordLabel(getActivity(), records.get(records.size()-i-1)));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        topTabBar.loadState("TimeBar");
        graphDataBar.loadState("GraphData");
    }

    @Override
    public void onPause() {
        super.onPause();
        topTabBar.saveState("TimeBar");
        topTabBar.saveState("GraphData");
    }

    public static BaseFragment create() {
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(createBundle(R.layout.layout_history_content, "HISTORIK"));
        return fragment;
    }
}
