package am.drawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;

/**
 * 文字Drawable
 */
public class TextDrawable extends Drawable {

    private final TextPaint mTextPaint;// 文字画笔
    private float mTextSize;
    private final Rect mMeasureRect = new Rect();
    private String mText;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private float mTextDesc;

    public TextDrawable(Context context, float textSize, int textColor, String text) {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        if (Build.VERSION.SDK_INT > 4) {
            updateTextPaintDensity(context);
        }
        setTextSize(textSize);
        setTextColor(textColor);
        setText(text);
    }

    @TargetApi(5)
    private void updateTextPaintDensity(Context context) {
        mTextPaint.density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mTextPaint.setTextSize(getBounds().height());
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        mTextDesc = metrics.bottom;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mText == null || mText.length() <= 0)
            return;
        final int width = getBounds().width();
        final int height = getBounds().height();
        if (width <= 0 || height <= 0)
            return;
        mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
        final int textWidth = mMeasureRect.width();
        final int textHeight = mMeasureRect.height();
        if (textWidth <= 0 || textHeight <= 0)
            return;
        float scale;
        float scaleWidth = (float) textWidth / width;
        float scaleHeight = (float) textHeight / height;
        scale = Math.min(scaleWidth, scaleHeight);
        canvas.save();
        final float offsetY = getBounds().centerY() + mTextDesc;
        canvas.translate(getBounds().centerX(), offsetY);
        if (scale != 1) {
            canvas.scale(scale, scale, 0, -offsetY + getBounds().centerY());
        }
        canvas.drawText(mText, 0, 0, mTextPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mTextPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(float size) {
        if (mTextSize != size) {
            mTextSize = size;
            mTextPaint.setTextSize(mTextSize);
            if (mText == null) {
                mIntrinsicWidth = 0;
                Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
                mIntrinsicHeight = metrics.bottom - metrics.top;
            } else {
                mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
                mIntrinsicWidth = mMeasureRect.width();
                mIntrinsicHeight = mMeasureRect.height();
            }
            invalidateSelf();
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color 颜色
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidateSelf();
    }

    /**
     * 设置文本
     *
     * @param text 文本
     */
    public void setText(String text) {
        if (mText == null || !mText.equals(text)) {
            mText = text;
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
            mIntrinsicWidth = mMeasureRect.width();
            mIntrinsicHeight = mMeasureRect.height();
            invalidateSelf();
        }
    }
}
