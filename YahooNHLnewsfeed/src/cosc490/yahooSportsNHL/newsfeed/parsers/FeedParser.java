package cosc490.yahooSportsNHL.newsfeed.parsers;
import java.util.List;

import cosc490.yahooSportsNHL.newsfeed.main.YahooItem;


public interface FeedParser {
	List<YahooItem> parse();
}
