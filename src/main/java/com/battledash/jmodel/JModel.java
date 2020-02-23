/*
 * This file is part of JModel.
 *
 * JModel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JModel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JModel
 * .  If not, see <https://www.gnu.org/licenses/>.
 */

package com.battledash.jmodel;

import com.battledash.jmodel.Methods.Controllers.JModelMain;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

public class JModel extends Application {

    public static JModelMain mainSceneController;

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JModel");

        new File("Output").mkdir();

        // Load the root layout from the fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/JModel_Main.fxml"));
        //FXMLLoader loader = new FXMLLoader(new File("src/main/java/com/battledash/jmodel/Graphical/FXML/JModel_Main.fxml").toURI().toURL());

        Parent root = loader.load();
        mainSceneController = loader.getController();
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();
    }

}
