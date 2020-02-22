package rocks.leight.scraper.scraper

import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.joda.time.DateTime
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobManager
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.message.AbstractMessage
import java.net.URL
import java.net.URLEncoder

class GcMessage : AbstractMessage("gc", "scraper")

class ScraperService(container: IContainer) {
    private val scraperServiceConfig: ScraperServiceConfig by container.lazy()
    private val logger = KotlinLogging.logger {}
    private val storage: IStorage by container.lazy()
    private val jobManager: IJobManager by container.lazy()

    fun scrape(url: String, ttl: DateTime? = null): PageEntity {
        return storage.transaction {
            PageEntity.find { PageTable.url eq url }.firstOrNull()?.let {
                if (ttl ?: DateTime() < DateTime()) {
                    this@ScraperService.logger.debug { "Scrape: Expired page [$url]; planning garbage collection." }
                    jobManager.schedule(GcMessage())
                    return@let
                }
                this@ScraperService.logger.debug { "Scrape: Cached page [$url]; expires on [${it.ttl}]." }
                return@transaction it
            }
            val chromie = scraperServiceConfig.chromie + URLEncoder.encode(url, "UTF-8")
            this@ScraperService.logger.debug { "Scrape: [$url] Downloading from [$chromie]" }
            val start = System.currentTimeMillis()
            var exception: Throwable? = null
            for (index in 0 until 5) {
                try {
                    val content = URL(chromie).openConnection().apply { connectTimeout = 5000; readTimeout = scraperServiceConfig.timeout }.getInputStream().use { it.bufferedReader().readText() }
                    this@ScraperService.logger.debug { "Scrape: [$url] Done in ${System.currentTimeMillis() - start}ms" }
                    return@transaction PageEntity.new {
                        this.url = url
                        this.content = content
                        this.ttl = ttl
                    }
                } catch (e: Throwable) {
                    this@ScraperService.logger.debug { "Scrape: [$url] Attempt #${index + 1} failed, retrying" }
                    exception = e
                }
            }
            this@ScraperService.logger.warn("Scrape: [$url] 5 Attempts failed, failing [${exception?.message}]", exception)
            throw exception!!
        }
    }

    fun gc() {
        this@ScraperService.logger.debug { "Gc: Deleting dead pages." }
        storage.transaction {
            PageTable.deleteWhere {
                PageTable.ttl lessEq DateTime()
            }.also {
                this@ScraperService.logger.debug { "Gc: Deleted #$it dead pages." }
            }
        }
    }
}
