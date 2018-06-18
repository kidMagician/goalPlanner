package com.example.nss.goalplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Network.GoalWebService;
import com.example.nss.goalplanner.util.NetworkUtil;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.Resonse.ResponseGoalCreate;
import com.example.nss.goalplanner.util.KeyboardUtil;

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



public class GoalCreateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final int CONNECT_TIMEOUT_IN_MS= 30000;
    private static final int CREATE_GOAL_OK= 1;
    private static final String GOAL ="goal";

    private static final String TAG ="GoalCreateFragment";

    String myFormat = "yyyy/MM/dd";
    Calendar myCalendar = Calendar.getInstance();

    @BindView(R.id.edit_goalname)
    EditText edit_goalname;
    @BindView(R.id.edit_reason)
    EditText edit_reason;
    @BindView(R.id.edit_enddate)
    EditText edit_enddate;
    @BindView(R.id.btn_OK)
    Button btn_OK;

    GoalWebService goalWebService;

    Goal goal;

    public GoalCreateFragment() {
        // Required empty public constructor
    }


    public static GoalCreateFragment newInstance(String param1, String param2) {
        GoalCreateFragment fragment = new GoalCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View v= inflater.inflate(R.layout.fragment_goal_create, container, false);

        ButterKnife.bind(this,v);

        initNet();


        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createGoal();
            }
        });

        edit_enddate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                KeyboardUtil.hideKyboard(edit_enddate);


            }
        });



        edit_enddate.setKeyListener(null);


        return v;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

            edit_enddate.setText(sdf.format(myCalendar.getTime()));

        }

    };



    private void createGoal(){

        if(validate()){

            if(NetworkUtil.isConnected(getActivity())){

                goal = new Goal();

                goal.setName(edit_goalname.getText().toString());
                goal.setReason(edit_reason.getText().toString());

                String str_enddate =edit_enddate.getText().toString().toString();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat);

                try{
                    Long end_date=simpleDateFormat.parse(str_enddate).getTime();
                    goal.setEnd_date(end_date);

                }catch (ParseException e){
                    Log.e(TAG,e.toString());
                    goal.setEnd_date(0);

                }finally {
                    Log.e(TAG,"goal enddate set 0 in createGoal()");

                }

                Long start_date = System.currentTimeMillis();
                goal.setStart_date(start_date);

                goal.setTotal_time(0);

                goalWebService.createGoal(goal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseGoalCreate>() {
                            @Override
                            public void accept(ResponseGoalCreate response) throws Exception {
                                response(response);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                failCreatGoal();
                            }
                        });


            }else{

                Toast.makeText(getActivity(),getString(R.string.network_not_connected),Toast.LENGTH_LONG).show();

            }
        }

    }
    private void response(ResponseGoalCreate response){

        switch (response.getResoult_code()){
            case ResponseGoalCreate.FAIL_REQUEST:

                failCreatGoal();

                break;
            case ResponseGoalCreate.SUCEES_REQUEST:

                Intent i = new Intent();

                i.putExtra(GOAL,goal);

                getActivity().setResult(CREATE_GOAL_OK,i);

                getActivity().finish();

                break;
            case ResponseGoalCreate.GOAL_DUPLICATION:

                edit_goalname.setError(getString(R.string.goalcreate_err_edit_goal_duplication));
                edit_goalname.requestFocus();

        }

    }

    private void failCreatGoal(){

        Toast.makeText(getActivity(),"failed",Toast.LENGTH_LONG).show();
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
