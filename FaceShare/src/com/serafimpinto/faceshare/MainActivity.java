package com.serafimpinto.faceshare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;

import data.Pic;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    protected static final int REQUEST_CAMERA = 0;
	protected static final int SELECT_FILE = 1;
	private static final int ACTION_NEW = 3;
 
	private List<Pic> Pics = new ArrayList<Pic>();
	private Pic p; 
	ImageView image;
	ImageButton share;
	int x = 0;
	
	private UiLifecycleHelper uiHelper;
	private FacebookDialog.Callback callback = new FacebookDialog.Callback(){
		@Override
		public void onComplete(PendingCall pendingCall, Bundle data) {
			boolean success = FacebookDialog.getNativeDialogDidComplete(data);
			Log.i("Script", "Successo! "+success);
		}

		@Override
		public void onError(PendingCall pendingCall, Exception error, Bundle data) {
			Log.i("Script", "Falhou!"+error.toString());
			Toast.makeText(MainActivity.this, "Ocorreu um erro. Tente novamente",Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);
        
        /*Pic a = new Pic("/storage/emulated/0/Pictures/Screenshots/al.jpg", "Cartaz Festa de Revinhade");
        Pic b1 = new Pic("/storage/emulated/0/Download/cesium.jpg","CeSIUM T-Shirt");
        Pics.add(a);
        Pics.add(b1);*/
        fillList(); 
        clickItem();
        
        Button b = (Button) findViewById(R.id.selectPic);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectImage();
			}	
		});        
    }
	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}
	
	// SHARE
			public void shareContent(View view, int position){
				/*if(FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
					FacebookDialog sd = new FacebookDialog.ShareDialogBuilder(this)
						.setName("ABC")
						.setCaption("Caption: abc")
						.setDescription("Description: 123")
						.setLink("http://www.abc.com")
						.setPicture("http://www.clahrc-ndl.nihr.ac.uk/clahrc-ndl-nihr/images-multimedia/Primary-Care/ABC-study.jpg")
						.build();
					
					uiHelper.trackPendingDialogCall(sd.present());
				}*/
				
				if(FacebookDialog.canPresentOpenGraphActionDialog(this, FacebookDialog.OpenGraphActionDialogFeature.OG_ACTION_DIALOG)){
					
					Pic mypic = Pics.get(position);
					
					File imgFile = new  File(mypic.getImagem());
					Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					List<Bitmap> bitmaps = new ArrayList<Bitmap>();
					bitmaps.add(bitmap);
					
					OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
					action.setProperty("photo", "http://samples.ogp.me/280797828783543");
					
					FacebookDialog sd = new FacebookDialog.OpenGraphActionDialogBuilder(this, action, "serafimpintoapp:share", "photo")
						.setImageAttachmentsForAction(bitmaps, true)
						.build();
					
					uiHelper.trackPendingDialogCall(sd.present());
				}
			}
			
	Uri mCapturedImageURI;
	
    private void selectImage() {
        final CharSequence[] items = { "Câmara", "Imagens", "Cancelar"};
 
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Adiciona");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Câmara")) {
                	x += 1;
                    try {
                    	//File f = new File(Environment.getExternalStorageDirectory().toString()+"/FaceShare/");
                        //String fileName = "temp.jpg";
                        ContentValues values = new ContentValues();
                        //values.put(MediaStore.Images.Media.TITLE, f.getAbsolutePath()+fileName);
                        mCapturedImageURI = getContentResolver()
                        		.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,mCapturedImageURI);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (Exception e) {
                        Log.e("", "", e);
                    }
                    
                } else if (items[item].equals("Imagens")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Escolhe uma foto"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();   
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	uiHelper.onActivityResult(requestCode, resultCode, data, callback);
    	if(requestCode == REQUEST_CAMERA) { 
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String path = cursor.getString(column_index_data);

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
    		byte[] byteArray = stream.toByteArray();
    	
    		Intent intent = new Intent(MainActivity.this, AddPicActivity.class);
    		intent.putExtra("picture", byteArray);
    		intent.putExtra("path",path);
    		startActivityForResult(intent,ACTION_NEW);
    	}
    	if(requestCode == SELECT_FILE) {
    		Uri selectedImage = data.getData();
    		String[] colunas = {MediaStore.Images.Media.DATA};
    		
    		Cursor cursor = getContentResolver().query(selectedImage, colunas, null, null, null);
    		cursor.moveToFirst();
    		
    		int indexColuna = cursor.getColumnIndex(colunas[0]);
    		String path = cursor.getString(indexColuna);
    		cursor.close();
    		
    		Bitmap bitmap = BitmapFactory.decodeFile(path);
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
    		byte[] byteArray = stream.toByteArray();
    		
    		Intent intent = new Intent(MainActivity.this, AddPicActivity.class);
    		intent.putExtra("picture", byteArray);
    		intent.putExtra("path", path);
    		startActivityForResult(intent,ACTION_NEW);
    	}
    	if(resultCode == RESULT_OK) { 
			switch (requestCode) {
			case ACTION_NEW: {
				p = (Pic) data.getSerializableExtra("pic");
				Pics.add(p);
				fillList();
			} break;
			}
		}
		}

    private void fillList() {
		ArrayAdapter<Pic> a = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(a);
    }
    
    private class MyListAdapter extends ArrayAdapter<Pic> {

		public MyListAdapter() {
			super(MainActivity.this,R.layout.item_view, Pics);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			//
			View itemView = convertView;
			if(itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent,false);
			}
			
			p = Pics.get(position);
    		
			File imgFile = new  File(p.getImagem());
			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    		image = (ImageView) itemView.findViewById(R.id.imageList);
			image.setImageBitmap(bitmap);
			
			TextView titulo = (TextView) itemView.findViewById(R.id.textList);
			titulo.setText(p.getTitulo());
			return itemView;
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.exit) {
        	System.exit(1);
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void clickItem() {
    	ListView list = (ListView) findViewById(R.id.list);
    	list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		
			public void onItemClick(AdapterView<?> parent, View viewClicked, int pos,
					long id) {
				Pic clicked = Pics.get(pos);
				shareContent(viewClicked,pos);
				//post(pos);
			}
		});
    }
    
    public void post(int pos) {
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.setType("image/");
    	
    	Pic mypic = Pics.get(pos);
		
		File imgFile = new  File(mypic.getImagem());
		Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		
		Uri uri = Uri.fromFile(imgFile);
	    intent.putExtra(Intent.EXTRA_STREAM, uri);
    	boolean facebookAppFound = false;
    	List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
    	for (ResolveInfo info : matches) {
    	    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
    	        intent.setPackage(info.activityInfo.packageName);
    	        facebookAppFound = true;
    	        break;
    	    }
    	}
    	if(!facebookAppFound)
    		Toast.makeText(this, "Facebook não encontrado. Por favor instale", Toast.LENGTH_SHORT).show();
    	else {
    		startActivity(intent);
    	}
    }
}
