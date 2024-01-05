package com.example.albumapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumapp.R;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.GetAllPhotoFromDisk;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PrivateAlbumActivity extends AppCompatActivity {
    private ImageView imgBackPrivateAlbum;
    Button btnCreatePass;
    Button btnEnterPass;
    EditText createPass;
    EditText confirmPass;
    EditText enterPass;
    EditText question;
    EditText answer;
    TextInputLayout enterField;
    TextInputLayout createField;
    TextInputLayout confirmField;
    TextInputLayout questionField;
    TextInputLayout answerField;
    String password;
    private LinearLayout createPassView;
    private LinearLayout enterPassView;
    TextView forgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
//        DataLocalManager.getInstance().savePassword("");
        password = DataLocalManager.getInstance().getPassword();
        mappingControls();

        if (!password.equals("")) {
            createPassView.setVisibility(View.INVISIBLE);
        }
        else {
            enterPassView.setVisibility(View.INVISIBLE);
        }
        eventEnterPass();
        eventCreatePass();
        imgBackPrivateAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                showForgotPassDialog();
            }
        });
    }

    private void mappingControls() {
        imgBackPrivateAlbum = findViewById(R.id.img_back_private_album);
        enterPass = findViewById(R.id.enterPass);
        btnEnterPass = findViewById(R.id.btnEnterPass);
        createPass = findViewById(R.id.createPass);
        confirmPass = findViewById(R.id.confirmPass);
        btnCreatePass = findViewById(R.id.btnCreatePass);
        createPassView = findViewById(R.id.createPassView);
        enterPassView = findViewById(R.id.enterPassView);
        enterField = findViewById(R.id.enterField);
        createField = findViewById(R.id.createField);
        confirmField = findViewById(R.id.confirmField);
        questionField = findViewById(R.id.questionField);
        answerField = findViewById(R.id.answerField);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);
        forgotPassword = findViewById(R.id.forgotPassword);

    }

    public  void eventEnterPass(){
        btnEnterPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String enterText = enterPass.getText().toString();
                if (checkBcrypt(enterText, password)) {
                    Toast.makeText(getApplicationContext(),"Password correct", Toast.LENGTH_SHORT).show();
                    accessSecret();
                    enterPass.setText("");
                }
                else{
                    enterField.setError("Wrong password");
                }
            }
        });
    }
    public void accessSecret(){
        Intent intent = new Intent(getApplicationContext(), ItemAlbumActivity.class);
        ArrayList<String> list = new ArrayList<>();
        list.addAll(DataLocalManager.getInstance().getPrivateAlbum());
        List<MyImage> allImages = GetAllPhotoFromDisk.getImages(getApplicationContext());
        ArrayList<MyImage> dataImages = new ArrayList<>();
        for (int i = 0; i < list.size(); i++){
            for (int j = 0; j < allImages.size(); j++) {
                if (Objects.equals(list.get(i), allImages.get(j).getPath())) {
                    dataImages.add(allImages.get(j));
                }
            }
        }

        intent.putParcelableArrayListExtra("dataImages", dataImages);
        intent.putExtra("name", "Private Album");
        intent.putExtra("isPrivate", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(intent);
    }

    public void eventCreatePass(){
        btnCreatePass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String passwordText = createPass.getText().toString();
                String confirmText = confirmPass.getText().toString();
                String questionText = question.getText().toString();
                String answerText = answer.getText().toString();
                boolean errorInput = false;
                if (passwordText.equals("")) {
                    createField.setError("Empty input");
                    errorInput = true;
                }
                if (confirmText.equals("")) {
                    confirmField.setError("Empty input");
                    errorInput = true;
                }
                if (questionText.equals("")) {
                    questionField.setError("Empty input");
                    errorInput = true;
                }
                if (answerText.equals("")) {
                    answerField.setError("Empty input");
                    errorInput = true;
                }
                if (!passwordText.equals(confirmText)) {
                    confirmField.setError("Passwords doesn't match");
                    errorInput = true;
                }
                if (errorInput) {
                    return;
                }

                password = hashBcrypt(passwordText);
                DataLocalManager.getInstance().savePassword(password);
                DataLocalManager.getInstance().saveAnswer(hashBcrypt(answerText));
                DataLocalManager.getInstance().saveQuestion(questionText);
                createPassView.setVisibility(View.INVISIBLE);
                enterPassView.setVisibility(View.VISIBLE);
                accessSecret();
            }
        });

    }
    public void showForgotPassDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);

        String securityQuestion = DataLocalManager.getInstance().getQuestion();
        TextView textSecurityQuestion = dialogView.findViewById(R.id.textSecurityQuestion);
        textSecurityQuestion.setText(securityQuestion);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
               .setTitle("Forgot Password")
               .setPositiveButton("Submit", null)
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = DataLocalManager.getInstance().getAnswer();
                EditText editTextAnswer = dialogView.findViewById(R.id.fAnswer);
                TextInputLayout fAnswerField = dialogView.findViewById(R.id.fAnswerField);
                String enteredAnswer = editTextAnswer.getText().toString().trim();
                if (enteredAnswer.equals("")) {
                    fAnswerField.setError("Empty Input");
                    return;
                }
                if (checkBcrypt(enteredAnswer, answer)){
                    Toast.makeText(getApplicationContext(),"Answer is correct", Toast.LENGTH_SHORT).show();
                    showChangePasswordDialog();
                    dialog.dismiss();
                }
                else {
                    fAnswerField.setError("Incorrect answer");
                    Toast.makeText(getApplicationContext(), "Incorrect answer. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
            .setTitle("Change Password")
            .setPositiveButton("Change Password", null)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editNewPassword = dialogView.findViewById(R.id.newPassword);
                EditText editConfirmPassword = dialogView.findViewById(R.id.confirmNewPassword);

                TextInputLayout newPassField = dialogView.findViewById(R.id.newPasswordField);
                TextInputLayout confirmNewPassField = dialogView.findViewById(R.id.confirmPasswordField);
                String newPassword = editNewPassword.getText().toString().trim();
                String confirmPassword = editConfirmPassword.getText().toString().trim();
                if (newPassword.equals("")) {
                    newPassField.setError("Empty Input");
                }
                else if (confirmPassword.equals("")) {
                    confirmNewPassField.setError("Empty Input");
                }
                if (newPassword.equals("") || confirmPassword.equals("")) {
                    return;
                }

                if (newPassword.equals(confirmPassword)) {
                    password = hashBcrypt(newPassword);
                    DataLocalManager.getInstance().savePassword(password);
                    Toast.makeText(getApplicationContext(),"Password changed successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    confirmNewPassField.setError("Passwords do not match.");
                    Toast.makeText(getApplicationContext(),"Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String hashBcrypt(String password) {
        int costFactor = 12;

        return BCrypt.withDefaults().hashToString(costFactor, password.toCharArray());
    }

    public boolean checkBcrypt(String plainTextPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainTextPassword.toCharArray(), hashedPassword).verified;
    }
}