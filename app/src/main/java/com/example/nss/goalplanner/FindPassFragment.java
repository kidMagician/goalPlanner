package com.example.nss.goalplanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.nss.goalplanner.Network.Authenficater;
import com.example.nss.goalplanner.util.NetworkUtil;
import com.example.nss.goalplanner.Network.Requestintercepter;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class FindPassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "findpassfragment";
    private static final int CONNECT_TIMEOUT_IN_MS = 30000;

    private Authenficater authenficater;

    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.btn_submit)
    Button btn_submit;


    public FindPassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindPassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindPassFragment newInstance(String param1, String param2) {
        FindPassFragment fragment = new FindPassFragment();
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
        View v= inflater.inflate(R.layout.fragment_find_pass, container, false);

        ButterKnife.bind(this,v);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        initNetwork();

        return v;
    }

    private void submit(){
        if(validate()){
            if(NetworkUtil.isConnected(getActivity())){


            }else{

            }
        }

    }

    private boolean validate(){

        boolean valid =true;

        String email = edit_email.getText().toString();

        if(email.isEmpty()){

            edit_email.setError(getString(R.string.findpass_edit_email_empty_err));
            valid =false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            edit_email.setError(getString(R.string.findpass_in_edit_not_email_err));
            valid =false;
        }

        return valid;
    }

    private void initNetwork(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient =new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Requestintercepter())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        authenficater=retrofit.create(Authenficater.class);

    }




}
