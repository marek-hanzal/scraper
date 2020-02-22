@file:Suppress("unused", "UNUSED_PARAMETER")

package rocks.leight.scraper.pub

import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.message.Handler
import rocks.leight.core.api.message.IMessage
import rocks.leight.core.message.AbstractMessage
import rocks.leight.core.message.AbstractMessageService
import rocks.leight.scraper.scraper.ScraperService

data class ScrapeMessage(val url: String) : AbstractMessage("scrape", "scraper")

class ScraperMessageService(container: IContainer) : AbstractMessageService<ScraperMessageService>() {
    private val scraperService: ScraperService by container.lazy()

    override fun getTarget(): String? = "scraper"

    @Handler("scrape")
    fun onScrapeMessage(message: ScrapeMessage): IMessage? {
        scraperService.scrape(message.url)
        return null
    }

    @Handler("gc")
    fun onGarbageMessage(message: IMessage): IMessage? {
        scraperService.gc()
        return null
    }
}
