package cosc490.yahooSportsNHL.newsfeed.main;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import cosc490.yahooSportsNHL.newsfeed.R;
import cosc490.yahooSportsNHL.newsfeed.data.YahooData;
import cosc490.yahooSportsNHL.newsfeed.parsers.AndroidSaxFeedParser;

public class YahooService extends Service {

	public static final String REDDIT_SERVICE = "cosc490.yahooSportsNHL.newsfeed.main.YahooService.SERVICE";
	private String feedURL = "http://sports.yahoo.com/nhl/rss.xml";
	private List<YahooItem> yahoos;
	private YahooData yahoodata;

	@Override
	public void onCreate() {
		super.onCreate();
	    yahoodata = new YahooData(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doServiceStart(intent, startId);
	    return Service.START_REDELIVER_INTENT;
	}
	
	public void doServiceStart(Intent intent, int startID) {
		checkFeed();
		System.out.println("Running service...");
	}
	
	private void checkFeed(){
    	try{
    	
	    	AndroidSaxFeedParser parser = new AndroidSaxFeedParser(feedURL);
	    	yahoos = parser.parse();
	    	
	       	String link = yahoos.get(0).getLink().toString();
	        System.out.println("The title" + link);
	        
	        SQLiteDatabase db = yahoodata.getReadableDatabase();
	        
	        
	        Cursor cursor = db.rawQuery("select 1 from NHL_News where link=?", new String[] {link});
	        boolean exists = (cursor.getCount() > 0);
	        cursor.close();
	        db.close();
	           
	        if (!exists){
	        	doNotification();
	        }
    	} catch (Throwable t){}
    }
	
	private void doNotification(){
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.yahoo_notify;
		CharSequence tickerText = "New NHL News articles!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "New NHL news from Yahoo Sports!";
		CharSequence contentText = "Your source for the most up-to-date news in the NHL";
		Intent notificationIntent = new Intent(this, YahooMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(100, notification);
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
