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

import me.fungames.jfortniteparse.fileprovider.DefaultFileProvider;
import me.fungames.jfortniteparse.ue4.FGuid;
import me.fungames.jfortniteparse.ue4.pak.GameFile;
import me.fungames.jfortniteparse.ue4.pak.PakFileReader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PakFileContainer {

    public File pakFolder;
    public DefaultFileProvider provider;

    public PakFileContainer(File pakFolder, String aesKey) {
        this.pakFolder = pakFolder;
        this.provider = new DefaultFileProvider(pakFolder, 1);
        this.provider.submitKey(FGuid.Companion.getMainGuid(), aesKey);
    }

    public List<GameFile> getDirectory(String path) {
        List<GameFile> files = new ArrayList<>();
        try {
            for (GameFile file : provider.getFiles().values()) {
                if (FilenameUtils.getPath(file.getPath()).equals(path))
                    files.add(file);
            }
        } catch (Exception e) { }
        return files;
    }

}
