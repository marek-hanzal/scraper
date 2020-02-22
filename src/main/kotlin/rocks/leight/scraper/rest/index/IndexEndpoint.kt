package rocks.leight.scraper.rest.index

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.rest.AbstractEndpoint
import rocks.leight.core.rest.Link
import rocks.leight.core.rest.LinkIndex
import rocks.leight.core.rest.LinkType

class IndexEndpoint(container: IContainer) : AbstractEndpoint(container, "/") {
    override suspend fun onGet(call: ApplicationCall) = handle(call) {
        call.respond(LinkIndex(listOf(
                Link("scraper", LinkType.RESOURCE, href("/scraper/{url}")),
                Link("scheduler", LinkType.ACTION, href("/scheduler/{url}"))
        )))
    }
}
