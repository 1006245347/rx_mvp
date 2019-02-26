package jason.com.rxremvplib.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jason.com.rxremvplib.R;

/**
 * Created by jason on 18/7/6.
 */

public class GlobalReHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SparseArray<View> viewSparseArray;  //性能比hashmap好，但是key只可以是int
    private onItemGlobalClickListener globalClickListener;

    public GlobalReHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        viewSparseArray = new SparseArray<>();
    }

    //根据id 获取view
    public <T extends View> T getView(int viewId) {
        View view = viewSparseArray.get(viewId);
        if (null == view) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public GlobalReHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    public GlobalReHolder setViewVisibility(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
        return this;
    }

    public GlobalReHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    public GlobalReHolder setHttpResource(int viewId, Context context,String url) {
        ImageView imageView = getView(viewId);
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
        return this;
    }

    public GlobalReHolder setBtn(int viewId, String text) {
        Button button = getView(viewId);
        button.setText(text);
        return this;
    }

    public interface onItemGlobalClickListener {
        void onItemClickListener(int position);
    }

    public void setGlobalClickListener(onItemGlobalClickListener listener) {
        this.globalClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (null != globalClickListener) {
            globalClickListener.onItemClickListener(getAdapterPosition());
        }
    }
}
