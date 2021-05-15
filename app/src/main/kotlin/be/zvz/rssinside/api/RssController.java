package be.zvz.rssinside.api;

import be.zvz.kotlininside.KotlinInside;
import be.zvz.kotlininside.api.article.ArticleList;
import be.zvz.kotlininside.http.DefaultHttpClient;
import be.zvz.kotlininside.http.HttpException;
import be.zvz.kotlininside.session.user.Anonymous;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/{gallId}/rss")
public class RssController {
    private final List<SimpleDateFormat> dateFormats = Arrays.asList(
            new SimpleDateFormat("HH:mm"),
            new SimpleDateFormat("y.MM.dd")
    );
    @NotNull
    private Date tryParseDate(@NotNull final String dateString) {
        for (final SimpleDateFormat dateFormat : dateFormats) {
            try
            {
                return dateFormat.parse(dateString);
            }
            catch (ParseException ignored) {}
        }
        return new Date();
    }

    @Autowired
    RssController() {
        KotlinInside.createInstance(new Anonymous(
                "ㅇㅇ",
                "1234"
        ), new DefaultHttpClient());
    }

    @RequestMapping(method = RequestMethod.GET)
    Channel getRssList(@PathVariable String gallId) {
        Channel channel = new Channel("rss_2.0");
        ArticleList articleListRequest = new ArticleList(gallId);
        try {
            articleListRequest.request();
        } catch (HttpException httpException) {
            throw new GalleryNotFoundException(gallId);
        }
        ArticleList.GallInfo galleryInfo = articleListRequest.getGallInfo();
        channel.setTitle(galleryInfo.getTitle());
        channel.setGenerator(galleryInfo.getTitle());
        channel.setLink("https://gall.dcinside.com/list.php?id=" + gallId);
        Date postDate = new Date();
        channel.setPubDate(postDate);

        List<Item> items = new ArrayList<>();
        List<ArticleList.GallList> articleList = articleListRequest.getGallList();

        for (ArticleList.GallList articleInfo : articleList) {
            Item item = new Item();
            item.setTitle(articleInfo.getSubject());
            if (!"".equals(articleInfo.getIp())) {
                item.setAuthor(articleInfo.getName() + " (" + articleInfo.getIp() + ")");
            } else {
                item.setAuthor(articleInfo.getName());
            }
            item.setPubDate(tryParseDate(articleInfo.getDateTime()));
            item.setLink("https://gall.dcinside.com/board/view/?id=" + gallId + "&no=" + articleInfo.getIdentifier());
            items.add(item);
        }
        channel.setItems(items);
        return channel;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class GalleryNotFoundException extends RuntimeException {

        public GalleryNotFoundException(String gallId) {
            super("could not find gallery '" + gallId + "'.");
        }
    }
}