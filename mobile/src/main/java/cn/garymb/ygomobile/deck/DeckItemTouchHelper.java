package cn.garymb.ygomobile.deck;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelperCompat;
import android.util.Log;

import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

public class DeckItemTouchHelper extends ItemTouchHelperCompat.Callback {
    private DeckDrager mDeckDrager;
    private static final String TAG = "drag";
    private static final boolean DEBUG = false;
    private DeckAdapater deckAdapater;

    public DeckItemTouchHelper(DeckAdapater deckAdapater) {
        this.mDeckDrager = new DeckDrager(deckAdapater);
        this.deckAdapater = deckAdapater;
    }

    private int Min_Pos = -10;

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        int id = target.getAdapterPosition();
        if (isLongPressMode()) {
            return id == DeckItem.HeadView;
        }
        return id != DeckItem.HeadView;
    }

    /**
     * 控制哪些可以拖拽
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int id = viewHolder.getAdapterPosition();
        if (DeckItemUtils.isLabel(id) || id == DeckItem.HeadView) {
            return makeMovementFlags(0, 0);
        }
        if (DeckItemUtils.isMain(id)) {
            if (id >= DeckItem.MainStart + deckAdapater.getMainCount()) {
                return makeMovementFlags(0, 0);
            }
        } else if (DeckItemUtils.isExtra(id)) {
            if (id >= DeckItem.ExtraStart + deckAdapater.getExtraCount()) {
                return makeMovementFlags(0, 0);
            }
        } else if (DeckItemUtils.isSide(id)) {
            if (id >= DeckItem.SideStart + deckAdapater.getSideCount()) {
                return makeMovementFlags(0, 0);
            }
        }
        int dragFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected,
                                                    List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        RecyclerView.ViewHolder viewHolder = super.chooseDropTarget(selected, dropTargets, curX, curY);
        if (viewHolder != null) {
            int id = viewHolder.getAdapterPosition();
            if (viewHolder instanceof DeckViewHolder) {
                DeckViewHolder deckholder = (DeckViewHolder) viewHolder;
                if (deckholder.getItemType() == DeckItemType.HeadView) {
                    if (isLongPressMode()) {
                        return viewHolder;
                    }
                    return null;
                }
                if (deckholder.getItemType() == DeckItemType.MainLabel
                        || deckholder.getItemType() == DeckItemType.SideLabel
                        || deckholder.getItemType() == DeckItemType.ExtraLabel) {
//                Log.d("kk", "move is label or space " + id);
                    return null;
                }
            } else {
                if (DeckItemUtils.isLabel(id) || id == DeckItem.HeadView) {
//                Log.d("kk", "move is label " + id);
                    if (isLongPressMode()) {
                        return viewHolder;
                    }
                    return null;
                }
            }
        }
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            mDeckDrager.onDragStart();
        } else if (actionState == ACTION_STATE_IDLE) {
            mDeckDrager.onDragEnd();
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getAdapterPosition() < 0) {
            return false;
        }
        return mDeckDrager.move((DeckViewHolder) viewHolder, (DeckViewHolder) target);
    }

    public void remove(int id) {
        mDeckDrager.delete(id);
    }

    @Override
    public boolean canAnimation(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder != null && viewHolder.getAdapterPosition() < 0) {
            return false;
        }
        return super.canAnimation(recyclerView, viewHolder);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if (toPos == DeckItem.HeadView) {
            if (isLongPressMode()) {
                //防止删除后，弹回来
                if (y > Min_Pos) {
                    endLongPressMode();
                    if (DEBUG)
                        Log.i(TAG, "delete " + fromPos + " x=" + x + ",y=" + y);
                    mDeckDrager.delete(fromPos);
                } else {
                    if (DEBUG)
                        Log.d(TAG, "cancel delete " + fromPos + " x=" + x + ",y=" + y);
                }
            }
        }
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
