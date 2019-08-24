package com.singlefood.sinfo.models;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.singlefood.sinfo.R;
import com.singlefood.sinfo.models.productos.Platillos;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class informacionFragment extends Fragment {

    TextView prueba;
    ArrayList<Platillos> datos;
    public informacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_informacion, container, false );
        prueba=(TextView)view.findViewById(R.id.prueba  );
        int position = (int)getArguments().getInt("key");
        datos = (ArrayList<Platillos>)getArguments().getSerializable("datos");
        Toast.makeText( getContext(),"posicion"+position,Toast.LENGTH_SHORT ).show();
        prueba.setText( datos.get( position ).getNombrePlatillo() );
        return view;

    }

}
