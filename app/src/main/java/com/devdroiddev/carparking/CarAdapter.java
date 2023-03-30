package com.devdroiddev.carparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {
    private Context context;
    private List<ParkCarModel> parkCarModelList;
    private CarAdapterInterface listener;

    public CarAdapter(CarAdapterInterface listener, Context context) {
        this.listener = listener;
        parkCarModelList = new ArrayList<>();
    }



    public void add(ParkCarModel parkCarModel) {
        parkCarModelList.add(parkCarModel);
        notifyDataSetChanged();
    }
    public void remove(int position) {
        parkCarModelList.remove(position);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParkCarModel model = parkCarModelList.get(position);
        // Attach the data with Each TextView
        holder.driverName.setText(model.getDriverName());
        holder.driverNumber.setText(model.getDriverNumber());
        holder.vehicleType.setText(model.getVehicleType());
        holder.numberPlate.setText(model.getNumberPlate());
        holder.amount.setText(model.getParkFee());
        String[] dataAndTime = longIntoString(model.getTime());
        holder.time.setText(dataAndTime[0] + "\n" + dataAndTime[1]);
        holder.status.setText(model.getStatus());
    }

    @Override
    public int getItemCount() {
        return parkCarModelList.size();
    }

    // Create a view-Holder class
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, driverNumber, numberPlate, vehicleType, amount, time, status;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleType = itemView.findViewById(R.id.vehicleType);
            driverName = itemView.findViewById(R.id.driverName);
            driverNumber = itemView.findViewById(R.id.driverNumber);
            numberPlate = itemView.findViewById(R.id.numberPlate);
            amount = itemView.findViewById(R.id.parkingAmount);
            time = itemView.findViewById(R.id.parkingDate);
            status = itemView.findViewById(R.id.status);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.OnLongClick(getAdapterPosition(),parkCarModelList.get(getAdapterPosition()).getId());
                    return true;
                }
            });

        }
    }

    // Create a method to convert Long into String
    private String[] longIntoString(long milliseconds) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return new String[] { dateFormat.format(milliseconds), timeFormat.format(milliseconds)};
    }
}
