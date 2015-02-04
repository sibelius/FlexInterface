package br.icmc.contact.loweducation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import br.icmc.contact.ContactOpenCOM;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertMoreInfoActivity extends ElderlyActivity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.le_insert_more_info);
        
        Util.getUtil().say(getResources().getString(R.string.insert_other_info));
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 1) {
	    	setResult(resultCode);
	    	finish();
    	}
    }
    
    public void click_photo(View view) {
    	//InteractionLogging.getInstance().logClick((Button) view);
    	InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "Escolha a foto");
    	
    	try {
			startActivityForResult(new Intent(this, 
				Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().nextScreen())),
				0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	Util.getUtil().vibrate();
    }
    
    public void click_email(View view) {
    	//InteractionLogging.getInstance().logClick((Button) view);
    	InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "Informe o E-mail");
    	
    	startActivityForResult(new Intent(this, InsertEmailActivity.class), 0);
    	Util.getUtil().vibrate();
    }
    
    public void click_address(View view) {
    	//InteractionLogging.getInstance().logClick((Button) view);
    	InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "Informe o endereço");
    	
    	startActivityForResult(new Intent(this, InsertAddressActivity.class),0);
    	Util.getUtil().vibrate();
    }
}
