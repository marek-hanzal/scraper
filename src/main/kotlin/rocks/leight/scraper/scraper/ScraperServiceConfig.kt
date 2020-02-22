package rocks.leight.scraper.scraper

data class ScraperServiceConfig(
        val chromie: String,
        val timeout: Int = 5000,
        val ttl: Int? = null
)
