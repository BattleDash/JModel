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
 * along with JModel.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.battledash.jmodel.Methods.Controllers;

import com.battledash.jmodel.JModel;
import com.battledash.jmodel.Methods.Utilities.Logger;
import com.battledash.jmodel.Methods.Utilities.PAKsUtility;
import com.battledash.jmodel.Methods.Utilities.TreeUtility;
import com.battledash.jmodel.Methods.PakReader.PakFileContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.fungames.jfortniteparse.converters.fort.ItemDefinitionContainer;
import me.fungames.jfortniteparse.converters.fort.ItemDefinitionsKt;
import me.fungames.jfortniteparse.converters.ue4.SoundWave;
import me.fungames.jfortniteparse.converters.ue4.SoundsKt;
import me.fungames.jfortniteparse.ue4.assets.Package;
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundWave;
import me.fungames.jfortniteparse.ue4.assets.exports.athena.AthenaItemDefinition;
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortWeaponMeleeItemDefinition;
import me.fungames.jfortniteparse.ue4.pak.GameFile;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class JModelMain {

    @FXML
    public TextArea LogText;
    @FXML
    public TextArea JsonAssetData;
    @FXML
    public TreeView PakDirectoryTree;
    @FXML
    public ListView PakDirectoryFiles;
    @FXML
    public ImageView AssetImage;
    @FXML
    public Button ExtractButton;
    @FXML
    public TextField FileSearchField;
    @FXML
    public Label VersionText;

    public static Logger logger;

    public PakFileContainer container;
    public GameFile loadedGameFile;
    public Package loadedPackage;

    @FXML
    public void initialize() {
        LogText.setText("Initialized JModel, " + JModel.version + " by BattleDash");
        VersionText.setText("JModel " + JModel.version + " by BattleDash");
        logger = new Logger(LogText);

        PakDirectoryTree.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                TreeItem<String> item = (TreeItem) PakDirectoryTree.getSelectionModel().getSelectedItem();
                PakDirectoryFiles.getItems().clear();
                List<GameFile> files = container.getDirectory(TreeUtility.getPathFromItem(item));
                if (files == null) return;
                for (GameFile gameFile : files) {
                    if (PakDirectoryFiles.getItems().contains(gameFile.getNameWithoutExtension())) continue;
                    PakDirectoryFiles.getItems().add(gameFile.getNameWithoutExtension());
                    Collections.sort(PakDirectoryFiles.getItems(), (Comparator<String>) (s1, s2) -> s1.compareToIgnoreCase(s2));
                    PakDirectoryFiles.setVisible(true);
                }
            }
        });

        PakDirectoryFiles.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (event.getClickCount() == 2 && (node instanceof Text || (node instanceof ListCell && ((ListCell) node).getText() != null))) {
                JsonAssetData.setText("");
                AssetImage.setImage(null);
                String folder = TreeUtility.getPathFromItem((TreeItem) PakDirectoryTree.getSelectionModel().getSelectedItem());
                String assetName = PakDirectoryFiles.getSelectionModel().getSelectedItem().toString();
                String path = folder + assetName;
                logger.info("Loading game asset " + path);
                // Gets GameFile Object for pak name from asset path
                loadedGameFile = container.provider.findGameFile(path);
                // Loads Package from path
                loadedPackage = container.provider.loadGameFile(path);
                logger.info("Loaded game file from " + loadedGameFile.getPakFileName());
                logger.debug("Attempting JSON conversion");
                // Attempts to grab JSON data from the package
                try {
                    String jsonUnparsed = loadedPackage.toJson();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonElement je = JsonParser.parseString(jsonUnparsed);
                    String prettyJsonString = gson.toJson(je.getAsJsonObject().get("export_properties"));
                    JsonAssetData.setText(prettyJsonString);
                } catch (Exception e) {
                    logger.warn("Failed, resetting JsonFields");
                    JsonAssetData.setText("");
                }
                // Attempts to generate an item image from the asset. Experimental
                // Attempts to parse it as a soundwave
                if (loadedPackage.getExports() != null) {
                    UEExport export = loadedPackage.getExports().get(0);
                    if (export instanceof AthenaItemDefinition) {
                        logger.debug("Found AthenaItemDefinition, generating Image");
                        try {
                            // TODO Add support for non AthenaItemDefinition objects
                            AthenaItemDefinition item = (AthenaItemDefinition) loadedPackage.getExports().get(0);
                            ItemDefinitionContainer container1 = ItemDefinitionsKt.createContainer(item, container.provider, true, false, null);
                            logger.debug("Loading image " + item.getDisplayName().getText());
                            BufferedImage image = container1.getImage();
                            AssetImage.setImage(SwingFXUtils.toFXImage(image, null));
                        } catch (Exception e) {
                            logger.warn("Failed, resetting BufferedImage");
                            AssetImage.setImage(null);
                        }
                    } else if (export instanceof FortWeaponMeleeItemDefinition) {
                        // TODO
                    } else if (export instanceof USoundWave) {
                        logger.debug("Found USoundWave, converting Sound");
                        try {
                            USoundWave item = (USoundWave) loadedPackage.getExports().get(0);
                            SoundWave sound = SoundsKt.convert(item);
                            File file = File.createTempFile(loadedGameFile.getNameWithoutExtension(), ".wav");
                            FileOutputStream writer = new FileOutputStream(file);
                            writer.write(sound.getData());
                            writer.close();
                            file.deleteOnExit();
                            Desktop.getDesktop().open(file);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        });
    }

    public void onExtractButton(MouseEvent event) throws IOException {
        Stage window = new Stage();
        window.setTitle("JModel Extract");

        // Load the root layout from the fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/JModel_Extract.fxml"));
        //FXMLLoader loader = new FXMLLoader(new File("src/main/java/com/battledash/jmodel/Graphical/FXML/JModel_Extract.fxml").toURI().toURL());

        Parent root = loader.load();
        JModelExport controller = loader.getController();
        controller.handleAsset(TreeUtility.getSelectedPath(), loadedGameFile, loadedPackage, JsonAssetData.getText(), AssetImage.getImage());
        window.setScene(new Scene(root));
        window.show();
    }

    public void onSearchFieldChanged(KeyEvent event) {

    }

    public void loadAllPaks(Event e) {
        new Thread(() -> {
            File directory = PAKsUtility.getGameFilesLocation();
            AtomicReference<String> aes = new AtomicReference<>("");
            try {
                URL url = new URL("https://fortnite-api.com/v2/aes");
                URLConnection con = url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 2.0; Windows NT 5.0; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");
                InputStream stream = con.getInputStream();
                Scanner scan = new Scanner(stream);
                StringBuilder builder = new StringBuilder();
                while (scan.hasNext()) {
                    builder.append(scan.next());
                }

                JSONObject obj = new JSONObject(builder.toString());
                aes.set(obj.getJSONObject("data").getString("updated"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            container = new PakFileContainer(directory, aes.get());

            logger.info("Requesting pak index tree generation");
            Platform.runLater(() -> {
                PakDirectoryTree.setRoot(TreeUtility.generateTree(container.provider.getFiles()));
                logger.info("Done generating");
                PakDirectoryTree.setShowRoot(false);
                System.gc();
            });
        }).start();
    }

}
