package jason.com.rxremvplib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import jason.com.rxremvplib.R;


/**
 * Created by jason on 17/9/12.
 */

public class SearchView extends EditText {

    private float searchSize = 0;
    private float textSize = 0;
    private int textColor = 0xFF000000;
    private String txtContent = "搜索";
    private Drawable mDrawable;
    private Paint paint;
    private Context mContext;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        InitResource(context, attrs);
        InitPaint();
    }

    private void InitResource(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.searchedit);
        float density = context.getResources().getDisplayMetrics().density;
        searchSize = mTypedArray.getDimension(R.styleable.searchedit_imagewidth, 18 * density + 0.5F);  //图片宽度
        textColor = mTypedArray.getColor(R.styleable.searchedit_txtColor, 0xFF848484);
        textSize = mTypedArray.getDimension(R.styleable.searchedit_txtSize, 14 * density + 0.5F);
        txtContent = mTypedArray.getString(R.styleable.searchedit_txtContent);
        mTypedArray.recycle();
    }

    private void InitPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawSearchIcon(canvas);
    }

    private void DrawSearchIcon(Canvas canvas) {
        if (this.getText().toString().length() == 0) {
//            float textWidth = paint.measureText("找工作 找房子");
            float textWidth = paint.measureText(txtContent);
            float textHeight = getFontLeading(paint);

            float dx = (getWidth() - searchSize - textWidth - 8) / 2;
            float dy = (getHeight() - searchSize) / 2;

            canvas.save();
            canvas.translate(getScrollX() + dx, getScrollY() + dy);
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
            canvas.drawText(txtContent, getScrollX() + searchSize + 8, getScrollY() + (getHeight() - (getHeight() - textHeight) / 2) - paint.getFontMetrics().bottom - dy, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mDrawable == null) {
            try {
//                mDrawable = getContext().getResources().getDrawable(R.drawable.search);
                mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.search);
                mDrawable.setBounds(0, 0, (int) searchSize, (int) searchSize);
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
            mDrawable = null;
        }
        super.onDetachedFromWindow();
    }

    public float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.bottom - fm.top;
    }
}
