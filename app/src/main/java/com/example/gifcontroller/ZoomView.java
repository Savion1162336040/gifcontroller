package com.example.gifcontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ZoomView extends RelativeLayout {
    private static final int TYPE_IDEL = 0;
    private static final int TYPE_DRAG = 1;
    private static final int TYPE_SCALE = 2;
    private static final int TYPE_LEFT_TOP = 3;
    private static final int TYPE_RIGHT_TOP = 4;
    private static final int TYPE_RIGHT_BOTTOM = 5;
    private static final int TYPE_LEFT_BOTTOM = 6;

    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float rotation; // 旋转角度

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;


    private int moveType = TYPE_IDEL; // 0=未选择，1=拖动，2=缩放
    private ZoomImageView imageView;
    //中心点
    private PointF center = new PointF();
    //图片rect
    private RectF imgLayout = new RectF();


    public ZoomView(Context context) {
        this(context, null);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.zoom_layout, this, true);
        imageView = findViewById(R.id.image);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getParent() != null && getParent() instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) getParent();
            if (vg.getWidth() > 0 && vg.getHeight() > 0) {
                center.set(vg.getWidth() / 2f, vg.getHeight() / 2f);
            }
        }
    }

    public void setImageRes(String path) {
        if (path.endsWith(".gif")) {
            Glide.with(this).asGif().load(path)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
                            imgLayout.set(center.x - width / 2f,
                                    center.y - height / 2f,
                                    center.x + width / 2f,
                                    center.y + height / 2f);
                            ViewGroup.LayoutParams params = getLayoutParams();
                            params.width = width + imageView.getControlWidth();
                            params.height = height + imageView.getControlWidth();
                            setLayoutParams(params);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Glide.with(this).asDrawable().load(path)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
                            imgLayout.set(center.x - width / 2f,
                                    center.y - height / 2f,
                                    center.x + width / 2f,
                                    center.y + height / 2f);
                            ViewGroup.LayoutParams params = getLayoutParams();
                            params.width = width + imageView.getControlWidth();
                            params.height = height + imageView.getControlWidth();
                            setLayoutParams(params);
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    private int judgeStatus(float x, float y) {
        return TYPE_IDEL;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveType = judgeStatus(event.getX(), event.getY());
                actionX = event.getRawX();
                actionY = event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == 1) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                } else if (moveType == 2) {
                    scale = scale * getSpacing(event) / spacing;
                    setScaleX(scale);
                    setScaleY(scale);
                    rotation = rotation + getDegree(event) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    setRotation(rotation);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }
        return super.onTouchEvent(event);
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}