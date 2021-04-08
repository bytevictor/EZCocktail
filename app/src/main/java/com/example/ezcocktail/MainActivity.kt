package com.example.ezcocktail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.recipe_list_view)

        val recipeList = arrayOf("cosa1", "cosa2", "cosa3", "cosa4", "cosa5");

        val listItems = arrayOfNulls<String>(recipeList.size)

        for (i in 0 until recipeList.size) {
            val recipe = recipeList[i]
            listItems[i] = recipe
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter


        var caj = findViewById<TextView>(R.id.cajatexto)
        caj.text = "pepe el pistolas 2"
    }
}