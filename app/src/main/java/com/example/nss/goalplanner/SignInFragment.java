package com.example.nss.goalplanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nss.goalplanner.Activity.AuthActivity;
import com.example.nss.goalplanner.Activity.MainActivity;
import com.example.nss.goalplanner.Model.Authinfo;
import com.example.nss.goalplanner.Resonse.Responsesignin;
import com.example.nss.goalplanner.Service.TokenPrefernce;
import com.example.nss.goalplanner.Network.Authenficater;
import com.example.nss.goalplanner.util.NetworkUtil;

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


public class SignInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "SignInFragment";

    public static final int CONNECT_TIMEOUT_IN_MS = 30000;

    private Authenficater authenficater;

    private ProgressDialog progress_signin;

    @BindView(R.id.edit_email)
    EditText edit_emaill;
    @BindView(R.id.edit_pass)
    EditText edit_pass;
    @BindView(R.id.btn_ok)
    Button btn_OK;
    @BindView(R.id.txt_signup)
    TextView txt_signup;
    @BindView(R.id.txt_find_account)
    TextView txt_findPass;

    AuthActivity authActivity;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
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
        View v= inflater.inflate(R.layout.fragment_sign_in, container, false);

        ButterKnife.bind(this,v);

        authActivity = (AuthActivity) getActivity();

        initNetwork();

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });
        txt_findPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchtoFindpass();
            }
        });
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSignup();
            }
        });
        return v;
    }

    private void switchToSignup(){

        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();

        if(transaction!=null){

            transaction.replace(R.id.container,SignUpFragment.newInstance(null,null));
            transaction.addToBackStack(null);
            transaction.commit();

        }

    }

    private void switchtoFindpass(){

        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();

        if(transaction!=null){

            transaction.replace(R.id.container,FindPassFragment.newInstance(null,null));
            transaction.addToBackStack(null);
            transaction.commit();

        }

    }

    private void signin(){
        if(validate()){
            if(NetworkUtil.isConnected(getContext())){

                progressSignin();

                Authinfo authinfo = new Authinfo(edit_emaill.getText().toString(),edit_pass.getText().toString());

                authenficater.signIn(authinfo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Responsesignin>() {
                            @Override
                            public void accept(Responsesignin responsesignin) throws Exception {
                                responseSignin(responsesignin);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                progressDismiss();
                                alertfailsignin();
                            }
                        });
            }else{
                authActivity.networknotworking();
            }
        }
    }

    private void responseSignin(Responsesignin responsesignin){

        switch (responsesignin.getResoult_code()){

            case Responsesignin.FAIL_SIGNUP:
                alertfailsignin();
                break;
            case Responsesignin.SUCEES_SIGNUP:

                TokenPrefernce tokenPrefernce =new TokenPrefernce(getActivity());
                tokenPrefernce.setTokenInfo(responsesignin.getTokeninfo());

                gotoMain();
                break;
        }

        progressDismiss();
    }

    private void alertfailsignin(){

        Toast.makeText(getActivity(),getString(R.string.sign_in_fail),Toast.LENGTH_LONG).show();
    }

    private void gotoMain(){

        Intent i = new Intent(getActivity(),MainActivity.class);

        startActivity(i);

    }

    private  boolean validate(){

        String email = edit_emaill.getText().toString();
        String password = edit_pass.getText().toString();

        boolean valid =true;

        if(email.isEmpty()){

            edit_emaill.setError(getString(R.string.sign_in_edit_email_empty_err));
            valid =false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            edit_emaill.setError(getString(R.string.sign_in_edit_not_email_err));
            valid =false;
        }

        if(password.isEmpty() || password.length() < 8 || password.length() > 12 ){

            edit_pass.setError(getString(R.string.sign_in_edit_pass_length_err));
            valid =false;
        }


        return valid;
    }

    private void progressSignin(){

        if(progress_signin==null){

            progress_signin = new ProgressDialog(getContext());

            progress_signin.setMessage(getString(R.string.sign_in_progress));

        }

        progress_signin.show();

        progress_signin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void progressDismiss(){

        if(progress_signin !=null){
            progress_signin.dismiss();
            progress_signin.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    private void initNetwork(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient =new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
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
