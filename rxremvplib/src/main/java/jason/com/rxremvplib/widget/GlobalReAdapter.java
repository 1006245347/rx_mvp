package jason.com.rxremvplib.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jason on 18/7/6.
 */

public abstract class GlobalReAdapter<T> extends RecyclerView.Adapter<GlobalReHolder> {
    protected LayoutInflater layoutInflater;
    public List<T> dataList;
    protected int layoutId;
    protected Context mContext;
    protected GlobalReHolder.onItemGlobalClickListener mItemListener;

    public GlobalReAdapter(Context context, List<T> dataList, int layoutId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.layoutId = layoutId;
        this.mContext = context;
    }

    public GlobalReAdapter(Context context, List<T> dataList, int layoutId, GlobalReHolder.onItemGlobalClickListener listener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.layoutId = layoutId;
        this.mContext = context;
        this.mItemListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public GlobalReHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = layoutInflater.inflate(layoutId, parent, false);
        return new GlobalReHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GlobalReHolder holder, int position) {
        //adapter中是算上顶部动画，  adatper 1 Layout 1 Postion 0
//        Log.v("TAG", "XRE-->" + "Adapter " + holder.getAdapterPosition() + " Layout " + holder.getLayoutPosition() + " Position " + position);
        bindData(holder, dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected void bindData(GlobalReHolder holder, T data) {
    }
}
