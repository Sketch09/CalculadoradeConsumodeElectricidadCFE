package com.example.calculadoradeconsumodeelectricidadcfe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculadoradeconsumodeelectricidadcfe.LineChartView.DataPoint


class StatisticsActivity : AppCompatActivity() {


    lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        dbHelper = DatabaseHelper(this)  // Assuming DatabaseHelper is your SQLiteOpenHelper class

        val lineChartView = findViewById<LineChartView>(R.id.lineChart)
        val dataPoints = fetchDataPoints()  // Fetch the data points from the database


        lineChartView.setDataPoints(dataPoints) // Update the chart with fetched data points
    }

    private fun fetchDataPoints(): List<DataPoint> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT valor_kWh, fecha_hora FROM LecturasMedidor 
        ORDER BY valor_kWh ASC 
        LIMIT 10
    """
        val cursor = db.rawQuery(query, null)
        val dataPoints = mutableListOf<DataPoint>()

        val valueIndex = cursor.getColumnIndex("valor_kWh")
        val dateIndex = cursor.getColumnIndex("fecha_hora")

        if (valueIndex == -1 || dateIndex == -1) {
            throw IllegalStateException("Database column not found")
        }

        while (cursor.moveToNext()) {
            val value = cursor.getFloat(valueIndex)
            val date = cursor.getString(dateIndex)
            dataPoints.add(DataPoint(value, date))
        }
        cursor.close()
        db.close()

        return dataPoints
    }

}

