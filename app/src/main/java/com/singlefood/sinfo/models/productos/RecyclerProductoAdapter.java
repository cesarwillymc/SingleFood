package com.singlefood.sinfo.models.productos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlefood.sinfo.R;

import java.util.ArrayList;

public class RecyclerProductoAdapter extends RecyclerView.Adapter<RecyclerProductoAdapter.ProductViewHolder> {
    private Context mcontex;
    private  int layoutResources;
    private ArrayList<Platillos> arrayPlatillos;
    private OnItemClickListener Listen;

    public RecyclerProductoAdapter(Context mcontex, int layoutResources, ArrayList<Platillos> arrayPlatillos, OnItemClickListener Listen) {
        this.mcontex = mcontex;
        this.layoutResources = layoutResources;
        this.arrayPlatillos = arrayPlatillos;
        this.Listen = Listen;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( mcontex ).inflate( layoutResources,parent,false );
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(arrayPlatillos.get(position),Listen);

    }

    @Override
    public int getItemCount() {
        if(arrayPlatillos.size()>0){
            return arrayPlatillos.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder  {
        TextView mPrecio,mNombre;
        ImageView mImage;
        public ProductViewHolder(@NonNull View itemView) {
            super( itemView );
            mNombre=(TextView) itemView.findViewById( R.id.TVname );
            mPrecio=(TextView) itemView.findViewById( R.id.TVprecio );
            mImage =(ImageView) itemView.findViewById( R.id.itemImage );

        }


        public void bind(final Platillos platillos, final OnItemClickListener listen) {
            mNombre.setText( platillos.getNombrePlatillo());
            mPrecio.setText( platillos.getPrecio() );
            mImage.setImageResource( R.drawable.ic_place_black_24dp );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listen.OnClickListener(platillos,getAdapterPosition());
                }
            });
        }
    }
    public interface OnItemClickListener{
        void OnClickListener(Platillos platillos, int position);
    }
}
