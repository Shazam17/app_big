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

                data.sheetIterator().forEach {
                    val int_value = it.text.toIntOrNull()
                    val double_value = it.text.toDoubleOrNull()

                    if (int_value != null)
                        sheet.addCell(Number(it.col, it.row, int_value.toDouble()))
                    else if (double_value != null)
                        sheet.addCell(Number(it.col, it.row, double_value))
                    else
                        sheet.addCell(Label(it.col, it.row, it.text))

                }

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