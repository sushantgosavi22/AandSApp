package com.aandssoftware.aandsinventory.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.update_order_dialog.view.*


class UpdateOrderDialogFragment(var orderModel: OrderModel?, var onOrderUpdateListener: OnOrderUpdateListener) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.update_order_dialog, null)
        setView(view)
        bottomSheet.setContentView(view)
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        //bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }



    interface OnOrderUpdateListener {
        fun onOrderUpdate(orderModel: OrderModel)
    }

    private fun setView(view: View) {
        orderModel?.let {order->

            if (Utils.isEmpty(order.orderStatus).equals(OrderStatus.CREATED.toString(), ignoreCase = true)){
                view.edtInvoiceNumber.visibility = View.GONE
                view.edtDateOfInvoice.visibility = View.GONE
                view.edtDueDate.visibility = View.GONE
            }else{
                view.edtInvoiceNumber.setText(order.invoiceNumber)
                view.edtDateOfInvoice.setText(DateUtils.getFormatedDatePickerFormat(order.invoiceDate))
                view.edtDueDate.setText(DateUtils.getFormatedDatePickerFormat(order.dueDate))
            }

            view.edtDiscount.setText(order.discount.toString())
            view.edtCess.setText(order.cessAmount.toString())
            view.edtPaymentTerms.setText(order.paymentTerm.toString())
            view.edtTotalCreditApplied.setText(order.totalCreditApplied.toString())
            view.edtTotalDebitApplied.setText(order.totalDebitApplied.toString())
            view.edtPaymentReceive.setText(order.paymentReceived.toString())
            view.edtBalanceDue.setText(order.balanceDue.toString())
            view.edtBankName.setText(order.bankName)
            view.edtAccountNumber.setText(order.accountNumber)
            view.edtIfscCode.setText(order.ifscCode)
            view.btnOrderUpdate.setOnClickListener {
                if (Utils.isEmpty(order.orderStatus).equals(OrderStatus.CREATED.toString(), ignoreCase = true)){
                    orderModel?.invoiceDate = DateUtils.getLongFromDatePickerFormat(view.edtDateOfInvoice.getText())
                    orderModel?.dueDate = DateUtils.getLongFromDatePickerFormat(view.edtDueDate.getText())
                    orderModel?.invoiceNumber = view.edtInvoiceNumber.getText()
                }
                orderModel?.discount =  view.edtDiscount.getText().toDouble()
                orderModel?.cessAmount = view.edtCess.getText().toInt()
                orderModel?.paymentTerm = view.edtPaymentTerms.getText().toInt()
                orderModel?.totalCreditApplied = view.edtTotalCreditApplied.getText().toInt()
                orderModel?.totalDebitApplied = view.edtTotalDebitApplied.getText().toInt()
                orderModel?.paymentReceived = view.edtPaymentReceive.getText().toInt()
                orderModel?.balanceDue = view.edtBalanceDue.getText().toInt()
                orderModel?.bankName = view.edtBankName.getText()
                orderModel?.accountNumber = view.edtAccountNumber.getText()
                orderModel?.ifscCode = view.edtIfscCode.getText()
                orderModel?.let {
                    onOrderUpdateListener.onOrderUpdate(order)
                    dialog?.dismiss()
                }
                
            }
        }
    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet?.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet?.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}