package com.manny.doctable.Controllers

import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.manny.doctable.Controllers.RootLayoutController.Companion.bf_russian


class BorderEvent : PdfPTableEventAfterSplit {

    var rowCount: Int = 0
    var bottom = true
    var top = true

    override fun splitTable(table: PdfPTable?) {
        if (table?.rows?.size != rowCount) {
            bottom = false
        }
    }

    override fun tableLayout(
        table: PdfPTable?,
        widths: Array<out FloatArray>?,
        heights: FloatArray?,
        headerRows: Int,
        rowStart: Int,
        canvases: Array<out PdfContentByte>?
    ) {
        val width = widths!![0]
        val y1 = heights!![0]
        val y2 = heights[heights.size - 1]
        val cb = canvases!![PdfPTable.LINECANVAS]
        for (i in width.indices) {
            cb.moveTo(width[i], y1)
            cb.lineTo(width[i], y2)
        }
        val x1 = width[0]
        val x2 = width[width.size - 1]
        for (i in (if (top) 0 else 1) until if (bottom) heights.size else heights.size - 1) {
            cb.moveTo(x1.toDouble(), heights[i].toDouble())
            cb.lineTo(x2.toDouble(), heights[i].toDouble())
        }
        cb.stroke()
        cb.resetRGBColorStroke()
        bottom = true
        top = true
    }

    override fun afterSplitTable(table: PdfPTable?, startRow: PdfPRow?, startIdx: Int) {
    }

}

class HeaderPage(var title: String) : PdfPageEventHelper() {

    private var t: PdfTemplate? = null
    private var total: Image? = null


    override fun onOpenDocument(writer: PdfWriter?, document: Document?) {
        t = writer?.directContent?.createTemplate(30F, 16F)
        total = Image.getInstance(t)
        total?.role = PdfName.ARTIFACT
    }

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        addHeader(writer!!)
        addFooter(writer)
    }

    private fun addFooter(writer: PdfWriter) {

        val font = Font(bf_russian, 12F, Font.NORMAL)
        val footer = PdfPTable(1)
        footer.totalWidth = 527F
        //header.setWidths(floatArrayOf(24F))
        footer.isLockedWidth = true
        footer.defaultCell.fixedHeight = 40F
        footer.defaultCell.border = Rectangle.NO_BORDER

        footer.defaultCell.horizontalAlignment = Element.ALIGN_RIGHT
        val text = PdfPCell()
        text.border = Rectangle.NO_BORDER
        text.addElement(Phrase(writer.pageNumber.toString(), font))

        footer.addCell(text)

        footer.writeSelectedRows(0, -1, 535F, 45F, writer.directContent)
    }

    fun addHeader(writer: PdfWriter) {

        val font = Font(bf_russian, 14F, Font.BOLD)

        val header = PdfPTable(1)

        header.totalWidth = 527F
        header.isLockedWidth = true
        header.defaultCell.fixedHeight = 40F
        header.defaultCell.border = Rectangle.NO_BORDER

        val text = PdfPCell()
        text.border = Rectangle.NO_BORDER
        when (writer.pageNumber) {
            1 -> text.addElement(Phrase("Таблица 1 — $title", font))
            else -> text.addElement(Phrase("Продолжение таблицы 1", font))
        }

        header.addCell(text)

        header.writeSelectedRows(0, -1, 45F, 825F, writer.directContent)
    }
}
