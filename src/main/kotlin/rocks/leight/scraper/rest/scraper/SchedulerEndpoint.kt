package rocks.leight.scraper.rest.scraper

import io.ktor.application.ApplicationCall
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.rest.AbstractEndpoint
import rocks.leight.scraper.pub.ScrapeMessage

class SchedulerEndpoint(container: IContainer) : AbstractEndpoint(container, "/scheduler/{url}") {
    override suspend fun onPost(call: ApplicationCall) = handle(call) {
        storage.write { jobManager.schedule(ScrapeMessage(call.parameters["url"]!!)) }
        created(call, href(""))
    }
}
