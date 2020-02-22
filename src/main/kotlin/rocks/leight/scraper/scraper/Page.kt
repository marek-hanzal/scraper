package rocks.leight.scraper.scraper

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.joda.time.DateTime
import java.util.*

object PageTable : UUIDTable("page") {
    /**
     * obvious ;)
     */
    val url = varchar("url", 256).index()
    /**
     * scraped content itself, ou yaaay!
     */
    val content = text("content")
    /**
     * download timestamp
     */
    val stamp = datetime("stamp").clientDefault { DateTime() }
    /**
     * optional time to live
     */
    val ttl = datetime("ttl").nullable()
}

class PageEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PageEntity>(PageTable)

    var url by PageTable.url
    var content by PageTable.content
    var stamp by PageTable.stamp
    var ttl by PageTable.ttl
}
