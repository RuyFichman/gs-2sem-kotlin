package com.ruy.vielasegura.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ruy.vielasegura.data.local.dao.AlertaDao
import com.ruy.vielasegura.data.local.dao.CasaDao
import com.ruy.vielasegura.data.local.dao.SensorDao
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import com.ruy.vielasegura.data.local.entity.CasaEntity
import com.ruy.vielasegura.data.local.entity.SensorEntity

@Database(
    entities = [CasaEntity::class, SensorEntity::class, AlertaEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun casaDao(): CasaDao
    abstract fun sensorDao(): SensorDao
    abstract fun alertaDao(): AlertaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obter(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viela_segura.db",
                ).addMigrations(MIGRACAO_1_2).build().also { instancia = it }
            }

        /** Versão 2: coluna pediuAjuda para a resposta "Preciso de ajuda". Mantém os dados já salvos. */
        val MIGRACAO_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE alertas ADD COLUMN pediuAjuda INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
