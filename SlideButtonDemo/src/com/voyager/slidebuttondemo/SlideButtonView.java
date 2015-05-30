package com.voyager.slidebuttondemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author wuhaojie 滑动按钮
 */
public class SlideButtonView extends View implements OnClickListener {

	private Bitmap background;
	private Bitmap slider;
	private Paint paint;
	private float slider_dis;
	private boolean state = false;
	private int x2;
	private int x1;
	private boolean isSliding;

	public SlideButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		findResource();
		readAttrs(context, attrs);
		initView();
	}

	/**
	 * 获取默认图片
	 */
	private void findResource() {
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button_bg);
		slider = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button_slider);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		paint = new Paint();
		paint.setAntiAlias(true);
		if (state) {
			slider_dis = background.getWidth() - slider.getWidth();
		} else {
			slider_dis = 0;
		}
		setOnClickListener(this);
		refreshView();
	}

	/**
	 * @param context
	 * @param attrs
	 *            读取自定义属性值
	 */
	private void readAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SlideButton);
		for (int i = 0; i < ta.getIndexCount(); i++) {
			int index = ta.getIndex(i);
			int resourceId;
			switch (index) {
			case R.styleable.SlideButton_slider:
				resourceId = ta.getResourceId(index, -1);
				if (resourceId == -1) {
					throw new RuntimeException("SliderButton Slider 错误！");
					// return;
				}
				slider = BitmapFactory.decodeResource(getResources(),
						resourceId);
				break;

			case R.styleable.SlideButton_background:
				resourceId = ta.getResourceId(index, -1);
				if (resourceId == -1) {
					throw new RuntimeException("SliderButton Background 错误！");
					// return;
				}
				background = BitmapFactory.decodeResource(getResources(),
						resourceId);
				break;

			case R.styleable.SlideButton_state:
				state = ta.getBoolean(index, false);
				break;

			}
		}
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
		System.out
				.println("onClick: state=" + state + " isSliding" + isSliding);
		if (isSliding) {
			return;
		}
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
		slider_dis = slider_dis > background.getWidth() - slider.getWidth() ? background
				.getWidth() - slider.getWidth()
				: slider_dis;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		float dis = 0;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x1 = x2 = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			dis = event.getX() - x1;
			System.out.println("dis=" + Math.abs(dis));
			if (Math.abs(dis) > 8) {
				isSliding = true;
			}
			x2 = (int) event.getX();
			slider_dis += dis;
			break;
		case MotionEvent.ACTION_UP:
			if (isSliding) {
				int max = background.getWidth() - slider.getWidth();
				if (slider_dis > max / 2) {
					state = true;
					slider_dis = max;
				} else {
					state = false;
					slider_dis = 0;
				}
				refreshView();
			}
			break;

		}
		dis = 0;
		isSliding = false;
		refreshView();
		return true;
	}

}
