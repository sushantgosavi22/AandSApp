package com.aandssoftware.aandsinventory.pdfgenarator

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat


/*

https://www.convertapi.com/xlsx-to-pdf
https://v2.convertapi.com/convert/xlsx/to/pdf?Secret=xHdVqfkmy68wtEdT&Token=495272875
xHdVqfkmy68wtEdT  secret Key
495272875  API Key
Content-Type: application/json
{
    "Parameters": [
        {
            "Name": "File",
            "FileValue": {
                "Name": "SaleTransactionPreview.xlsx",
                "Data": "<Base64 encoded file content>"
            }
        }
    ]
}
 */
class PdfGenerator(private val context: Context) {


    fun generatePdf(model: OrderModel): String {
        val outFileName = "filetoShows.xlsx"
        val mFolder = File(context.filesDir.absolutePath.plus("/AandS"))
        val outFile = File(mFolder.absolutePath + "/".plus(outFileName))
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
        if (!outFile.exists()) {
            outFile.createNewFile()
        }

        model.orderItems?.let {
            if (model.orderItems.size > 1) {
                RowCopy.createAdditionalRows(context, outFile.absolutePath, model)
            } else {
                RowCopy.writeFileOnToDisk(context, outFile.absolutePath)
            }
        }

        val map = getValueHashMap(model);
        try {
            val workbook = XSSFWorkbook(FileInputStream(outFile))
            val sheet = workbook.getSheetAt(0)
            var rowsCount = sheet.physicalNumberOfRows
            rowsCount = sheet.physicalNumberOfRows
            val formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()
            var orderNo: Int = 0
            var list: List<InventoryItem> = ArrayList<InventoryItem>(model.orderItems.values)
            for (r in 0 until rowsCount) {
                val row = sheet.getRow(r)
                val cellsCount = row.physicalNumberOfCells
                for (c in 0 until cellsCount) {
                    val value = getCellAsString(row, c, formulaEvaluator)
                    val cell = row.getCell(c)
                    if (value.contains("ORDER_SR_NO", false)) {
                        var item = list[orderNo]
                        orderNo += 1
                        setRowData(row, item, formulaEvaluator, orderNo);
                        break
                    } else {
                        if (map.containsKey(value.trim())) {
                            cell?.setCellValue(map.get(value))
                        }
                    }
                }
            }
            val out = FileOutputStream(outFile)
            workbook.write(out)
            out.close()

            viewXlsxFile(outFile.absolutePath)
        } catch (e: Exception) {
            printlnToUser(e.toString())
            e.printStackTrace()
        }

        return outFile.absolutePath
    }


    private fun setRowData(row: XSSFRow, item: InventoryItem?, formulaEvaluator: XSSFFormulaEvaluator, sr: Int) {
        val cellsCount = row.physicalNumberOfCells
        var map = getCoulmnValueHashMap(item, sr)
        for (c in 0 until cellsCount) {
            val value = getCellAsString(row, c, formulaEvaluator)
            val cell = row.getCell(c)
            if (map.containsKey(value.trim())) {
                cell?.setCellValue(map.get(value))
            }
        }
    }


    protected fun getCellAsString(row: Row, c: Int, formulaEvaluator: FormulaEvaluator): String {
        var value = ""
        try {
            val cell = row.getCell(c)
            val cellValue = formulaEvaluator.evaluate(cell)
            cellValue?.let {
                when (cellValue.cellType) {
                    Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.booleanValue
                    Cell.CELL_TYPE_NUMERIC -> {
                        val numericValue = cellValue.numberValue
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            val date = cellValue.numberValue
                            val formatter = SimpleDateFormat("dd/MM/yy")
                            value = formatter.format(HSSFDateUtil.getJavaDate(date))
                        } else {
                            value = "" + numericValue
                        }
                    }
                    Cell.CELL_TYPE_STRING -> value = "" + cellValue?.stringValue
                }
            }

        } catch (e: NullPointerException) {
            /* proper error handling should be here */
            printlnToUser(e.toString())
        }

