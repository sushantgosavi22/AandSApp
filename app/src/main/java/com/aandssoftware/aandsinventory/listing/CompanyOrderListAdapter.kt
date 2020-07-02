package com.aandssoftware.aandsinventory.listing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.aandssoftware.aandsinventory.ui.activity.CompanyOrderListActivity
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.AD_COUNT_LIMIT
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.order_item.view.*
import java.io.Serializable

class CompanyOrderListAdapter(private val activity: ListingActivity) : ListingOperations {

    var positionClicked = 0
    var orderId: String? = EMPTY_STRING
    var isReady: Boolean = false
    private lateinit var mInterstitialAd: InterstitialAd

    init {
        initAndLoadInterstitialAd()
    }
    inner class OrderViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var tvOrderDate: AppCompatTextView = itemView.tvOrderDate
        var tvOrderStatus: AppCompatTextView = itemView.tvOrderStatus
        var tvFinalAmount: AppCompatTextView = itemView.tvFinalAmount
        var tvItemCount: AppCompatTextView = itemView.tvItemCount
        var tvInvoiceNumber: AppCompatTextView = itemView.tvInvoiceNumber
        var tvCustomerName: AppCompatTextView = itemView.tvCustomerName
        var tvContactNameAndNumber: AppCompatTextView = itemView.tvContactNameAndNumber
        var cardView: CardView = itemView.cardView
        var imgDelete: ImageView = itemView.imgDelete

