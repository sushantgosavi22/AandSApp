package com.aandssoftware.aandsinventory.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmObject;

public abstract class AbstractRealmAdapter<T extends RealmObject, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  
}
