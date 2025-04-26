package com.si.colorpalettefx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Taskbar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ColorPaletteApplication extends Application {
    // Store initial mouse position for window dragging
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        // Set stage style to undecorated
        stage.initStyle(StageStyle.UNDECORATED);

        // Load the main content
        FXMLLoader fxmlLoader = new FXMLLoader(ColorPaletteApplication.class.getResource("color-palette-view.fxml"));
        BorderPane mainContent = fxmlLoader.load();

        // Create custom titlebar
        HBox titleBar = createTitleBar(stage);

        // Create root container
        BorderPane root = new BorderPane();
        root.setTop(titleBar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);

        // Set application icon for the window
        Image appIcon = new Image(getClass().getResourceAsStream("/images/appicon.png"));
        stage.getIcons().add(appIcon);

        // Set dock icon for macOS
        if (System.getProperty("os.name").toLowerCase().contains("mac") && Taskbar.isTaskbarSupported()) {
            try {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream("/images/appicon.png"));
                    taskbar.setIconImage(bufferedImage);
                }
            } catch (Exception e) {
                System.err.println("Error setting macOS dock icon: " + e.getMessage());
            }
        }

        stage.show();
    }

    /**
     * Creates a custom titlebar with the specified height.
     *
     * @param stage the primary stage
     * @return the titlebar as an HBox
     */
    private HBox createTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.setPrefHeight(64); // Set titlebar height to 64 pixels
        titleBar.setStyle("-fx-background-color: #f0f0f0;");
        titleBar.setPadding(new Insets(0, 10, 0, 10));

        // App icon
        Image appIcon = new Image(getClass().getResourceAsStream("/images/appicon.png"));
        ImageView appIconView = new ImageView(appIcon);
        appIconView.setFitWidth(64);
        appIconView.setFitHeight(64);
        appIconView.setPreserveRatio(true);

        // Title label
        Label title = new Label("Color Palette Viewer");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        title.setPadding(new Insets(0, 0, 0, 5));

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Window controls
        Button minimizeBtn = new Button("_");
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        Button maximizeBtn = new Button("□");
        maximizeBtn.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });

        Button closeBtn = new Button("✕");
        closeBtn.setOnAction(e -> Platform.exit());

        // Style buttons
        String buttonStyle = "-fx-background-color: transparent; -fx-font-weight: bold;";
        minimizeBtn.setStyle(buttonStyle);
        maximizeBtn.setStyle(buttonStyle);
        closeBtn.setStyle(buttonStyle + "-fx-text-fill: #E81123;");

        // Add all elements to titlebar
        titleBar.getChildren().addAll(appIconView, title, spacer, minimizeBtn, maximizeBtn, closeBtn);

        // Make the titlebar draggable to move the window
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        return titleBar;
    }

    public static void main(String[] args) {
        launch();
    }
}
