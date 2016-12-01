package com.gaodun.www.zimaxydemo;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by guolinyao on 16/11/30 15:59.
 */

public class ZhiMaView extends View {

    private int maxNum;
    private int startAngle, sweepAngle;
    private int mWidth, mHeight;
    private Context mContext;
    private int radius;
    private Paint paint;
    private Paint paint_2, paint_3, paint_4;
    private int sweepInWidth, sweepOutWidth;
    private int mCurrentNum;

    public int getmCurrentNum() {
        return mCurrentNum;
    }

    public void setmCurrentNum(int mCurrentNum) {
        this.mCurrentNum = mCurrentNum;
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        //根据进度差计算动画时间
        float duration = Math.abs(num - mCurrentNum) / maxNum * 1500 + 500;
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "mCurrentNum", mCurrentNum, num);
        anim.setDuration((long) Math.min(duration, 2000));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                setmCurrentNum(value);
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }

    private int calculateColor(int value) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if (value <= maxNum / 2) {
            fraction = value / (maxNum / 2);
            color = (int) argbEvaluator.evaluate(fraction, 0xffff6347, 0xffff8c00);//红到橙
        } else {
            fraction = (value - maxNum / 2) / (maxNum / 2);
            color = (int) argbEvaluator.evaluate(fraction, 0xffff8c00, 0xff00c1d1);//橙到蓝
        }
        return color;
    }

    public ZhiMaView(Context context) {
        super(context);
    }

    public ZhiMaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public ZhiMaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                .RoundIndicatorView);
        maxNum = typedArray.getInt(R.styleable.RoundIndicatorView_maxNum, 500);
        startAngle = typedArray.getInt(R.styleable.RoundIndicatorView_startAngle, 160);
        sweepAngle = typedArray.getInt(R.styleable.RoundIndicatorView_sweepAngle, 220);
        typedArray.recycle();
        sweepInWidth = dip2px(context, 8);
        sweepOutWidth = dip2px(context, 3);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY) {
            mWidth = wSize;
        } else {
            mWidth = dip2px(mContext, 300);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            mHeight = hSize;
        } else {
            mHeight = dip2px(mContext, 400);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //不能在构造方法初始化，那时还没测量宽高
        radius = getMeasuredWidth() / 4;
        canvas.save();
        canvas.translate(mWidth / 2, mWidth / 2);
        drawRound(canvas);//画内外圆弧
        drawScale(canvas);//画刻度
        drawIndicator(canvas);//画当前进度值
        drawCenterText(canvas);//画中间文字
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas) {
        canvas.save();
        paint_4.setStyle(Paint.Style.FILL);
        paint_4.setTextSize(radius / 2);
        paint_4.setColor(0xffffffff);
        canvas.drawText(mCurrentNum + "", -paint_4.measureText(mCurrentNum + "") / 2,
                0, paint_4);
        paint_4.setTextSize(radius / 4);
        String content = "信用";
        if (mCurrentNum < maxNum / 5) {
            content += text[0];
        } else if (mCurrentNum >= maxNum / 5 && mCurrentNum < maxNum * 2 / 5) {
            content += text[1];
        } else if (mCurrentNum >= maxNum * 2 / 5 && mCurrentNum < maxNum * 3 / 5) {
            content += text[2];
        } else if (mCurrentNum >= maxNum * 3 / 5 && mCurrentNum < maxNum * 4 / 5) {
            content += text[3];
        } else if (mCurrentNum >= maxNum * 4 / 5) {
            content += text[4];
        }
        Rect r = new Rect();
        paint_4.getTextBounds(content, 0, content.length(), r);
        canvas.drawText(content, -r.width() / 2, r.height() + 20, paint_4);
        canvas.restore();
    }

    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    private void drawIndicator(Canvas canvas) {
        canvas.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if (mCurrentNum <= maxNum) {
            sweep = (int) ((float) mCurrentNum / (float) maxNum * sweepAngle);
        } else {
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        int w = dip2px(mContext, 10);
        Shader sweepGradient = new SweepGradient(0, 0, indicatorColor, null);
        paint_2.setShader(sweepGradient);
        RectF rectF = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(rectF, startAngle, sweep, false, paint_2);
        float x = (float) ((radius + w) * Math.cos(Math.toRadians(startAngle + sweep)));
        float y = (float) ((radius + w) * Math.sin(Math.toRadians(startAngle + sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xffffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dip2px(mContext, 3), BlurMaskFilter
                .Blur.SOLID));//需要关闭硬件加速
        canvas.drawCircle(x, y, dip2px(mContext, 3), paint_3);
        canvas.restore();
    }

    private String[] text = {"较差", "中等", "良好", "优秀", "极好"};

    private void drawScale(Canvas canvas) {
        canvas.save();
        float angle = (float) sweepAngle / 30;//弧度间隔
        canvas.rotate(-270 + startAngle);
        for (int i = 0; i <= 30; i++) {
            if (i % 6 == 0) {//画粗刻度和刻度值
                paint.setStrokeWidth(dip2px(mContext, 2));
                paint.setAlpha(0x70);
                canvas.drawLine(0, -radius - sweepInWidth / 2, 0, -radius +
                        sweepInWidth / 2 + dip2px(mContext, 1), paint);
                drawText(canvas, i * maxNum / 30 + "", paint);
            } else {//画刻度
                paint.setStrokeWidth(dip2px(mContext, 1));
                paint.setAlpha(0x50);
                canvas.drawLine(0, -radius + sweepInWidth / 2, 0, -radius -
                        sweepInWidth / 2, paint);
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {//画刻度间文字
                paint.setAlpha(0x50);
                drawText(canvas, text[(i - 3) / 6], paint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String text, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dip2px(mContext, 8));
        float width = paint.measureText(text);//相比getTextBounds来说，这个方法获得的类型是float，更精确
        canvas.drawText(text, -width / 2, -radius + dip2px(mContext, 15), paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawRound(Canvas canvas) {
        canvas.save();
        //内圈
        paint.setAlpha(0x40);
        paint.setStrokeWidth(sweepInWidth);
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
        //外园
        paint.setStrokeWidth(sweepOutWidth);
        int w = dip2px(mContext, 10);
        RectF rectF2 = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        canvas.drawArc(rectF2, startAngle, sweepAngle, false, paint);
        canvas.restore();
    }

    public int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
