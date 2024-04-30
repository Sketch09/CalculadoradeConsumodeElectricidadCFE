package com.example.calculadoradeconsumodeelectricidadcfe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class EnterReadingsActivity : AppCompatActivity() {
    private lateinit var editTextMeterValue: EditText
    private lateinit var editTextDate: EditText  // New EditText for date input
    private lateinit var buttonSend: Button
    private lateinit var buttonClear: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_readings)

        editTextMeterValue = findViewById(R.id.editTextMeterValue)
        editTextDate = findViewById(R.id.editTextDate)  // Initialize the date EditText
        buttonSend = findViewById(R.id.buttonSend)
        buttonClear = findViewById(R.id.buttonClear)
        dbHelper = DatabaseHelper(this)

        buttonSend.setOnClickListener {
            val meterValue = editTextMeterValue.text.toString()
            val dateStr = editTextDate.text.toString()  // Get the user inputted date

            if (meterValue.isNotEmpty() && dateStr.isNotEmpty()) {
                saveReading(meterValue.toDouble(), dateStr)
            } else {
                Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            }
        }

        buttonClear.setOnClickListener {
            clearDatabase()
        }
    }

    private fun saveReading(value: Double, dateStr: String) {
        // Validate the date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            dateFormat.parse(dateStr)  // This will throw an exception if the date is invalid
            val success = dbHelper.insertMeterReading(value, dateStr)
            if (success) {
                Toast.makeText(this, "Reading saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save reading", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearDatabase() {
        val success = dbHelper.clearLecturasMedidor()
        if (success) {
            Toast.makeText(this, "All data cleared successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to clear data", Toast.LENGTH_SHORT).show()
        }
    }

}
