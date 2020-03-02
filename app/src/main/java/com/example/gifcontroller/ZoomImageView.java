package com.example.gifcontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class ZoomImageView extends AppCompatImageView {
    private Drawable del;
    private Drawable edit;
    private Drawable control;
    private RectF rectF = new RectF();
    private Paint paint;

    private int frameWidth = 2;
    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(frameWidth);

        del = getResources().getDrawable(R.mipmap.icon_red_controller_del);
        edit = getResources().getDrawable(R.mipmap.icon_red_controller_edit);
        control = getResources().getDrawable(R.mipmap.icon_red_controller_draw);
        setPadding(CONTROL_WIDTH / 2, CONTROL_WIDTH / 2, CONTROL_WIDTH / 2, CONTROL_WIDTH / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPoint(getWidth() / 2f, getHeight() / 2f, paint);
        canvas.drawRect(CONTROL_WIDTH / 2, CONTROL_WIDTH / 2, getWidth() - CONTROL_WIDTH / 2, getHeight() - CONTROL_WIDTH / 2, paint);

        del.setBounds(getWidth() - CONTROL_WIDTH, 0, getWidth(), CONTROL_WIDTH);
        del.draw(canvas);
        edit.setBounds(0, 0, CONTROL_WIDTH, CONTROL_WIDTH);
        edit.draw(canvas);
        control.setBounds(getWidth() - CONTROL_WIDTH, getHeight() - CONTROL_WIDTH, getWidth(), getHeight());
        control.draw(canvas);
    }

    private static final int CONTROL_WIDTH = 30;

    public int getControlWidth() {
        return CONTROL_WIDTH;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("savion", String.format("onSizeChange:%s-%s-%s-%s", w, h, oldh, oldh));
        rectF.set(0, 0, w, h);
    }
}
