package com.software.ssp.erkc.utils

import com.software.ssp.erkc.modules.history.valuehistory.ValueHistoryPresenter
import jxl.Workbook
import jxl.write.Label
import jxl.write.Number
import timber.log.Timber
import java.io.File


class ExcelUtils {
    companion object {
        fun writeToFile(file: File, data: ValueHistoryPresenter.ShareData): Boolean {
            try {
                val workbook = Workbook.createWorkbook(file)

                val sheet = workbook.createSheet("Показания", 0)



                val max_width = HashMap<Int, Int>()

                data.sheetIterator().forEach {
                    val int_value = it.text.toIntOrNull()
                    val double_value = it.text.toDoubleOrNull()

                    if (int_value != null)
                        sheet.addCell(Number(it.col, it.row, int_value.toDouble()))
                    else if (double_value != null)
                        sheet.addCell(Number(it.col, it.row, double_value))
                    else
                        sheet.addCell(Label(it.col, it.row, it.text))

                    val cur_width = max_width.get(it.col) ?: 1
                    max_width.put(it.col, Math.max(cur_width, it.text.length))
                }

                max_width.keys.forEach({ col -> sheet.setColumnView(col, max_width.get(col)!!) })
                //max_width.forEach { col, width -> sheet.setColumnView(col, width)}

                workbook.write()
                workbook.close()

                return true
            } catch (e: Exception) {
                Timber.e(e)
                return false
            }
        }
    }
}