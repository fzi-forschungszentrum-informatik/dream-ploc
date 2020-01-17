package de.fzi.dream.ploc.ui.callback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.ExpertPreviewFragment;
import de.fzi.dream.ploc.ui.holder.ExpertsPreviewViewHolder;
import de.fzi.dream.ploc.ui.listener.OnExpertPreviewCardViewListener;

/**
 * The ExpertPreviewSwipeCallback provides the callback functionality to response to the users
 * swipes on {@link CardView}s within the {@link ExpertPreviewFragment} and its {@link RecyclerView}.
 * It is also responsibly for the layout and style of the background displayed when a card is swiped.
 *
 * @author Felix Melcher
 */
public class ExpertPreviewSwipeCallback extends ItemTouchHelper.Callback {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertPreviewSwipeCallback.class.getSimpleName();

    private OnExpertPreviewCardViewListener mListener;

    /**
     * Constructor
     *
     * @param l The listener to callback the swipe interactions.
     */
    public ExpertPreviewSwipeCallback(OnExpertPreviewCardViewListener l) {
        mListener = l;
    }

    /**
     * Helper method to generateHash a bitmap from an drawable resource id.
     *
     * @param context    The embedded context to get access to the resource folder.
     * @param drawableId The identifier of the drawable to be converted.
     */
    private static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    /**
     * This method defines the possible directions in which the ViewHolder can be swiped.
     *
     * @param recyclerView The RecyclerView which items can be swiped.
     * @param viewHolder   The ViewHolder which is swiped.
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    /**
     * This method is called after the swipe occurred.
     *
     * @param direction  The RecyclerView which items can be swiped.
     * @param viewHolder The swipe direction.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onCardRightSwipe(((ExpertsPreviewViewHolder) viewHolder).getExpertPreview());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint p = new Paint();
        Bitmap icon;
        RectF iconFrame;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            if (dX > 0) {
                p.setColor(Color.parseColor("#28A745"));
                iconFrame = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                icon = getBitmapFromDrawable(recyclerView.getContext(), R.drawable.ic_star_white_24dp);
            } else {
                iconFrame = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                icon = getBitmapFromDrawable(recyclerView.getContext(), R.drawable.ic_thumb_down_darkgrey_24dp);
            }
            c.drawRoundRect(background, 15, 15, p);
            c.drawBitmap(icon, null, iconFrame, p);

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
}
