package com.voyager.slidebuttondemo;

import com.example.slidebuttondemo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class SlideButtonView extends View implements OnClickListener {

	private Bitmap background;
	private Bitmap slider;
	private Paint paint;
	private float slider_dis;
	private boolean state = false;

	public SlideButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button_bg);
		slider = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button_slider);
		paint = new Paint();
		paint.setAntiAlias(true);
		slider_dis = 0;
		setOnClickListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(background.getWidth(), background.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.drawBitmap(slider, slider_dis, 0, paint);
	}

	@Override
	public void onClick(View v) {
		if (state) {
			slider_dis = 0;
		} else {
			slider_dis = background.getWidth() - slider.getWidth();
		}
		state = !state;
		refreshView();
	}

	private void refreshView() {
		slider_dis = slider_dis < 0 ? 0 : slider_dis;
		slider_dis = slider_dis > background.getWidth() ? background.getWidth()
				: slider_dis;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
}
