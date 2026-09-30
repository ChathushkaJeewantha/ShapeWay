package lk.jiat.shapeway.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.CaloriesFragment;
import lk.jiat.shapeway.activity.MeasurementsFragment;
import lk.jiat.shapeway.activity.SupplementsFragment;
import lk.jiat.shapeway.activity.WalkFragment;
import lk.jiat.shapeway.activity.WaterFragment;
import lk.jiat.shapeway.activity.WorkoutsFragment;
import lk.jiat.shapeway.models.PlanItem;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    List<PlanItem> list;
    Context context;

    public PlanAdapter(Context context, List<PlanItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_plan, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {

        PlanItem item = list.get(i);

        h.title.setText(item.title);
        h.sub.setText(item.subtitle);
        h.icon.setImageResource(item.icon);

        ObjectAnimator animation = ObjectAnimator.ofInt(h.progress, "progress", 0, item.progress);
        animation.setDuration(800);
        animation.start();

        h.itemView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }
            return false;
        });

        h.itemView.setOnClickListener(v -> {
            Fragment fragment = null;

            switch (i) {
                case 0:
                    fragment = new CaloriesFragment();
                    break;
                case 1:
                    fragment = new WorkoutsFragment();
                    break;
                case 2:
                    fragment = new MeasurementsFragment();
                    break;
                case 3:
                    fragment = new WaterFragment();
                    break;
                case 4:
                    fragment = new WalkFragment();
                    break;
                case 5:
                    fragment = new SupplementsFragment();
                    break;
            }

            if (fragment != null) {
                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        )
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, sub;
        ImageView icon;
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            sub = itemView.findViewById(R.id.txtSub);
            icon = itemView.findViewById(R.id.imgIcon);

        }
    }
}