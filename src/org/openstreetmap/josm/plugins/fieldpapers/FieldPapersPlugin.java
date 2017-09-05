package org.openstreetmap.josm.plugins.fieldpapers;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Main class for the field papers plugin.
 *
 * @author Ian Dees <ian.dees@gmail.com>
 * @author Frederik Ramm <frederik@remote.org>
 *
 */
public class FieldPapersPlugin extends Plugin
{
    public FieldPapersPlugin(PluginInformation info)
    {
        super(info);

        MainMenu menu = MainApplication.getMenu();
        JMenu fpMenu = menu.addMenu("Field Papers", tr("Field Papers"), KeyEvent.VK_K, menu.getDefaultMenuPos(), ht("/Plugin/FieldPapers"));
        fpMenu.add(new JMenuItem(new FieldPapersAddLayerAction()));
    }
}
