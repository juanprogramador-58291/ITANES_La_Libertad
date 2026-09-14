package com.example.itanes_la_libertad.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<PlaceEntity> places;
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(int placeId);
    }

    public PlaceAdapter(List<PlaceEntity> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    public void updateList(List<PlaceEntity> newPlaces) {
        this.places = newPlaces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceEntity place = places.get(position);
        holder.textPlaceName.setText(place.getName());
        holder.textPlaceDescription.setText(place.getShortDescription());

        Glide.with(holder.imagePlace.getContext())
                .load(place.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(holder.imagePlace);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaceClick(place.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView textPlaceName;
        TextView textPlaceDescription;
        ImageView imagePlace;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlaceName = itemView.findViewById(R.id.textPlaceName);
            textPlaceDescription = itemView.findViewById(R.id.textPlaceDescription);
            imagePlace = itemView.findViewById(R.id.imagePlace);
        }
    }
}
