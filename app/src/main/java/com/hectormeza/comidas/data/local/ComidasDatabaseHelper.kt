package com.hectormeza.comidas.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.hectormeza.comidas.model.AppCurrency
import com.hectormeza.comidas.model.MealType
import com.hectormeza.comidas.model.Transaction
import com.hectormeza.comidas.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComidasDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        // 1. Transactions Table
        db.execSQL(
            """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COL_TX_ID TEXT PRIMARY KEY,
                $COL_TX_TYPE TEXT NOT NULL,
                $COL_TX_AMOUNT REAL NOT NULL,
                $COL_TX_TIMESTAMP INTEGER NOT NULL,
                $COL_TX_MEAL_TYPE TEXT,
                $COL_TX_QUANTITY INTEGER NOT NULL DEFAULT 1,
                $COL_TX_NOTE TEXT
            )
            """.trimIndent()
        )

        // 2. Meal Prices Table
        db.execSQL(
            """
            CREATE TABLE $TABLE_MEAL_PRICES (
                $COL_PRICE_MEAL_TYPE TEXT PRIMARY KEY,
                $COL_PRICE_AMOUNT REAL NOT NULL
            )
            """.trimIndent()
        )

        // 3. Custom Currencies Table
        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOM_CURRENCIES (
                $COL_CURRENCY_CODE TEXT PRIMARY KEY,
                $COL_CURRENCY_SYMBOL TEXT NOT NULL,
                $COL_CURRENCY_NAME TEXT NOT NULL,
                $COL_CURRENCY_EMOJI TEXT NOT NULL DEFAULT '🪙'
            )
            """.trimIndent()
        )

        // 4. App Settings Table
        db.execSQL(
            """
            CREATE TABLE $TABLE_SETTINGS (
                $COL_SETTING_KEY TEXT PRIMARY KEY,
                $COL_SETTING_VALUE TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEAL_PRICES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOM_CURRENCIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    // --- Transactions Operations ---
    suspend fun getAllTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COL_TX_TIMESTAMP DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(COL_TX_ID))
                val typeStr = it.getString(it.getColumnIndexOrThrow(COL_TX_TYPE))
                val amount = it.getDouble(it.getColumnIndexOrThrow(COL_TX_AMOUNT))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(COL_TX_TIMESTAMP))
                val mealTypeStr = it.getString(it.getColumnIndexOrThrow(COL_TX_MEAL_TYPE))
                val quantity = it.getInt(it.getColumnIndexOrThrow(COL_TX_QUANTITY))
                val note = it.getString(it.getColumnIndexOrThrow(COL_TX_NOTE))

                val type = if (typeStr == "PAYMENT") TransactionType.PAYMENT else TransactionType.MEAL
                val mealType = mealTypeStr?.let { str ->
                    try { MealType.valueOf(str) } catch (e: Exception) { null }
                }

                list.add(
                    Transaction(
                        id = id,
                        type = type,
                        amount = amount,
                        timestamp = timestamp,
                        mealType = mealType,
                        quantity = quantity,
                        note = note
                    )
                )
            }
        }
        list
    }

    suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TX_ID, transaction.id)
            put(COL_TX_TYPE, transaction.type.name)
            put(COL_TX_AMOUNT, transaction.amount)
            put(COL_TX_TIMESTAMP, transaction.timestamp)
            put(COL_TX_MEAL_TYPE, transaction.mealType?.name)
            put(COL_TX_QUANTITY, transaction.quantity)
            put(COL_TX_NOTE, transaction.note)
        }
        db.insertWithOnConflict(TABLE_TRANSACTIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun deleteTransaction(transactionId: String) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_TRANSACTIONS, "$COL_TX_ID = ?", arrayOf(transactionId))
    }

    suspend fun clearTransactions() = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_TRANSACTIONS, null, null)
    }

    // --- Meal Prices Operations ---
    suspend fun getAllPrices(): Map<MealType, Double> = withContext(Dispatchers.IO) {
        val map = mutableMapOf(
            MealType.BREAKFAST to MealType.BREAKFAST.defaultPrice,
            MealType.LUNCH to MealType.LUNCH.defaultPrice,
            MealType.DINNER to MealType.DINNER.defaultPrice
        )
        val db = readableDatabase
        val cursor = db.query(TABLE_MEAL_PRICES, null, null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val mealTypeStr = it.getString(it.getColumnIndexOrThrow(COL_PRICE_MEAL_TYPE))
                val price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE_AMOUNT))
                val mealType = try { MealType.valueOf(mealTypeStr) } catch (e: Exception) { null }
                if (mealType != null) {
                    map[mealType] = price
                }
            }
        }
        map
    }

    suspend fun setMealPrice(mealType: MealType, price: Double) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PRICE_MEAL_TYPE, mealType.name)
            put(COL_PRICE_AMOUNT, price)
        }
        db.insertWithOnConflict(TABLE_MEAL_PRICES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // --- Custom Currencies Operations ---
    suspend fun getAllCustomCurrencies(): List<AppCurrency> = withContext(Dispatchers.IO) {
        val list = mutableListOf<AppCurrency>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CUSTOM_CURRENCIES, null, null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val code = it.getString(it.getColumnIndexOrThrow(COL_CURRENCY_CODE))
                val symbol = it.getString(it.getColumnIndexOrThrow(COL_CURRENCY_SYMBOL))
                val name = it.getString(it.getColumnIndexOrThrow(COL_CURRENCY_NAME))
                val emoji = it.getString(it.getColumnIndexOrThrow(COL_CURRENCY_EMOJI))
                list.add(
                    AppCurrency(
                        code = code,
                        symbol = symbol,
                        displayName = name,
                        flagEmoji = emoji,
                        isCustom = true
                    )
                )
            }
        }
        list
    }

    suspend fun insertCustomCurrency(currency: AppCurrency) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CURRENCY_CODE, currency.code)
            put(COL_CURRENCY_SYMBOL, currency.symbol)
            put(COL_CURRENCY_NAME, currency.displayName)
            put(COL_CURRENCY_EMOJI, currency.flagEmoji)
        }
        db.insertWithOnConflict(TABLE_CUSTOM_CURRENCIES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    suspend fun clearCustomCurrencies() = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_CUSTOM_CURRENCIES, null, null)
    }

    // --- App Settings Operations ---
    suspend fun getSetting(key: String): String? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SETTINGS,
            arrayOf(COL_SETTING_VALUE),
            "$COL_SETTING_KEY = ?",
            arrayOf(key),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                it.getString(it.getColumnIndexOrThrow(COL_SETTING_VALUE))
            } else {
                null
            }
        }
    }

    suspend fun setSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_SETTING_KEY, key)
            put(COL_SETTING_VALUE, value)
        }
        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    companion object {
        const val DATABASE_NAME = "comidas_app.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TRANSACTIONS = "transactions"
        const val COL_TX_ID = "id"
        const val COL_TX_TYPE = "type"
        const val COL_TX_AMOUNT = "amount"
        const val COL_TX_TIMESTAMP = "timestamp"
        const val COL_TX_MEAL_TYPE = "meal_type"
        const val COL_TX_QUANTITY = "quantity"
        const val COL_TX_NOTE = "note"

        const val TABLE_MEAL_PRICES = "meal_prices"
        const val COL_PRICE_MEAL_TYPE = "meal_type"
        const val COL_PRICE_AMOUNT = "price"

        const val TABLE_CUSTOM_CURRENCIES = "custom_currencies"
        const val COL_CURRENCY_CODE = "code"
        const val COL_CURRENCY_SYMBOL = "symbol"
        const val COL_CURRENCY_NAME = "display_name"
        const val COL_CURRENCY_EMOJI = "flag_emoji"

        const val TABLE_SETTINGS = "app_settings"
        const val COL_SETTING_KEY = "setting_key"
        const val COL_SETTING_VALUE = "setting_value"
    }
}
