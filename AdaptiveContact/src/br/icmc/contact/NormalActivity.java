package br.icmc.contact;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class NormalActivity extends Activity {
	private EditText txtFirstName;
	private EditText txtLastName;
	private EditText txtNumberPhone;
	private EditText txtEmailAddress;
	private EditText txtMsgInstant;
	private EditText txtBusiness;
	private EditText txtJobTitle;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal);
        
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
    	txtNumberPhone = (EditText) findViewById(R.id.txtNumberPhone);
    	txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
    	txtMsgInstant = (EditText) findViewById(R.id.txtMsgInstant);
    	txtBusiness = (EditText) findViewById(R.id.txtBusiness);
    	txtJobTitle = (EditText) findViewById(R.id.txtJobTitle);
        
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    
    public void click_save(View view) {
    	String firstName = txtFirstName.getText().toString();
    	String lastName = txtLastName.getText().toString();
    	String numberPhone = txtNumberPhone.getText().toString();
    	String emailAddress = txtEmailAddress.getText().toString();
    	String msgInstant = txtMsgInstant.getText().toString();
    	String business = txtBusiness.getText().toString();
    	String jobTitle = txtJobTitle.getText().toString();
    	
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	int rawContactInsertIndex = ops.size();
    	ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
    	    .withValue(RawContacts.ACCOUNT_TYPE, null)
    	    .withValue(RawContacts.ACCOUNT_NAME, null)
    	    .build());
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
    	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, firstName + " " + lastName)
    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
    	    .build());  
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
    	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numberPhone)
    	    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)                
    	    .build());
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
        	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        	    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailAddress)
        	    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)                
        	    .build());
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
        	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
        	    .withValue(ContactsContract.CommonDataKinds.Im.DATA, msgInstant)
        	    .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_HOME)                
        	    .build());
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
        	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
        	    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, business)
        	    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)                
        	    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
        	    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
        	    .build());
    	try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			
			Toast.makeText(getApplicationContext(), "Contato gravado com sucesso", Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
    	
    	finish();
    }
    
    public void click_cancelar(View view) {
    	finish();
    }
}
