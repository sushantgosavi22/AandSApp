package com.aandssoftware.aandsinventory.ui.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.CarouselMenuModel
import com.aandssoftware.aandsinventory.models.CarouselMenuType
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.ui.activity.BaseActivity
import com.aandssoftware.aandsinventory.ui.activity.CompanyOrderListActivity
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderListActivity
import com.aandssoftware.aandsinventory.ui.adapters.CarouselMenuAdapter.ViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_carousel_menu.view.*


class CarouselMenuAdapter(val activity: BaseActivity, orderedRealmCollection: List<CarouselMenuModel>) : RecyclerView.Adapter<ViewHolder>() {

    private var data: List<CarouselMenuModel> = orderedRealmCollection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_carousel_menu, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mCarouselMenuModel = data[position]
        holder.carouselItemName.text = data[position].aliceName
        holder.itemView.tag = mCarouselMenuModel

        mCarouselMenuModel.imageId?.let {
            if (!it.contains(AppConstants.HTTP)) {
                holder.carouselItemImage
                        .setImageResource(mCarouselMenuModel.defaultImageId)
            } else {
                Glide.with(activity)
                        .load(mCarouselMenuModel.imageId)
                        .placeholder(mCarouselMenuModel.defaultImageId)
                        .into(holder.carouselItemImage)
            }
        }

        holder.itemView.setOnClickListener { v ->
            val carouselId = data[position].carouselId
            when (CarouselMenuType.valueOf(carouselId!!)) {
                CarouselMenuType.ORDERS -> showOrderActivity(activity, ListType.LIST_TYPE_ORDER)
                CarouselMenuType.CUSTOMERS -> showListing(activity, ListType.LIST_TYPE_CUSTOMERS)
                CarouselMenuType.INVENTORY -> showListing(activity, ListType.LIST_TYPE_INVENTORY)
                CarouselMenuType.INVENTORY_HISTORY -> showListing(activity, ListType.LIST_TYPE_INVENTORY_HISTORY)
                CarouselMenuType.MATERIALS -> showListing(activity, ListType.LIST_TYPE_MATERIAL)
                CarouselMenuType.COMPANY_ORDER -> showCompanyOrderActivity(activity, ListType.LIST_TYPE_COMPANY_ORDER)
                CarouselMenuType.COMPANY_MATERIALS -> showListing(activity, ListType.LIST_TYPE_MATERIAL)
                CarouselMenuType.COMPANY_PROFILE -> showCompanyProfile(activity)
                CarouselMenuType.ABOUT_US -> showCompanyProfile(activity, mCarouselMenuModel)
                else -> showListing(activity, ListType.LIST_TYPE_MATERIAL)
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var carouselItemName: AppCompatTextView = itemView.carouselItemName
        internal var carouselItemImage: AppCompatImageView = itemView.carouselItemImage
    }

    private fun showListing(activity: Activity, type: ListType) {
        val intent = Intent(activity, ListingActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, type.ordinal)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }

    private fun showCompanyProfile(activity: AppCompatActivity) {
        val user = SharedPrefsUtils.getUserPreference(activity, SharedPrefsUtils.CURRENT_USER)
        user?.let {
            user.id?.let { id ->
                val custId = user.customerID ?: AppConstants.ZERO_STRING
                Navigator.openCustomerScreen(activity, id, custId, ViewMode.VIEW_ONLY.ordinal, activity.getString(R.string.profile))
            }
        }
    }

    private fun showCompanyProfile(activity: BaseActivity, mCarouselMenuModel: CarouselMenuModel) {
        mCarouselMenuModel.let {
            val items = mCarouselMenuModel.tag?.split("#")
            items?.let {
                if (items.size >= 2) {
                    activity.showProgressBar()
                    FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(items.get(2),
                            object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                                    model?.let {
                                        model.id?.let { id ->
                                            var customerID = model.customerID
                                                    ?: AppConstants.ZERO_STRING
                                            Navigator.openCustomerScreen(activity, id, customerID, ViewMode.VIEW_ONLY.ordinal, activity.getString(R.string.about_us))
                                        }
                                    }
                                    activity.dismissProgressBar()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    activity.dismissProgressBar()
                                }
                            })
                }
            }
        }
    }


    private fun showOrderActivity(activity: Activity, type: ListType) {
        val intent = Intent(activity, OrderListActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, type.ordinal)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }

    private fun showCompanyOrderActivity(activity: Activity, type: ListType) {
        val intent = Intent(activity, CompanyOrderListActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, type.ordinal)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }
}
