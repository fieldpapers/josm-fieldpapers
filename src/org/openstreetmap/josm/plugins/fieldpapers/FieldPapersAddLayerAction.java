package org.openstreetmap.josm.plugins.fieldpapers;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.widgets.JosmTextField;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.HttpClient;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Utils;

@SuppressWarnings("serial")
public class FieldPapersAddLayerAction extends JosmAction {

    public FieldPapersAddLayerAction() {
        super(tr("Scanned Map..."), "fieldpapers",
            tr("Display a map that was previously scanned and uploaded to fieldpapers.org"), null, false);
    }

    public void actionPerformed(ActionEvent e) {
        JPanel panel = new JPanel(new GridBagLayout());

        // snapshot URL selection
        panel.add(new JLabel(tr("Enter a fieldpapers.org snapshot URL to download from")), GBC.eol());
        JosmTextField snapshotAddress = new JosmTextField(Main.pref.get("fieldpapers.last-used-id"));
        snapshotAddress.setToolTipText(tr("Enter an URL from where scanned map should be downloaded"));
        panel.add(snapshotAddress, GBC.eop().fill(GBC.BOTH));

        ExtendedDialog dialog = new ExtendedDialog(Main.parent,
                tr("Download Snapshot"),
                tr("Download"), tr("Cancel"))
            .setContent(panel, false)
            .setButtonIcons("download", "cancel")
            .setToolTipTexts(
                tr("Start downloading scanned map"),
                tr("Close dialog and cancel downloading"));

        if (dialog.showDialog().getValue() == 1) {
            openUrl(Utils.strip(snapshotAddress.getText()));
        }
    }

    public void openUrl(String url) {
        if (url == null || url.equals("")) return;

        if (!url.startsWith("http")) {
            url = Main.pref.get("fieldpapers-base-url", "http://fieldpapers.org/") + "snapshots/" + url;
        }

        try {
            // fetch metadata
            JsonObject metadata = getMetadata(url);

            String tileJsonUrl = metadata.getJsonString("tilejson_url").getString();
            String id = metadata.getJsonString("id").getString();

            // fetch TileJSON
            JsonObject tileJson = getTileJson(tileJsonUrl);

            String tileUrl = tileJson.getJsonArray("tiles").getString(0);
            int minZoom = tileJson.getJsonNumber("minzoom").intValue();
            int maxZoom = tileJson.getJsonNumber("maxzoom").intValue();

            JsonArray bounds = tileJson.getJsonArray("bounds");
            double west = bounds.getJsonNumber(0).doubleValue();
            double south = bounds.getJsonNumber(1).doubleValue();
            double east = bounds.getJsonNumber(2).doubleValue();
            double north = bounds.getJsonNumber(3).doubleValue();

            // save this atlas as the
            Main.pref.put("fieldpapers.last-used-id", id);

            Bounds b = new Bounds(new LatLon(south, west), new LatLon(north, east));

            FieldPapersLayer wpl = new FieldPapersLayer(id, tileUrl, b, minZoom, maxZoom);
            MainApplication.getLayerManager().addLayer(wpl);

        } catch (IOException ex) {
            Logging.error(ex);
            JOptionPane.showMessageDialog(Main.parent,tr("Could not read information for the id \"{0}\" from fieldpapers.org", url));
        }
    }

    private JsonObject getMetadata(String snapshotUrl) throws IOException {
        try (
            InputStream is = HttpClient.create(new URL(snapshotUrl)).setAccept("application/json").connect().getContent();
            JsonReader reader = Json.createReader(is)
        ) {
            return reader.readObject();
        } catch (JsonParsingException e) {
            throw new IOException(e);
        }
    }

    private JsonObject getTileJson(String tileJsonUrl) throws IOException {
        InputStream is = null;
        JsonReader reader = null;

        try {
            URL url = new URL(tileJsonUrl);

            is = url.openStream();
            reader = Json.createReader(is);

            return reader.readObject();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException err) {
                    // Ignore
                }
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}