        init {
            imgDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteOrder(itemView.tag as OrderModel, activity, pos)
            }
            cardView.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                showOrderInventoryActivity(activity, (itemView.tag as OrderModel).id, pos)
            }
        }
    }

    private fun showOrderInventoryActivity(activity: Activity, orderIdLocal: String?, pos: Int) {
        positionClicked = pos
        orderId = orderIdLocal
        var count = SharedPrefsUtils.getIntegerPreference(activity, SharedPrefsUtils.AD_INTERSTITIAL_ORDER_LIST_COUNT, 0)
        if (count <= AD_COUNT_LIMIT) {
            SharedPrefsUtils.setIntegerPreference(activity, SharedPrefsUtils.AD_INTERSTITIAL_ORDER_LIST_COUNT, (count + 1))
            navigateToOrderDetailsScreeen()
        } else {
            if (isReady && mInterstitialAd.isLoaded) {
                SharedPrefsUtils.setIntegerPreference(activity, SharedPrefsUtils.AD_INTERSTITIAL_ORDER_LIST_COUNT, 0)
                mInterstitialAd.show()
            } else {
                navigateToOrderDetailsScreeen()
            }
        }
    }

    private fun navigateToOrderDetailsScreeen() {
        var intent = Intent().putExtra(AppConstants.POSITION_IN_LIST, positionClicked)
        Navigator.openOrderDetailsScreen(activity, orderId!!, intent)
    }

    private fun initAndLoadInterstitialAd() {
        mInterstitialAd = InterstitialAd(activity)
        MobileAds.initialize(activity, activity.getString(R.string.app_id_for_adds))
        mInterstitialAd.adUnitId = activity.getString(R.string.interstitialAd_comp_order_list01)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            // If user clicks on the ad and then presses the back, s/he is directed to DetailActivity.
            override fun onAdClicked() {
                super.onAdOpened()
                mInterstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailActivity.
            override fun onAdClosed() {
                navigateToOrderDetailsScreeen()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                isReady = true
            }
        }
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_listing_with_fab_button
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return OrderViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.order_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as OrderViewHolder
        val mItem = item as OrderModel
        var date = EMPTY_STRING.plus(DateUtils.getDateFormatted(mItem.orderDateCreated))
        holder.tvCustomerName.text = Utils.isEmpty(mItem.invoiceNumber,date)
        holder.tvContactNameAndNumber.text = mItem.customerModel?.contactPerson + " " + mItem.customerModel?.contactPersonNumber
        holder.tvFinalAmount.text = Utils.currencyLocale(Utils.getOrderFinalPrice(mItem))     //Utils.round(Utils.getOrderFinalPrice(mItem),2).toString()
        holder.tvItemCount.text = mItem.orderItems.size.toString()
        holder.tvInvoiceNumber.text = date
        holder.tvOrderDate.text = Utils.getItemNames(mItem)
        holder.tvOrderStatus.text = Utils.isEmpty(mItem.orderStatusName)
        holder.tvOrderStatus.setBackgroundDrawable(
                Utils.getStatusBackgroud(baseHolder.itemView.context, Utils.isEmpty(mItem.orderStatus)))
        if (Utils.isEmpty(mItem.orderStatus).equals(OrderStatus.CREATED.toString(), ignoreCase = true)
                || Utils.isEmpty(mItem.orderStatus).equals(OrderStatus.FINISH.toString(), ignoreCase = true)) {
            holder.imgDelete.visibility = View.VISIBLE
        } else {
            holder.imgDelete.visibility = View.GONE
        }
    }

    override fun getTitle(): String {
        val user = SharedPrefsUtils.getUserPreference(activity, SharedPrefsUtils.CURRENT_USER)
        var name: String = user?.customerName ?: EMPTY_STRING
        return name
    }

    override fun getResult() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getCompanyOrders(Utils.getLoginCustomerId(activity), object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = FirebaseUtil.getInstance()
                        .getListData(dataSnapshot, OrderModel::class.java)
                var finalOrderList : List<OrderModel>
                if (list.isNotEmpty()) {
                    finalOrderList = list.sortedWith(compareBy(OrderModel::orderDateUpdated)).reversed()
                    activity.loadData(ArrayList(finalOrderList))
                }else{
                    finalOrderList  = ArrayList<OrderModel>()
                    activity.loadData(ArrayList(finalOrderList))
                    activity.validateRecyclerView()
                }

                activity.dismissProgressBar()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.dismissProgressBar()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.inventory_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }
            R.id.actionAdd -> {
                addOrderSelectCompany()
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        activity.finish()
    }

    private fun addOrderSelectCompany() {
        (activity as CompanyOrderListActivity).showInventoryListingActivity(Utils.getLoginCustomerId(activity), EMPTY_STRING)
    }

    fun deleteOrder(orderModel: OrderModel, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_order_item_title))
                .setMessage(context.getString(R.string.remove_order_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { dialogBox, id ->
                    FirebaseUtil.getInstance().getCustomerDao()
                            .removeOrder(orderModel, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                                //getCompanyOrder is addValueEventListener so it reload and remove automatically

                            })
                }
                .setNegativeButton(
                        context.getString(R.string.no)
                ) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AppConstants.LISTING_REQUEST_CODE // open order List activity ,
            -> {
                updateOrder(data,resultCode)

            }
        }


    }

    private fun updateOrder(data: Intent?,resultCode: Int) {
        data?.let {
            var isOrderUpdated = data.getBooleanExtra(AppConstants.UPDATED, false)
            if (isOrderUpdated) {
                var position = data.getIntExtra(AppConstants.POSITION_IN_LIST, AppConstants.INVALID_ID)
                if (position != AppConstants.INVALID_ID) {
                    var orderID = data.getStringExtra(AppConstants.ORDER_ID)
                    orderID?.let {
                        activity.showProgressBar()
                        FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderID, object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                activity.dismissProgressBar()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                activity.dismissProgressBar()
                                var model = p0.getValue(OrderModel::class.java)
                                model?.let {
                                    activity.updateElement(model, position)
                                    if(resultCode ==ORDER_RELOAD_LIST_RESULT_CODE){
                                        positionClicked =  position?:0
                                        orderId = orderID
                                        orderID?.let {
                                            navigateToOrderDetailsScreeen()
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}
