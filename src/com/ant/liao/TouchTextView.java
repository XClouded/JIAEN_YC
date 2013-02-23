package com.ant.liao;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * �̳�ImageView ʵ���˶�㴥�����϶�������
 * @author Administrator
 *
 */
public class TouchTextView extends TextView
{
	public TouchTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setPadding(0, 0, 0, 0);
		screenW = getWidth();
		screenH = getHeight();
	}

	static final int NONE = 0;
    static final int DRAG = 1;	   //�϶���
    static final int ZOOM = 2;     //������
    static final int BIGGER = 3;   //�Ŵ�ing
    static final int SMALLER = 4;  //��Сing
    private int mode = NONE;	   //��ǰ���¼� 

    private float beforeLenght;   //���������
    private float afterLenght;    //���������
    private float scale = 0.04f;  //���ŵı��� X Y���������ֵ Խ�����ŵ�Խ��
   
    public int screenW;
    public int screenH;
    
    /*�����϶� ���� */
    private int start_x;
    private int start_y;
	private int stop_x ;
	private int stop_y ;
	
    private TranslateAnimation trans; //���?���߽�Ķ���
	
    public TouchTextView(Context context,int w,int h) {
		super(context, null);
		this.setPadding(0, 0, 0, 0);
		screenW = w;
		screenH = h;
	}
	
	/**
	 * ���������ľ���
	 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    
    /**
     * ���?��..
     */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
	    Log.d("zheng", "gif onTouchEvent");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        		mode = DRAG;
    	    	stop_x = (int) event.getRawX();
    	    	stop_y = (int) event.getRawY();
        		start_x = (int) event.getX();
            	start_y = stop_y - this.getTop();
            	
            	Log.i("dsm", "start_x>>>>>"+start_x+">>>>>>>start_y>>>>>>"+start_y+">>>>>>>>stop_x>>>>>>>>"+stop_x+">>>>>>>>>stop_y>>>>>>>>>>"+stop_y);
            	
            	if(event.getPointerCount()==2)
            		beforeLenght = spacing(event);
                break;
        case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                        mode = ZOOM;
                		beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:
        	/*�ж��Ƿ񳬳���Χ     ������*/
        	mode = NONE;
//        	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//			
//        	int width = PictureWorkingActivity.mFrameWebView.getWidth();
//        	int height = PictureWorkingActivity.mFrameWebView.getHeight();
//        	params.setMargins(getLeft(), getTop(), getRight() - getWidth(), getBottom() - getHeight());
//			Log.d("zheng", "left:" + left + " top:" + top + " right:" + right + " bottom:" + bottm);
//			Log.d("zheng", "getLeft:" + getLeft() + " getTop:" + getTop() + " getRight:" + getRight() + " getBottom:" + getBottom());
//	    	Log.d("zheng", "width: " + getWidth() + " height: " + getHeight());
//	    	Log.d("zheng", "mFrameWebView height: " + PictureWorkingActivity.mFrameWebView.getHeight() + " mFrameWebView width: " + PictureWorkingActivity.mFrameWebView.getWidth());
//            this.setLayoutParams(params);
        	if (true) {
        		break;
        	}
        		int disX = 0;
        		int disY = 0;
	        	if(getHeight()<=screenH || this.getTop()<0)
	        	{
		        	if(this.getTop()<0 )
		        	{
		        		int dis = getTop();
	                	this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
	            		disY = dis - getTop();
		        	}
		        	else if(this.getBottom()>screenH)
		        	{
		        		disY = getHeight()- screenH+getTop();
	                	this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
		        	}
	        	}
	        	if(getWidth()<=screenW)
	        	{
		        	if(this.getLeft()<0)
		        	{
		        		disX = getLeft();
	                	this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
		        	}
		        	else if(this.getRight()>screenW)
		        	{
		        		disX = getWidth()-screenW+getLeft();
	                	this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
		        	}
	        	}
	        	if(disX!=0 || disY!=0)
	        	{
            		trans = new TranslateAnimation(disX, 0, disY, 0);
            		trans.setDuration(500);
            		this.startAnimation(trans);
	        	}
	        	
//				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//	        	params.setMargins(10, 10, 0 , 0);
//	        	setLayoutParams(params);
	        	mode = NONE;
        		break;
        case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        case MotionEvent.ACTION_MOVE:
        		/*�����϶�*/
                if (mode == DRAG) {
                	if(Math.abs(stop_x-start_x-getLeft())<88 && Math.abs(stop_y - start_y-getTop())<85)
                	{
                    	this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());           	
                    	stop_x = (int) event.getRawX();
                    	stop_y = (int) event.getRawY();
                	}
                } 
                /*��������*/
                else if (mode == ZOOM) {
                	if(spacing(event)>10f)
                	{
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;                     
                        if(gapLenght == 0) {  
                           break;
                        }
                        else if(Math.abs(gapLenght)>5f)
                        {
                            if(gapLenght>0) { 
                                this.setScale(scale,BIGGER);   
                            }else {  
                                this.setScale(scale,SMALLER);   
                            }                             
                            beforeLenght = afterLenght; 
                        }
                	}
                }
                break;
        }
        return true;	
	}
	
	/**
	 * ʵ�ִ�������
	 */
    private void setScale(float temp,int flag) {   
        
        if(flag==BIGGER) {   
            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),    
                          this.getTop()-(int)(temp*this.getHeight()),    
                          this.getRight()+(int)(temp*this.getWidth()),    
                          this.getBottom()+(int)(temp*this.getHeight()));  
            screenW = this.getRight()+(int)(temp*this.getWidth()) - this.getLeft()-(int)(temp*this.getWidth());
            screenH = this.getBottom()+(int)(temp*this.getHeight()) - this.getTop()-(int)(temp*this.getHeight());
//            setMaxHeight(screenH);
//            setMaxWidth(screenW);
            //            setShowDimension(screenW, screenH);
        }else if(flag==SMALLER){   
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),    
                          this.getTop()+(int)(temp*this.getHeight()),    
                          this.getRight()-(int)(temp*this.getWidth()),    
                          this.getBottom()-(int)(temp*this.getHeight()));   
            
            screenW = this.getRight()-(int)(temp*this.getWidth()) - this.getLeft()+(int)(temp*this.getWidth());
            screenH = this.getBottom()-(int)(temp*this.getHeight()) - this.getTop()+(int)(temp*this.getHeight());
//            setMaxHeight(screenH);
//            setMaxWidth(screenW);
            //            setShowDimension(screenW, screenH);
        }   
    }
    
	/**
	 * ʵ�ִ����϶�
	 */
    private void setPosition(int left,int top,int right,int bottom) {  
    	this.layout(left,top,right,bottom);     
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottm = bottom;
    	Log.d("zheng", "left: " + left + "top: " + top + "right: " + right + "bottom: " +bottom);
    }
    
    public int left = 90;
    public int top= 90;
    public int right= 90;
    public int bottm= 90;
}
