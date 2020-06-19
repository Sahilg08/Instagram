package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText fullname,username,password,email;
    Button txt_login;
    FloatingActionButton register;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_register);

        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        username =findViewById(R.id.username);
        txt_login=findViewById(R.id.txt_login);
        register= findViewById(R.id.register);


        auth=FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd=new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait...");
                pd.show();

                String str_username=username.getText().toString();
                String str_fullname=fullname.getText().toString();
                String str_password=password.getText().toString();
                String str_email=email.getText().toString();

                if (TextUtils.isEmpty(str_username)|TextUtils.isEmpty(str_email)|TextUtils.isEmpty(str_password)|TextUtils.isEmpty(str_fullname)){
                    Toast.makeText(RegisterActivity.this, "All Fields are required!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();


                }else if (str_password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password must contain 6 characters", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }else {
                    register(str_username,str_fullname,str_email,str_password);



                }


            }
        });
    }
    public void register(final String username, final String fullname, String email, final String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    String userid=firebaseUser.getUid();
                    reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username.toLowerCase());
                    hashMap.put("fullname",fullname);
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instagram-b7435.appspot.com/o/placeholder.png?alt=media&token=bd55846a-e8aa-4366-bb2e-629dbf31cef1");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });

                }else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "You are not registered with this email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
