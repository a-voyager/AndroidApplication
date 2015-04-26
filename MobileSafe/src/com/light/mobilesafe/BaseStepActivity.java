package com.light.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseStepActivity extends Activity {
		
		public GestureDetector detector;
		public SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		detector = new GestureDetector(this, new OnGestureListener() {
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				if(e2.getRawX() - e1.getRawX() > 600){
					//向左滑动
					System.out.println("toleft");

					return true;
				}
				if(e1.getRawX() - e2.getRawX() > 600){
					//向右滑动
					System.out.println("toright");
					
					return true;
				}
				if(e1.getRawY() - e2.getRawY() > 600){
					System.out.println("totop");
					return true;
				}
				if(e2.getRawY() - e1.getRawY() > 600){
					System.out.println("toend");
					return true;
				}
				if(Math.abs(velocityX) < 200 || Math.abs(velocityY) < 200){
					System.out.println("tooSlow");
					return true;
				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
		 		
		 	}
		 	
	 	});
	}

	public abstract void toNext(View v);

	public abstract void toLast(View v);
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
	return super.onTouchEvent(event);
	}
	
}
