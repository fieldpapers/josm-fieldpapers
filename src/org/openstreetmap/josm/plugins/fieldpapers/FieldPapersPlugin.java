package org.openstreetmap.josm.plugins.fieldpapers;

import javax.swing.JMenuItem;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.preferences.StringProperty;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Logging;

/**
 * Main class for the field papers plugin.
 *
 * @author Ian Dees <ian.dees@gmail.com>
 * @author Frederik Ramm <frederik@remote.org>
 *
 */
public class FieldPapersPlugin extends Plugin {
    public static final StringProperty LAST_USED_ID = new StringProperty("fieldpapers.last-used-id", "");

    public static final StringProperty BASE_URL = new StringProperty("fieldpapers.base-url", "http://fieldpapers.org/");

    public FieldPapersPlugin(PluginInformation info) {
        super(info);

        // Migrate old setting, remove this in one of the next versions.
        final String oldValue = Preferences.main().get("fieldpapers-base-url");
        if (!"".equals(oldValue) && !BASE_URL.isSet()) {
            BASE_URL.put(oldValue);
            Preferences.main().put("fieldpapers-base-url", null);
            Logging.info("Preference `fieldpapers-base-url`={0} was renamed to `fieldpapers.base-url`!", oldValue);
        }

        MainApplication.getMenu().imageryMenu.add(new JMenuItem(new FieldPapersAddLayerAction()));
    }
}
