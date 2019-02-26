package jason.com.rxremvplib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by jason on 18/11/22.
 */

public class NoScrollGridView extends GridView {
    public NoScrollGridView(Context context) {
        super(context);
    }

    public NoScrollGridView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public NoScrollGridView(Context context, AttributeSet attributeSet, int def) {
        super(context,attributeSet,def);
    }
    //exactly-match-确定的值
    //atmost-wrap
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
