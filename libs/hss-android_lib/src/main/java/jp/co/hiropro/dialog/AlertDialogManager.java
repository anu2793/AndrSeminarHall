package jp.co.hiropro.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.co.hiropro.mylibrary.R;

public class AlertDialogManager {

	private static AlertDialog dialog;
	private static OnClickListener listenerDefault = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	
	public static void show(Context context, String message){
		show(context, null, message);
	}
	
	public static void show(Context context, String title, String message){
		show(context, title, message, context.getString(R.string.btn_accept), listenerDefault, null, null);
	}
	
	public static void show(Context context, String title, String message, String txtAccept){
		show(context, title, message, txtAccept, listenerDefault, null, null);
	}
	
	public static void show(Context context, String title, String message, OnClickListener accept){
		show(context, title, message, context.getString(R.string.btn_accept), accept, null, null);
	}
	
	public static void show(Context context, String title, String message, 
			String txtAccept, OnClickListener accept){
		show(context, title, message, txtAccept, accept, null, null);
	}
	
	public static void show(Context context, String title, String message, 
			OnClickListener accept, OnClickListener cancel){
		show(context, title, message, 
				context.getString(R.string.btn_accept), accept, 
				context.getString(R.string.btn_cancel), cancel);
	}
	
	public static void show(Context context, String title, String message, 
			String txtAccept, OnClickListener accept,
			String txtCancel, OnClickListener cancel){
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(!TextUtils.isEmpty(title))	builder.setTitle(title);
		if(!TextUtils.isEmpty(message))	builder.setMessage(message);
		
		if(accept!=null){
			if(TextUtils.isEmpty(txtAccept)) 
				builder.setPositiveButton(R.string.btn_accept, accept);
			else 
				builder.setPositiveButton(txtAccept, accept);
		}
		if(cancel!=null){
			if(TextUtils.isEmpty(txtCancel))
				builder.setNegativeButton(R.string.btn_cancel, cancel);
			else
				builder.setNegativeButton(txtCancel, cancel);
		}
		dialog = builder.show();
	}
	
	public static void showInputDialog(Context context, int titleID, 
			final StringBuilder carrier, int type, OnDismissListener event){
		showInputDialog(context, context.getString(titleID), null,  carrier, type, event);
	}
	public static void showInputDialog(Context context, int titleID, int messageID,
			final StringBuilder carrier, int type, OnDismissListener event){
		showInputDialog(context, context.getString(titleID), context.getString(messageID), carrier, type, event);
	}

	public static void showInputDialog(Context context, String title, String message,
			final StringBuilder carrier, int type, OnDismissListener event){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.input_dialog);
		dialog.setTitle(title);
		dialog.setCancelable(false);
		TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
		if(message == null)
			txtMessage.setVisibility(View.GONE);
		else
			txtMessage.setText(message);
		final EditText ed = (EditText) dialog.findViewById(R.id.txtInput);
		ed.setInputType(type);
		ed.setText(carrier.toString());
		Button btnAccept = (Button)dialog.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				carrier.delete(0, carrier.length());
				carrier.append(ed.getText().toString().trim());
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(event);
		dialog.show();
	}
}
