package com.bdoks54.dynamicsine;

import android.app.Activity;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.view.View;

public class DynamicSineView1 extends View {
    float second;   // 몇초 경과했는지
    int width;  // 화면넓이
    int height; // 화면높이
    int maxwidth;   //넓이와 높이 중 큰 값
    int bigRadius = 200;    //큰 원 반지름
    float ht = 6.0f;    //각도 변화량. 6초에 한바퀴 회전
    int verticalOffset = 50;
    Context context;    //Activity의 정보를 갖는 Activity의 부모 클래스
    public DynamicSineView1(Context context){
        super(context);
        this.context = context; //어떤 컨텍스트 -> 어떤 Activity
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        init(); //넓이, 높이 구하기
        drawBigCircle(canvas);  //큰 원과 초침 그리기, 작은 원 그리기
        drawSine(canvas);   //싸인 그리기
        clockCalc();    //0.1초후 각도 변경.0.1초마다 onDraw() 호출
    }

    public void init(){
        width = getWidth();
        height = getHeight();
        //넓이, 높이 : 생성자에서 0이 됨 onDraw()에서 구할것
        maxwidth = Math.max(width,height);  //넓이와 높이 중 최대값
    }

    //큰 원과 초침 그리기
    public void drawBigCircle(Canvas canvas){
        //큰 원 그리기
        Paint circleIn = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleIn.setColor(Color.LTGRAY);
        canvas.drawCircle(0+bigRadius, 0+ bigRadius + verticalOffset, bigRadius, circleIn);
        //초침 그리기- 일정 시간마다 초침 이동
        Matrix mt = new Matrix();   //이동각
        Path secondPin = new Path();    //이동 전 정보
        secondPin.moveTo(0+ bigRadius, 0+ bigRadius+verticalOffset);
        secondPin.lineTo(0+bigRadius *2, 0+bigRadius+verticalOffset);   //0초를 그림
        mt.setRotate(-1.0f*ht * second, 0+ bigRadius, 0+ bigRadius +verticalOffset);    //이동 정보
        secondPin.transform(mt);    //이동 시간에 따른 각도 변화 반영
        Paint secondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondPaint.setColor(Color.GREEN);
        secondPaint.setStyle(Paint.Style.STROKE);
        secondPaint.setStrokeWidth(3);
        canvas.drawPath(secondPin, secondPaint);    //이동된 초침 그리기
        //큰 원의 x,y 좌표 구하기
        float rex= (float)(bigRadius*Math.cos(Math.toRadians(-1.0f*ht*second)));
        float rey=(float)(bigRadius*Math.sin(Math.toRadians(-1.0f*ht*second)));
        // (rex+a, rey+b, 10) 원의 공식. 작은 원 그리기
        drawSmallCircle(canvas, rex+bigRadius, rey+bigRadius+verticalOffset, 10);
    }

    public void drawSmallCircle(Canvas canvas, float fx, float fy, float r) {
        Paint circleIn = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleIn.setStyle(Paint.Style.FILL);
        circleIn.setColor(Color.RED);
        canvas.drawCircle(fx, fy, r, circleIn);   //붉은색으로 채운 원
    }

    public void drawSine(Canvas canvas){
        Path path = new Path();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        int step = (maxwidth - bigRadius*2)/360;    //넓이에서 원의 지름을 뺀다. 360등분
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        path.moveTo(0+bigRadius*2, verticalOffset+bigRadius);
        for(int i=0; i<= second*(int)ht; i++){
            path.lineTo(i*step+ bigRadius*2,
                    (float)(bigRadius*(1-Math.sin(Math.toRadians(i)))+verticalOffset));
        }
        canvas.drawPath(path,paint);
    }

    //0.1초마다 onDraw() 호출 초기화
    public void clockCalc(){
        if(second >= 59){
            second=0;
        }else{
            second++;
        }
        new Thread(){   //쓰레드 생성
            public void run(){  //쓰레드는 run()을 오버라이딩
                //서브 쓰레드에서 ui 직접 조작 불가능 UI쓰레드 이용
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() { //UI쓰레드 run() 구현
                        invalidate();   //onDraw() 호출
                        SystemClock.sleep(100); //0.1초
                    }
                });

            }
        }.start();  //쓰레드 start() -> run() 호출
    }
}
