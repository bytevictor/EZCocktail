package com.example.ezcocktailjava;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ezcocktailjava.helpers.QueueUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cocktail implements Comparable<Cocktail>, Parcelable {

    private int id;

    //Ingrediente y cantidad
    private ArrayList<CocktailPairParcelable> pasosCocktail = new ArrayList<>();

    public ArrayList<String> prueba = new ArrayList<>();

    private String nombre;
    private String urlImagen;
    private String descripcion;

    private static QueueUtils.QueueObject queue;

    private static final String pre_url = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=";

    public Cocktail(int id, MainActivity _interface){
         this.id = id;
         this.queue = QueueUtils.getInstance(_interface.getApplicationContext());
    }

    //Para parceable
     //
    protected Cocktail(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        urlImagen = in.readString();
        descripcion = in.readString();
        pasosCocktail = in.readArrayList(CocktailPairParcelable.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(nombre);
        parcel.writeString(urlImagen);
        parcel.writeString(descripcion);
        parcel.writeList(pasosCocktail);
    }

    public static final Creator<Cocktail> CREATOR = new Creator<Cocktail>() {
        @Override
        public Cocktail createFromParcel(Parcel in) {
            return new Cocktail(in);
        }

        @Override
        public Cocktail[] newArray(int size) {
            return new Cocktail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
     //
    //

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public ArrayList<CocktailPairParcelable> getPasosCocktail() {
        return pasosCocktail;
    }

    //Para el set que luego usamos de cocktails (las peticiones a la api pueden venir con cocktails duplicados)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cocktail cocktail = (Cocktail) o;
        return this.id == cocktail.id;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    public void requestCocktail(ArrayList<Cocktail> listaCocktails, MainActivity _interface) {
        //Para pasarselo a la respuesta
        Cocktail esto = this;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET,
                        pre_url + Integer.toString(this.id),
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.has("drinks")) {
                            try {
                                JSONArray list = response.getJSONArray("drinks");
                                //System.out.println(list);
                                for (int i=0; i < list.length(); i++) {
                                    JSONObject o = list.getJSONObject(i);

                                    //Cargamos el cocktail en memoria
                                    cargarCocktailfromJSON(o);

                                    //añadimos a la lista si no esta ya (se puede repetir por culpa de la API)
                                    if( !listaCocktails.contains(esto) )
                                        listaCocktails.add(esto);
                                    else{
                                        System.out.println(esto);
                                    }

                                    System.out.println("Cargado cocktail [id:" + o.getString("idDrink") + "]");
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
                })
            //PARA CAMBIAR LA PRIORIDAD DE LA PETICION HAY QUE HACER ESTA SOBRECARGA
            //las peticiones de texto deben priorizarse sobre las de la imagen para que fluya todo
            //mejor
        {
            private Priority prior = Priority.IMMEDIATE;

            @Override
            public Priority getPriority() {
                return prior;
            }
        };



        this.queue.addToRequestQueue(jsonObjectRequest);
    }

    private void cargarCocktailfromJSON(JSONObject o) throws JSONException {
        this.nombre      = o.getString("strDrink");
        this.descripcion = o.getString("strInstructions");
        this.urlImagen   = o.getString("strDrinkThumb");


        //Para las medidas e ingredientes
        //vienen 15 en cada JSON cuando se acaban se vuelven nulos
        for (int i = 1; i <= 15
                && o.getString("strIngredient" + Integer.toString(i)) != "null"
                && o.getString("strIngredient" + Integer.toString(i)).compareTo("") != 0; i++){
            this.pasosCocktail.add(new CocktailPairParcelable(o.getString("strIngredient"+ Integer.toString(i)),
                                                              o.getString("strMeasure"   + Integer.toString(i))));
        }
    }

    @Override
    public int compareTo(Cocktail cocktail) {
        if(this.nombre == null || cocktail.nombre == null)
            return 0;
        return (this.nombre).compareTo(cocktail.nombre);
    }

}
