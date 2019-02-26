package jason.com.rxremvplib.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by jason on 18/7/16.
 */

public abstract class GlobalLvAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mList;
    protected int mLayoutId;

    public GlobalLvAdapter(Context context, List<T> list, int layoutId) {
        mContext = context;
        mList = list;
        mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 根据OO思想,第一行和最后一行不存在变化,所以封装起来,中间适配内容的部分通过convertView抽象方法交给调用者去实现
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GlobalLvHolder holder = GlobalLvHolder.getHolder(mContext, mLayoutId, convertView, parent);
        convertView(holder, mList.get(position),position);
        return holder.getConvertView();
    }

    /**
     * 真正内容适配的方法
     *
     * @param holder
     * @param t
     */
    public abstract void convertView(GlobalLvHolder holder, T t,int position);
}
