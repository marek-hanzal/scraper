package rocks.leight.scraper.http

import io.ktor.application.Application
import io.ktor.routing.routing
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.server.AbstractHttpModule
import rocks.leight.scraper.rest.index.IndexEndpoint

class IndexHttpModule(container: IContainer) : AbstractHttpModule(container) {
    override fun install(application: Application) {
        application.routing {
            endpoint(this, IndexEndpoint::class)
        }
    }
}
