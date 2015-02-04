package br.icmc.contact;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

/**
 * Representa um contato
 * @author Sibelius Seraphini
 *
 */
public class Contact {
	private String mName;
	private String mFirstName;
	private String mFamilyName;
	private String mPhone;
	private String mEmail;
	private String mAddress;
	private Bitmap mPhoto;
	
	private static Contact mSingleton;
	
	/**
	 * Só pode existir um único contato por vez
	 * @return
	 */
	private Contact() {
	}
	
	public static Contact getContact() {
		if(mSingleton == null)
			mSingleton = new Contact();
		return mSingleton;
	}
	
	/**
	 * Retorna a ArrayList<ContentProviderOperation> necessário para gravar o contato
	 * @return
	 */
	public int save(ContentResolver contentResolver) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
    	    .withValue(RawContacts.ACCOUNT_TYPE, null)
    	    .withValue(RawContacts.ACCOUNT_NAME, null)
    	    .build());
    	
    	if(mName != "") 
	    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	    	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	    	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
	    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, mName)
	    	    .build());
    	else
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	    	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	    	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    	    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, mFirstName + mFamilyName)
    	    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, mFirstName)
    	    	    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, mFamilyName)
    	    	    .build());
    	
    	if(mPhone != "")
	    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	    	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	    	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
	    	    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mPhone)
	    	    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)                
	    	    .build());
    	
    	if(mEmail != "")
	    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
	            .withValue(ContactsContract.CommonDataKinds.Email.DATA, mEmail)
	            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)                
	            .build());
    	
    	if(mAddress != "")
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        	    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        	    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
        	    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, mAddress)
        	    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)                
        	    .build());
    	
    	
    	if(mPhoto != null) {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	mPhoto.compress(CompressFormat.JPEG, 0, bos);
    		
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            	.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            	//.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO_ID, 0)
            	.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bos.toByteArray())                
            	.build());
    	}
		
    	int status=1;
    	try {
			contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
			status=0;
		} catch (OperationApplicationException e) {
			e.printStackTrace();
			status=0;
		}
    	
    	return status;
	}
	
	/**
	 * Usado para limpar os dados de um contato
	 */
	public void clear() {
		mName = null;
		mFirstName = null;
		mFamilyName = null;
		mPhone = null;
		mEmail = null;
		mAddress = null;
		mPhoto = null;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String Name) {
		this.mName = Name;
	}
	public String getFirstName() {
		return mFirstName;
	}
	public void setFirstName(String FirstName) {
		this.mFirstName = FirstName;
	}
	public String getFamilyName() {
		return mFamilyName;
	}
	public void setFamilyName(String FamilyName) {
		this.mFamilyName = FamilyName;
	}
	public String getPhone() {
		return mPhone;
	}
	public void setPhone(String Phone) {
		this.mPhone = Phone;
	}
	public String getEmail() {
		return mEmail;
	}
	public void setEmail(String Email) {
		this.mEmail = Email;
	}
	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String Address) {
		this.mAddress = Address;
	}
	public Bitmap getPhoto() {
		return mPhoto;
	}
	public void setPhoto(Bitmap Photo) {
		this.mPhoto = Photo;
	}
}
