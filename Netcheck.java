package services;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.varad.URL_OF_SERVER;
import com.example.varad.lib.DatabaseHandler;
import com.example.varad.lib.JSONParser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class Netcheck extends Service {


	static URL_OF_SERVER os = new URL_OF_SERVER();

	static String return_url = os.RETURN_URL();

	private static String url_insert = return_url + "/getTS.php";
	int timestamp=0;

	DatabaseHandler db=new DatabaseHandler(Netcheck.this);


	JSONParser jsonParser = new JSONParser();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Check_Ping();

		Toast.makeText(getApplicationContext(), "Service Running ", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}




	//check internet
	public Boolean Check_Ping(){
		
		Toast.makeText(this, "Pinging ...", Toast.LENGTH_SHORT).show();

		if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		HttpURLConnection connection = null;
		
		
		
		 try{
			 
	            ConnectivityManager connectivityManager = (ConnectivityManager)       
	                                                                      this.getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	            NetworkInfo mobileInfo = 
	                                 connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	            NetworkInfo inet = 
	                    connectivityManager.getActiveNetworkInfo();
	        	Log.i("INET","INET:"+inet);

	        	
	        	
	        	if (inet!=null&&inet.isAvailable()&&(wifiInfo.isConnected() || mobileInfo.isConnected())) {
	            	Log.i("time passsssssssss","in if");
	            	Log.i("INET","INET:"+inet);
	    		    try {                                                                                     
	    		    	
	    		    	
	    		  /*
	    		    	  URL u = new URL(return_url);
	      		        connection = (HttpURLConnection) u.openConnection();
	      		        connection.setRequestProperty("User-Agent", "Android Application:");
	      		        connection.setRequestProperty("Connection", "close");
	      		       connection.setConnectTimeout(10000 ); // mTimeout is in seconds
	    		final HttpParams httpparam=connection.set
	           		connection.connect();
	           		*/
	    		    	HttpGet httpget =new HttpGet(return_url);
	    		    	HttpParams httpparameters=new BasicHttpParams();
	    		    	
	    		    	int t=3000;
	    		    	HttpConnectionParams.setConnectionTimeout(httpparameters, t);
	    		    	int to=5000;
	    		    	HttpConnectionParams.setSoTimeout(httpparameters, to);
	    		    	
	    		    	DefaultHttpClient httpclient=new DefaultHttpClient(httpparameters);
	    		    	HttpResponse respose=httpclient.execute(httpget);
	           	
	           	
	    		       int code=respose.getStatusLine().getStatusCode();
	    		      
	    		       
	    		       Log.i("Res Codeeeeeeeeeeeeeeeeeeeeeeeeee",""+code);
	   		        if ( code== 200) {
	   			    	Toast.makeText(getApplicationContext(), "sucess ping", Toast.LENGTH_SHORT).show();
	   			    	Log.i("getResponseCode == 200","suceses ping");
	   			        
	   			    	new getServerTS().execute();
	   			
	   			    	int ts=db.getSyncTime();
	   			    	Log.i("server timestamp1111111111111", ""+timestamp);
	   			    	Log.i("Local synctimestamp",""+ts);
	   			    	if(timestamp!=0&&ts!=timestamp){
	   			    		Log.i("in if server timestamp",""+timestamp);
	   			    	
	   			    		stopService(new Intent(Netcheck.this, SyncBuyService.class));
	   			    		startService(new Intent(Netcheck.this, SyncBuyService.class));
	   			    		
	   			    		stopService(new Intent(Netcheck.this, Sync.class));
	   			    		startService(new Intent(Netcheck.this, Sync.class));
	   			    		
	   			    		stopService(new Intent(Netcheck.this, SyncRentinService.class));
	   			    		startService(new Intent(Netcheck.this, SyncRentinService.class));
	   			    		
	   			    		
	   			    		stopService(new Intent(Netcheck.this, SyncRentoutService.class));
	   			    		startService(new Intent(Netcheck.this, SyncRentoutService.class));
	   			    		

	   			    		stopService(new Intent(Netcheck.this, SyncTigerService.class));
	   			    		startService(new Intent(Netcheck.this, SyncTigerService.class));
	   			    		
	   			    		
	   			    		Log.i("After srvice call","");
	   			    	
	   			    	}else{
	   			    		
	   			    		Log.i("Service is not call becccc in else","1333333333333");
	   			    	} 
	   			    			
	   			  
	   			    	
	   			    }else{
	   			    	
	   			    	
		    			    Log.i("if !=200 else",""+connection);
	   			    	
	   			    	
	   			    }
	           		
	           		
	    		
	                		
	                	

	    		        
	    		        
	    		  
	    		       
	    			   
	    			} catch (Exception e1) {
	    			    e1.printStackTrace();
	    			    Toast.makeText(getApplicationContext(), "You Don't Have Data Connection avilable", Toast.LENGTH_SHORT).show();
	    			    Log.i("In catch exception",""+connection);
	    			    Log.i("navjot exception","details="+e1);
	    			    Log.i("In catch exception",""+connection);
	    			    return true;
	    			} 

	    			
	            	
	            	
	                return true;
	            }
	            else{
	                Toast.makeText(getApplicationContext(), "Network Not Avilable", Toast.LENGTH_SHORT).show();
	            	Log.i("time passsssssssss","in elsesssssss");
	            }
	        }
	        catch(Exception e){
	        	
	           e.printStackTrace();
	        }
		return null;
		
		
	}


	class getServerTS extends AsyncTask<String, String, String> {


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			
			Log.i("Create Response", "onpreexecte");
			super.onPreExecute();
			Log.i("below super", "onpreexecte");


		}

		
		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", "1234"));
			
			Log.i("in dobackground starting ", ""+timestamp);
			
			JSONObject json = jsonParser.makeHttpRequest(url_insert, "POST",
					params);

			// check log cat fro response
			Log.i("Create Response", json.toString());

			
			try {
				timestamp = json.getInt("timestamp");
				Log.i("in try in asycn timestamp22222222222222", ""+timestamp);
				
			} catch (JSONException e) {
				e.printStackTrace();
				Log.i("in catch in asycn timestamp22222222222222", ""+e);
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * @return 
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			Log.i("server timestamp22222222222222", ""+timestamp);
			Log.i("Create Response", "onpostexecte");
			

		}
	}
	}
