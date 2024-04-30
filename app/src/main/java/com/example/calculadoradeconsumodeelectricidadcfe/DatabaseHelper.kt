package com.example.calculadoradeconsumodeelectricidadcfe
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ElectricityDB"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USUARIOS_TARIFAS_TABLE = """
            CREATE TABLE IF NOT EXISTS UsuariosTarifas (
                user_id INTEGER PRIMARY KEY,
                tarifa_id TEXT,
                FOREIGN KEY (tarifa_id) REFERENCES TarifasCFE(tarifa_id)
            );
        """.trimIndent()

        val CREATE_LECTURAS_MEDIDOR_TABLE = """
            CREATE TABLE IF NOT EXISTS LecturasMedidor (
                lectura_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                fecha_hora TEXT,
                valor_kWh REAL,
                FOREIGN KEY (user_id) REFERENCES UsuariosTarifas(user_id)
            );
        """.trimIndent()

        val CREATE_TARIFAS_CFE_TABLE = """
            CREATE TABLE IF NOT EXISTS TarifasCFE (
                tarifa_id TEXT,
                tipo_consumo TEXT,
                precio_kWh REAL,
                temporada TEXT DEFAULT 'Todo el año',
                PRIMARY KEY (tarifa_id, tipo_consumo, temporada)
            );
        """.trimIndent()

        db.execSQL(CREATE_USUARIOS_TARIFAS_TABLE)
        db.execSQL(CREATE_LECTURAS_MEDIDOR_TABLE)
        db.execSQL(CREATE_TARIFAS_CFE_TABLE)

        insertInitialData(db)
    }

     override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS UsuariosTarifas")
        db.execSQL("DROP TABLE IF EXISTS LecturasMedidor")
        db.execSQL("DROP TABLE IF EXISTS TarifasCFE")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val insertData = """
            INSERT INTO TarifasCFE (tarifa_id, tipo_consumo, precio_kWh, temporada) VALUES
            ('1', 'Básico', 0.793, 'Todo el año'),
            ('1', 'Intermedio', 0.956, 'Todo el año'),
            ('1', 'Excedente', 2.802, 'Todo el año'),
            ('1A', 'Básico', 0.793, 'Todo el año'),
            ('1A', 'Intermedio', 0.956, 'Todo el año'),
            ('1A', 'Excedente', 2.802, 'Todo el año'),
            ('1B', 'Básico', 0.793, 'Todo el año'),
            ('1B', 'Intermedio', 0.956, 'Todo el año'),
            ('1B', 'Excedente', 2.802, 'Todo el año'),
            ('1C', 'Básico', 0.697, 'Verano'),
            ('1C', 'Intermedio bajo', 0.822, 'Verano'),
            ('1C', 'Intermedio alto', 1.05, 'Verano'),
            ('1C', 'Excedente', 2.802, 'Verano'),
            ('1C', 'Básico', 0.793, 'Fuera de verano'),
            ('1C', 'Intermedio', 0.956, 'Fuera de verano'),
            ('1C', 'Excedente', 2.802, 'Fuera de verano');
        """.trimIndent()

        db.execSQL(insertData)
    }
    fun insertMeterReading(value: Double, dateTime: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("user_id", 1)  // Assuming a fixed user ID for simplicity
        values.put("fecha_hora", dateTime)
        values.put("valor_kWh", value)

        val result = db.insert("LecturasMedidor", null, values)
        db.close()
        return (result != -1L)
    }

    fun getAllMeterReadings(): List<Pair<String, Double>> {
        val dataList = mutableListOf<Pair<String, Double>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT fecha_hora, valor_kWh FROM LecturasMedidor ORDER BY fecha_hora", null)
        if (cursor.moveToFirst()) {
            val dateIndex = cursor.getColumnIndex("fecha_hora")
            val valueIndex = cursor.getColumnIndex("valor_kWh")

            // Check if indices are valid
            if (dateIndex != -1 && valueIndex != -1) {
                do {
                    val date = cursor.getString(dateIndex)
                    val value = cursor.getDouble(valueIndex)
                    dataList.add(Pair(date, value))
                } while (cursor.moveToNext())
            } else {
                Log.e("DatabaseHelper", "Error: Column not found.")
            }
        }
        cursor.close()
        db.close()
        return dataList
    }
    fun clearLecturasMedidor(): Boolean {
        val db = writableDatabase
        return try {
            db.execSQL("DELETE FROM LecturasMedidor")  // SQL command to clear the table
            true
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error clearing database", e)
            false
        }
    }

    fun getTariffInfo(tariffId: String): List<Tariff> {
        val tariffs = mutableListOf<Tariff>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
        SELECT tipo_consumo, precio_kWh FROM TarifasCFE 
        WHERE tarifa_id = ?
    """, arrayOf(tariffId))

        while (cursor.moveToNext()) {
            val type = cursor.getString(cursor.getColumnIndexOrThrow("tipo_consumo"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_kWh"))
            tariffs.add(Tariff(type, price))
        }
        cursor.close()
        db.close()
        return tariffs
    }
    fun getOldestAndNewestReadings(): Pair<Double, Double> {
        val db = this.readableDatabase
        val queryOldest = "SELECT valor_kWh FROM LecturasMedidor ORDER BY fecha_hora ASC LIMIT 1"
        val queryNewest = "SELECT valor_kWh FROM LecturasMedidor ORDER BY fecha_hora DESC LIMIT 1"

        val cursorOldest = db.rawQuery(queryOldest, null)
        val oldestReading = if (cursorOldest.moveToFirst()) cursorOldest.getDouble(0) else 0.0
        cursorOldest.close()

        val cursorNewest = db.rawQuery(queryNewest, null)
        val newestReading = if (cursorNewest.moveToFirst()) cursorNewest.getDouble(0) else 0.0
        cursorNewest.close()

        db.close()
        return Pair(oldestReading, newestReading)
    }


    data class Tariff(val type: String, val price: Double)




}
