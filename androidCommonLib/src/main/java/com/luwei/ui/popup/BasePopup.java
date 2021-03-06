package com.luwei.ui.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by LiCheng
 * Date：2018/12/6
 */
public abstract class BasePopup<T extends BasePopup> implements PopupWindow.OnDismissListener {
    private static final String TAG = "CustomPopup";
    private static final float DEFAULT_DIM = 0.7f;

    private Context mContext;
    private PopupWindow mPopupWindow;
    //设置 锚点View
    private View mAnchorView;
    @YGravity
    private int mYGravity = YGravity.BELOW;
    @XGravity
    private int mXGravity = XGravity.LEFT;
    private int mOffsetX;
    private int mOffsetY;

    //设置 PopupWindow  内展示的内容view
    private View mContentView;
    //设置 PopupWindow  内展示的内容layoutId
    private int mLayoutId;

    //设置 PopupWindow的宽高
    private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    //设置 PopupWindow 获取焦点+点击外部取消
    private boolean mFocusAndOutsideEnable = true;
    //设置 PopupWindow 是否可以获取焦点
    private boolean mFocusable = true;
    //设置 点击 PopupWindow以外区域，隐藏 PopupWindow
    private boolean mOutsideTouchable = true;
    //设置 PopupWindow 是否可触摸 false情况下点击直接关闭
    private boolean mTouchable = true;

    //弹出pop时，背景是否变暗
    private boolean isBackgroundDim;
    //背景变暗时透明度
    private float mDimValue = DEFAULT_DIM;
    //背景变暗颜色
    @ColorInt
    private int mDimColor = Color.BLACK;
    //背景变暗的view
    @NonNull
    private ViewGroup mDimView;

    //设置展示和消失动画
    private int mAnimationStyle;
    //设置 PopupWindow的入场动画
    private Transition mEnterTransition;
    //设置 PopupWindow的出场动画
    private Transition mExitTransition;

    //设置输入法的操作模式
    private int mInputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
    private int mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;


    //是否重新测量宽高
    private boolean isNeedReMeasureWH = true;
    //真实的宽高是否已经准备好
    private boolean isRealWHAlready = false;
    private boolean isAtAnchorViewMethod = false;

    //监听 PopupWindow关闭的事件
    private PopupWindow.OnDismissListener mOnDismissListener;
    //PopupWindow触摸时的监听回调
    private View.OnTouchListener mTouchInterceptor;
    //用于获取准确的PopupWindow宽高
    private OnRealWHAlreadyListener mOnRealWHAlreadyListener;


    protected T self() {
        return (T) this;
    }

    public T create() {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow();
        }
        //调用生命周期 创建PopupWindow
        onPopupWindowCreated();
        //创建PopupWindow
        createPopup();
        //调用生命周期 PopupWindow创建完成
        onPopupWindowViewCreated(mContentView);
        //设置PopupWindow属性
        setPopup();

