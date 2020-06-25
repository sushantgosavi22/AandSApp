package com.aandssoftware.aandsinventory.listing;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aandssoftware.aandsinventory.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can createAdditionalRows this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     InventoryItemListDialogFragment.newInstance(30).createAdditionalRows(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link InventoryItemListDialogFragment.Listener}.</p>
 */
public class InventoryItemListDialogFragment extends BottomSheetDialogFragment {
  
  // TODO: Customize parameter argument names
  private static final String ARG_ITEM_COUNT = "item_count";
  private Listener mListener;
  
  // TODO: Customize parameters
  public static InventoryItemListDialogFragment newInstance(int itemCount) {
    final InventoryItemListDialogFragment fragment = new InventoryItemListDialogFragment();
    final Bundle args = new Bundle();
    args.putInt(ARG_ITEM_COUNT, itemCount);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_inventoryitem_list_dialog, container, false);
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    final RecyclerView recyclerView = (RecyclerView) view;
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new InventoryItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
  }
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
  
  @Override
  public void onDetach() {
    mListener = null;
    super.onDetach();
  }
  
  public interface Listener {
    
    void onInventoryItemClicked(int position);
  }
  
  private class ViewHolder extends RecyclerView.ViewHolder {
    
    final TextView text;
    
    ViewHolder(LayoutInflater inflater, ViewGroup parent) {
      // TODO: Customize the inventory layout
      super(inflater.inflate(R.layout.fragment_inventoryitem_list_dialog_item, parent, false));
      text = (TextView) itemView.findViewById(R.id.text);
      text.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.onInventoryItemClicked(getAdapterPosition());
            dismiss();
          }
        }
      });
    }
    
  }
  
  private class InventoryItemAdapter extends RecyclerView.Adapter<ViewHolder> {
    
    private final int mItemCount;
    
    InventoryItemAdapter(int itemCount) {
      mItemCount = itemCount;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.text.setText(String.valueOf(position));
    }
    
    @Override
    public int getItemCount() {
      return mItemCount;
    }
    
  }
  
  public Listener getListener() {
    return mListener;
  }
  
  public InventoryItemListDialogFragment setListener(
      Listener mListener) {
    this.mListener = mListener;
    return this;
  }
  
}
