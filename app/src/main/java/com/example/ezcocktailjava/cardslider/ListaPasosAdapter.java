package com.example.ezcocktailjava.cardslider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.ezcocktailjava.Cocktail;
import com.example.ezcocktailjava.CocktailPairParcelable;
import com.example.ezcocktailjava.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListaPasosAdapter extends PagerAdapter {

    private String urlImagenCocktail;
    private String descripcion;
    private ArrayList<CocktailPairParcelable> pasos;
    private Context contexto;

    public ListaPasosAdapter(Cocktail cocktail, Context context){
        this.pasos = cocktail.getPasosCocktail();
        this.urlImagenCocktail = cocktail.getUrlImagen();
        this.descripcion = cocktail.getDescripcion();
        this.contexto = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(contexto);
        View pasosView;

        if(position == 0){
            pasosView = layoutInflater.inflate(R.layout.descripcion_paso, container, false);

            TextView vistaTingrediente = pasosView.findViewById(R.id.nombreingrediente);
            TextView vistaTcantidad = pasosView.findViewById(R.id.cantidadingrediente);

            //asignamos los valores a la tarjeta
            CocktailPairParcelable paso = pasos.get(position);

            vistaTingrediente.setText(paso.first);

            if(paso.second.compareTo("null") != 0)
                vistaTcantidad.setText(paso.second);

            container.addView( pasosView, 0);
        } else {
            pasosView = layoutInflater.inflate(R.layout.paso, container, false);

            ImageView vistaImagen = pasosView.findViewById(R.id.imgingrediente);
            TextView vistaTingrediente = pasosView.findViewById(R.id.nombreingrediente);
            TextView vistaTcantidad = pasosView.findViewById(R.id.cantidadingrediente);

            //asignamos los valores a la tarjeta
            CocktailPairParcelable paso = pasos.get(position);

            String url;
            //Para la foto final
            if(position == pasos.size() - 1 ){
                url = urlImagenCocktail;
                Picasso.get().load(url).placeholder(R.drawable.shakerplaceholder).into(vistaImagen);
            }
            else{
                url = "https://www.thecocktaildb.com/images/ingredients/"
                        + (paso.first).replaceAll("\\s", "%20") + ".png";
                Picasso.get().load(url).placeholder(R.drawable.jiggerplaceholder).into(vistaImagen);
            }




            if(paso.first.compareTo("null") != 0)
                vistaTingrediente.setText(paso.first);

            if(paso.second.compareTo("null") != 0)
                vistaTcantidad.setText(paso.second);

            container.addView( pasosView, 0);
        }
        return pasosView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return pasos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


}
