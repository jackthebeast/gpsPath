package jacopo.com.gpspath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jacopo.com.gpspath.R;


/**
 * Created by jacop on 29/10/2017.
 */

public class ListDivider extends RecyclerView.ItemDecoration {

private Drawable divider;

public ListDivider(Context context) {
        divider = context.getResources().getDrawable(R.drawable.list_divider);
        }

@Override
public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
        View child = parent.getChildAt(i);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + divider.getIntrinsicHeight();

        divider.setBounds(left, top, right, bottom);
        divider.draw(c);
        }
        }
        }