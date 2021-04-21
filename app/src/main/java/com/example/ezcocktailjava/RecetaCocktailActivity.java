package com.example.ezcocktailjava;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.ezcocktailjava.cardslider.ListaPasosAdapter;

import java.util.ArrayList;

public class RecetaCocktailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta_cocktail);

        Cocktail cocktail = getIntent().getParcelableExtra("cocktail");

        ArrayList<CocktailPairParcelable> pasos = cocktail.getPasosCocktail();
        pasos.add(0, new CocktailPairParcelable(cocktail.getNombre(), cocktail.getDescripcion()));
        pasos.add( new CocktailPairParcelable("Enjoy!","") );

        ListaPasosAdapter listaPasosAdapter =
                new ListaPasosAdapter(cocktail, this);

        ViewPager vistaPasos = findViewById(R.id.viewpasos);
        vistaPasos.setAdapter(listaPasosAdapter);

    }
}