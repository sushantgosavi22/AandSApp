package com.aandssoftware.aandsinventory.ui.adapters

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.activity.BaseActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.multi_image_item.view.*
import java.io.Serializable


class MultiImageSelectionAdapter(val activity: BaseActivity, val listner: View.OnClickListener, val deleteListner: View.OnClickListener) : RecyclerView.Adapter<MultiImageSelectionAdapter.ViewHolder>() {
    private var data: MutableList<Uri> = ArrayList<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.multi_image_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if ((data.size - 1) == 0) {
            holder.itemView.isFocusableInTouchMode = true
            holder.itemView.requestFocus()
        }
        holder.ivGallery
        //holder.itemView.tag = item
        holder.imgDelete.tag = item
        Glide.with(activity)
                .load(item)
                .placeholder(R.drawable.ic_image_add)
                .into(holder.ivGallery)
        holder.itemView.setOnClickListener(listner)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivGallery: AppCompatImageView = itemView.ivGallery
        internal var imgDelete: AppCompatImageView = itemView.imgDelete

        init {
            itemView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    // run scale animation and make it bigger
                    val anim = AnimationUtils.loadAnimation(activity, R.anim.scale_in)
                    itemView.startAnimation(anim)
                    anim.setFillAfter(true)
                    imgDelete.visibility = View.GONE
                } else {
                    // run scale animation and make it smaller
                    val anim = AnimationUtils.loadAnimation(activity, R.anim.scale_out)
                    itemView.startAnimation(anim)
                    anim.setFillAfter(true)
                    imgDelete.visibility = View.VISIBLE
                }
            }

            imgDelete.setOnClickListener(deleteListner)
        }
    }

    fun loadData(collection: MutableList<Uri>) {
        if (collection.isEmpty()) {
            data = ArrayList<Uri>()
        } else {
            data = collection
        }
        notifyDataSetChanged()
    }


    fun removeAt(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    fun addElement(item: Uri, pos: Int) {
        data.add(pos, item)
        notifyItemInserted(pos)
    }

    fun updateElement(item: Uri, pos: Int) {
        data.set(pos, item)
        notifyItemChanged(pos)
    }
}
