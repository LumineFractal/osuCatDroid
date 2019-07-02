package piro13.osucatdroid3;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;


import piro13.osucatdroid3.data.Beatmap;

public class BeatmapAdapter extends ListAdapter<Beatmap, BeatmapAdapter.BeatmapHolder> {
    private OnItemClickListener listener;

    public BeatmapAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Beatmap> DIFF_CALLBACK = new DiffUtil.ItemCallback<Beatmap>() {
        @Override
        public boolean areItemsTheSame(@NonNull Beatmap beatmap, @NonNull Beatmap t1) {
            return beatmap.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Beatmap beatmap, @NonNull Beatmap t1) {
            return beatmap.getBeatmapset_id() == t1.getBeatmapset_id();
        }
    };

    @NonNull
    @Override
    public BeatmapHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beatmap_item, viewGroup, false);
        return new BeatmapHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeatmapHolder beatmapHolder, int i) {
        Beatmap currentBeatmap = getItem(i);
        beatmapHolder.textViewName.setText(currentBeatmap.getTitle());
        String desc = currentBeatmap.getArtist() + " // " + currentBeatmap.getCreator();
        beatmapHolder.textViewDesc.setText(desc);
        String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/beatmaps/" + currentBeatmap.getBeatmapset_id() + ".jpeg";
        File file = new File(path);
        if(file.exists()){
            Picasso.get().load(file).into(beatmapHolder.image);
        }else{
            String imageURL = "https://b.ppy.sh/thumb/"+ currentBeatmap.getBeatmapset_id() +".jpg";
            Picasso.get().load(imageURL).placeholder(R.drawable.ic_map).into(beatmapHolder.image);
        }
    }

    public Beatmap getBeatmapAt(int position) {
        return getItem(position);
    }

    class BeatmapHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDesc;
        private ImageView image;

        public BeatmapHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.beatmap_item_name);
            textViewDesc = itemView.findViewById(R.id.beatmap_item_artist_mapper);
            image = itemView.findViewById(R.id.beatmap_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Beatmap beatmap);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
