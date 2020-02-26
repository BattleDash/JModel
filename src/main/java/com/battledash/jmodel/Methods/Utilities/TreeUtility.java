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

package com.battledash.jmodel.Methods.Utilities;

import com.battledash.jmodel.JModel;
import javafx.scene.control.TreeItem;
import me.fungames.jfortniteparse.ue4.pak.GameFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeUtility {

    public static TreeItem generateTree(List<GameFile> index) {
        TreeItem<String> root = new TreeItem<>("root");
        for (GameFile gameFile : index) {
            TreeItem<String> current = root;
            for (String component : gameFile.getPath().split("/")) {
                if (component.contains(".")) continue;
                current = getOrCreateChild(current, component);
            }
        }
        System.out.println("Finished generating tree");
        return root;
    }

    // Gets or creates the child node for a given path part
    private static TreeItem<String> getOrCreateChild(TreeItem<String> parent, String value) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (value.equals(child.getValue())) {
                return child;
            }
        }
        TreeItem<String> newChild = new TreeItem<>(value);
        parent.getChildren().add(newChild);
        return newChild;
    }

    public static String getPathFromItem(TreeItem<String> item) {
        List<TreeItem<String>> items = new ArrayList<>();
        String path = "";
        TreeItem<String> parent = item;
        while (parent.getParent() != null) {
            items.add(parent);
            parent = parent.getParent();
        }
        Collections.reverse(items);
        for (TreeItem<String> stringTreeItem : items)
            path += stringTreeItem.getValue() + "/";
        return path;
    }

    public static String getSelectedPath() {
        return TreeUtility.getPathFromItem((TreeItem) JModel.mainSceneController.PakDirectoryTree.getSelectionModel().getSelectedItem());
    }

}
