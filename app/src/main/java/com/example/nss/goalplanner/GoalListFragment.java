package com.example.nss.goalplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nss.goalplanner.Activity.GoalCreateActiviy;
import com.example.nss.goalplanner.Activity.MainActivity;
import com.example.nss.goalplanner.EventBus.GoalTotaltimeChangeEvent;
import com.example.nss.goalplanner.EventBus.SelectGoalEvent;
import com.example.nss.goalplanner.Listener.GoalItemChangeListner;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Model.GoalWarpper;
import com.example.nss.goalplanner.Network.GoalWebService;
import com.example.nss.goalplanner.util.NetworkUtil;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.adapter.GoalListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class GoalListFragment extends Fragment implements GoalItemChangeListner {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String TAG="GoalListFragment";

    private static final int CREATE_GOAL= 1;
    private static final String GOAL ="goal";

    @BindView(R.id.layout_goalList)
    View layout_goalList;
    @BindView(R.id.rv_goallist)
    RecyclerView rv_goallist;
    @BindView(R.id.fab_create)
    FloatingActionButton fab_create;

    @BindView(R.id.layout_nothing)
    View layout_nothing;
    @BindView(R.id.layout_progress)
    View layout_progress;

    List<Goal> goals;
    GoalListAdapter goalListAdapter;

    int screen_state;

    private static final int NOTHING=0;
    private static final int GOALLIST =1;
    private static final int PROGRESSING = 2;

    GoalWebService goalWebService;

    public static final int CONNECT_TIMEOUT_IN_MS = 30000;


    public GoalListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoalListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoalListFragment newInstance(String param1, String param2) {
        GoalListFragment fragment = new GoalListFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_goal_list, container, false);
        ButterKnife.bind(this, v);

        initNet();

        getAllGoal();

        initView();

        fab_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoGoalCreate();

            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    private void initView(){

        goals = new ArrayList<Goal>();
        initRecycle();

    }

    public void getAllGoal(){

        progressing();

        if(NetworkUtil.isConnected(getActivity())){

            goalWebService.getallGoal()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<GoalWarpper, List<Goal>>() {
                        @Override
                        public List<Goal> apply(GoalWarpper goalWarpper) {
                            return goalWarpper.getGoals();
                        }
                    })
                    .subscribe(new Consumer<List<Goal>>() {
                        @Override
                        public void accept(List<Goal> goals) throws Exception {
                            response(goals);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            failedResponse();
                        }
                    });

        }else{

            ((MainActivity)getActivity()).networknotworking();
        }
    }

    private void deleteGoal(){

    }

    private void modifyGoal(){

    }


    private void failedResponse(){

        ((MainActivity)getActivity()).serverWrong();
    }

    private void response(List<Goal> goals){

        if(goals !=null) {
            this.goals.clear();
            this.goals.addAll(goals);

            if(goals.size()>0){

                visibleList();
                goalListAdapter.notifyDataSetChanged();

            }else{
                nothing();
            }
        }

    }

    private void gotoGoalCreate(){

        Intent intent = new Intent(getActivity(),GoalCreateActiviy.class);

        startActivityForResult(intent,CREATE_GOAL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case CREATE_GOAL:

                Goal goal= data.getParcelableExtra(GOAL);

                goals.add(goal);

                goalListAdapter.notifyDataSetChanged();

                if (screen_state == NOTHING){

                    visibleList();
                }

                break;
        }
    }

    private void initRecycle(){

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_goallist.setLayoutManager(llm);

        goalListAdapter = new GoalListAdapter(getActivity(),goals);
        goalListAdapter.setGoalItemChangeListner(this);
        rv_goallist.setAdapter(goalListAdapter);


    }

    private void initNet(){

        Requestintercepter requestintercepter = new Requestintercepter();

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                        .addInterceptor(requestintercepter)
                        .addInterceptor(new HttpLoggingInterceptor())
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder().
                        baseUrl(BuildConfig.BASE_SERVER_URL).
                        client(okHttpClient).
                        addConverterFactory(GsonConverterFactory.create()).
                        addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                        build();

        goalWebService=retrofit.create(GoalWebService.class);


    }

    private void visibleList(){

        screen_state = GOALLIST;

        layout_goalList.setVisibility(View.VISIBLE);
        layout_nothing.setVisibility(View.GONE);
        layout_progress.setVisibility(View.GONE);


    }

    private void nothing(){

        screen_state = NOTHING;

        layout_nothing.setVisibility(View.VISIBLE);
        layout_goalList.setVisibility(View.GONE);
        layout_progress.setVisibility(View.GONE);

    }


    private void progressing(){

        screen_state =PROGRESSING;

        layout_progress.setVisibility(View.VISIBLE);
        layout_goalList.setVisibility(View.GONE);
        layout_nothing.setVisibility(View.GONE);

    }



    @Override
    public void onDelteGoalItem(int position) {

        deleteGoal();
    }


    @Override
    public void onModifyGoalItem(int position) {

        modifyGoal();
    }

    @Subscribe
    public void onEvent(GoalTotaltimeChangeEvent goalTotaltimeChangeEvent){

        goalListAdapter.notifyDataSetChanged();

    }
}
