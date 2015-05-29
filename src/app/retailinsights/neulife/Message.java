package app.retailinsights.neulife;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Message extends Dialog implements
		android.view.View.OnClickListener {

	public Activity c;
	public Button yes;
	Context cntxt;
	TextView message;

	public Message(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		cntxt = this.c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message);

		message = (TextView) findViewById(R.id.tvMessage);
		message.setText(SessionStorage.message);

		yes = (Button) findViewById(R.id.btnLogin);
		yes.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			this.dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}
}