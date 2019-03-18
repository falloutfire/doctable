package com.manny.doctable.Controllers

import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.manny.doctable.Main
import com.manny.doctable.alertShow
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.print.PrinterJob
import java.io.File
import java.io.FileOutputStream
import javax.print.DocPrintJob


class RootLayoutController {

    private var main: Main? = null

    fun setMainApp(mainApp: Main) {
        this.main = mainApp
    }

    fun onClickPrint() {
        if (main!!.mainLoader!!.tableData.size > 0) {
            val fileChooser = FileChooser()
            val extensionFilter = FileChooser.ExtensionFilter(
                "PDF files (*.pdf)", "*.pdf"
            )
            fileChooser.extensionFilters.add(extensionFilter)

            var file: File? = fileChooser.showSaveDialog(main?.primaryStage)
            if (file != null) {
                if (!file.path.endsWith(".pdf")) {
                    file = File(file.path + ".pdf")
                }
            }

            GlobalScope.launch(Dispatchers.Default) {
                if (file != null) {
                    exportToPdf(file)
                } else {
                    alertShow(Alert.AlertType.ERROR, "Отмена сохранения", "Файл не был сохранен!", "")
                }
            }
        } else {
            alertShow(Alert.AlertType.ERROR, "Отмена операции", "Таблица пустая", "Пожалуйста заполните таблицу.")
        }


    }

    private suspend fun exportToPdf(file: File) {

        val data = main?.mainLoader?.tableData
        val name = main?.mainLoader?.nameTableField?.text
        val document = Document(PageSize.A4, 30F, 30F, 45F, 45F)
        val writer = PdfWriter.getInstance(document, FileOutputStream(file))
        val headerPage = HeaderPage(name!!)

        writer.pageEvent = headerPage

        document.open()

        val table = PdfPTable(data!![0].size)
        table.paddingTop = -15F
        table.totalWidth = 500F
        table.isLockedWidth = true
        val event = BorderEvent()
        table.tableEvent = event
        table.widthPercentage = 100F
        table.defaultCell.border = Rectangle.NO_BORDER
        table.isSplitLate = true


        addTableHeader(table, data)
        table.headerRows = 1
        addRows(table, data)
        document.add(table)
        document.close()

        withContext(Dispatchers.IO) {
            val pdf = PDDocument.load(file)
            val service = PrinterJob.lookupPrintServices()
            var docPrintJob: DocPrintJob? = null

            val count = service.size
            var i = 0
            while (i < count) {
                //if (service[i].name.equals(printerNameDesired, ignoreCase = true)) {
                docPrintJob = service[i].createPrintJob()
                i = count
                //}
                i++
            }

            val pjob = PrinterJob.getPrinterJob()
            if (docPrintJob != null) {
                pjob.printService = docPrintJob.printService
                pjob.jobName = "job"
                pjob.setPageable(PDFPageable(pdf))

                pjob.print()
                withContext(Dispatchers.Main) {
                    alertShow(
                        Alert.AlertType.INFORMATION,
                        "Операция выполнена",
                        "Печать файла",
                        "Файл отправлен на печать "
                    )
                }
            } else {
                withContext(Dispatchers.Main) {
                    alertShow(
                        Alert.AlertType.ERROR,
                        "Отмена операции",
                        "Печать невозможна",
                        "Проверьте, подключен ли принтер"
                    )
                }
            }
        }
    }

    private fun addTableHeader(table: PdfPTable, data: List<List<String>>) {
        val font = Font(bf_russian, 14F, Font.BOLD)
        for (i in data[0]) {
            val header = PdfPCell()

            header.phrase = Phrase("$i ", font)
            table.addCell(header)
        }

    }

    private fun addRows(table: PdfPTable, data: List<List<String>>) {
        val font = Font(bf_russian, 14F, Font.NORMAL)
        for (i in 1 until data.size) {
            for (a in 0 until data[i].size) {
                val cell = PdfPCell()
                cell.phrase = Phrase("${data[i][a]} ", font)
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)
            }
        }
    }

    companion object {
        val bf_russian =
            BaseFont.createFont(
                File(Main::class.java.protectionDomain.codeSource.location.toString()).parent.substring(
                    5
                ) + "/times-new-roman.ttf", "CP1251", BaseFont.EMBEDDED
            )!!
    }
}
