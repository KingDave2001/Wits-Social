package com.example.softwareproject;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Sign_Up extends AppCompatActivity {
    //Declarations of variables
    EditText edtEmail, edtPhoneNo, edtUsername, edtPassword, edtConfirmPassword, edtFirstName, edtLastName;
    Button btnSignUp;
    TextView tv, pa;
    FirebaseDatabase fb;
    DatabaseReference Gdb;
    Field_Validations fv = new Field_Validations();
    String fcm_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Assign variables
        edtEmail = (EditText) findViewById(R.id.email_address);
        edtPhoneNo = (EditText) findViewById(R.id.Phone_number);
        edtUsername = (EditText) findViewById(R.id.username);
        edtPassword = (EditText) findViewById(R.id.pass1);
        edtConfirmPassword = (EditText) findViewById(R.id.pass2);
        btnSignUp = (Button) findViewById(R.id.sibt);
        edtFirstName = (EditText) findViewById(R.id.first_name);
        edtLastName = (EditText) findViewById(R.id.last_name);
        tv = (TextView) findViewById(R.id.tv);
        //pa = (TextView) findViewById(R.id.pad);

        btnSignUp.setOnClickListener(new View.OnClickListener() {//btn you click to signup
            @Override
            public void onClick(View view) {
                //Convert to string
                String email = edtEmail.getText().toString();
                String number = edtPhoneNo.getText().toString();
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String ConfirmPassword = edtConfirmPassword.getText().toString();
                String name = edtFirstName.getText().toString() + " " + edtLastName.getText().toString();
                String bio = "";
                String imageUrl = "";
                getDeviceToken();

                //Instance of a database
                fb = FirebaseDatabase.getInstance();
                Gdb = fb.getReference("Users");

                //Decarations and Assignments
                //checking to see if all required information is filled in
                boolean completed = completed();
                boolean matchingPassword = fv.passwords_match(password, ConfirmPassword, edtConfirmPassword);
                boolean validPassword = fv.valid_password(password, edtPassword);
                boolean validEmail = fv.check_email(email,edtEmail);
                boolean validNUmber = fv.Valid_number(number,edtPhoneNo);

                if (completed && matchingPassword && validPassword && validEmail && validNUmber) {//iff all info complete add to database
                    //Valid input- Add to database
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    Query checkUsername = ref.orderByChild("username").equalTo(username);
                    checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {//check if username is already in system
                                edtUsername.setError("Username already taken");//if in system stop process
                            } else {//if not in process and to database
                                User createUserClass = new User(username, email, number, password, name, bio, imageUrl, fcm_token);
                                Gdb.child(username).setValue(createUserClass);

                                Intent intent = new Intent(Sign_Up.this, Add_Profile_Pic.class);//move to add a profile pic
                                intent.putExtra("Username", username);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    // Get Device Token
    public void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        fcm_token = task.getResult();
                    }
                });
    }

    public boolean completed() {
        boolean key = true;
        //Validate username
        if (TextUtils.isEmpty(edtUsername.getText().toString())) {
            edtUsername.setError("Please enter in a username");
            key = false;
        }

        //Validate email address
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Please enter in an email address");
            key = false;
        }

        //Validate Phone number
        if (TextUtils.isEmpty(edtPhoneNo.getText().toString())) {
            edtPhoneNo.setError("Please enter in a phone number");
            key = false;
        }

        //Validate first name
        if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
            edtFirstName.setError("Please enter in your first name");
            key = false;
        }

        //Validate surname
        if (TextUtils.isEmpty(edtLastName.getText().toString())) {
            edtLastName.setError("Please enter in your surname ");
            key = false;
        }

        //Validate password
        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Please enter in a password ");
            key = false;
        }

        //Validate password
        if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError("Please enter in a password ");
            key = false;
        }
        if (key == false) {
            tv.setText("Please ensure you have\ncompleted all the fields");
        }
        return key;
    }
}
