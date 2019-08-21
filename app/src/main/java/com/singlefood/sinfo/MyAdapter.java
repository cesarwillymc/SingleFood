package com.singlefood.sinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> names;
    private  int layout;
    private  OnItemClickListener Listen;
    public MyAdapter(List<String> names, int layout, OnItemClickListener Listen){
        this.names = names;
        this.layout = layout;
        this.Listen = Listen;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(names.get(position),Listen);
    }

    @Override
    public int getItemCount() {
        return this.names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombres;
        public ViewHolder(View itemView) {
            super(itemView);
            this.nombres = itemView.findViewById(R.id.TVname);
        }
        public void bind(final String name, final OnItemClickListener listen){
            this.nombres.setText(name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listen.OnClickListener(name,getAdapterPosition());
                }
            });
        }
    }
    public interface OnItemClickListener{
        void OnClickListener(String name, int position);
    }

}
