package com.serafimpinto.faceshare;

import data.Pic;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddPicActivity extends ActionBarActivity{

	private EditText title;
	private Pic pic;
	private ImageView image;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpic);
        
        final Button b = (Button) findViewById(R.id.gravar);
        title = (EditText) findViewById(R.id.titleID);
        
        title.addTextChangedListener(new TextWatcher() {
			@SuppressLint("NewApi") public void onTextChanged(CharSequence s, int start, int before, int count) {
				b.setEnabled(!title.getText().toString().trim().isEmpty());
			}  
			public void afterTextChanged(Editable s) {
				
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
        
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        
        Intent intent = getIntent();
        final String pathImage = intent.getExtras().getString("path");
  
        final Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image = (ImageView) findViewById(R.id.image1);
        image.setImageBitmap(bmp);
        
        b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Foi adicionada uma imagem nova ", Toast.LENGTH_SHORT).show();
				Pic p = null;
				if(pic == null) {
					p = new Pic(pathImage,title.getText().toString());
				}
				Intent i = new Intent();
				i.putExtra("pic", p);
				setResult(RESULT_OK,i);
				finish();
			}	
		});
	}
}
