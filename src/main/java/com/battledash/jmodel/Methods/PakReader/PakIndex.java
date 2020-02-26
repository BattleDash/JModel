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

package com.battledash.jmodel.Methods.PakReader;

import com.battledash.jmodel.JModel;
import me.fungames.jfortniteparse.ue4.pak.GameFile;
import me.fungames.jfortniteparse.ue4.pak.PakFileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PakIndex {

    private List<GameFile> index;
    private static List<PakFileReader> readers = new ArrayList<>();

    private PakFileReader reader;

    public PakIndex() {
        clearIndex();
    }

    public void addPak(PakFileReader reader) {
        readers.add(reader);
        this.reader = reader;
        reader.readIndex();
        index.addAll(reader.getFiles());
    }

    public void clearIndex() {
        index = new ArrayList<>();
    }

    public List<GameFile> getIndex() {
        return index;
    }

    public List<GameFile> searchForFiles(String search) {
        return index.stream()
                .filter(gameFile -> search.toLowerCase().startsWith(gameFile.getNameWithoutExtension().toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<GameFile> getDirectory(String path) {
        List<GameFile> files = new ArrayList<>();
        for (PakFileReader pakFileReader : readers) {
            try {
                String pathMount = path.substring(pakFileReader.getMountPrefix().length());
                Map<String, Integer> dataMap = pakFileReader.getDirectoryIndex().get(pathMount);
                if (dataMap == null)
                    continue;
                Set<String> rawFiles = dataMap.keySet();
                for (String file : rawFiles)
                    files.add(JModel.mainSceneController.container.provider.findGameFile(path + file));
            } catch (Exception e) { }
        }
        return files;
    }

}
