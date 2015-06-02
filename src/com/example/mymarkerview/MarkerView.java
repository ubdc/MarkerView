package com.example.mymarkerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class MarkerView extends TextView {
	private Paint bgPaint = new Paint();
	private Paint borderPaint = new Paint();
	private int textMeasuredWidth;
	private int textMeasuredHeight;
	private float radius;
	private float cornerRadius;
	private Path path = new Path();
	private RectF oval = new RectF();
	private GradientDrawable shadow;
	private float shadowRadius;
	private float shadowScallX = 1.33f;
	private float shadowScallY = 0.33f;
	private Rect shadowRect = new Rect();
	private Path borderPath = new Path();
	private float borderWidth = 2f;
	
	private static final int[] ATTRS = {
		android.R.attr.colorForeground,
		android.R.attr.colorBackground,
		android.R.attr.radius,
	};

	public MarkerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		int backgroundColor = a.getColor(1, 0);
		radius = a.getDimension(2, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics()));
		int strokeColor = a.getColor(0, 0);
		shadowRadius = radius / 2;
		a.recycle();
		bgPaint.setAntiAlias(true);
		bgPaint.setDither(true);
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(backgroundColor);
		
		borderPaint.setAntiAlias(true);
		borderPaint.setDither(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setColor(strokeColor);
		borderPaint.setStrokeWidth(borderWidth);
		
		shadow = new GradientDrawable();
		shadow.setShape(GradientDrawable.RECTANGLE);
		shadow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		shadow.setGradientRadius(shadowRadius);
		shadow.setColors(new int[] {0x40000000, 0x00000000});
	}

	public MarkerView(Context context) {
		this(context, null);
	}

	@Override
	public void setBackgroundColor(int color) {
		bgPaint.setColor(color);
		invalidate();
	}
	
	@Override
	public void setBackground(Drawable background) {
	}
	
	@Override
	public void setBackgroundDrawable(Drawable background) {
	}
	
	@Override
	public void setBackgroundResource(int resid) {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		textMeasuredWidth = getMeasuredWidth();
		textMeasuredHeight = getMeasuredHeight();
		
		cornerRadius = Math.min(textMeasuredWidth - radius * 2, textMeasuredHeight) / 2;
		constructPath();
		constructBorderPath();
		//shadow's width/height is half of the radius
		shadowRect.set(
				(int) (textMeasuredWidth / 2 - shadowRadius), 
				(int) (textMeasuredHeight + radius - shadowRadius), 
				(int) (textMeasuredWidth / 2 + shadowRadius), 
				(int) (textMeasuredHeight + radius + shadowRadius));
		shadow.setBounds(shadowRect);
		setMeasuredDimension(textMeasuredWidth, (int) (textMeasuredHeight + radius + shadowRadius * shadowScallY + 0.5f));
	}
	
	private void constructPath() {
		path.reset();
		path.moveTo(cornerRadius, 0);
		path.lineTo(textMeasuredWidth - cornerRadius, 0);
		oval.set(textMeasuredWidth - cornerRadius * 2, 0, textMeasuredWidth, cornerRadius * 2);
		path.arcTo(oval, -90, 90);
		oval.set(textMeasuredWidth - cornerRadius * 2, textMeasuredHeight - cornerRadius * 2, textMeasuredWidth, textMeasuredHeight);
		path.lineTo(oval.right, oval.centerY());
		path.arcTo(oval, 0, 90);
		oval.set(textMeasuredWidth / 2f, textMeasuredHeight, textMeasuredWidth / 2f + radius * 2, textMeasuredHeight + radius * 2);
		path.lineTo(oval.centerX(), oval.top);
		path.arcTo(oval, -90, -90);
		oval.left -= radius * 2;
		oval.right -= radius * 2;
		path.arcTo(oval, 0, -90);
		path.lineTo(cornerRadius, textMeasuredHeight);
		oval.set(0, textMeasuredHeight - cornerRadius * 2, cornerRadius * 2, textMeasuredHeight);
		path.arcTo(oval, 90, 90);
		oval.set(0, 0, cornerRadius * 2, cornerRadius * 2);
		path.lineTo(oval.left, oval.centerY());
		path.arcTo(oval, 180, 90);
		path.close();
	}
	
	private void constructBorderPath() {
		float halfBorderWidth = borderWidth / 2;
		borderPath.reset();
		borderPath.moveTo(cornerRadius, halfBorderWidth);
		borderPath.lineTo(textMeasuredWidth - cornerRadius, halfBorderWidth);
		oval.set(textMeasuredWidth - cornerRadius * 2 + halfBorderWidth, halfBorderWidth, textMeasuredWidth - halfBorderWidth, cornerRadius * 2 - halfBorderWidth);
		borderPath.arcTo(oval, -90, 90);
		oval.set(textMeasuredWidth - cornerRadius * 2 - halfBorderWidth, textMeasuredHeight - cornerRadius * 2 + halfBorderWidth, textMeasuredWidth - halfBorderWidth, textMeasuredHeight - halfBorderWidth);
		borderPath.lineTo(oval.right, oval.centerY());
		borderPath.arcTo(oval, 0, 90);
		oval.set(textMeasuredWidth / 2f, textMeasuredHeight, textMeasuredWidth / 2f + radius * 2, textMeasuredHeight + radius * 2);
		borderPath.lineTo(oval.centerX(), oval.top);
		borderPath.arcTo(oval, -90, -90);
		oval.left -= radius * 2;
		oval.right -= radius * 2;
		borderPath.arcTo(oval, 0, -90);
		borderPath.lineTo(cornerRadius, textMeasuredHeight - halfBorderWidth);
		oval.set(halfBorderWidth, textMeasuredHeight - cornerRadius * 2 + halfBorderWidth, cornerRadius * 2 - halfBorderWidth, textMeasuredHeight - halfBorderWidth);
		borderPath.arcTo(oval, 90, 90);
		oval.set(halfBorderWidth, halfBorderWidth, cornerRadius * 2 - halfBorderWidth, cornerRadius * 2 - halfBorderWidth);
		borderPath.lineTo(oval.left, oval.centerY());
		borderPath.arcTo(oval, 180, 90);
		borderPath.close();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, left + textMeasuredWidth, top + textMeasuredHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.scale(shadowScallX, shadowScallY, shadowRect.centerX(), shadowRect.centerY());
		shadow.draw(canvas);
		canvas.restore();
		canvas.drawPath(path, bgPaint);
		canvas.drawPath(borderPath, borderPaint);
		super.onDraw(canvas);
	}
}