package com.manny.doctable.Controllers

import com.manny.doctable.alertShow
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField


open class MainLayoutController {

    var N_COLS = 0
    //var names = ArrayList<String>(N_COLS)

    @FXML
    lateinit var nameTableField: TextField
    @FXML
    var docTableView: TableView<ObservableList<String>> = TableView()

    var tableData: ObservableList<ObservableList<String>> = FXCollections.observableArrayList()

    fun initialize() {
        docTableView.setSortPolicy { false }
        //docTableView.skinProperty().addListener(this::installHeaderHandler)
        docTableView.items = tableData
        docTableView.isEditable = true
        docTableView.selectionModel.isCellSelectionEnabled = true
    }

    fun onClickAddColumn() {
        N_COLS++
        for (i in tableData) {
            i.add("")
        }
        updateTable()
    }

    fun onClickAddRow() {
        val newTableData = FXCollections.observableArrayList<String>()
        for (i in 0 until N_COLS) {
            newTableData.add("")
        }
        tableData.add(newTableData)
    }

    fun onClickDeleteRow() {
        if (docTableView.selectionModel.selectedIndex > -1) {
            tableData.removeAt(docTableView.selectionModel.selectedIndex)
        } else {
            alertShow(
                Alert.AlertType.ERROR,
                "Ошибка удаления",
                "Невозможно удалить строку!",
                "Пожалуйста выберите строку для удаления!"
            )
        }
    }

    fun onClickDeleteColumn() {
        if (docTableView.selectionModel.selectedIndex > -1) {
            val column = docTableView.selectionModel.selectedCells[0].column
            println(column)
            tableData.map {
                it.removeAt(column)
            }
            N_COLS--
            if (N_COLS > 0) {
                updateTable()
            } else {
                tableData.clear()
                updateTable()
            }

        } else {
            alertShow(
                Alert.AlertType.ERROR,
                "Ошибка удаления",
                "Невозможно удалить колонку!",
                "Пожалуйста выберите колонку для удаления!"
            )
        }

    }

    private fun updateTable() {
        docTableView.columns.clear()
        for (i in 0 until N_COLS) {
            val column = TableColumn<ObservableList<String>, String>(
                "Колонка $i"
            )
            column.setCellValueFactory {
                SimpleStringProperty(it.value[i])
            }
            column.cellFactory = TextAreaTableCell.forTableColumn()
            column.setOnEditCommit {
                tableData[it.tablePosition.row][it.tablePosition.column] = it.newValue
            }

            docTableView.columns.add(column)

        }
        docTableView.items = tableData

    }

    /*private fun installHeaderHandler(s: Observable) {
        docTableView.addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
            if (e.isPrimaryButtonDown && e.clickCount > 1) {
                var target = e.target
                var column: TableColumnBase<*, *>? = null
                while (target is Node) {
                    target = target.parent
                    if (target is TableColumnHeader) {
                        column = target.tableColumn
                        if (column != null) break
                    }
                }
                if (column != null) {
                    val tableColumn = column
                    val textField = TextField(column.text)
                    textField.maxWidth = column.width
                    textField.setOnAction { a ->
                        tableColumn.text = textField.text
                        tableColumn.graphic = null
                    }
                    textField.focusedProperty().addListener { _, _, nv -> if (!nv) tableColumn.graphic = null }
                    column.graphic = textField
                    textField.requestFocus()
                }
                e.consume()
            }
        }
    }*/

    fun onClickTest() {
        if (tableData.size == 0) {
            nameTableField.text = "Булки + Чай = Вкусно!"
            onClickAddColumn()
            onClickAddColumn()
            onClickAddColumn()
            onClickAddRow()
            tableData[0][0] = "Чай"
            tableData[0][1] = "Булки"
            tableData[0][2] = "Вкусно"
            for (i in 1..20) {
                onClickAddRow()
                for (a in 0..2)
                    tableData[i][a] = "Съешь ещё этих мягких\nфранцузских булок, да выпей же чаю."
            }
        }
    }

}
