package com.lenovohit.swipemenu;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 自定义滑动菜单
 * Created by yuzhijun on 2017/9/20.
 */
public class SwipeMenu extends FrameLayout {
    private View mContent;
    private View mMenu;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mSwipeMenuWidth;
    private int mContentWidth;
    private int mMenuWidth;
    //分别记录上次滑动的坐标
    private int mLastX;
    private int mLastY;
    //分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastInterceptX;
    private int mLastInterceptY;
    //最小滑动距离
    private int mMinimumVelocity;

    public SwipeMenu(@NonNull Context context) {
        super(context);
        init();
    }

    public SwipeMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
         init();
    }

    public SwipeMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContent = getChildAt(0);
        mMenu = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);
        int width = mContent.getMeasuredWidth() + mMenu.getMeasuredWidth();
        int height = Math.max(mContent.getMeasuredHeight(),mMenu.getMeasuredHeight());

        setMeasuredDimension(MeasureSpec.EXACTLY == widthMode ? widthMeasureSpec : width,
                MeasureSpec.EXACTLY == heightMode ? heightMeasureSpec : height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentWidth = mContent.getMeasuredWidth();
        mMenuWidth = mMenu.getMeasuredWidth();
        mSwipeMenuWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mMenu.layout(mContentWidth,0,mContentWidth + mMenuWidth,getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                if (mIStatusChangedListener != null){
                    mIStatusChangedListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                //判断如果x方向滑动大于y方向滑动，则拦截(防止内容里面有控件点击事件消耗了此事件)
                if (Math.abs(deltaX) > Math.abs(deltaY)){
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;

        mLastInterceptX = x;
        mLastInterceptY = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                mVelocityTracker.computeCurrentVelocity(1000);

                int scrollerX = getScrollX() - deltaX;
                //边界检测，如果向右滑动则scrollerX不动，如果左滑则不能超过菜单距离
                if (scrollerX < 0){
                    scrollerX = 0;
                }else if (scrollerX > mMenuWidth){
                    scrollerX = mMenuWidth;
                }
                scrollTo(scrollerX,getScrollY());

                int xVelocity = (int) mVelocityTracker.getXVelocity();
                //当x方向滑动大于y方向滑动，且x方向滑动大于最小滑动距离则说明这个控件要滑动了，告诉父控件不要把事件拦截了
                if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(xVelocity) > Math.abs(mMinimumVelocity)){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                int totalX = getScrollX();
                //回弹检测
                if (totalX < mMenuWidth/2){
                    closeSwipeMenu();
                }else{
                    openSwipeMenu();
                }
                mVelocityTracker.clear();
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    public void closeSwipeMenu(){
        int distanceX = 0 - getScrollX();
        mScroller.startScroll(getScrollX(), getScrollY(), distanceX, getScrollY());
        invalidate();
        if (mIStatusChangedListener != null){
            mIStatusChangedListener.onClose(this);
        }
    }

    public void openSwipeMenu(){
        int distanceX = mMenuWidth - getScrollX();
        mScroller.startScroll(getScrollX(), getScrollY(), distanceX, getScrollY());
        invalidate();
        if (mIStatusChangedListener != null){
            mIStatusChangedListener.onOpen(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }

    IStatusChangedListener mIStatusChangedListener;
    public void setOnStatusChangedListener(IStatusChangedListener statusChangedListener){
        this.mIStatusChangedListener = statusChangedListener;
    }
    interface IStatusChangedListener{
        void onDown(SwipeMenu swipeMenu);
        void onOpen(SwipeMenu swipeMenu);
        void onClose(SwipeMenu swipeMenu);
    }
}
