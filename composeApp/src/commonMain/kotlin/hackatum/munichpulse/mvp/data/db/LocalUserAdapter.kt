package hackatum.munichpulse.mvp.data.db

import app.cash.sqldelight.ColumnAdapter

/**
 * Adapter for mapping database types to Kotlin types for LocalUser table.
 */
class LocalUserAdapter {
    /**
     * Adapter for converting Boolean to Long (INTEGER) and vice versa.
     */
    val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
        override fun decode(databaseValue: Long): Boolean {
            return databaseValue != 0L
        }

        override fun encode(value: Boolean): Long {
            return if (value) 1L else 0L
        }
    }
}