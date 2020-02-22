package rocks.leight.scraper.config

import rocks.leight.core.job.JobConfig
import rocks.leight.core.pool.PoolConfig
import rocks.leight.core.server.HttpServerConfig
import rocks.leight.scraper.scraper.ScraperServiceConfig

data class ScraperConfig(
        val version: String,
        val pool: PoolConfig,
        val httpServer: HttpServerConfig,
        val scraper: ScraperServiceConfig,
        val jobConfig: JobConfig
)
