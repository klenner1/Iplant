package puc.iot.com.iplant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import puc.iot.com.iplant.models.Plant;
import puc.iot.com.iplant.utils.UtilsFireBase;

class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {

    private List<Plant> mPlantsList;
    private Context context;

    PlantsAdapter(Context context) {
        this.context = context;
        mPlantsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder( LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_plant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PlantsAdapter.ViewHolder holder, int i) {
        final Plant plant = mPlantsList.get(i);
        holder.textViewPlantName.setText(plant.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context,PlantActivity.class);
                intent.putExtra(Plant._ID,plant.getId());
                intent.putExtra(Plant.NAME,plant.getName());
                intent.putExtra(Plant.HUMIDITY,plant.getHumidity());
                intent.putExtra(Plant.IS_OPEN_TAP,plant.isOpen_tap());

                context.startActivity(intent);
            }
        });

        float humidity = plant.getHumidity();
        holder.textViewHumidity.setText(humidity+"%");
        switch (plant.getStatus()){
            case Plant.DRY: case Plant.WATERING_DRY:{
                holder.content.setBackgroundColor(ContextCompat.getColor(context,R.color.colorDesertPrimary));
                break;
            }
            case Plant.NORMAL: case Plant.WATERING_NORMAL:{
                holder.content.setBackgroundColor(ContextCompat.getColor(context,R.color.colorGreenPrimary));
                break;
            }
            case Plant.HUMID: case Plant.WATERING_HUMID:{
                holder.content.setBackgroundColor(ContextCompat.getColor(context,R.color.colorHumidPrimary));
                break;
            }
        }

        getPhoto(holder, plant);
    }

    private void getPhoto(@NonNull final ViewHolder holder, Plant plant) {
        StorageReference imagesStorageReference = UtilsFireBase.getImagesStorageReference(plant.getId());
        final long limit = 1024 * 1024;
        imagesStorageReference.getBytes(limit).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.imageViewPhoto.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlantsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPlantName,textViewHumidity;
        private ConstraintLayout content;
        private ImageView imageViewPhoto;

        ViewHolder(View itemView) {
            super(itemView);
            textViewPlantName=itemView.findViewById(R.id.textViewPlantName);
            textViewHumidity=itemView.findViewById(R.id.textViewHumidity);
            content =itemView.findViewById(R.id.content);
            imageViewPhoto=itemView.findViewById(R.id.imageViewPhoto);
        }
    }
    public void add(Plant plant){
        if (mPlantsList!=null){
            mPlantsList.add(plant);
            notifyItemInserted(mPlantsList.size() - 1);
        }
    }


    public void add(String newPlantId) {
        Plant plant = new Plant(newPlantId);
        mPlantsList.add(plant);
        int position = mPlantsList.size() - 1;
        getPlatValues(position);
        notifyItemInserted(position);
    }

    public void remove(String removedPlantId) {
        Plant plant = new Plant(removedPlantId);
        int i = mPlantsList.indexOf(plant);
        if (i>=0) {
            mPlantsList.remove(i);
            notifyItemRemoved(i);
        }
    }

    private void getPlatValues(final int position) {

        Plant plant = mPlantsList.get(position);
        DatabaseReference userPlants = UtilsFireBase.getPlantReference(plant.getId());
        userPlants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Plant newPlant = dataSnapshot.getValue(Plant.class);
                if (newPlant!=null) {
                    mPlantsList.get(position).update(newPlant);
                    notifyItemChanged(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
