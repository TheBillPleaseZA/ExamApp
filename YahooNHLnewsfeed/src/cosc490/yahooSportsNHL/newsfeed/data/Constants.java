package cosc490.yahooSportsNHL.newsfeed.data;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "NHL_News";

   // Columns in the nhl_news database
   public static final String TITLE = "title";
   public static final String DES = "des";
   public static final String LINK = "link";
   public static final String PUBDATE = "pubDate";
}
