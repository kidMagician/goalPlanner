package com.example.nss.goalplanner;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nss.goalplanner.Activity.AuthActivity;
import com.example.nss.goalplanner.Model.Authinfo;
import com.example.nss.goalplanner.Resonse.Responsesignup;
import com.example.nss.goalplanner.Network.Authenficater;
import com.example.nss.goalplanner.util.NetworkUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.edit_username)
    EditText edit_username;
    @BindView(R.id.edit_email)
    EditText edit_emaill;
    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.edit_vertify_pass)
    EditText edit_vertify_pass;

    @BindView(R.id.btn_ok)
    Button btn_OK;

    ProgressDialog progress_signup;

    public static final int CONNECT_TIMEOUT_IN_MS = 30000;

    private static final String TAG = "SignUpFragment";

    private Authenficater authenficater;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        View v=inflater.inflate(R.layout.fragment_sign_up, container, false);

        ButterKnife.bind(this,v);

        initNetwork();

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        return v;
    }


    private void signup(){

        progressSignup();

        if(validate()){
            if(NetworkUtil.isConnected(getContext())) {

                Authinfo authinfo = new Authinfo(edit_username.getText().toString(), edit_emaill.getText().toString(), edit_pass.getText().toString());

                authenficater.signup(authinfo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Responsesignup>() {
                            @Override
                            public void accept(Responsesignup s) throws Exception {
                                responseSignUp(s);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                progressDismiss();
                                alertfailsinup();
                            }
                        });

            }else{
                ((AuthActivity)getActivity()).networknotworking();
            }
        }

    }

    private void responseSignUp(Responsesignup responsesignup){

        int result =responsesignup.getResult_signup();

        progressDismiss();

        switch (result){

            case Responsesignup.FAIL_SIGNUP:

                alertfailsinup();

                break;
            case Responsesignup.SUCEES_SIGNUP:

                swithTosignin();

                break;
            case Responsesignup.USERNAM_DUPLICATION:

                edit_username.setError(getString(R.string.sign_up_edit_username_dublicate_err));

                break;

            case Responsesignup.EMAIL_NOT_EXIST:

                edit_emaill.setError(getString(R.string.sign_up_edit_email_not_exist_err));

                break;

            case Responsesignup.EMAIL_DUPLICATION:

                edit_emaill.setError(getString(R.string.sign_up_edit_email_dublicate_err));

                break;
            case Responsesignup.DUPLICATION_USERNAME_EMIL:

                edit_emaill.setError(getString(R.string.sign_up_edit_email_dublicate_err));
                edit_username.setError(getString(R.string.sign_up_edit_username_dublicate_err));

                break;
        }

    }

    private void progressSignup(){

        if(progress_signup==null){

            progress_signup = new ProgressDialog(getContext());

            progress_signup.setMessage(getString(R.string.sign_up_progress));

        }

        progress_signup.show();

        progress_signup.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void progressDismiss(){

        if(progress_signup !=null){
            progress_signup.dismiss();
            progress_signup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void alertfailsinup(){

        Toast.makeText(getContext(),getString(R.string.sign_up_fail),Toast.LENGTH_LONG).show();
    }

    private void swithTosignin(){

        getActivity().getSupportFragmentManager().popBackStack();

    }

    private  boolean validate(){

        String email = edit_emaill.getText().toString();
        String username = edit_username.getText().toString();
        String password = edit_pass.getText().toString();
        String pass_vertify = edit_vertify_pass.toString();

        boolean valid =true;

        if(email.isEmpty()){

            edit_emaill.setError(getString(R.string.sign_up_edit_email_empty_err));
            valid =false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            edit_emaill.setError(getString(R.string.sign_up_edit_not_email_err));
            valid =false;
        }

        if(username.isEmpty() || username.length() < 3){

            edit_username.setError(getString(R.string.sign_up_edit_username_length_err));
            valid = false;
        }

        if(password.isEmpty() || password.length() < 8 || password.length() > 12 ){

            edit_pass.setError(getString(R.string.sign_up_edit_pass_length_err));
            valid =false;
        }

        if(password.equals(pass_vertify)){
            edit_vertify_pass.setError(getString(R.string.sign_up_edit_pass_not_vertify_err));
            valid=false;
        }

        return valid;
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
