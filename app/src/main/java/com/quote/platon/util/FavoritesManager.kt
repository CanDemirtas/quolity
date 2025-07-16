package com.quote.platon.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addFavorite(quote: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(quote)
        saveFavorites(favorites)
    }

    fun removeFavorite(quote: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(quote)
        saveFavorites(favorites)
    }

    fun getFavorites(): Set<String> {
        val json = sharedPreferences.getString("favorites", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<Set<String>>() {}.type)
        } else {
            emptySet()
        }
    }

    private fun saveFavorites(favorites: Set<String>) {
        val json = gson.toJson(favorites)
        sharedPreferences.edit().putString("favorites", json).apply()
    }
}