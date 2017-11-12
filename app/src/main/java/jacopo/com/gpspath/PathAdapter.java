package jacopo.com.gpspath;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import jacopo.com.gpspath.data.MapDatabase;
import jacopo.com.gpspath.data.model.Path;

/**
 * Created by jacop on 08/11/2017.
 */

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder>{

    private final MapDatabase database;
    private List<Path> paths;
    private View.OnClickListener clickListener;

    public PathAdapter(List<Path> list, View.OnClickListener clickListener, MapDatabase database){
        paths = list;
        this.clickListener = clickListener;
        this.database = database;
    }

    public class PathViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView end;
        public TextView start;
        public TextView distance;
        public TextView time;

        public PathViewHolder(View v){
            super(v);
            id = (TextView) v.findViewById(R.id.path_id);
            start = (TextView) v.findViewById(R.id.path_start);
            end = (TextView) v.findViewById(R.id.path_end);
            distance = (TextView) v.findViewById(R.id.path_distance);
            time = (TextView) v.findViewById(R.id.path_time);
        }
    }

    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_path, parent, false);
        itemView.setOnClickListener(clickListener);
        return new PathViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PathViewHolder holder, int position) {
        Path path = paths.get(position);
        holder.id.setText(path.getIdString());
        holder.start.setText(path.getStartFormatted());
        holder.end.setText(path.getEndFormatted());

        double d = path.getDistance(database);
        if(d!=-1)
            if(d<1000){
                holder.distance.setText(String.format("%.2f", d) + " m");
            }else{
                holder.distance.setText(String.format("%.2f", d/1000) + " km");
            }

        holder.time.setText(path.getDuration());
    }

    public void updateList(List<Path> list){
        paths = list;
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }
}
