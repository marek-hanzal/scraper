@file:Suppress("ClassName")

package rocks.leight.scraper.upgrade

import org.jetbrains.exposed.sql.SchemaUtils
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.upgrade.AbstractUpgrade
import rocks.leight.scraper.scraper.PageTable

class u2019_01_26(container: IContainer) : AbstractUpgrade(container) {
    override fun upgrade() {
        storage.transaction {
            SchemaUtils.create(PageTable)
        }
    }
}
