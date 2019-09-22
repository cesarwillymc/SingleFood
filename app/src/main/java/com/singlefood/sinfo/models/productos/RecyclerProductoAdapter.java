package com.singlefood.sinfo.models.productos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlefood.sinfo.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerProductoAdapter extends RecyclerView.Adapter<RecyclerProductoAdapter.ProductViewHolder> {
    private Context mcontex;
    private  int layoutResources;
    private ArrayList<Platillos> arrayPlatillos;
    private ArrayList<ArrayList<Comentarios>> arrayKeys;
    private OnItemClickListener Listen;

    public RecyclerProductoAdapter(Context mcontex, int layoutResources, ArrayList<Platillos> arrayPlatillos,ArrayList<ArrayList<Comentarios>> arrayKeys, OnItemClickListener Listen) {
        this.mcontex = mcontex;
        this.layoutResources = layoutResources;
        this.arrayPlatillos = arrayPlatillos;
        this.arrayKeys = arrayKeys;
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
        holder.bind(arrayPlatillos.get(position),Listen,arrayKeys.get( position ));

    }

    @Override
    public int getItemCount() {
        if(arrayPlatillos.size()>0){
            return arrayPlatillos.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder  {
        TextView mPrecio,mNombre,mStarts;
        ImageView mImage;
        RatingBar ratingBar;
        public ProductViewHolder(@NonNull View itemView) {
            super( itemView );
            mNombre=(TextView) itemView.findViewById( R.id.rv_comentarios_text_nombre_platillo );
            mPrecio=(TextView) itemView.findViewById( R.id.rv_comentarios_text_precio );
            mImage =(ImageView) itemView.findViewById( R.id.rv_comentarios_imagen_platillo );
            mStarts =(TextView) itemView.findViewById( R.id.rv_comentarios_text_rating );
            ratingBar=(RatingBar)itemView.findViewById( R.id.rv_comentarios_rating );

        }


        public void bind(final Platillos platillos, final OnItemClickListener listen, ArrayList<Comentarios> arrayComentarios) {
            mNombre.setText( platillos.getNombrePlatillo());
            mPrecio.setText( platillos.getPrecio() );
            mImage.setImageBitmap( StringToBitmap( platillos.getImagenbase64() ) );
            ratingBar.setRating( promedioRating(arrayComentarios));
            DecimalFormat formato = new DecimalFormat();
            formato.setMaximumFractionDigits(2);
            mStarts.setText(  formato.format(promedioRating( arrayComentarios )) );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listen.OnClickListener(platillos,arrayComentarios,getAdapterPosition());
                }
            });
        }
        public Bitmap StringToBitmap(String imageBytes){
            byte[] decodedString = Base64.decode(imageBytes, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
        public Float promedioRating(ArrayList<Comentarios> arrayComentarios){
            float temp=0;
            for (int i=0;i<arrayComentarios.size();i++){
                temp+=arrayComentarios.get(  i).getRating();
            }
            return temp/arrayComentarios.size();

        }
    }
    public interface OnItemClickListener{
        void OnClickListener(Platillos platillos, ArrayList<Comentarios> arrayComentarios, int position);


    }
}