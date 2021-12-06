package com.luwei.recyclerview.decoration;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class LinearSpaceDecoration extends RecyclerView.ItemDecoration {
    private int mSize;
    private boolean isTop;
    private Rect mOutRect;

    public LinearSpaceDecoration(@Px int size) {
        this(size, false);
    }

    public LinearSpaceDecoration(@Px int size, boolean isTop) {
        mSize = size;
        this.isTop = isTop;
    }

    public LinearSpaceDecoration(@NonNull Rect outRect) {
        mOutRect = outRect;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOutRect == null) {
            if (isTop && parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSize;
            }
            outRect.bottom = mSize;
        } else {
            outRect.top = mOutRect.top;
            outRect.bottom = mOutRect.bottom;
            outRect.left = mOutRect.left;
            outRect.right = mOutRect.right;
        }
    }
}
