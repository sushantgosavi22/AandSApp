package veeresh.a3c.realm.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import org.greenrobot.eventbus.EventBus;
import veeresh.a3c.realm.R;
import veeresh.a3c.realm.database.RealmManager;
import veeresh.a3c.realm.events.VehicalEvents;
import veeresh.a3c.realm.models.InventoryItem;

/**
 * Created by Veeresh on 3/11/17.
 */
public class VehicalAdapter extends RealmRecyclerViewAdapter<InventoryItem, VehicalAdapter.ViewHolder> {

    //private Context context;
    public static int ACTION_ITEM_CLICKED =0;
    public static int ACTION_ITEM_DELETED =1;
    public static int ACTION_ITEM_EDITED =2;

    public VehicalAdapter(OrderedRealmCollection<InventoryItem> orderedRealmCollection) {
        super(orderedRealmCollection, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_records, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.batsmanName.setText(getData().get(position).getName());
        holder.totalRuns.setText(" " + getData().get(position).getVehicalNumber());
        holder.totalMatches.setText("" + getData().get(position).getDescription());


        if (getData().get(position).isAvailable())
            holder.favCheck.setChecked(true);
        else
            holder.favCheck.setChecked(false);
    
        if(getData().get(position).getImagePath()!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(getData().get(position).getImagePath());
            if(null!=holder.profilePicture && null!=bitmap){
                holder.profilePicture.setImageBitmap(bitmap);
            }
        }
        
        
        
        holder.favCheck.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            if (checkBox.isChecked()) {
                RealmManager.recordsDao().saveToAvailable(getData().get(position));
            } else {
                RealmManager.recordsDao().removeFromAvailable(getData().get(position));
            }

        });
    }

    public void deleteVehical(InventoryItem inventoryItem, Context context){
        AlertDialog.Builder alertDialogBuilderUserInput =
            new AlertDialog.Builder(context);
        alertDialogBuilderUserInput
            .setTitle("Remove Vehicals")
            .setMessage("Do you really want to delete inventoryItem ?")
            .setCancelable(false)
            .setPositiveButton( "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        RealmManager.recordsDao().removeVehical(inventoryItem);
                        updateData(RealmManager.recordsDao().loadRecords());
                    }
                })
            .setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
    
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        
        @BindView(R.id.imgEditVehical)
        ImageView imgEditVehical;
        @BindView(R.id.imgDeleteVehical)
        ImageView imgDeleteVehical;
        @BindView(R.id.imgVehicalPhoto)
        ImageView profilePicture;
        @BindView(R.id.name)
        TextView batsmanName;
        @BindView(R.id.check_fav)
        CheckBox favCheck;
        @BindView(R.id.currentPlace)
        TextView totalRuns;
        @BindView(R.id.dieselRemain)
        TextView totalMatches;
        @BindView(R.id.card_view)
        CardView cardView;


        @OnClick(R.id.card_view)
        public void navigate() {
            EventBus.getDefault().post(new VehicalEvents.RecordEvent(getData().get(getAdapterPosition()), ACTION_ITEM_CLICKED));
        }
    
        @OnClick(R.id.imgDeleteVehical)
        public void onDeleteClick(){
            deleteVehical(getData().get(getAdapterPosition()), itemView.getContext());
        }
        @OnClick(R.id.imgEditVehical)
        public void onEditClick(){
            EventBus.getDefault().post(new VehicalEvents.RecordEvent(getData().get(getAdapterPosition()), ACTION_ITEM_EDITED));
        }
       
        
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    
 /*   public void updateData(RealmList<Task> tasksUpdated) {
        tasks = new RealmList<>();
        tasks.addAll(tasksUpdated);
        notifyDataSetChanged();
    }*/
}
