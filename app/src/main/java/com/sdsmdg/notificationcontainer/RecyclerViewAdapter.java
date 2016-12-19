package com.sdsmdg.notificationcontainer;

/**
 * Created by Chirag on 19-12-2016.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdsmdg.notificationcontainer.models.NotificationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * RecyclerView adapter enabling undo on a swiped away item.
 */
class RecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private Activity activity;
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    //    private List<NotificationModel> items;
    private List<NotificationModel> itemsPendingRemoval;
    //    private int lastInsertedIndex; // so we can add some more items for testing purposes
    private boolean undoOn; // is undo on

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private HashMap<NotificationModel, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    private static Realm realm = Realm.getDefaultInstance();
    private RealmResults<NotificationModel> items;

    RecyclerViewAdapter(Activity activity) {
        this.activity = activity;
        itemsPendingRemoval = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        items = realm.where(NotificationModel.class).equalTo("important", true).findAll().sort("time", Sort.DESCENDING);
        RealmChangeListener<RealmResults<NotificationModel>> changeListener = new RealmChangeListener<RealmResults<NotificationModel>>() {
            @Override
            public void onChange(RealmResults<NotificationModel> results) {
                notifyDataSetChanged();
            }
        };
        items.addChangeListener(changeListener);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        final NotificationModel item = items.get(position);

        if (itemsPendingRemoval.contains(item)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(Color.RED);
            viewHolder.container.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                }
            });
        } else {
            // we need to show the "normal" state
            try {
                Drawable appIcon = activity.getPackageManager().getApplicationIcon(item.getPackageName());
                viewHolder.icon.setImageDrawable(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.container.setVisibility(View.VISIBLE);
            viewHolder.titleTextView.setText(item.getTitle());
            if(item.getDescription()!=null) {
                viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                viewHolder.descriptionTv.setText(item.getDescription());
            } else {
                viewHolder.descriptionTv.setVisibility(View.GONE);
            }
            Log.i("time:",item.getTime().toString());
            viewHolder.timeTv.setText(DateFormatter.getTimeAgo(item.getTime()));
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    boolean isUndoOn() {
        return undoOn;
    }

    void pendingRemoval(int position) {
        final NotificationModel item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    void remove(final int position) {
        final NotificationModel item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
//            items.remove(position);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    item.setImportant(false);
//                    notifyItemRemoved(position);
                }
            });

        }

    }

    boolean isPendingRemoval(int position) {
        NotificationModel item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }
}

/**
 * ViewHolder capable of presenting two states: "normal" and "undo" state.
 */
class CustomViewHolder extends RecyclerView.ViewHolder {

    LinearLayout container;
    TextView titleTextView, descriptionTv, timeTv;
    ImageView icon;
    Button undoButton;

    CustomViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        container = (LinearLayout) itemView.findViewById(R.id.container);
        titleTextView = (TextView) itemView.findViewById(R.id.title);
        descriptionTv = (TextView) itemView.findViewById(R.id.description);
        timeTv = (TextView) itemView.findViewById(R.id.time);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);
    }

}
