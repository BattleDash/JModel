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

package com.battledash.jmodel.PakReader;

import me.fungames.jfortniteparse.ue4.pak.GameFile;
import me.fungames.jfortniteparse.ue4.pak.PakFileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PakIndex {

    private List<GameFile> index;
    private Map<String, List<GameFile>> directories = new HashMap<>();

    public PakIndex() {
        clearIndex();
    }

    public void addPak(PakFileReader reader) {
        reader.readIndex();
        index.addAll(reader.getFiles());
        for (GameFile gameFile : index) {
            String path = gameFile.getPathWithoutExtension().split(Pattern.quote(gameFile.getNameWithoutExtension()))[0];
            List<GameFile> files = new ArrayList<>();
            if (directories.get(path) != null)
                files = directories.get(path);

            files.add(gameFile);
            directories.put(path, files);
        }
    }

    public void clearIndex() {
        index = new ArrayList<>();
    }

    public List<GameFile> getIndex() {
        return index;
    }

    public Map<String, List<GameFile>> getDirectories() {
        return directories;
    }

    public List<GameFile> searchForFiles(String search) {
        return index.stream()
                .filter(gameFile -> search.toLowerCase().startsWith(gameFile.getNameWithoutExtension().toLowerCase()))
                .collect(Collectors.toList());
    }

    // Future use
    public List<GameFile> getDirectory(String path) {
        return directories.get(path);
    }

    public GameFile getGameFileAtPath(String folder, String fileName) {
        for (GameFile gameFile : directories.get(folder)) {
            if (gameFile.getNameWithoutExtension().equals(fileName)) {
                return gameFile;
            }
        }
        return null;
    }

}
