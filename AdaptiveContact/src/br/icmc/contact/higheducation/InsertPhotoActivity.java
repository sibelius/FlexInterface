package br.icmc.contact.higheducation;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import br.icmc.contact.Contact;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;

public class InsertPhotoActivity extends ElderlyActivity {
	private static final int IMAGE_PICK = 0;

	private ImageButton btnPhoto;
	//private Button mSave;
	private Bitmap mPhoto = null;

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.he_insert_photo);

		btnPhoto = (ImageButton) findViewById(R.id.btn_photo);
/*
		mSave = (Button) findViewById(R.id.save);
		mSave.setEnabled(false);*/
	}

	
	public void onResume() {
		super.onResume();

		Util.getUtil().say(getResources().getString(R.string.insert_photo));
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			click_back(null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void click_save(View view) {
		if (mPhoto == null) {
			DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// Ação quando o usuário clicar no botão sim
						Contact.getContact().setPhoto(mPhoto);

						save_contact();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						// Ação quando o usuário clicar no botão não
						break;
					}
				}
			};

			String msg = getResources().getString(R.string.cancel_send_photo);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg)
					.setPositiveButton(getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.no),
							dialogClickListener).show();
			Util.getUtil().say(msg);
		} else {
			Contact.getContact().setPhoto(mPhoto);

			save_contact();
		}
	}

	public void click_back(View view) {
		DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Ação quando o usuário clicar no botão sim
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// Ação quando o usuário clicar no botão não
					break;
				}
			}
		};

		String msg = getResources().getString(R.string.cancel_send_photo);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
		Util.getUtil().say(msg);
	}

	public void click_photo(View view) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.setType("image/*");
		startActivityForResult(i, IMAGE_PICK);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case IMAGE_PICK:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				mPhoto = BitmapFactory.decodeStream(imageStream);

				btnPhoto.setImageBitmap(mPhoto);
				//mSave.setEnabled(true);
			} else {
/*
				if (mPhoto == null)
					mSave.setEnabled(false);
					*/
			}
		}
	}
}
