package rocks.leight.scraper

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import mu.KotlinLogging
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobManager
import rocks.leight.core.api.message.IMessageBus
import rocks.leight.core.api.server.IHttpServer
import rocks.leight.core.api.upgrade.IUpgradeManager
import rocks.leight.core.api.upgrade.IVersionService
import rocks.leight.core.container.ContainerFactory
import rocks.leight.core.job.JobConfig
import rocks.leight.core.pool.PoolConfig
import rocks.leight.core.server.HttpServerConfig
import rocks.leight.core.upgrades.JobUpgrade
import rocks.leight.core.utils.asStamp
import rocks.leight.scraper.config.ScraperConfig
import rocks.leight.scraper.http.IndexHttpModule
import rocks.leight.scraper.http.ScraperHttpModule
import rocks.leight.scraper.pub.ScraperMessageService
import rocks.leight.scraper.scraper.ScraperServiceConfig
import rocks.leight.scraper.upgrade.u2019_01_26
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime

class Scraper(container: IContainer) {
    private val scraperConfig: ScraperConfig by container.lazy()
    private val upgradeManager: IUpgradeManager by container.lazy()
    private val versionService: IVersionService by container.lazy()
    private val jobManager: IJobManager by container.lazy()
    private val httpServer: IHttpServer by container.lazy()
    private val logger = KotlinLogging.logger {}

    fun run() = measureTimeMillis {
        logger.info { "Starting Scraper: version: ${scraperConfig.version}, upgrade: ${versionService.getVersion()}" }
        versionService.getCollection().toList().apply {
            logger.info { if (count() > 0) "Installed upgrades:" else "Initial application state" }
        }.forEach {
            logger.info { "\t\tstamp: [${it.stamp.asStamp()}], version: ${it.version}" }
        }
        if (upgradeManager.upgrade() > 0) {
            logger.info { "Installed upgrades:" }
            versionService.getCollection().forEach {
                logger.info { "\t\tstamp: [${it.stamp.asStamp()}], version: ${it.version}" }
            }
        }
        jobManager.start()
    }.also {
        logger.info { "Boobstrap time ${it}ms" }
        httpServer.start("Scraper Server [${versionService.getVersion()}]")
    }
}

@ExperimentalTime
fun main() {
    ContainerFactory.container().apply {
        register(ScraperConfig::class) { ConfigFactory.load().extract("scraper") }
        register(PoolConfig::class) { create(ScraperConfig::class).pool }
        register(HttpServerConfig::class) { create(ScraperConfig::class).httpServer }
        register(ScraperServiceConfig::class) { create(ScraperConfig::class).scraper }
        register(JobConfig::class) { create(ScraperConfig::class).jobConfig }
        configurator(IUpgradeManager::class) {
            register(JobUpgrade::class)
            register(u2019_01_26::class)
        }
        configurator(IMessageBus::class) {
            register(ScraperMessageService::class)
        }
        configurator(IHttpServer::class) {
            register(IndexHttpModule::class)
            register(ScraperHttpModule::class)
        }
        create(Scraper::class).run()
    }
}
