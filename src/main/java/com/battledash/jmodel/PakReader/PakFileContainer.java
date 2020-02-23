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

import me.fungames.jfortniteparse.fileprovider.DefaultFileProvider;

import java.io.File;

public class PakFileContainer {

    public File pakFolder;
    public DefaultFileProvider provider;

    public PakFileContainer(File pakFolder, String aesKey) {
        this.pakFolder = pakFolder;
        this.provider = new DefaultFileProvider(pakFolder, 1);
        this.provider.submitKey(this.provider.requiredKeys().get(0), aesKey);
    }

}
