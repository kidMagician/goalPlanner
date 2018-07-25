package com.example.nss.goalplanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Network.GoalWebService;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.Resonse.Response;
import com.example.nss.goalplanner.Resonse.ResponseGoalCreate;
import com.example.nss.goalplanner.Resonse.ResponseGoalUpdate;
import com.example.nss.goalplanner.util.NetworkUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalUPdateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GOAL = "goal";
    private static final String TAG="goalupdatefragment";
    private static final int UPDATE_GOAL_OK= 2;

    String myFormat = "yyyy/MM/dd";

    private static final int CONNECT_TIMEOUT_IN_MS= 30000;

    GoalWebService goalWebService;

    Goal goal;
    Goal updatedGoal;

    @BindView(R.id.edit_goalname)
    EditText edit_goalname;
    @BindView(R.id.edit_reason)
    EditText edit_reason;
    @BindView(R.id.edit_enddate)
    EditText edit_enddate;
    @BindView(R.id.btn_OK)
    Button btn_OK;

    public GoalUPdateFragment() {
        // Required empty public constructor
    }

    public static GoalUPdateFragment newInstance(Goal goal) {
        GoalUPdateFragment fragment = new GoalUPdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(GOAL, goal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goal = getArguments().getParcelable(GOAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_goal_update, container, false);

        ButterKnife.bind(this,v);

        initNet();

        if(goal!=null){

            edit_goalname.setText(goal.getName());

            edit_reason.setText(goal.getReason());

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);

            calendar.setTimeInMillis(goal.getEnd_date());

            edit_enddate.setText(simpleDateFormat.format(calendar.getTime()));

        }

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateGoal();
            }
        });

        return v;
    }

    private void updateGoal(){

        if(validate()){

            if(NetworkUtil.isConnected(getActivity())){

                updatedGoal = new Goal();

                updatedGoal.setName(edit_goalname.getText().toString());
                updatedGoal.setReason(edit_reason.getText().toString());

                String str_enddate =edit_enddate.getText().toString();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat);

                try{
                    Long end_date=simpleDateFormat.parse(str_enddate).getTime();
                    updatedGoal.setEnd_date(end_date);

                }catch (ParseException e){
                    Log.e(TAG,e.toString());
                    updatedGoal.setEnd_date(0);

                }finally {
                    Log.e(TAG,"goal enddate set 0 in updateGoal()");

                }
                updatedGoal.setId(goal.getId());
                updatedGoal.setStart_date(goal.getStart_date());

                updatedGoal.setTotal_time(goal.getTotal_time());

                goalWebService.updateGoal( updatedGoal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Response>() {
                            @Override
                            public void accept(Response response) throws Exception {
                                response(response);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                failUpdateGoal();
                            }
                        });


            }else{

                Toast.makeText(getActivity(),getString(R.string.network_not_connected),Toast.LENGTH_LONG).show();

            }
        }
    }

    private void response(Response response){
        switch (response.getResoult_code()){
            case ResponseGoalUpdate.FAIL_REQUEST:

                failUpdateGoal();

                break;
            case ResponseGoalUpdate.SUCEES_REQUEST:

                goal.setGoal(updatedGoal);

                Intent i =new Intent();

                i.putExtra(GOAL,updatedGoal);

                getActivity().setResult(UPDATE_GOAL_OK,i);

                getActivity().finish();

                break;

            case ResponseGoalUpdate.GOAL_DUPLICATION:

                edit_goalname.setError(getString(R.string.goalcreate_err_edit_goal_duplication));
                edit_goalname.requestFocus();

        }
    }

    private void failUpdateGoal(){

    }

    private boolean validate(){
        boolean valid = true;

        String name = edit_goalname.getText().toString();
        String reason = edit_reason.getText().toString();
        String enddate = edit_enddate.toString();

        if(name.isEmpty()){

            edit_goalname.setError(getString(R.string.goalcreate_txt_start));
            valid =false;
        }

        if(reason.isEmpty()){

            edit_reason.setError(getString(R.string.goalcreate_err_edit_empty_reason));
            valid =false;
        }

        if(enddate.isEmpty()){

            edit_enddate.setError(getString(R.string.goalcreate_err_edit_empty_enddate));
            valid =false;
        }

        return valid;
    }

    private void initNet(){

        Requestintercepter requestintercepter = new Requestintercepter(getActivity());

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                        .addInterceptor(requestintercepter)
                        .addInterceptor(new HttpLoggingInterceptor())
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BuildConfig.BASE_SERVER_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

        goalWebService=retrofit.create(GoalWebService.class);

    }

}


