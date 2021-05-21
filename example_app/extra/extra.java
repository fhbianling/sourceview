package component;

import dto.RichTextConfig;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import tools.RichTextTool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * author 边凌
 * date 2020/12/7 21:37
 * 类描述：
 */

public class RichTextComponent extends JPanel {
    private WebView webView;
    private Scene scene;
    private final JFXPanel jfxPanel;

    public RichTextComponent() {
        setLayout(new GridLayout(1, 1));
        jfxPanel = new JFXPanel();
        jfxPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(jfxPanel);
        Platform.runLater(() -> {
            if (webView == null) {
                webView = new WebView();
                scene = new Scene(webView);
                jfxPanel.setScene(scene);
            }
            loadContent(cache);
            cache = "";
        });
        if (Platform.isImplicitExit()) {
            Platform.setImplicitExit(false);
        }
    }

    private String cache;

    public String setText(String text, List<RichTextConfig> configs) {
        cache = RichTextTool.INSTANCE.getPreviewHTMLContent(text, configs);
        if (webView == null) {
            return cache;
        }
        loadContent(cache);
        return cache;
    }

    private void loadContent(String content) {
        if (webView != null)
            Platform.runLater(() -> webView.getEngine().loadContent(content));
    }
}
