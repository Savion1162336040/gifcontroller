package com.example.gifcontroller;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class GifController extends AppCompatImageView {
    public GifController(Context context) {
        this(context, null);
    }

    public GifController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getResources().getDrawable(R.mipmap.member_tag_detail_bg));
        } else {
            setBackgroundDrawable(getResources().getDrawable(R.mipmap.member_tag_detail_bg));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e("savion", String.format("onSizeChange:%s-->%s-%s-%s-%s", changed, left, top, right, bottom));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("savion", String.format("onSizeChange:%s-%s-%s-%s", w, h, oldw, oldh));
    }

    boolean draging = false;
    private PointF center = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                center.set(event.getX(),event.getY());
                draging = true;
                return true;
            case MotionEvent.ACTION_UP:
                draging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (draging) {
                    setTranslationX(center.x-getWidth()/2f);
                    setTranslationY(center.y-getHeight()/2f);
                    float newX = event.getX()-center.x;
                    float newY = event.getY()-center.y;
                    center.set(newX,newY);
                    Log.e("savion",String.format("中心点:%s-%s,接触点:%s-%s",center.x,center.y,event.getX(),event.getY()));
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
