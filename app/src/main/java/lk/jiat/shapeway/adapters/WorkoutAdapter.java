package lk.jiat.shapeway.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.VideoPlayerActivity;
import lk.jiat.shapeway.models.Workout;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private Context context;
    private List<Workout> workoutList;

    public WorkoutAdapter(Context context, List<Workout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_workout, parent, false);

        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {

        Workout workout = workoutList.get(position);


        holder.txtTitle.setText(workout.getTitle());
        holder.txtFocus.setText(workout.getFocusZone());
        holder.txtDuration.setText(workout.getDurationMinutes() + " min");
        holder.txtCalories.setText(workout.getCalories() + " kcal");


        String thumbnail =
                "https://img.youtube.com/vi/"
                        + workout.getYoutubeVideoId()
                        + "/maxresdefault.jpg";


        Glide.with(context)
                .load(thumbnail)
                .placeholder(R.drawable.placeholder_workout)
                .error(R.drawable.placeholder_workout)
                .into(holder.imgThumbnail);


        if (workout.isLiked()) {

            holder.btnFavorite.setImageResource(R.drawable.ic_heart_filled);

        } else {

            holder.btnFavorite.setImageResource(R.drawable.ic_heart_outline);
        }


        holder.btnFavorite.setOnClickListener(v -> {

            workout.setLiked(!workout.isLiked());

            notifyItemChanged(position);
        });


        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, VideoPlayerActivity.class);

            intent.putExtra("videoId", workout.getYoutubeVideoId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumbnail;
        ImageView btnFavorite;

        TextView txtTitle;
        TextView txtFocus;
        TextView txtDuration;
        TextView txtCalories;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtFocus = itemView.findViewById(R.id.txtFocus);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtCalories = itemView.findViewById(R.id.txtCalories);
        }
    }
}