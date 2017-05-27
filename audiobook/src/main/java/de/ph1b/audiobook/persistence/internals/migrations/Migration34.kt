package de.ph1b.audiobook.persistence.internals.migrations

import android.database.sqlite.SQLiteDatabase

/**
 * Due to a bug negative book ids were inserted
 *
 * @author Paul Woitaschek
 */
class Migration34 : Migration {

  private val TABLE_NAME = "tableBooks"

  override fun migrate(db: SQLiteDatabase) {
    db.delete(TABLE_NAME, "bookId<=-1", null)
  }
}