package rocks.leight.scraper.http

import io.ktor.application.Application
import io.ktor.routing.routing
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.server.AbstractHttpModule
import rocks.leight.scraper.rest.scraper.SchedulerEndpoint
import rocks.leight.scraper.rest.scraper.ScraperEndpoint

class ScraperHttpModule(container: IContainer) : AbstractHttpModule(container) {
    override fun install(application: Application) {
        application.routing {
            endpoint(this, ScraperEndpoint::class)
            endpoint(this, SchedulerEndpoint::class)
        }
    }
}
