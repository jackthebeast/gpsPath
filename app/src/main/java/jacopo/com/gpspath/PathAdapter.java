package jacopo.com.gpspath;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jacopo.com.gpspath.data.model.Path;

/**
 * Created by jacop on 08/11/2017.
 */

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder>{

    private List<Path> paths;

    public PathAdapter(List<Path> list){
        paths = list;
    }

    public class PathViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView end;
        public TextView start;

        public PathViewHolder(View v){
            super(v);
            id = (TextView) v.findViewById(R.id.path_id);
            start = (TextView) v.findViewById(R.id.path_start);
            end = (TextView) v.findViewById(R.id.path_end);
        }
    }

    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_path, parent, false);

        return new PathViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PathViewHolder holder, int position) {
        Path movie = paths.get(position);
        holder.id.setText(movie.getIdString());
        holder.start.setText(movie.getStartFormatted());
        holder.end.setText(movie.getEndFormatted());
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }
}
