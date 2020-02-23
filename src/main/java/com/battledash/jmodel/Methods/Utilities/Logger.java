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

import javafx.scene.control.TextArea;

public class Logger {

    private TextArea logArea;

    public Logger(TextArea textArea) {
        this.logArea = textArea;
    }

    public void info(Object log) {
        String logs = this.logArea.getText();
        this.logArea.setText("[INFO] " + log + "\n" + logs);
    }

    public void debug(Object log) {
        String logs = this.logArea.getText();
        this.logArea.setText("[DEBUG] " + log + "\n" + logs);
    }

    public void warn(Object log) {
        String logs = this.logArea.getText();
        this.logArea.setText("[WARNING] " + log + "\n" + logs);
    }

    public void error(Object log) {
        String logs = this.logArea.getText();
        this.logArea.setText("[ERROR] " + log + "\n" + logs);
    }

}
