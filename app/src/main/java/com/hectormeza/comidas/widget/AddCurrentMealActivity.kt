package com.hectormeza.comidas.widget

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.hectormeza.comidas.R
import com.hectormeza.comidas.data.repository.ComidasRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Invisible one-tap entry point used by the home widget.
 * Saves the current hour's meal the same way the center button does for "now".
 */
class AddCurrentMealActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContext = applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val repository = ComidasRepository.getInstance(appContext)
                repository.initialize()
                val transaction = repository.addCurrentMealFromClock()
                val mealName = transaction.mealType?.displayName.orEmpty()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        getString(R.string.widget_meal_saved, mealName),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        getString(R.string.widget_meal_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }
}
