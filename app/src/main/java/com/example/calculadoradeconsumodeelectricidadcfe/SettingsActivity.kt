package com.example.calculadoradeconsumodeelectricidadcfe
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadoradeconsumodeelectricidadcfe.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val spinnerFees = findViewById<Spinner>(R.id.spinnerFees)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.fee_array, // Define this array in strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFees.adapter = adapter
        }

        buttonSave.setOnClickListener {
            val selectedFee = spinnerFees.selectedItem.toString()
            saveFeeSetting(selectedFee)
        }
    }

    private fun saveFeeSetting(fee: String) {
        val sharedPreferences = getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("SelectedFee", fee)
        editor.apply()

        Toast.makeText(this, "Fee saved: $fee", Toast.LENGTH_SHORT).show()
    }
}
