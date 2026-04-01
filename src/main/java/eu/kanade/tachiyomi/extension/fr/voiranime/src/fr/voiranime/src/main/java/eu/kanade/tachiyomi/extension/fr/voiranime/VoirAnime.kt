package eu.kanade.tachiyomi.extension.fr.voiranime

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SAnime
import eu.kanade.tachiyomi.source.model.SEpisode
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class VoirAnime : ParsedHttpSource() {

    override val name = "VoirAnime"
    override val baseUrl = "https://voir-anime.to"
    override val lang = "fr"
    override val supportsLatest = true

    override fun popularAnimeRequest(page: Int): Request = GET("$baseUrl/page/$page")
    override fun popularAnimeSelector() = "div.card, div.anime-item"
    override fun popularAnimeFromElement(element: Element): SAnime {
        val anime = SAnime.create()
        anime.title = element.select("h3, .title").text()
        anime.thumbnail_url = element.select("img").attr("abs:src")
        anime.url = element.select("a").attr("abs:href")
        return anime
    }

    override fun latestUpdatesRequest(page: Int) = popularAnimeRequest(page)
    override fun latestUpdatesSelector() = popularAnimeSelector()
    override fun latestUpdatesFromElement(element: Element) = popularAnimeFromElement(element)

    override fun searchAnimeRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/?s=$query&page=$page")
    }
    override fun searchAnimeSelector() = popularAnimeSelector()
    override fun searchAnimeFromElement(element: Element) = popularAnimeFromElement(element)

    override fun animeDetailsParse(document: Document): SAnime {
        val anime = SAnime.create()
        anime.title = document.select("h1").text()
        anime.description = document.select(".synopsis, .description").text()
        anime.thumbnail_url = document.select("img.poster").attr("abs:src")
        return anime
    }

    override fun episodeListSelector() = "a.episode-link, li.episode-item a"
    override fun episodeFromElement(element: Element): SEpisode {
        val episode = SEpisode.create()
        episode.name = element.text()
        episode.url = element.attr("abs:href")
        return episode
    }

    override fun videoListParse(document: Document): List<Page> {
        val videoUrl = document.select("video source").attr("abs:src")
        return if (videoUrl.isNotBlank()) listOf(Page(0, "", videoUrl)) else emptyList()
    }
}
