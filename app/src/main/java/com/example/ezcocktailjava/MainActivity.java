package com.example.ezcocktailjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ezcocktailjava.chipview.ChipView;
import com.example.ezcocktailjava.chipview.SimpleChipAdapter;
import com.example.ezcocktailjava.helpers.QueueUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ListaCocktailsAdapter listacocktAdapter = null;

    //Cola para las peticiones de los cocktails
    QueueUtils.QueueObject queue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context mContext = getApplicationContext();

        //Lista de ingredientes
        JSONArray JSONingredientes = null;

        //Lista de Cocktails
        ListView cocktaillistView;

        //Menu de búsqueda
        ChipView cvTag = (ChipView)findViewById(R.id.cvTag);
        ArrayList<Object> data = new ArrayList<>();

        //Leemos los ingredientes y los ponemos en orden alfabético
        try {
            JSONingredientes = (JSONArray) leerJSONfromRaw(mContext, "ingredientes").get("drinks");
            JSONingredientes = sortJSONArrayAlphabetically(JSONingredientes);

            for(int i = 0; i < JSONingredientes.length(); i++){
                //System.out.println( ((JSONObject)JSONingredientes.get(i)).get("strIngredient1") );
                data.add( ((JSONObject)JSONingredientes.get(i)).get("strIngredient1") );
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        //creamos la lista de tags
        SimpleChipAdapter adapter = new SimpleChipAdapter( data );
        cvTag.setAdapter(adapter);

        //LISTA DE COCKTAILS
        //Creamos lista de cocktails
        cocktaillistView=(ListView) findViewById(R.id.lista_cocktails);

        ArrayList<Cocktail> cocktailarrayList = new ArrayList<>();

        listacocktAdapter = new ListaCocktailsAdapter(mContext, cocktailarrayList);
        cocktaillistView.setAdapter(listacocktAdapter);

        //Definimos la accion al clickar en un cocktail de la lista
        //redirigira a otra actividad con los pasos
        cocktaillistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cocktail cocktail_clickado = (Cocktail) adapterView.getItemAtPosition(i);
                System.out.println("Seleccionado cocktail: " + cocktail_clickado.getId() + " | " + cocktail_clickado.getNombre());

                Intent intent = new Intent(MainActivity.this, RecetaCocktailActivity.class);
                intent.putExtra("cocktail", cocktail_clickado);
                startActivity(intent);
            }
        });

        // PARA REBUSCAR LA LISTA AL DARLE AL BOTON
        // Y para ocultar/mostrar la lista de las tags al abrir el buscador
        EditText barraBusqueda = findViewById(R.id.etSearch);
        barraBusqueda.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //Toast.makeText(mContext, "Focus Lose", Toast.LENGTH_SHORT).show();
                    GridView listaTags = findViewById(R.id.lvList);
                    listaTags.setVisibility(View.GONE);

                    //obtenemos la lista de ingredientes seleccionados
                    ArrayList<CharSequence> ingredientes_seleccionados = new ArrayList<>();
                    FlexboxLayout chips = findViewById(R.id.flChips);
                    //- 1 para no coger la caja de búsqueda
                    for(int i=0; i < chips.getChildCount() - 1; i++){
                        int esto = chips.getChildCount();
                        ingredientes_seleccionados.add(((Chip)((LinearLayout)chips.getChildAt(i)).getChildAt(0)).getText());
                    }

                    //definimos el filtro
                    String secuencia_filtro = barraBusqueda.getText().toString();
                    for(int i = 0; i < ingredientes_seleccionados.size(); i++ ){
                        secuencia_filtro += ";" + ingredientes_seleccionados.get(i).toString();
                    }

                    System.out.println(secuencia_filtro);

                    //aplicamos los cambios de la búsqueda
                    listacocktAdapter.getFilter().filter(secuencia_filtro);
                } else {
                    //Toast.makeText(mContext, "Get Focus", Toast.LENGTH_SHORT).show();
                    GridView listaTags = findViewById(R.id.lvList);
                    listaTags.setVisibility(View.VISIBLE);
                }

            }
        });

        //Pedimos todos los cocktails asociados a cada ingrediente (no se pueden pedir todos de golpe,
        // asi que pedimos la lista de cada uno de los ingredientes.
        try {
            for(int i = 0; i < JSONingredientes.length(); i++){
                System.out.println("INGREDIENTE: " +  ((JSONObject)JSONingredientes.get(i)).get("strIngredient1") );

                String url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i="
                        // Obtenemos el nombre del ingrediente y le damos formato de url
                        + ((String)((JSONObject)JSONingredientes.get(i)).get("strIngredient1")).replaceAll("\\s", "%20");

                queue = QueueUtils.getInstance(this.getApplicationContext());
                injectCocktailsFromURL(url, queue, cocktailarrayList, this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //
    }

    public void refreshList(){
        if ( listacocktAdapter!= null ) {
            listacocktAdapter.notifyDataSetChanged();
        }
    }

    private static void injectCocktailsFromURL(String url, final QueueUtils.QueueObject o,
                                                final ArrayList<Cocktail> arrayCocktails,
                                                //final Set<Cocktail> cocktailSet,
                                                final MainActivity _interface) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("drinks")) {
                            try {
                                JSONArray list = response.getJSONArray("drinks");
                                //System.out.println(list);
                                for (int i=0; i < list.length(); i++) {
                                    JSONObject o = list.getJSONObject(i);
                                    //System.out.println(o);

                                    Cocktail nuevo_cocktail = new Cocktail(Integer.parseInt(o.getString("idDrink")), _interface);
                                    //Creamos un nuevo cocktail y lo pedimos, se añadira a la lista cuando se reciba
                                    nuevo_cocktail.requestCocktail(arrayCocktails, _interface);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            _interface.refreshList();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){
        };

        o.addToRequestQueue(jsonObjectRequest);
    }

    //Para tratar facilmente con los JSON
    public static JSONObject leerJSONfromRaw(Context context,String nombre) throws IOException, JSONException {
        InputStream ins = context.getResources().openRawResource(
                context.getResources().getIdentifier("ingredientes",
                        "raw", context.getPackageName()));

        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return new JSONObject(sb.toString());
    }

    public static JSONArray sortJSONArrayAlphabetically(JSONArray jArray) throws JSONException
    {
        if (jArray != null) {
            // Sort:
            ArrayList<String> arrayForSorting = new ArrayList<String>();
            for (int i = 0; i < jArray.length(); i++) {
                arrayForSorting.add(jArray.get(i).toString());
            }
            Collections.sort(arrayForSorting);

            // Prepare and send result:
            JSONArray resultArray = new JSONArray();
            for (int i = 0; i < arrayForSorting.size(); i++) {
                resultArray.put(new JSONObject(arrayForSorting.get(i)));
            }
            return resultArray;
        }
        return null; // Return error.
    }

}

