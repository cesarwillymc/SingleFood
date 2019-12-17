package com.singlefood.sinfo.utils.adapters;

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
import com.singlefood.sinfo.models.comentarios;

import java.util.ArrayList;

public class RecyclerComentariosAdapter extends RecyclerView.Adapter< RecyclerComentariosAdapter.ViewHolder> {
    private Context mcontex;
    private  int layoutResources;
    private ArrayList<comentarios> comentariosArrayList;
    private OnItemClickListener2 Listen;

    public RecyclerComentariosAdapter(Context mcontex, int layoutResources, ArrayList<comentarios> comentariosArrayList, OnItemClickListener2 Listen) {
        this.mcontex = mcontex;
        this.layoutResources = layoutResources;
        this.comentariosArrayList = comentariosArrayList;
        this.Listen = Listen;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( mcontex ).inflate( layoutResources,parent,false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(comentariosArrayList.get(position),Listen);

    }


    @Override
    public int getItemCount() {
        if(comentariosArrayList.size()>0){
            return comentariosArrayList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView mComentario,mNombreuser,mTituloComentario,mFecha;
        ImageView mImage;

        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            mFecha=(TextView) itemView.findViewById( R.id.rv_comentarios_text_user_fech );
//            mTituloComentario.setVisibility( View.GONE );
            mComentario=(TextView) itemView.findViewById( R.id.rv_comentarios_text_user_comentarios );
            mNombreuser=(TextView) itemView.findViewById( R.id.rv_comentarios_name_user );
            mImage =(ImageView) itemView.findViewById( R.id.rv_comentarios_imagen_user_image );
            ratingBar=(RatingBar)itemView.findViewById( R.id.rv_comentarios_user_rating );


        }


        public void bind(final comentarios comentarios, final OnItemClickListener2 listen) {
            mNombreuser.setText( comentarios.getUser());
            mComentario.setText( comentarios.getTexto() );
            // mImage.setImageBitmap( StringToBitmap(comentarios.) );
            ratingBar.setRating( comentarios.getRating());
            mFecha.setText(comentarios.getFecha()+" " +comentarios.getHora());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listen.OnClickListener2(comentarios,getAdapterPosition());
                }
            });
        }
        public Bitmap StringToBitmap(String imageBytes){
            byte[] decodedString = Base64.decode(imageBytes, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }
    }
    public interface OnItemClickListener2{
        void OnClickListener2(comentarios comentarios, int adapterPosition);
    }
}