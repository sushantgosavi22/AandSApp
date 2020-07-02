package com.aandssoftware.aandsinventory.pdfgenarator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.utilities.AppConstants
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.*
import org.apache.poi.util.IOUtils
import org.apache.poi.util.Units
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*
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

    fun generateXlsxFile(model: OrderModel, isConsumerCopy : Boolean): String {
        val outFileName = model.id+"invoice.xlsx"
        val mFolder = File(context.filesDir.absolutePath.plus("/AandS"))
        val outFile = File(mFolder.absolutePath + "/".plus(outFileName))
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
        if (!outFile.exists()) {
            outFile.createNewFile()
        }



        val map = getValueHashMap(model);
        try {

            model.orderItems?.let {
                if (model.orderItems.size > 1) {
                    RowCopy.createAdditionalRows(context, outFile.absolutePath, model)
                } else {
                    RowCopy.writeFileOnToDisk(context, outFile.absolutePath)
                }
            }



            val workbook = XSSFWorkbook(FileInputStream(outFile))
            val sheet = workbook.getSheetAt(0)
            var rowsCount = sheet.physicalNumberOfRows
            rowsCount = sheet.physicalNumberOfRows
            val formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()
            var orderNo: Int = 0
            var list: List<InventoryItem> = ArrayList<InventoryItem>(model.orderItems.values)
            for (r in 0 until rowsCount) {
                val row = sheet.getRow(r)
                if(row!=null){
                    val cellsCount = row.physicalNumberOfCells
                    for (c in 0 until cellsCount) {
                        val value = getCellAsString(row, c, formulaEvaluator)
                        val cell = row.getCell(c)
                        if (value.contains("ORDER_SR_NO", false)) {
                            var item = list[orderNo]
                            orderNo += 1
                            setRowData(row, item, formulaEvaluator, orderNo);
                            break
                        } else if(value.contains("IMAGE", false)){
                            cell.setCellValue("")
                            val signImage: String? = if(isConsumerCopy){
                                model.signature
                            }else{
                                model.signatureDistributor
                            }
                            signImage?.let {
                                val inputStream: InputStream =   ByteArrayInputStream(Base64.decode(signImage, Base64.DEFAULT))
                                row.height = (150 * 20).toShort() //100pt height * 20 = twips (twentieth of an inch point)
                                sheet.setColumnWidth(c, 35 * 256) //30 default characters width
                                val bytes = IOUtils.toByteArray(inputStream)
                                val pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG)
                                inputStream.close()
                                val left = 20// 20px
                                val top = 20 // 20pt
                                val width = Math.round(sheet.getColumnWidthInPixels(c) - left - left) //width in px
                                val height = Math.round(row.heightInPoints - top - 10 /*pt*/) //height in pt
                                drawImageOnExcelSheet(sheet as XSSFSheet, row.rowNum, c , left, top, width, height, pictureIdx)
                            }
                        } else {
                            if (map.containsKey(value.trim())) {
                                cell?.setCellValue(map.get(value))
                            }
                        }
                    }
                }

            }


            Log.d("outFile.extension",outFile.extension)
            val out = FileOutputStream(outFile)
            workbook.write(out)
            out.close()

        } catch (e: Exception) {
            printlnToUser(e.toString())
            e.printStackTrace()
            printlnToUser(e.toString())
        }

        return outFile.absolutePath
    }


    public fun uploadAndGetPdfFile(outXlsxFile : File) {
        var parameters =  ArrayList<PdfHandler.InputFile>()
        var inputeFile = PdfHandler.InputFile("File", PdfHandler.FileValue(outXlsxFile.name,encodeFileToBase64Binary(outXlsxFile.absolutePath)));
        parameters.add(inputeFile)
        var input = PdfHandler.UploadInput(parameters)
        try {
            var result = PdfHandler().requestToDownload(input,context)
            if(null!=result && result.Files?.isNotEmpty() == true){
                result?.Files?.first()?.FileData?.let {
                    val file = File(outXlsxFile.absolutePath)
                    var newFilePath = outXlsxFile.absolutePath.replace("xlsx","pdf")
                    val outputPdfFile = File(newFilePath)
                    val success = file.renameTo(outputPdfFile)
                    if (success) {
                        var result = decodeBase64StringToPDF(outputPdfFile.absolutePath, it)
                        if(result){
                            showPdfFile(outputPdfFile.absolutePath)
                        }else{
                            (context as Activity).runOnUiThread(java.lang.Runnable {
                                Utils.showToast("Unable to open pdf file. Please try again",context)
                            })
                        }
                    }
                }
            }else{
                (context as Activity).runOnUiThread(java.lang.Runnable {
                    Utils.showToast("Unable to convert file. Please try again",context)
                })
            }
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun encodeFileToBase64Binary(path: String): String? {
        var file = File(path)
        val finput: InputStream = FileInputStream(file)
        val imageBytes = IOUtils.toByteArray(finput)
        finput.read(imageBytes, 0, imageBytes.size)
        finput.close()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP) //Base64.DEFAULT
    }


    private fun decodeBase64StringToPDF(path: String,base64String : String) : Boolean{
        var result = false
        try{
            var fos = FileOutputStream(path);
            fos.write(Base64.decode(base64String, Base64.NO_WRAP));
            fos.close(); //Base64.DEFAULT
            result = true
        }catch (e : Exception){
            e.printStackTrace()
        }
       return result
    }


    public fun viewXlsxFile(xlsxFile: String,context: Context) {
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

    private fun drawImageOnExcelSheet(sheet: XSSFSheet, row: Int, col: Int,
                                      left: Int /*in px*/, top: Int /*in pt*/, width: Int /*in px*/, height: Int /*in pt*/, pictureIdx: Int) {
        val helper: CreationHelper = sheet.workbook.creationHelper
        val drawing: Drawing = sheet.createDrawingPatriarch()
        val anchor = helper.createClientAnchor()
        anchor.anchorType = ClientAnchor.MOVE_AND_RESIZE
        anchor.setCol1(col) //first anchor determines upper left position
        anchor.row1 = row
        anchor.dx1 = Units.toEMU(left.toDouble()) //dx = left in px
        anchor.dy1 = Units.toEMU(top.toDouble()) //dy = top in pt
        anchor.setCol2(col) //second anchor determines bottom right position
        anchor.row2 = row
        anchor.dx2 = Units.toEMU((left + width).toDouble()) //dx = left + wanted width in px
        anchor.dy2 = Units.toEMU((top + height).toDouble()) //dy= top + wanted height in pt
        drawing.createPicture(anchor, pictureIdx)
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

       /* val filecontent = FileInputStream(File(sourcepath))
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
        return destinationPath;*/
        return "";
    }

    private fun getValueHashMap(model: OrderModel): HashMap<String, String> {
        var map = HashMap<String, String>();
        map["INVOICE_NO_VAL"] = model?.invoiceNumber ?: AppConstants.EMPTY_STRING
        map["SALES_ORDER_NO"] = model?.salesOrderNumber ?: AppConstants.EMPTY_STRING
        map["SALES_ORDER_DATE"] = model?.salesOrderDate ?: AppConstants.EMPTY_STRING
        map["PO_NO"] = model?.poNo ?: AppConstants.EMPTY_STRING
        map["PO_DATE"] = model?.poDate ?: AppConstants.EMPTY_STRING
        map["REF_NO"] = model?.refranceNo ?: AppConstants.EMPTY_STRING
        map["INVOICE_DATE_VAL"] = DateUtils.getFormatedDatePickerFormat(model.invoiceDate)
        map["DUE_DATE_VAL"] = DateUtils.getFormatedDatePickerFormat(model.dueDate)
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
        map["TAXABLE_AMOUNT_AFTER_DISCOUNT"] = model.taxableAmountAfterDiscount.toString()
        map["DISCOUNT_AMOUNT"] = model.discount.toString()
        map["GST_AMOUNT"] = model.gstTotalAmount.toString()
        map["CESS_AMOUNT"] = model.cessAmount.toString()
        map["ROUND_OFF"] = model.roundOff.toString()
        map["TOTAL_FIGURE"] = model.totalFigure.toString()
        map["PAYMENT_RECEIVED"] = model.paymentReceived.toString()
        map["TOTAL_CREDIT_APPLIED"] = model.totalCreditApplied.toString()
        map["TOTAL_DEBIT_APPLIED"] = model.totalDebitApplied.toString()
        map["BALANCE_DUE"] = model.balanceDue.toString()
        map["IMAGE"] = "Sushant Gosavi android"
        return map
    }

    private fun getCoulmnValueHashMap(item: InventoryItem?, sr: Int): HashMap<String, String> {
        var map = HashMap<String, String>()
        map["ORDER_SR_NO"] = sr.toString()
        map["ORDER_DISCRIPTION"] = item?.inventoryItemName ?: ""
        map["ORDER_HSN"] = item?.hsnCode ?: ""
        map["ORDER_QTY"] = item?.itemQuantity ?: ""
        map["ORDER_UNIT"] = item?.itemQuantityUnit ?: ""
        map["ORDER_UNIT_PRICE"] = item?.itemUnitPrice ?: ""
        map["ORDER_TAXABLE_AMOUNT"] = item?.taxableAmount.toString() ?: ""
        map["ORDER_GST_PERCENT"] = item?.gstPercentage.toString() ?: ""
        map["ORDER_GST_AMOUNT"] = item?.gstAmount.toString() ?: ""
        map["ORDER_SGST_PERCENT"] = item?.sgstPercentage.toString() ?: ""
        map["ORDER_SGST_AMOUNT"] = item?.sgstAmount.toString() ?: ""
        map["ORDER_TOTAL_AMOUNT"] = item?.finalBillAmount.toString() ?: ""
        return map
    }
}
