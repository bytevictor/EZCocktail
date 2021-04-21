package com.example.ezcocktailjava.chipview;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.ezcocktailjava.R;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SimpleChipAdapter extends ChipAdapter{

    ArrayList<Object>search_data = new ArrayList<>();
    ArrayList<Object>chips = new ArrayList<>();

    public SimpleChipAdapter(ArrayList<Object>search_data){
        this.search_data = search_data;
        this.data = search_data;
    }

    @Override
    public Object getItem(int pos) {
        return search_data.get(pos);
    }

    @Override
    public boolean isSelected(int pos) {
        if(chips.contains(search_data.get(pos))) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public View createSearchView(Context context, boolean is_checked, final int pos) {
        View view = View.inflate(context,R.layout.search,null);
        CheckBox cbCheck = view.findViewById(R.id.cbCheck);
        cbCheck.setText((String)search_data.get(pos));

        //Pedimos la foto del ingrediente para mostrarla (async)
        ImageView cbImage = view.findViewById(R.id.cbTagimg);
        String url = "https://www.thecocktaildb.com/images/ingredients/" + ((String)search_data.get(pos)).replaceAll("\\s", "%20") + "-Small.png";
        System.out.println(url);
        Picasso.get().load(url).into(cbImage);

        cbCheck.setChecked(is_checked);
        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    chips.add(search_data.get(pos));
                    refresh();
                }else{
                    chips.remove(search_data.get(pos));
                    refresh();
                }
            }
        });
        return view;
    }

    @Override
    public View createChip(Context context, final int pos) {
        View view = View.inflate(context,R.layout.chip,null);

        Chip chiptag = view.findViewById(R.id.chiptag);
        chiptag.setText((String)search_data.get(pos));

        chiptag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chips.remove(search_data.get(pos));
                refresh();
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return search_data.size();
    }
}
