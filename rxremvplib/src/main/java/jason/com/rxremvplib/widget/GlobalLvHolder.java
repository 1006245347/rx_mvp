package jason.com.rxremvplib.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

/**
 * https://www.jianshu.com/p/2bc7edde983f
 * Created by jason on 18/7/16.
 */

public class GlobalLvHolder {

    private View mConvertView;
    private Map<Integer, View> mViewMap;
    private Context mContext;

    public GlobalLvHolder(Context context, int layoutId, ViewGroup parent) {
//        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
//        mConvertView = LayoutInflater.from(context).inflate(layoutId, null);
        mConvertView = View.inflate(context, layoutId, null);
        mViewMap = new HashMap<>();
        mConvertView.setTag(this);
        mContext = context;
    }

    public synchronized static GlobalLvHolder getHolder(Context context, int layoutId, View convertView, ViewGroup parent) {
        if (null == convertView) {
            return new GlobalLvHolder(context, layoutId, parent);
        } else {
            return (GlobalLvHolder) convertView.getTag();
        }
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 获取控件,mViewMap里有则直接取,没有则通过findViewById拿到并保存到mViewMap
     *
     * @param viewId 控件id
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        if (mViewMap.containsKey(viewId)) {
            return (T) mViewMap.get(viewId);
        } else {
            View view = mConvertView.findViewById(viewId);
            mViewMap.put(viewId, view);
            return (T) view;
        }
    }

    /**
     * 内容适配,一般就用到ImageView和TextView,而且ImageView通常是url,所以就用反射封装一下,返回自身实现链式调用
     *
     * @param viewId  控件id
     * @param content 要适配的内容(文字字符串/图片url)
     * @return
     */
    public GlobalLvHolder set(int viewId, String content) {   //可链式调用
        View view = getView(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(content);
        } else if (view instanceof ImageView) {
            Glide.with(mContext).load(content).into((ImageView) view);
        }
        return this;
    }

    public GlobalLvHolder setImageRes(int viewId, int res) {
        View view = getView(viewId);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(res);
        }
        return this;
    }

    public GlobalLvHolder setCirImageRes(int viewId, int res) {
        View view = getView(viewId);
        if (view instanceof CircleImageView) {
            ((CircleImageView) view).setBackgroundColor(res);
        }
        return this;
    }

}
