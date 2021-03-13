package com.dazzlerr_usa.utilities

import android.content.SharedPreferences
import javax.inject.Inject

public class HelperSharedPreferences
@Inject
constructor(var mSharedPreferences: SharedPreferences)
{

    fun saveString(key: String, value: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
        editor.commit()
    }

    /**
     * Method to store `int` value corresponding to a key
     *
     * @param key   - Key
     * @param value - int value to be stored
     */
    fun saveInt(key: String, value: Int) {
        val editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
        editor.commit()
    }

    /**
     * Method to store `long` value corresponding to a key
     *
     * @param key   - Key
     * @param value - long value to be stored
     */
    fun saveLong(key: String, value: Long) {
        val editor = mSharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
        editor.commit()
    }

    /**
     * Method to store `boolean` value corresponding to a key
     *
     * @param key   - Key
     * @param value - boolean value to be stored
     */
    fun saveBoolean(key: String, value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
        editor.commit()
    }

    /**
     * Method to store `String` value corresponding to a key
     * @param key   - Key
     * @param value - String value to be stored
     */
    /*public void saveString(final String key,final String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }*/

    /**
     * Method to clear all the shared preferences stored, this will be helpful in
     * scenarios like logout
     */
    fun clear() {
        val editor = mSharedPreferences.edit()
        editor.clear()
        editor.apply()
        editor.commit()
    }

    /**
     * Method to get `String` value from preferences for a given Key
     *
     * @param key - Key to fetch the value
     * @return - Actual value, returns null if not found
     */
    fun getString(key: String): String? {
        return mSharedPreferences.getString(key, "")
    }

    /**
     * Method to get `long` value from preferences for a given Key
     *
     * @param key - Key to fetch the value
     * @return - Actual value, returns 0 if not found
     */
    fun getLong(key: String): Long {
        return mSharedPreferences.getLong(key, 0L)
    }

    /**
     * Method to get `int` value from preferences for a given Key
     *
     * @param key - Key to fetch the value
     * @return - Actual value, returns 0 if not found
     */
    fun getInt(key: String): Int {
        return mSharedPreferences.getInt(key, 0)
    }

    /**
     * Method to get `float` value from preferences for a given Key
     *
     * @param key - Key to fetch the value
     * @return - Actual value, returns 0 if not found
     */
    fun getFloat(key: String): Double {
        return mSharedPreferences.getFloat(key, 0.0f).toDouble()
    }

    /**
     * Method to get `boolean` value from preferences for a given Key
     *
     * @param key - Key to fetch the value
     * @return - Actual value, returns false if not found
     */
    fun getBoolean(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

}
