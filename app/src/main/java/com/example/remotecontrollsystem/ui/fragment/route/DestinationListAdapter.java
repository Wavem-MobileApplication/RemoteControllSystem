package com.example.remotecontrollsystem.ui.fragment.route;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ItemListDestinationBinding;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.ui.util.TextWatcherImpl;

import java.util.ArrayList;
import java.util.List;

public class DestinationListAdapter extends RecyclerView.Adapter<DestinationListAdapter.ViewHolder> {
    private List<Pose> poseList;

    public DestinationListAdapter() {
        poseList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pose pose = poseList.get(position);

        holder.binding.tvDestinationNum.setText(String.valueOf(holder.getAdapterPosition()));

        holder.binding.etPoseX.setText(String.valueOf(pose.getPosition().getX()));
        holder.binding.etPoseY.setText(String.valueOf(pose.getPosition().getY()));
        holder.binding.etOriZ.setText(String.valueOf(pose.getOrientation().getZ()));
        holder.binding.etOriW.setText(String.valueOf(pose.getOrientation().getW()));

        applyMemoryTextWatcher(holder.binding.etPoseX, "x", holder.getAdapterPosition());
        applyMemoryTextWatcher(holder.binding.etPoseY, "y", holder.getAdapterPosition());
        applyMemoryTextWatcher(holder.binding.etOriZ, "z", holder.getAdapterPosition());
        applyMemoryTextWatcher(holder.binding.etOriW, "w", holder.getAdapterPosition());

/*        holder.binding.etPoseX.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float x = Float.parseFloat(editable.toString());
                    poseList.get(holder.getAdapterPosition()).getPosition().setX(x);
                } catch (NumberFormatException ignored) {}
            }
        });

        holder.binding.etPoseY.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float y = Float.parseFloat(editable.toString());
                    poseList.get(holder.getAdapterPosition()).getPosition().setY(y);
                } catch (NumberFormatException ignored) {}
            }
        });

        holder.binding.etOriZ.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float z = Float.parseFloat(editable.toString());
                    poseList.get(holder.getAdapterPosition()).getOrientation().setZ(z);
                } catch (NumberFormatException ignored) {}
            }
        });

        holder.binding.etOriW.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float w = Float.parseFloat(editable.toString());
                    poseList.get(holder.getAdapterPosition()).getOrientation().setW(w);
                } catch (NumberFormatException ignored) {}
            }
        });*/

        holder.binding.btnDeleteDestination.setOnClickListener(view ->
                deleteButtonClickListener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return poseList.size();
    }

    public void setPoseList(List<Pose> poseList) {
        this.poseList.clear();
        this.poseList.addAll(poseList);
        notifyDataSetChanged();
    }

    public void addPose(Pose pose) {
        this.poseList.add(pose);
        notifyItemInserted(poseList.size());
    }

    public void removePose(int position) {
        this.poseList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, poseList.size());
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    private OnDeleteButtonClickListener deleteButtonClickListener = new OnDeleteButtonClickListener() {
        @Override
        public void onClick(int position) {
            removePose(position);
        }
    };

    private interface OnDeleteButtonClickListener {
        void onClick(int position);
    }

    private void applyMemoryTextWatcher(EditText editText, String mode, int position) {
        TextWatcherImpl textWatcher = new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float data = Float.parseFloat(editable.toString());
                    switch (mode) {
                        case "x":
                            poseList.get(position).getPosition().setX(data);
                            break;
                        case "y":
                            poseList.get(position).getPosition().setY(data);
                            break;
                        case "z":
                            poseList.get(position).getOrientation().setZ(data);
                            break;
                        case "w":
                            poseList.get(position).getOrientation().setW(data);
                            break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        };

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText.addTextChangedListener(textWatcher);
                } else {
                    editText.removeTextChangedListener(textWatcher);
                }
            }
        });
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ItemListDestinationBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListDestinationBinding.bind(itemView);
        }
    }
}
