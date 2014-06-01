package cosc490.yahooSportsNHL.newsfeed.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
//import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cosc490.yahooSportsNHL.newsfeed.R;
import cosc490.yahooSportsNHL.newsfeed.data.Constants;
import cosc490.yahooSportsNHL.newsfeed.data.YahooData;
import cosc490.yahooSportsNHL.newsfeed.parsers.AndroidSaxFeedParser;
//import edu.towson.cis.cosc490.wegher.lab4.SwipeDismissListViewTouchListener;

public class YahooMainActivity extends ListActivity {
	private List<YahooItem> yahoos;
	private YahooData yahoodata;
	private boolean serviceFlag;
	AlarmManager alarms;
	private String feedURL = "http://sports.yahoo.com/nhl/rss.xml";
    
	@Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
              
        setContentView(R.layout.main);
        yahoodata = new YahooData(this);       
        loadFeed();
        
        setAlarm();   
        registerForContextMenu(getListView());
        
    } 
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse(yahoos.get(position).getLink().toExternalForm()));
		startActivity(viewMessage);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.context_menu, menu);
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
    	
    	switch(item.getItemId()) {
    	// TEXT
    	case R.id.item1:
    		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    		smsIntent.putExtra("sms_body", "" + yahoos.get((int)info.id).getInfo());
    		smsIntent.putExtra("address", "");
    		smsIntent.setType("vnd.android-dir/mms-sms");
    		startActivity(smsIntent);
    		return true;
    		
    	// EMAIL
    	case R.id.item2:
    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		//mAdapter.getItem((int)info.id));
    		String to = android.content.Intent.EXTRA_EMAIL;
    		String title = android.content.Intent.EXTRA_SUBJECT;
    		String description = android.content.Intent.EXTRA_TEXT;
    		String email = "bwegs14@gmail.com";
    		emailIntent.putExtra(to, email);
    		emailIntent.putExtra(title, "New Yahoo Sports NHL Article");
    		emailIntent.setType("plain/text");
    		emailIntent.putExtra(description, yahoos.get((int)info.id).getInfo());
    		
    		startActivity(emailIntent);
    		return true;
    		
    	// TWEET
    	case R.id.item3:
    		String tweetUrl = "https://twitter.com/intent/tweet?text=" + yahoos.get((int)info.id).getTitle() + "&url="
                    + yahoos.get((int)info.id).getLink();
    		Uri uri = Uri.parse(tweetUrl);
    		startActivity(new Intent(Intent.ACTION_VIEW, uri));
    		
    		
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
	
//	protected void onListItemLongClick() {
//		
//	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater(); 
		inflater.inflate(R.menu.options_menu, menu); 
		return true;
	}
 
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) 
		{
		case R.id.Refresh:
			loadFeed();
		break;
		case R.id.Start: 
	          if(!serviceFlag) setAlarm();
	    break;
		case R.id.Stop: 
	          if(serviceFlag) cancelAlarm();
	    break;
		} 
		return true;
	}
	
	private void setAlarm(){
		serviceFlag = true;
	    Intent downloader = new Intent(this, AlarmReceiver.class);
	    PendingIntent recurringDownload = PendingIntent.getBroadcast(this, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
	    
	    alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	    alarms.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 60000, recurringDownload);
	    System.out.println("Setting alarm...");
	}
	
	private void cancelAlarm(){
		serviceFlag = false;
		Intent downloader = new Intent(this, AlarmReceiver.class);
	    PendingIntent recurringDownload = PendingIntent.getBroadcast(this, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		
	    alarms.cancel(recurringDownload);
	}
	
	private void loadFeed(){
		
        
    	try{
    	
	    	AndroidSaxFeedParser parser = new AndroidSaxFeedParser(feedURL);
	    	yahoos = parser.parse();
	    	
	    	List<String> titles = new ArrayList<String>(yahoos.size());
	    	
	    	for (YahooItem rd : yahoos){
	    		titles.add(rd.getTitle());
	    		
	    		try{
	    			addFarkItem(rd.getDescription(), "" + rd.getTitle(), rd.getLink().toString(), rd.getDate());
	    		}
	    		catch(Exception e){e.printStackTrace();}
	    	}
	    	
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.yahoo_item,titles);
	    	this.setListAdapter(adapter);
	    	
    	} catch (Throwable t){}
    }
		
	private void addFarkItem(String des,String title,String link,String pubDate){
        SQLiteDatabase db = yahoodata.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DES, des);
        values.put(Constants.TITLE, title);
        values.put(Constants.LINK, link);
        values.put(Constants.PUBDATE, pubDate);
        db.insertOrThrow(Constants.TABLE_NAME, null, values);
     }	
}