        return value
    }

    /**
     * print line to the output TextView
     * @param str
     */
    private fun printlnToUser(str: String) {
        Log.i("PDF_DATA", str)
    }

    fun share(fileName: String, context: Context) {
        val fileUri = Uri.parse("content://" + context.packageName + "/" + fileName)
        printlnToUser("sending $fileUri ...")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.type = "application/octet-stream"
        context.startActivity(Intent.createChooser(shareIntent, context.resources.getText(com.aandssoftware.aandsinventory.R.string.app_name)))
    }

    private fun viewXlsxFile(xlsxFile: String) {
        val pdfFile = File(xlsxFile)
        val path = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(path, "application/vnd.ms-excel")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showPdfFile(pdfFile: String) {
        val pdfFile = File(pdfFile)
        val path = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(path, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    private fun openOtherApp(file: String) {
        val pdfFile = File(file)
        val path = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", pdfFile)
        var list = ArrayList<String>()
        list.add(file)
        val pdfIntent = Intent()
        pdfIntent.setClassName("com.sipl01.epc", "com.samyak.MainActivity")
        pdfIntent.action = Intent.ACTION_MAIN
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    private fun convertXlsxToPDF(xlsxFile: File) {
        try {
            val outFileName = "filetoShow.pdf"
            val mFolder = File(context.filesDir.absolutePath.plus("/AandS"))
            val outFile = File(mFolder.absolutePath + "/".plus(outFileName))
            if (!mFolder.exists()) {
                mFolder.mkdir()
            }
            if (!outFile.exists()) {
                outFile.createNewFile()
            }
            printlnToUser("writing Created PDF file ${outFile.absolutePath}")
            var finalFile = getPdfFileFromXls(xlsxFile.absolutePath, outFile.absolutePath);
            showPdfFile(finalFile)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getPdfFileFromXls(sourcepath: String, destinationPath: String): String {

        val filecontent = FileInputStream(File(sourcepath))
        val out = FileOutputStream(File(destinationPath))

        var my_xls_workbook: HSSFWorkbook? = null
        var my_worksheet: HSSFSheet? = null
        var my_xlsx_workbook: XSSFWorkbook? = null
        var my_worksheet_xlsx: XSSFSheet? = null

        val document = Document(PageSize.ARCH_E, 0F, 0F, 0F, 0F)
        val writer = PdfWriter.getInstance(document, out)
        document.open()
        var magnify: PdfDestination? = null
        val magnifyOpt = 70.0.toFloat()
        magnify = PdfDestination(PdfDestination.XYZ, -1f, -1f, magnifyOpt / 100)
        val pageNumberToOpenTo = 1
        val zoom = PdfAction.gotoLocalPage(pageNumberToOpenTo, magnify, writer)
        writer.setOpenAction(zoom)
        document.addDocListener(writer)

        var rowIterator: Iterator<Row>? = null
        var maxColumn = 0
        if (sourcepath.contains(".xlsx")) {
            try {
                my_xlsx_workbook = XSSFWorkbook(filecontent)
                my_worksheet_xlsx = my_xlsx_workbook.getSheetAt(0)
                rowIterator = my_worksheet_xlsx!!.iterator()
                maxColumn = my_worksheet_xlsx.getRow(0).lastCellNum.toInt()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        val my_table = PdfPTable(maxColumn)
        my_table.horizontalAlignment = Element.ALIGN_CENTER
        my_table.widthPercentage = 100f
        my_table.spacingBefore = 0f
        my_table.spacingAfter = 0f
        var table_cell: PdfPCell
        while (rowIterator!!.hasNext()) {
            val row = rowIterator.next()
            val cellIterator = row.cellIterator()
            while (cellIterator.hasNext()) {
                val cell = cellIterator.next() //Fetch CELL
                when (cell.getCellType()) {
                    //Identify CELL type
                    Cell.CELL_TYPE_STRING -> {
                        //Push the data from Excel to PDF Cell
                        table_cell = PdfPCell(Phrase(cell.getStringCellValue()))
                        if (row.getRowNum() === 0) {
                            table_cell.backgroundColor = BaseColor.LIGHT_GRAY
                            table_cell.borderColor = BaseColor.BLACK
                        }
                        my_table.addCell(table_cell)
                    }
                }
            }
        }
        document.add(my_table)
        document.close()
        println("Excel file converted to PDF successfully")
        return destinationPath;
    }

    private fun getValueHashMap(model: OrderModel): HashMap<String, String> {
        var map = HashMap<String, String>();
        map["INVOICE_NO_VAL"] = model?.invoiceNumber ?: AppConstants.EMPTY_STRING
        map["INVOICE_DATE_VAL"] = DateUtils.getDateFormatted(model.invoiceDate)
        map["DUE_DATE_VAL"] = DateUtils.getDateFormatted(model.dueDate)
        map["CONSIGNEE_NAME"] = model.customerModel?.customerName ?: AppConstants.EMPTY_STRING
        map["COMPANY_NAME"] = model.customerModel?.customerName ?: AppConstants.EMPTY_STRING
        map["COMPANY_GST"] = model.customerModel?.customerGstNumber ?: AppConstants.EMPTY_STRING
        map["CONSIGNEE_ADDRESS"] = model.customerModel?.address ?: AppConstants.EMPTY_STRING
        map["COMPANY_ADDRESS"] = model.customerModel?.address ?: AppConstants.EMPTY_STRING
        map["CONSIGNEE_MOBILE_NO"] = model.customerModel?.contactPersonNumber
                ?: AppConstants.EMPTY_STRING
        map["CONSIGNEE_PINCODE"] = "-"
        map["ORDER_TOTAL_TEXABLE_AMOUNT"] = model.totalTaxableAmount.toString()
        map["ORDER_TOTAL_GST_AMOUNT"] = model.gstOrderTotalAmount.toString()
        map["ORDER_TOTAL_SGST_AMOUNT"] = model.sgstOrderTotalAmount.toString()
        map["ORDER_TOTAL_FINAL_AMOUNT"] = model.finalBillAmount.toString()
        map["TAXABLE_AMOUNT_BEFORE_DISCOUNT"] = model.taxableAmountBeforeDiscount.toString()
        map["GST_AMOUNT"] = model.gstTotalAmount.toString()
        map["CESS_AMOUNT"] = model.cessAmount.toString()
        map["ROUND_OFF"] = model.roundOff.toString()
        map["TOTAL_FIGURE"] = model.totalFigure.toString()
        map["PAYMENT_RECEIVED"] = model.paymentReceived.toString()
        map["TOTAL_CREDIT_APPLIED"] = model.totalCreditApplied.toString()
        map["TOTAL_DEBIT_APPLIED"] = model.totalDebitApplied.toString()
        map["BALANCE_DUE"] = model.balanceDue.toString()
        return map
    }

    private fun getCoulmnValueHashMap(item: InventoryItem?, sr: Int): HashMap<String, String> {
        var map = HashMap<String, String>()
        map["ORDER_SR_NO"] = sr.toString()
        map["ORDER_DISCRIPTION"] = item?.inventoryItemName ?: ""
        map["ORDER_HSN"] = item?.inventoryItemName ?: ""
        map["ORDER_QTY"] = item?.itemQuantity ?: ""
        map["ORDER_UNIT"] = item?.itemQuantityUnit ?: ""
        map["ORDER_UNIT_PRICE"] = item?.itemUnitPrice ?: ""
        map["ORDER_TAXABLE_AMOUNT"] = item?.itemQuantity ?: ""
        map["ORDER_GST_PERCENT"] = item?.itemQuantity ?: ""
        map["ORDER_GST_AMOUNT"] = item?.itemQuantity ?: ""
        map["ORDER_SGST_PERCENT"] = item?.itemQuantity ?: ""
        map["ORDER_SGST_AMOUNT"] = item?.itemQuantity ?: ""
        map["ORDER_TOTAL_AMOUNT"] = item?.itemQuantity ?: ""
        return map
    }
}
