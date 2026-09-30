package lk.jiat.shapeway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.viewmodels.SignUpViewModel;
import lk.jiat.shapeway.databinding.FragmentSubscritionBinding;
import lk.jiat.shapeway.models.User;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;


public class Subscription extends Fragment {
    public SignUpViewModel viewModel;
    private int planPrice = 3000;

    private String subId;
    private FragmentSubscritionBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    TextView timerText;
    int seconds = 600;

    public Subscription() {
        // Required empty public constructor
        super(R.layout.fragment_subscrition);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        super.onViewCreated(view, savedInstanceState);
        timerText = view.findViewById(R.id.timerText);

        startTimer();


        binding.subBtn.setOnClickListener(view1 -> {

            checkout();



        });


    }

    private String generateOrderId() {
        long timestamp = System.currentTimeMillis(); // unique time
        int random = (int) (Math.random() * 1000);   // small random

        return "ORD_" + timestamp + "_" + random;
    }

    private void checkout() {
        subId = generateOrderId();

        InitRequest req = new InitRequest();
        req.setSandBox(true);
        req.setMerchantId("1225624");       // Merchant ID
        req.setMerchantSecret("MjM5MjUzMTQyMDIwNTQ3OTg2NzczMTMwNzU0ODQ3MTk2OTcyMTUxNg==");
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(planPrice);             // Final Amount to be charged
        req.setOrderId(subId);        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName(viewModel.name);
        req.getCustomer().setLastName(viewModel.name);
        req.getCustomer().setEmail(viewModel.email);
        req.getCustomer().setPhone("");
        req.getCustomer().getAddress().setAddress("");
        req.getCustomer().getAddress().setCity("");
        req.getCustomer().getAddress().setCountry("");

        Intent intent = new Intent(getActivity(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

        payhereLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent>
            payhereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

            Intent data = result.getData();
            if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)){

                PHResponse<StatusResponse> response  =(PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                if (response!=null && response.isSuccess()){
                    StatusResponse responseData = response.getData();
                    Log.i("paynere", "payment Success");

                   viewModel.paymentNo = String.valueOf(response.getData().getPaymentNo());
                    subscribe();

                }else {
                    Log.e("PAYHERE" ,response.getData().getMessage());
                }

            }
        } else if (result.getResultCode()==Activity.RESULT_CANCELED) {

                    Log.e("PAYHERE" ,"paymeent Canccels");
        }
    });


    private void subscribe() {

        binding.subBtn.setEnabled(false);
        binding.subBtn.setText("Logging...");

            firebaseAuth.createUserWithEmailAndPassword(viewModel.email, viewModel.password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            User user =
                                    User.builder().uid(uid)
                                            .name(viewModel.name)
                                            .email(viewModel.email)
                                            .goal(viewModel.goal)
                                            .somatotypes(viewModel.somatotypes)
                                            .currentBody(viewModel.currentBody)
                                            .gain(viewModel.gain)
                                            .tpw(viewModel.tpw)
                                            .dailyRt(viewModel.dailyRt)
                                            .pushUp(viewModel.pushUp)
                                            .diet(viewModel.diet)
                                            .wkSchedule(viewModel.wkSchedule)
                                            .heightcm(viewModel.heightcm)
                                            .heightft(viewModel.heightft)
                                            .weight(viewModel.weight)
                                            .age(viewModel.age)
                                            .paymentNo(viewModel.paymentNo)
                                            .build();
                            firebaseFirestore.collection("users")
                                    .document(uid)
                                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            binding.subBtn.setEnabled(true);
                                            binding.subBtn.setText("GetMyPlan");

                                            Toast.makeText(getContext().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(requireActivity(), HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            binding.subBtn.setEnabled(true);
                                            binding.subBtn.setText("GetMyPlan");
                                            Toast.makeText(getContext().getApplicationContext(), "Register Fail", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });


    }

    private void showError(EditText field, String msg) {
        field.setBackgroundResource(R.drawable.bg_edittext_error);
        field.setError(msg);
    }

    private void startTimer() {

        new CountDownTimer(seconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                int min = (int) (millisUntilFinished / 1000) / 60;
                int sec = (int) (millisUntilFinished / 1000) % 60;

                timerText.setText(String.format("%02d:%02d", min, sec));
            }

            public void onFinish() {
                timerText.setText("00:00");
            }

        }.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSubscritionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}