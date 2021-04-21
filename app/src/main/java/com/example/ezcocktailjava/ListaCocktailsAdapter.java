package com.example.ezcocktailjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListaCocktailsAdapter extends ArrayAdapter<Cocktail> {

    private Context context;
    private List<Cocktail> listaCocktails = new ArrayList<>();
    private List<Cocktail> cocktailsMostrados = new ArrayList<>();

    public ListaCocktailsAdapter(@NonNull Context context, ArrayList<Cocktail> lista) {
        super(context, 0, lista);
        this.context = context;
        this.listaCocktails = lista;
        this.cocktailsMostrados = lista;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //Buscamos los tags que hay seleccionados
                String[] filtros = charSequence.toString().split(";");
                String texto_busqueda = filtros[0];
                ArrayList<String> tag_ingredientes = new ArrayList<>();
                for(int i = 1; i < filtros.length; i++){
                    tag_ingredientes.add(filtros[i]);
                }

                //si no hay nada para buscar
                if(charSequence == null || charSequence.length() == 0){
                    results.values = listaCocktails;
                    results.count = listaCocktails.size();
                }else{ //buscamos

                    ArrayList<Object> tmp = new ArrayList<>();
                    for(int i = 0;i < listaCocktails.size();i++){
                        Cocktail c = listaCocktails.get(i);

                       //Obtenemos los ingredientes del cocktail
                        ArrayList<String> ingredientes = new ArrayList<>();
                        for(int j = 0; j < c.getPasosCocktail().size(); j++){
                            ingredientes.add(c.getPasosCocktail().get(j).first);
                        }

                        //Si tiene todos los ingredientes de los tags
                        if( ingredientes.containsAll(tag_ingredientes) ){
                            //Si coincide con el texto de busqueda
                            if(listaCocktails.get(i).toString().toLowerCase().indexOf(texto_busqueda.toLowerCase()) != -1){

                                System.out.println("Ha pasado el filtro: "+listaCocktails.get(i));
                                tmp.add(listaCocktails.get(i));
                            }
                        }

                    }
                    results.values = tmp;
                    results.count = tmp.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cocktailsMostrados = (List<Cocktail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return cocktailsMostrados.size();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listaCockt = convertView;
        if(listaCockt == null)
            listaCockt = LayoutInflater.from(context).inflate(R.layout.cocktail_itemlista,parent,false);

        Cocktail cocktail = cocktailsMostrados.get(position);

        //ASIGNAMOS LOS VALORES CORRESPONDIENTES AL ITEM
        ImageView ckimage = listaCockt.findViewById(R.id.ckimage);

        //System.out.println("Carga picaso listacocktails: " + cocktail.getUrlImagen());
        Picasso.get().load(cocktail.getUrlImagen()).placeholder(R.drawable.shakerplaceholder).into(ckimage);

        TextView name = (TextView) listaCockt.findViewById(R.id.cktitulo);
        name.setText(cocktail.getNombre());

        //Sacamos la lista de ingredientes a partir del par ingrediente/cantidad
        TextView release = (TextView) listaCockt.findViewById(R.id.ckingredientes);
        String listaIngredientes = "";
        for(int i = 0; i < cocktail.getPasosCocktail().size() && cocktail.getPasosCocktail().get(i).first != "null"; i++){
            listaIngredientes += cocktail.getPasosCocktail().get(i).first + ", ";
        }
        //quitamos la ultima coma
        release.setText(listaIngredientes.substring(0, listaIngredientes.length() - 2));

        return listaCockt;
    }

    @Nullable
    @Override
    public Cocktail getItem(int position) {
        return cocktailsMostrados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cocktailsMostrados.indexOf(cocktailsMostrados.get(position));
    }
}
