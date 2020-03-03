package com.example.Orlando_GarageApplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteGarageAdapter extends RecyclerView.Adapter<FavoriteGarageAdapter.FavoriteGarageViewHOlder> {

    private Context context;
    private Cursor cursor;

    public FavoriteGarageAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public FavoriteGarageViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.favoritegarage_item, parent, false);
        return new FavoriteGarageViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteGarageViewHOlder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String favoriteTitleName = cursor.getString(cursor.getColumnIndex(FavoriteGarage.FavoriteGarageEntry.COLUMN_FAVORITETITLE));
        String favoriteGarageName = cursor.getString(cursor.getColumnIndex(FavoriteGarage.FavoriteGarageEntry.COLUMN_GARAGENAME));
        String favoriteGarageDay = cursor.getString(cursor.getColumnIndex(FavoriteGarage.FavoriteGarageEntry.COLUMN_ACTIVATIONDATE));
        long id = this.cursor.getLong(this.cursor.getColumnIndex(FavoriteGarage.FavoriteGarageEntry._ID));

        holder.nameText.setText(favoriteTitleName);
        holder.garageText.setText("Garage " + favoriteGarageName);
        holder.dayText.setText(favoriteGarageDay);
        holder.itemView.setTag(R.id.keyItemID, id);
        holder.itemView.setTag(R.id.keyDayWanted, favoriteGarageDay);
        holder.itemView.setTag(R.id.keyGarageName, favoriteGarageName);
        holder.itemView.setTag(R.id.keyFavoriteTitle, favoriteTitleName);


    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }

        this.cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public class FavoriteGarageViewHOlder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView dayText;
        TextView garageText;

        FavoriteGarageViewHOlder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.garageTitle);
            dayText = itemView.findViewById(R.id.activationDateShow);
            garageText = itemView.findViewById(R.id.garageName);
        }
    }


}
