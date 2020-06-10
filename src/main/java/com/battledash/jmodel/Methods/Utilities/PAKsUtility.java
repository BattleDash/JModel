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

import com.battledash.jmodel.Graphical.Popups.AlertBox;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class PAKsUtility {

    public static File getGameFilesLocation() {
        File installLocationData = new File("C:\\ProgramData\\Epic\\UnrealEngineLauncher\\LauncherInstalled.dat");
        if (installLocationData.exists()) {
            Gson gson =  new Gson();
            try (Reader reader = new FileReader(installLocationData)) {
                LauncherInstalled launcherInstalled = gson.fromJson(reader, LauncherInstalled.class);
                for (LauncherInstalled.InstallationData installationData : launcherInstalled.InstallationList) {
                    if (installationData.AppName.equals("Fortnite")) {
                        return new File(installationData.InstallLocation + "\\FortniteGame\\Content\\Paks");
                    } else {
						return new File("C:\\Program Files\\Epic Games\\Fortnite\\fn\\FortniteGame\\Content\\Paks");
					}
                }
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.display("Error", "There was an error parsing install location data!");
            }
        } else {
            AlertBox.display("Error", "You do not have Fortnite installed!");
        }
        return null;

    }

    private class LauncherInstalled {
        private List<LauncherInstalled.InstallationData> InstallationList;
        class InstallationData {
            private String InstallLocation;
            private String AppName;
            private String AppVersion;
        }
    }

}
