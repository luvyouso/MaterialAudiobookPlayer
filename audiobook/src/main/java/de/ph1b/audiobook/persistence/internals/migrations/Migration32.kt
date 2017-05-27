package de.ph1b.audiobook.persistence.internals.migrations

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import de.ph1b.audiobook.persistence.internals.asTransaction
import de.ph1b.audiobook.persistence.internals.long
import de.ph1b.audiobook.persistence.internals.mapRows
import de.ph1b.audiobook.persistence.internals.string
import i


class Migration32 : Migration {

  private val BOOKMARK_TABLE_NAME = "tableBookmarks"
  private val BM_PATH = "bookmarkPath"
  private val BM_TITLE = "bookmarkTitle"
  private val BM_TIME = "bookmarkTime"

  private val PATH = "bookmarkPath"
  private val TITLE = "bookmarkTitle"
  private val TABLE_NAME = "tableBookmarks"
  private val TIME = "bookmarkTime"
  private val ID = BaseColumns._ID
  private val CREATE_TABLE_BOOKMARKS = """
    CREATE TABLE $TABLE_NAME (
      $ID INTEGER PRIMARY KEY AUTOINCREMENT,
      $PATH TEXT NOT NULL,
      $TITLE TEXT NOT NULL,
      $TIME INTEGER NOT NULL
    )
  """

  @SuppressLint("Recycle")
  override fun migrate(db: SQLiteDatabase) {
    // retrieve old bookmarks
    val cursor = db.query(BOOKMARK_TABLE_NAME, null, null, null, null, null, null)
    val entries = cursor.mapRows {
      val path = string(BM_PATH)
      val title = string(BM_TITLE)
      val time = long(BM_TIME)
      Holder(path, title, time)
    }
    i { "Restored bookmarks=$entries" }

    // delete table
    db.execSQL("DROP TABLE $BOOKMARK_TABLE_NAME")

    // create new bookmark scheme
    db.execSQL(CREATE_TABLE_BOOKMARKS)
    i { "Created $CREATE_TABLE_BOOKMARKS" }

    // add old bookmarks to new bookmark scheme
    db.asTransaction {
      entries.forEach {
        val cv = ContentValues().apply {
          put(PATH, it.path)
          put(TITLE, it.title)
          put(TIME, it.time)
        }
        db.insertOrThrow(TABLE_NAME, null, cv)
        i { "Inserted $cv to $TABLE_NAME" }
      }
    }
  }

  private data class Holder(val path: String, val title: String, val time: Long)
}