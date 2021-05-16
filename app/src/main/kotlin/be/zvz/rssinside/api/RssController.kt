package be.zvz.rssinside.api
import be.zvz.kotlininside.KotlinInside
import be.zvz.kotlininside.api.article.ArticleList
import be.zvz.kotlininside.http.DefaultHttpClient
import be.zvz.kotlininside.http.HttpException
import be.zvz.kotlininside.session.user.Anonymous
import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Item
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/{gallId}/rss")
class RssController @Autowired internal constructor() {
    private val log = LoggerFactory.getLogger(RssController::class.java)
    private val dateFormats = listOf(
        SimpleDateFormat("HH:mm"),
        SimpleDateFormat("y.MM.dd")
    )

    private fun tryParseDate(dateString: String): Date {
        dateFormats.forEach { dateFormat ->
            try {
                return dateFormat.parse(dateString)
            } catch (ignored: ParseException) {
            }
        }
        return Date()
    }

    fun getRssList(@PathVariable gallId: String): Channel {
        val articleListRequest = ArticleList(gallId).apply {
            try {
                request()
            } catch (httpException: HttpException) {
                throw GalleryNotFoundException(gallId)
            }
        }

        val galleryInfo = articleListRequest.getGallInfo()
        val channel = Channel("rss_2.0")
        channel.title = galleryInfo.title
        channel.description = galleryInfo.title
        channel.generator = galleryInfo.title
        channel.link = "https://gall.dcinside.com/list.php?id=$gallId"
        channel.pubDate = Date()

        channel.items = mutableListOf<Item>().apply {
            articleListRequest.getGallList().forEach { articleInfo ->
                add(
                    Item().apply {
                        title = articleInfo.subject
                        author = if ("" != articleInfo.ip) {
                            "${articleInfo.name} (${articleInfo.ip})"
                        } else {
                            articleInfo.name
                        }
                        pubDate = tryParseDate(articleInfo.dateTime)
                        link = "https://gall.dcinside.com/board/view/?id=$gallId&no=${articleInfo.identifier}"
                    }
                )
            }
        }
        return channel
    }

    @GetMapping(path = ["/json"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getRssListJson(@PathVariable gallId: String): Channel {
        return getRssList(gallId)
    }

    @GetMapping(produces = [MediaType.APPLICATION_XML_VALUE])
    @ResponseBody
    fun getRssListXml(@PathVariable gallId: String): Channel {
        return getRssList(gallId)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    internal class GalleryNotFoundException(gallId: String) : RuntimeException("could not find gallery '$gallId'.")

    init {
        log.info("init - KotlinInside")
        KotlinInside.createInstance(
            Anonymous(
                "ㅇㅇ",
                "1234"
            ),
            DefaultHttpClient(), true
        )
        dateFormats.forEach {
            it.isLenient = false
        }
        log.info("done - KotlinInside")
    }
}
