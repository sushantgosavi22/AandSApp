package com.aandssoftware.aandsinventory.pdfgenarator

import android.content.Context
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream


object RowCopy {


    @Throws(Exception::class)
    fun createAdditionalRows(context: Context, destination: String, orderModel: OrderModel): String {

        val stream = context.resources.openRawResource(R.raw.updated)
        val workbook = XSSFWorkbook(stream)
        val sheet = workbook.getSheetAt(0)
        var startTableIndex = 59

        var list: List<InventoryItem> = ArrayList<InventoryItem>(orderModel.orderItems.values)
        list.forEachIndexed { pos, item ->
            if (list.lastIndex != pos) {
                copyRow(workbook, sheet, startTableIndex.plus(pos), startTableIndex.plus(1).plus(pos))
            }
        }
        val out = FileOutputStream(destination)
        workbook.write(out)
        out.close()
        return destination
    }

    @Throws(Exception::class)
    fun writeFileOnToDisk(context: Context, destination: String): String {
        val stream = context.resources.openRawResource(R.raw.updated)
        val workbook = XSSFWorkbook(stream)
        val sheet = workbook.getSheetAt(0)
        val out = FileOutputStream(destination)
        workbook.write(out)
        out.close()
        return destination
    }

    private fun copyRow(workbook: XSSFWorkbook, worksheet: XSSFSheet, sourceRowNum: Int, destinationRowNum: Int) {
        // Get the source / new row
        var newRow: XSSFRow? = worksheet.getRow(destinationRowNum)
        val sourceRow = worksheet.getRow(sourceRowNum)


        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.lastRowNum, 1)
        }
        newRow = worksheet.createRow(destinationRowNum)

        // Loop through source columns to add to new row
        for (i in 0 until sourceRow.lastCellNum) {
            // Grab a copy of the old/new cell
            val oldCell = sourceRow.getCell(i)
            var newCell: XSSFCell? = newRow!!.createCell(i)

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null
                continue
            }

            // Copy style from old cell and apply to new cell
            val newCellStyle = workbook.createCellStyle()
            newCellStyle.cloneStyleFrom(oldCell.cellStyle)
            newCell!!.cellStyle = newCellStyle

            // If there is a cell comment, copy
            if (oldCell.cellComment != null) {
                newCell.cellComment = oldCell.cellComment
            }

            // If there is a cell hyperlink, copy
            if (oldCell.hyperlink != null) {
                newCell.hyperlink = oldCell.hyperlink
            }

            // Set the cell data type
            newCell.cellType = oldCell.cellType

            // Set the cell data value
            when (oldCell.cellType) {
                Cell.CELL_TYPE_BLANK -> newCell.setCellValue(oldCell.stringCellValue)
                Cell.CELL_TYPE_BOOLEAN -> newCell.setCellValue(oldCell.booleanCellValue)
                Cell.CELL_TYPE_ERROR -> newCell.setCellErrorValue(oldCell.errorCellValue)
                Cell.CELL_TYPE_FORMULA -> newCell.cellFormula = oldCell.cellFormula
                Cell.CELL_TYPE_NUMERIC -> newCell.setCellValue(oldCell.numericCellValue)
                Cell.CELL_TYPE_STRING -> newCell.setCellValue(oldCell.richStringCellValue)
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (i in 0 until worksheet.numMergedRegions) {
            val cellRangeAddress = worksheet.getMergedRegion(i)
            if (cellRangeAddress.firstRow == sourceRow.rowNum) {
                val newCellRangeAddress = CellRangeAddress(newRow!!.rowNum,
                        newRow.rowNum + (cellRangeAddress.lastRow - cellRangeAddress.firstRow),
                        cellRangeAddress.firstColumn,
                        cellRangeAddress.lastColumn)
                worksheet.addMergedRegion(newCellRangeAddress)
            }
        }
    }
}