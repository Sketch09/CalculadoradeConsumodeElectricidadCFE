package com.example.calculadoradeconsumodeelectricidadcfe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

private lateinit var txtConsumptionValue: TextView
private lateinit var dbHelper: DatabaseHelper
private lateinit var sharedPreferences: SharedPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE)
        txtConsumptionValue = findViewById(R.id.txtConsumptionValue)
        calculateTotalCost()


        txtConsumptionValue = findViewById(R.id.txtConsumptionValue)

        val btnEnterReadings = findViewById<Button>(R.id.btnEnterReadings)
        btnEnterReadings.setOnClickListener {
            startActivity(Intent(this, EnterReadingsActivity::class.java))
        }

        val btnViewStatistics = findViewById<Button>(R.id.btnViewStatistics)
        btnViewStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }
    private fun calculateTotalCost() {
        val selectedFee = sharedPreferences.getString("SelectedFee", "1") ?: "1"
        val tariffInfo = dbHelper.getTariffInfo(selectedFee)
        val (oldestReading, newestReading) = dbHelper.getOldestAndNewestReadings()


        val consumedKWh = newestReading - oldestReading


        val totalCost = calculateCostByCategory(consumedKWh)


        txtConsumptionValue.text = "Tu consumo es de: $totalCost"
    }

    private fun calculateCostByCategory(totalKWh: Double): Double {
        // Límites de consumo para cada categoría
        val basicLimit = 250.0
        val intermediateLimit = 450.0


        val basicPrice = 1.015
        val intermediatePrice = 1.239
        val exceedingPrice = 3.620
        // Calculando el consumo en cada categoría
        val basicKWh = minOf(totalKWh, basicLimit)
        val intermediateKWh = if (totalKWh > basicLimit) minOf(totalKWh - basicLimit, intermediateLimit - basicLimit) else 0.0
        val exceedingKWh = if (totalKWh > intermediateLimit) totalKWh - intermediateLimit else 0.0

        // Calculando el costo total
        val totalCost = (basicKWh * basicPrice) + (intermediateKWh * intermediatePrice) + (exceedingKWh * exceedingPrice)
        return totalCost
    }

}