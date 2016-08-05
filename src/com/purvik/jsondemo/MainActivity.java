package com.purvik.jsondemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	final String employee = " { \"employee\" : [ { \"name\": \"Purvik\"," +
			" \"Salary\": \"20000\"," +
			" \"Married\" : \"No\"}," +
			
			"{ \"name\": \"Viken\"," +
					" \"Salary\": \"30000\"," +
					" \"Married\" : \"Yes\"}" +
			"]} ";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void callGetList(View v){
		String finalString = "";
		TextView tvFinalList = (TextView)findViewById(R.id.tvFinalList);
		
		try {
			
			JSONObject array = new JSONObject(employee);
			
			JSONArray jNode = array.optJSONArray("employee");
			
		 
		 for(int a = 0; a < jNode.length(); a++)
         {
             JSONObject childObject = jNode.getJSONObject(a);
             
             finalString += "\nName:" + childObject.optString("name");
             finalString += "\nSalary:" + childObject.optString("Salary");
             finalString += "\nMarried:" + childObject.optString("Married") +"\n";
         
         }
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("JSONString", finalString);
		tvFinalList.setText(finalString);
		
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
		return super.onOptionsItemSelected(item);
	}
}
