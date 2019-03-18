package com.manny.doctable

import com.manny.doctable.Controllers.MainLayoutController
import com.manny.doctable.Controllers.RootLayoutController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.io.IOException

class Main : Application() {

    var primaryStage: Stage? = null
    var rootLoader: RootLayoutController? = null
    var mainLoader: MainLayoutController? = null
    private var rootPane: BorderPane? = null

    override fun start(primaryStage: Stage?) {
        this.primaryStage = primaryStage
        this.primaryStage?.sizeToScene()
        this.primaryStage?.title = "Table to pdf"
        this.primaryStage?.isResizable = false
        initRootLayout()
        showMainLayout()
    }

    @Throws(IOException::class)
    private fun showMainLayout() {
        val loader = FXMLLoader()
        loader.location = Main::class.java.getResource("Views/MainLayout.fxml")

        val mainPane = loader.load<AnchorPane>() as AnchorPane
        rootPane?.center = mainPane

        mainLoader = loader.getController()
    }

    @Throws(IOException::class)
    private fun initRootLayout() {
        val loader = FXMLLoader()
        loader.location = Main::class.java.getResource("Views/RootLayout.fxml")
        rootPane = loader.load<BorderPane>() as BorderPane

        val scene = Scene(rootPane)
        primaryStage?.scene = scene
        primaryStage?.show()

        rootLoader = loader.getController()
        rootLoader?.setMainApp(this)
    }

}

fun alertShow(type: Alert.AlertType, title: String, header: String, content: String) {
    val alert = Alert(type)
    alert.title = title
    alert.headerText = header
    alert.contentText = content

    alert.showAndWait()
}

fun main() {
    Application.launch(Main::class.java)
}