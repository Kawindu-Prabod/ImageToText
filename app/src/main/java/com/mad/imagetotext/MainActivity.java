package com.mad.imagetotext;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {


    ImageView clear, getImage, copy;
    EditText recogText;
    Uri imageUri;
    TextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        clear = findViewById(R.id.clear);
        getImage = findViewById(R.id.getImage);
        copy = findViewById(R.id.copy);
        recogText = findViewById(R.id.recogtext);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = recogText.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
                }
                else{
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",recogText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(MainActivity.this, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = recogText.getText().toString();

                if (text.isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    recogText.setText("");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            recognizeText();
        } else {
            Toast.makeText(this, "Image not Selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void recognizeText() {

        if (imageUri != null){

            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity.this,imageUri);

                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {

                                String recognizeText = text.getText();
                                recogText.setText(recognizeText);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}