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

import com.battledash.jmodel.Graphical.Popups.AlertBox;
import com.battledash.jmodel.Methods.Utilities.PAKsUtility;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.fungames.jfortniteparse.converters.ue4.SoundWave;
import me.fungames.jfortniteparse.converters.ue4.SoundsKt;
import me.fungames.jfortniteparse.ue4.assets.Package;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundWave;
import me.fungames.jfortniteparse.ue4.pak.GameFile;
import me.fungames.jfortniteparse.ue4.pak.PakFileReader;

import javax.imageio.ImageIO;
import java.io.*;

public class JModelExport {


    @FXML
    private CheckBox SoundExportCheckBox;
    @FXML
    private CheckBox JSONExportCheckBox;
    @FXML
    private CheckBox ImageExportCheckBox;
    @FXML
    private CheckBox UAssetExportCheckBox;
    @FXML
    private CheckBox UBulkExportCheckBox;
    @FXML
    private CheckBox UExpExportCheckBox;
    @FXML
    private Button ExportButton;

    private String path;
    private GameFile asset;
    private Package gamePackage;
    private String json;
    private Image image;

    public void handleAsset(String path, GameFile asset, Package gamePackage, String json, Image image) {
        this.path = path;
        this.asset = asset;
        this.gamePackage = gamePackage;
        this.json = json;
        this.image = image;
        if (this.gamePackage == null) SoundExportCheckBox.setDisable(true);
        if (asset.getUbulk() == null) UBulkExportCheckBox.setDisable(true);
        if (asset.getUexp() == null) UExpExportCheckBox.setDisable(true);
        if (json.equals("")) JSONExportCheckBox.setDisable(true);
        if (image == null) ImageExportCheckBox.setDisable(true);
    }

    public void onExportButtonClicked() throws IOException {
        boolean SoundExport = SoundExportCheckBox.isSelected();
        boolean JSONExport = JSONExportCheckBox.isSelected();
        boolean ImageExport = ImageExportCheckBox.isSelected();
        boolean UAssetExport = UAssetExportCheckBox.isSelected();
        boolean UBulkExport = UBulkExportCheckBox.isSelected();
        boolean UExpExport = UExpExportCheckBox.isSelected();

        JModelMain.logger.debug(this.asset.getPathWithoutExtension());
        File path = new File("Output/" + this.path);
        path.mkdirs();
        String name = this.asset.getNameWithoutExtension();

        // Parsed/Converted files
        if (SoundExport) {
            USoundWave item = (USoundWave) this.gamePackage.getExports().get(0);
            SoundWave sound = SoundsKt.convert(item);
            File file = new File(path + "/" + name + ".ogg");
            file.createNewFile();
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(sound.getData());
            writer.close();
        }
        if (JSONExport) {
            File file = new File(path + "/" + name + ".json");
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(this.json);
            writer.close();
        }
        if (ImageExport) {
            File file = new File(path + "/" + name + ".png");
            file.createNewFile();
            ImageIO.write(SwingFXUtils.fromFXImage(this.image, null), "png", file);
        }
        // Raw files
        PakFileReader reader = null;
        if (UAssetExport || UBulkExport || UExpExport) {
            reader = new PakFileReader(PAKsUtility.getGameFilesLocation() + "\\" + this.asset.getPakFileName());
        }
        if (UAssetExport) {
            File file = new File(path.getPath() + "/" + name + ".uasset");
            file.createNewFile();
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(reader.extract(this.asset));
            writer.close();
        }
        if (UBulkExport) {
            File file = new File(path.getPath() + "/" + name + ".ubulk");
            file.createNewFile();
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(reader.extract(this.asset.getUbulk()));
            writer.close();
        }
        if (UExpExport) {
            File file = new File(path.getPath() + "/" + name + ".uexp");
            file.createNewFile();
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(reader.extract(asset.getUexp()));
            writer.close();
        }

        AlertBox.display("Done!", "The requested assets have been exported to the Output folder!");
        ((Stage) ExportButton.getScene().getWindow()).close();

    }

}
