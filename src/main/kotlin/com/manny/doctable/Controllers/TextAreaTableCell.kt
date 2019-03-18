package com.manny.doctable.Controllers

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Cell
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.util.Callback
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter


class TextAreaTableCell<S, T> @JvmOverloads constructor(converter: StringConverter<T>? = null) : TableCell<S, T>() {

    private var textArea: TextArea? = null
    private val converter = SimpleObjectProperty<StringConverter<T>>(this, "converter")

    private fun startEdit(cell: Cell<T>, converter: StringConverter<T>) {
        textArea!!.text = getItemText(cell, converter)

        cell.text = null
        cell.graphic = textArea

        textArea!!.selectAll()
        textArea!!.requestFocus()
    }

    private fun updateItem(cell: Cell<T>, converter: StringConverter<T>) {

        if (cell.isEmpty) {
            cell.text = null
            cell.setGraphic(null)

        } else {
            if (cell.isEditing) {
                if (textArea != null) {
                    textArea!!.text =
                        getItemText(cell, converter)
                }
                cell.text = null
                cell.setGraphic(textArea)
            } else {
                cell.text = getItemText(cell, converter)
                cell.setGraphic(null)
            }
        }
    }

    init {
        this.styleClass.add("text-area-table-cell")
        setConverter(converter)
    }

    fun converterProperty(): ObjectProperty<StringConverter<T>> {
        return converter
    }

    fun setConverter(value: StringConverter<T>?) {
        converterProperty().set(value)
    }

    fun getConverter(): StringConverter<T> {
        return converterProperty().get()
    }

    override fun startEdit() {
        if (!isEditable || !tableView.isEditable || !tableColumn.isEditable) {
            return
        }

        super.startEdit()

        if (isEditing) {
            if (textArea == null) {
                textArea =
                    createTextArea(this, getConverter())
            }

            startEdit(this, getConverter())
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        cancelEdit(this, getConverter())
    }

    public override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)
        updateItem(this, getConverter())
    }

    companion object {

        fun <S> forTableColumn(): Callback<TableColumn<S, String>, TableCell<S, String>> {
            return forTableColumn(DefaultStringConverter())
        }

        fun <S, T> forTableColumn(converter: StringConverter<T>): Callback<TableColumn<S, T>, TableCell<S, T>> {
            return Callback { TextAreaTableCell(converter) }
        }

        private fun <T> getItemText(cell: Cell<T>, converter: StringConverter<T>?): String {
            return if (converter == null)
                if (cell.item == null)
                    ""
                else
                    cell.item
                        .toString()
            else
                converter.toString(cell.item)
        }

        private fun <T> createTextArea(cell: Cell<T>, converter: StringConverter<T>?): TextArea {
            val textArea = TextArea(
                getItemText(
                    cell,
                    converter
                )
            )
            textArea.setOnKeyReleased { t ->
                if (t.code == KeyCode.ESCAPE) {
                    cell.cancelEdit()
                    t.consume()
                } else if (t.code == KeyCode.ENTER && t.isShiftDown) {
                    if (converter == null) {
                        throw IllegalStateException(
                            "Attempting to convert text input into Object, but provided "
                                    + "StringConverter is null. Be sure to set a StringConverter "
                                    + "in your cell factory."
                        )
                    }
                    cell.commitEdit(converter.fromString(textArea.text))
                    t.consume()
                }
            }
            textArea.prefRowCountProperty().bind(Bindings.size(textArea.paragraphs))
            return textArea
        }

        private fun <T> cancelEdit(cell: Cell<T>, converter: StringConverter<T>) {
            cell.text = getItemText(cell, converter)
            cell.graphic = null
        }
    }

}
