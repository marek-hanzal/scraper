package rocks.leight.scraper.rest.scraper

import io.ktor.application.ApplicationCall
import io.ktor.response.respondText
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.rest.AbstractEndpoint
import rocks.leight.scraper.scraper.ScraperService

class ScraperEndpoint(container: IContainer) : AbstractEndpoint(container, "/scraper/{url}") {
    private val scraperService: ScraperService by container.lazy()

    override suspend fun onPost(call: ApplicationCall) = handle(call) { call.respondText(scraperService.scrape(call.parameters["url"]!!).content) }
}
