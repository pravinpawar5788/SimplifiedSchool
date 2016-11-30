package com.simplifiedschooling.app.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.simplifiedschooling.app.R;



public class CustomDailog {
	
	//Alert Dialog with 
	public static void displayDialog(int resId,Context context)
		 {
			  AlertDialog.Builder builder = new AlertDialog.Builder(context);
			  
			  builder.setTitle(R.string.dialog_title)
			  		 .setMessage(resId)
			  		 .setCancelable(true)
			         .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			             public void onClick(DialogInterface dialog, int id) {
			            	 dialog.dismiss();
			             }
			         });
			 builder.show();
		 }
	
	public static void displayErrorDialog(String errormessage,Context context)
	 {
		  AlertDialog.Builder builder = new AlertDialog.Builder(context);
		  
		  builder.setTitle(R.string.dialog_title)
		  		 .setMessage(errormessage)
		  		 .setCancelable(true)
		         .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		            	 dialog.dismiss();
		             }
		         });
		 builder.show();
	 }

}