        return self();
    }

    private void createPopup() {
        //传入layoutId时转成View
        if (mContentView == null) {
            if (mLayoutId != 0 && mContext != null) {
                mContentView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            } else {
                throw new IllegalArgumentException("The content view is null,the layoutId="
                        + mLayoutId + ",context=" + mContext);
            }
        }
        mPopupWindow.setContentView(mContentView);
        //创建PopupWindow对象，未设置size时设为wrap大小
        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.setWidth(mWidth);
        } else {
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.setHeight(mHeight);
        } else {
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //如未设置宽高，计算初始化后PopupWindow的宽高，传入mWidth和mHight中
        measureContentView();
        //获取contentView的精准大小
        registerOnGlobalLayoutListener();

        mPopupWindow.setInputMethodMode(mInputMethodMode);
        mPopupWindow.setSoftInputMode(mSoftInputMode);

    }

    private void setPopup() {
        setFocusAndBack();

        if (mAnimationStyle != 0) {
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }

        mPopupWindow.setOnDismissListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mEnterTransition != null) {
                mPopupWindow.setEnterTransition(mEnterTransition);
            }

            if (mExitTransition != null) {
                mPopupWindow.setExitTransition(mExitTransition);
            }
        }
    }

    private void setFocusAndBack() {
        if (!mFocusAndOutsideEnable) {
            //为false时，获取焦点且不可点击外部关闭
            //from https://github.com/pinguo-zhouwei/CustomPopwindow
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setBackgroundDrawable(null);
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            mPopupWindow.getContentView().setFocusable(true);
            mPopupWindow.getContentView().setFocusableInTouchMode(true);
            mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mPopupWindow.dismiss();

                        return true;
                    }
                    return false;
                }
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= mWidth) || (y < 0) || (y >= mHeight))) {
                        //outside
                        Log.d(TAG, "onTouch outside:mWidth=" + mWidth + ",mHeight=" + mHeight);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        //outside
                        Log.d(TAG, "onTouch outside event:mWidth=" + mWidth + ",mHeight=" + mHeight);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            //为true时，默认为获取焦点且可点击外部取消
            //用户可设置
            mPopupWindow.setFocusable(mFocusable);
            mPopupWindow.setOutsideTouchable(mOutsideTouchable);
            mPopupWindow.setTouchable(mTouchable);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * 是否需要测量 contentView的大小 如果需要重新测量并为宽高赋值
     * 注：此方法获取的宽高可能不准确 MATCH_PARENT时无法获取准确的宽高
     */
    private void measureContentView() {
        final View contentView = getContentView();
        if (mWidth <= 0 || mHeight <= 0) {
            //测量大小
            contentView.measure(0, View.MeasureSpec.UNSPECIFIED);
            if (mWidth <= 0) {
                mWidth = contentView.getMeasuredWidth();
            }
            if (mHeight <= 0) {
                mHeight = contentView.getMeasuredHeight();
            }
            //更新popup
            mPopupWindow.update();
        }
    }

    /**
     * 注册GlobalLayoutListener 获取精准的宽高
     */
    private void registerOnGlobalLayoutListener() {
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mWidth = getContentView().getWidth();
                mHeight = getContentView().getHeight();

                isRealWHAlready = true;
                //isNeedReMeasureWH = false;

                if (mOnRealWHAlreadyListener != null) {
                    mOnRealWHAlreadyListener.onRealWHAlready(BasePopup.this, mWidth, mHeight,
                            mAnchorView == null ? 0 : mAnchorView.getWidth(), mAnchorView == null ? 0 : mAnchorView.getHeight());
                }
                //updateLocation(mWidth, mHeight, mAnchorView, mYGravity, mXGravity, mOffsetX, mOffsetY);
//                Log.d(TAG, "onGlobalLayout finished. isShowing=" + isShowing());
                if (isShowing() && isAtAnchorViewMethod) {
                    updateLocation(mWidth, mHeight, mAnchorView, mYGravity, mXGravity, mOffsetX, mOffsetY);
                }
            }
        });
    }

    /**
     * 更新 PopupWindow 到精准的位置
     *
     * @param width
     * @param height
     * @param anchor
     * @param yGravity
     * @param xGravity
     * @param x
     * @param y
     */
    private void updateLocation(int width, int height, @NonNull View anchor,
                                @YGravity final int yGravity,
                                @XGravity int xGravity, int x, int y) {
        if (mPopupWindow == null) {
            return;
        }
        x = calculateOffsetX(anchor, xGravity, width, x);
        y = calculateOffsetY(anchor, yGravity, height, y);
        mPopupWindow.update(anchor, x, y, width, height);
    }


    /****自定义生命周期方法****/

    /**
     * PopupWindow对象创建完成
     */
    protected void onPopupWindowCreated() {
        initAttributes();
    }

    protected void onPopupWindowViewCreated(View contentView) {
        initViews(contentView, self());
    }

    protected void onPopupWindowDismiss() {
    }

    /**
     * 可以在此方法中设置PopupWindow需要的属性
     */
    protected abstract void initAttributes();

    /**
     * 初始化view {@see getView()}
     *
     * @param view
     */
    protected abstract void initViews(View view, T popup);


    /****设置属性方法****/

    public T setContext(Context context) {
        this.mContext = context;
        return self();
    }

    public T setContentView(View contentView) {
        this.mContentView = contentView;
        this.mLayoutId = 0;
        return self();
    }

    public T setContentView(@LayoutRes int layoutId) {
        this.mContentView = null;
        this.mLayoutId = layoutId;
        return self();
    }

    public T setContentView(Context context, @LayoutRes int layoutId) {
        this.mContext = context;
        this.mContentView = null;
        this.mLayoutId = layoutId;
        return self();
    }

    public T setContentView(View contentView, int width, int height) {
        this.mContentView = contentView;
        this.mLayoutId = 0;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setContentView(@LayoutRes int layoutId, int width, int height) {
        this.mContentView = null;
        this.mLayoutId = layoutId;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setContentView(Context context, @LayoutRes int layoutId, int width, int height) {
        this.mContext = context;
        this.mContentView = null;
        this.mLayoutId = layoutId;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setWidth(int width) {
        this.mWidth = width;
        return self();
    }

    public T setHeight(int height) {
        this.mHeight = height;
        return self();
    }

    public T setAnchorView(View view) {
        this.mAnchorView = view;
        return self();
    }

    public T setYGravity(@YGravity int yGravity) {
        this.mYGravity = yGravity;
        return self();
    }

    public T setXGravity(@XGravity int xGravity) {
        this.mXGravity = xGravity;
        return self();
    }

    public T setOffsetX(int offsetX) {
        this.mOffsetX = offsetX;
        return self();
    }

    public T setOffsetY(int offsetY) {
        this.mOffsetY = offsetY;
        return self();
    }

    public T setAnimationStyle(@StyleRes int animationStyle) {
        this.mAnimationStyle = animationStyle;
        return self();
    }

    public T setFocusable(boolean focusable) {
        this.mFocusable = focusable;
        return self();
    }

    public T setOutsideTouchable(boolean outsideTouchable) {
        this.mOutsideTouchable = outsideTouchable;
        return self();
    }

    public T setTouchable(boolean touchable) {
        this.mTouchable = touchable;
        return self();
    }

    /**
     * 是否可以点击PopupWindow之外的地方dismiss
     *
     * @param focusAndOutsideEnable
     * @return
     */
    public T setFocusAndOutsideEnable(boolean focusAndOutsideEnable) {
        this.mFocusAndOutsideEnable = focusAndOutsideEnable;
        return self();
    }

    /**
     * 背景变暗支持api>=18
     *
     * @param isDim
     * @return
     */
    public T setBackgroundDimEnable(boolean isDim) {
        this.isBackgroundDim = isDim;
        return self();
    }

    public T setDimValue(@FloatRange(from = 0.0f, to = 1.0f) float dimValue) {
        this.mDimValue = dimValue;
        return self();
    }

    public T setDimColor(@ColorInt int color) {
        this.mDimColor = color;
        return self();
    }

    public T setDimView(@NonNull ViewGroup dimView) {
        this.mDimView = dimView;
        return self();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public T setEnterTransition(Transition enterTransition) {
        this.mEnterTransition = enterTransition;
        return self();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public T setExitTransition(Transition exitTransition) {
        this.mExitTransition = exitTransition;
        return self();
    }

    public T setInputMethodMode(int mode) {
        this.mInputMethodMode = mode;
        return self();
    }

    public T setSoftInputMode(int mode) {
        this.mSoftInputMode = mode;
        return self();
    }

    /**
     * 是否需要重新获取宽高
     *
     * @param needReMeasureWH
     * @return
     */
    public T setNeedReMeasureWH(boolean needReMeasureWH) {
        this.isNeedReMeasureWH = needReMeasureWH;
        return self();
    }

    /**
     * 检查是否调用了 create() 方法
     *
     * @param isAtAnchorView 是否是 showAt
     */
    private void checkIsCreate(boolean isAtAnchorView) {
        if (this.isAtAnchorViewMethod != isAtAnchorView) {
            this.isAtAnchorViewMethod = isAtAnchorView;
        }
        if (mPopupWindow == null) {
            create();
        }
    }

    public void showAtLocation(View parent, int gravity) {
        showAtLocation(parent, gravity,0,0);
    }

    public void showAtLocation(View parent, int gravity, int offsetX, int offsetY) {
        //防止忘记调用 apply() 方法
        checkIsCreate(false);

        handleBackgroundDim();
        mAnchorView = parent;
        mOffsetX = offsetX;
        mOffsetY = offsetY;
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        mPopupWindow.showAtLocation(parent, gravity, mOffsetX, mOffsetY);
    }

    /**
     * 相对anchor view显示
     * <p>
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     */
    public void showAtAnchorView() {
        if (mAnchorView == null) {
            return;
        }
        showAtAnchorView(mAnchorView, mYGravity, mXGravity);
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。     *
     *
     * @param anchor
     * @param vertGravity
     * @param horizGravity
     */
    public void showAtAnchorView(@NonNull View anchor, @YGravity int vertGravity, @XGravity int horizGravity) {
        showAtAnchorView(anchor, vertGravity, horizGravity, 0, 0);
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     *
     * @param anchor
     * @param vertGravity  垂直方向的对齐方式
     * @param horizGravity 水平方向的对齐方式
     * @param x            水平方向的偏移
     * @param y            垂直方向的偏移
     */
    public void showAtAnchorView(@NonNull View anchor, @YGravity final int vertGravity, @XGravity int horizGravity, int x, int y) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        //防止忘记调用 apply() 方法
        checkIsCreate(true);

        mAnchorView = anchor;
        mOffsetX = x;
        mOffsetY = y;
        mYGravity = vertGravity;
        mXGravity = horizGravity;
        //处理背景变暗

        handleBackgroundDim();

        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }

        if (Build.VERSION.SDK_INT < 24) {
            x = calculateOffsetX(anchor, horizGravity, mWidth, mOffsetX);
            y = calculateOffsetY(anchor, vertGravity, mHeight, mOffsetY);
            mPopupWindow.showAsDropDown(anchor, x, y, Gravity.NO_GRAVITY);
        } else {
            x = calculateX(anchor, horizGravity, mWidth, mOffsetX);
            y = calculateY(anchor, vertGravity, mHeight, mOffsetY);
            mPopupWindow.showAtLocation(anchor.getRootView(), Gravity.NO_GRAVITY, x, y);
        }


    }

    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor
     * @param vertGravity
     * @param measuredH
     * @param offsetY
     * @return
     */
    private int calculateOffsetY(View anchor, int vertGravity, int measuredH, int offsetY) {
        switch (vertGravity) {
            case YGravity.ABOVE:
                //anchor view之上
                offsetY -= measuredH + anchor.getHeight();
                break;
            case YGravity.ALIGN_BOTTOM:
                //anchor view底部对齐
                offsetY -= measuredH;
                break;
            case YGravity.CENTER:
                //anchor view垂直居中
                offsetY -= anchor.getHeight() / 2 + measuredH / 2;
                break;
            case YGravity.ALIGN_TOP:
                //anchor view顶部对齐
                offsetY -= anchor.getHeight();
                break;
            case YGravity.BELOW:
                //anchor view之下
                // Default position.
                break;
        }

        return offsetY;
    }

    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor
     * @param horizGravity
     * @param measuredW
     * @param offsetX
     * @return
     */
    private int calculateOffsetX(View anchor, int horizGravity, int measuredW, int offsetX) {
        switch (horizGravity) {
            case XGravity.LEFT:
                //anchor view左侧
                offsetX -= measuredW;
                break;
            case XGravity.ALIGN_RIGHT:
                //与anchor view右边对齐
                offsetX -= measuredW - anchor.getWidth();
                break;
            case XGravity.CENTER:
                //anchor view水平居中
                offsetX += anchor.getWidth() / 2 - measuredW / 2;
                break;
            case XGravity.ALIGN_LEFT:
                //与anchor view左边对齐
                // Default position.
                break;
            case XGravity.RIGHT:
                //anchor view右侧
                offsetX += anchor.getWidth();
                break;
        }

        return offsetX;
    }


    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor
     * @param vertGravity
     * @param measuredH
     * @param y
     * @return
     */
    private int calculateY(View anchor, int vertGravity, int measuredH, int y) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int yPoint = location[1];
        switch (vertGravity) {
            case YGravity.ABOVE:
                //anchor view之上
                y += anchor.getTop() - measuredH;
                break;
            case YGravity.ALIGN_BOTTOM:
                //anchor view底部对齐
                y += anchor.getTop() - measuredH + anchor.getHeight();
                break;
            case YGravity.CENTER:
                //anchor view垂直居中
                y += anchor.getTop() - anchor.getY() / 2 + measuredH / 2;
                break;
            case YGravity.ALIGN_TOP:
                //anchor view顶部对齐
                y += anchor.getTop();
                break;
            case YGravity.BELOW:
                //anchor view之下
                // Default position.
                y += anchor.getTop() + anchor.getHeight();
                break;
        }

        return y;
    }

    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor
     * @param horizGravity
     * @param measuredW
     * @param x
     * @return
     */
    private int calculateX(View anchor, int horizGravity, int measuredW, int x) {
        switch (horizGravity) {
            case XGravity.LEFT:
                //anchor view左侧
                x += anchor.getLeft() - measuredW;
                break;
            case XGravity.ALIGN_RIGHT:
                //与anchor view右边对齐
                x += anchor.getLeft() - measuredW + anchor.getWidth();
                break;
            case XGravity.CENTER:
                //anchor view水平居中
                x += anchor.getLeft() - (measuredW - anchor.getWidth()) / 2;
                break;
            case XGravity.ALIGN_LEFT:
                //与anchor view左边对齐
                x += anchor.getLeft();
                break;
            case XGravity.RIGHT:
                //anchor view右侧
                x += anchor.getLeft() + anchor.getWidth();
                break;
        }

        return x;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public T setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mOnDismissListener = listener;
        return self();
    }

    public T setOnRealWHAlreadyListener(OnRealWHAlreadyListener listener) {
        this.mOnRealWHAlreadyListener = listener;
        return self();
    }

    /**
     * 处理背景变暗
     * https://blog.nex3z.com/2016/12/04/%E5%BC%B9%E5%87%BApopupwindow%E5%90%8E%E8%AE%A9%E8%83%8C%E6%99%AF%E5%8F%98%E6%9A%97%E7%9A%84%E6%96%B9%E6%B3%95/
     */
    private void handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isBackgroundDim) {
                return;
            }
            if (mDimView != null) {
                applyDim(mDimView);
            } else {
                if (getContentView() != null && getContentView().getContext() != null &&
                        getContentView().getContext() instanceof Activity) {
                    Activity activity = (Activity) getContentView().getContext();
                    applyDim(activity);
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void applyDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        //activity跟布局
//        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        Drawable dimDrawable = new ColorDrawable(mDimColor);
        dimDrawable.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dimDrawable.setAlpha((int) (255 * mDimValue));
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dimDrawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void applyDim(ViewGroup dimView) {
        Drawable dimDrawable = new ColorDrawable(mDimColor);
        dimDrawable.setBounds(0, 0, dimView.getWidth(), dimView.getHeight());
        dimDrawable.setAlpha((int) (255 * mDimValue));
        ViewGroupOverlay overlay = dimView.getOverlay();
        overlay.add(dimDrawable);
    }

    /**
     * 清除背景变暗
     */
    private void clearBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (isBackgroundDim) {
                if (mDimView != null) {
                    clearDim(mDimView);
                } else {
                    if (getContentView() != null) {
                        Activity activity = (Activity) getContentView().getContext();
                        if (activity != null) {
                            clearDim(activity);
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clearDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        //activity跟布局
//        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clearDim(ViewGroup dimView) {
        ViewGroupOverlay overlay = dimView.getOverlay();
        overlay.clear();
    }

    /**
     * 获取PopupWindow中加载的view
     *
     * @return
     */
    public View getContentView() {
        if (mPopupWindow != null) {
            return mPopupWindow.getContentView();
        } else {
            return null;
        }
    }

    /**
     * 获取PopupWindow对象
     *
     * @return
     */
    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    /**
     * 获取PopupWindow 宽
     *
     * @return
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * 获取PopupWindow 高
     *
     * @return
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * 获取纵向Gravity
     *
     * @return
     */
    public int getXGravity() {
        return mXGravity;
    }

    /**
     * 获取横向Gravity
     *
     * @return
     */
    public int getYGravity() {
        return mYGravity;
    }

    /**
     * 获取x轴方向的偏移
     *
     * @return
     */
    public int getOffsetX() {
        return mOffsetX;
    }

    /**
     * 获取y轴方向的偏移
     *
     * @return
     */
    public int getOffsetY() {
        return mOffsetY;
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * 是否精准的宽高获取完成
     *
     * @return
     */
    public boolean isRealWHAlready() {
        return isRealWHAlready;
    }

    /**
     * 获取view
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(@IdRes int viewId) {
        View view = null;
        if (getContentView() != null) {
            view = getContentView().findViewById(viewId);
        }
        return (T) view;
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        handleDismiss();
    }

    /**
     * PopupWindow消失后处理一些逻辑
     */
    private void handleDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }

        //清除背景变暗
        clearBackgroundDim();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        onPopupWindowDismiss();
    }

    /**
     * PopupWindow是否显示在window中
     * 用于获取准确的PopupWindow宽高，可以重新设置偏移量
     */
    public interface OnRealWHAlreadyListener {

        /**
         * 在 show方法之后 updateLocation之前执行
         *
         * @param popWidth  PopupWindow准确的宽
         * @param popHeight PopupWindow准确的高
         * @param anchorW   锚点View宽
         * @param anchorH   锚点View高
         */
        void onRealWHAlready(BasePopup basePopup, int popWidth, int popHeight, int anchorW, int anchorH);
    }


}
