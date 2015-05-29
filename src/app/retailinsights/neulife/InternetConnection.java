package app.retailinsights.neulife;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import app.retailinsights.neulife.R;

public class InternetConnection extends Dialog implements
		android.view.View.OnClickListener {
	
	public Activity c;
	public Dialog d;
	public Button yes;
	Context cntxt;

	public InternetConnection(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		cntxt = this.c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lost_internet_connection);
		initialize();
	}

	public void initialize() {
		yes = (Button) findViewById(R.id.okBtn);
		yes.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okBtn:
			this.dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}
}