package org.openstreetmap.josm.plugins.fieldpapers;

import javax.swing.JMenuItem;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Main class for the field papers plugin.
 *
 * @author Ian Dees <ian.dees@gmail.com>
 * @author Frederik Ramm <frederik@remote.org>
 *
 */
public class FieldPapersPlugin extends Plugin {
    public FieldPapersPlugin(PluginInformation info) {
        super(info);
        MainApplication.getMenu().imageryMenu.add(new JMenuItem(new FieldPapersAddLayerAction()));
    }
}
