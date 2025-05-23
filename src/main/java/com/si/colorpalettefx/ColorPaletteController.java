package com.si.colorpalettefx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.si.colorpalettefx.model.ColorPalette;
import com.si.colorpalettefx.model.ColorPalette.NamedColor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ColorPaletteController {
    @FXML
    private TabPane paletteTabPane;

    private List<ColorPalette> colorPalettes = new ArrayList<>();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Initialize the tab pane
        paletteTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * Handles the "Add Palette" button click.
     * Opens a dialog to create a new color palette.
     */
    @FXML
    protected void onAddPaletteButtonClick() {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-palette-dialog.fxml"));
            GridPane dialogContent = fxmlLoader.load();

            // Get the controller
            AddPaletteDialogController controller = fxmlLoader.getController();

            // Create the dialog
            Dialog<Pair<String, List<Color>>> dialog = new Dialog<>();
            dialog.setTitle("Add Color Palette");
            dialog.setHeaderText("Create a new color palette");

            // Set the button types
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            // Set the content
            dialog.getDialogPane().setContent(dialogContent);

            // Request focus on the palette name field by default
            Platform.runLater(controller.getPaletteNameField()::requestFocus);

            // Convert the result to a palette when the add button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return controller.getResultAsColors();
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Pair<String, List<Color>>> result = dialog.showAndWait();
            result.ifPresent(nameColors -> {
                String name = nameColors.getKey();
                List<Color> colors = nameColors.getValue();

                if (name != null && !name.trim().isEmpty() && !colors.isEmpty()) {
                    ColorPalette palette = new ColorPalette(name, colors);
                    addPalette(palette);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                            "Invalid Palette", 
                            "Palette must have a name and at least one color.");
                }
            });
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dialog Error", 
                    "Error Loading Dialog", 
                    "An error occurred while loading the dialog: " + e.getMessage());
        }
    }

    /**
     * Adds a color palette to the application and creates a new tab for it.
     *
     * @param palette the color palette to add
     */
    private void addPalette(ColorPalette palette) {
        colorPalettes.add(palette);

        // Create a new tab for the palette
        Tab tab = new Tab(palette.getName());

        // Create a grid pane to display the colors
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(8);

        // Add color squares to the grid
        List<ColorPalette.NamedColor> namedColors = palette.getNamedColors();
        int numCols = (int) Math.ceil(Math.sqrt(namedColors.size())); // Calculate grid dimensions

        for (int i = 0; i < namedColors.size(); i++) {
            ColorPalette.NamedColor namedColor = namedColors.get(i);
            Color color = namedColor.getColor();
            String colorName = namedColor.getName();

            // Create a square for each color
            StackPane colorSquare = new StackPane();
            colorSquare.setStyle("-fx-background-color: #" + toHexString(color) + "; -fx-border-color: lightgrey; -fx-border-width: 5px;");
            colorSquare.setPrefSize(128, 128);

            // Add a tooltip showing the RGB values as hex string
            Tooltip tooltip = new Tooltip(
                String.format("RGB: #%s", toHexString(color))
            );
            Tooltip.install(colorSquare, tooltip);

            // Create a titled pane with the color name as the title
            TitledPane titledPane = new TitledPane(colorName, colorSquare);
            titledPane.setCollapsible(false);

            // Add hover effect to the titled pane
            titledPane.setOnMouseEntered(event -> {
                titledPane.setStyle("-fx-background-color: #e4ade6; -fx-effect: dropshadow(three-pass-box, rgba(183,1,1,0.6), 8, 0, 1, 1);");
            });

            titledPane.setOnMouseExited(event -> {
                titledPane.setStyle("-fx-background-color: transparent;");
            });

            // Create context menu for right-click
            ContextMenu contextMenu = new ContextMenu();

            // Add menu items for copying RGB and name
            MenuItem copyRgbItem = new MenuItem("Copy RGB Value");
            copyRgbItem.setOnAction(event -> {
                String rgbValue = "#" + toHexString(color);
                copyToClipboard(rgbValue);
                showAlert(Alert.AlertType.INFORMATION, "Copied", "RGB Value Copied", 
                        "RGB value " + rgbValue + " has been copied to clipboard.");
            });

            MenuItem copyNameItem = new MenuItem("Copy Color Name");
            copyNameItem.setOnAction(event -> {
                copyToClipboard(colorName);
                showAlert(Alert.AlertType.INFORMATION, "Copied", "Color Name Copied", 
                        "Color name \"" + colorName + "\" has been copied to clipboard.");
            });

            contextMenu.getItems().addAll(copyRgbItem, copyNameItem);

            // Add context menu to the color square
            colorSquare.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(colorSquare, event.getScreenX(), event.getScreenY());
                }
            });

            // Add the titled pane to the grid
            gridPane.add(titledPane, i % numCols, i / numCols);
        }

        // Add the grid to a scroll pane in case there are many colors
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        tab.setContent(scrollPane);
        paletteTabPane.getTabs().add(tab);
        paletteTabPane.getSelectionModel().select(tab);
    }

    /**
     * Converts a JavaFX Color to a hex string.
     *
     * @param color the color to convert
     * @return the hex string representation of the color
     */
    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    /**
     * Handles the "Save Palettes" menu item click.
     * Opens a file chooser dialog to save the color palettes to a JSON file.
     */
    @FXML
    protected void onSavePalettesMenuItemClick() {
        if (colorPalettes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Palettes", 
                    "No Palettes to Save", 
                    "Please add at least one color palette before saving.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Color Palettes");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Get the window from any control in the scene
        Stage stage = (Stage) paletteTabPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(file, colorPalettes);
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", 
                        "Palettes Saved", 
                        "Color palettes were successfully saved to " + file.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Save Error", 
                        "Error Saving Palettes", 
                        "An error occurred while saving the palettes: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the "Load Palettes" menu item click.
     * Opens a file chooser dialog to load color palettes from a JSON file.
     */
    @FXML
    protected void onLoadPalettesMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Color Palettes");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Get the window from any control in the scene
        Stage stage = (Stage) paletteTabPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<ColorPalette> loadedPalettes = mapper.readValue(file, 
                        new TypeReference<List<ColorPalette>>() {});

                if (loadedPalettes != null && !loadedPalettes.isEmpty()) {
                    for (ColorPalette palette : loadedPalettes) {
                        // Check if a palette with this name already exists
                        boolean exists = colorPalettes.stream()
                                .anyMatch(p -> p.getName().equals(palette.getName()));

                        if (!exists) {
                            addPalette(palette);
                        }
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Load Successful", 
                            "Palettes Loaded", 
                            "Color palettes were successfully loaded from " + file.getName());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Palettes", 
                            "No Palettes Found", 
                            "No color palettes were found in the selected file.");
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Load Error", 
                        "Error Loading Palettes", 
                        "An error occurred while loading the palettes: " + e.getMessage());
            }
        }
    }

    /**
     * Shows an alert dialog with the given parameters.
     *
     * @param type the type of alert
     * @param title the title of the alert
     * @param header the header text of the alert
     * @param content the content text of the alert
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text the text to copy to the clipboard
     */
    private void copyToClipboard(String text) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    /**
     * Handles the "Load Legacy Palette" menu item click.
     * Opens a file chooser dialog to load color palettes from a legacy JSON file format.
     */
    @FXML
    protected void onLoadLegacyPalettesMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Legacy Color Palettes");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Get the window from any control in the scene
        Stage stage = (Stage) paletteTabPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                // Create a custom class to handle the legacy format
                List<LegacyColorPalette> loadedPalettes = mapper.readValue(file, 
                        new TypeReference<List<LegacyColorPalette>>() {});

                if (loadedPalettes != null && !loadedPalettes.isEmpty()) {
                    for (LegacyColorPalette legacyPalette : loadedPalettes) {
                        // Convert legacy palette to new format
                        ColorPalette palette = legacyPalette.toColorPalette();

                        // Check if a palette with this name already exists
                        boolean exists = colorPalettes.stream()
                                .anyMatch(p -> p.getName().equals(palette.getName()));

                        if (!exists) {
                            addPalette(palette);
                        }
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Load Successful", 
                            "Legacy Palettes Loaded", 
                            "Legacy color palettes were successfully loaded from " + file.getName());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Palettes", 
                            "No Palettes Found", 
                            "No color palettes were found in the selected file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Load Error", 
                        "Error Loading Legacy Palettes", 
                        "An error occurred while loading the legacy palettes: " + e.getMessage());
            }
        }
    }

    /**
     * A helper class to deserialize the legacy color palette format.
     */
    private static class LegacyColorPalette {
        private String name;
        private List<String> colors;
        private List<String> colorHexCodes;

        // Default constructor for Jackson
        public LegacyColorPalette() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getColors() {
            return colors;
        }

        public void setColors(List<String> colors) {
            this.colors = colors;
        }

        public List<String> getColorHexCodes() {
            return colorHexCodes;
        }

        public void setColorHexCodes(List<String> colorHexCodes) {
            this.colorHexCodes = colorHexCodes;
        }

        /**
         * Converts this legacy palette to the new ColorPalette format.
         * 
         * @return a new ColorPalette with the same name and colors
         */
        public ColorPalette toColorPalette() {
            List<Color> colorList = new ArrayList<>();

            // Use colors if available, otherwise use colorHexCodes
            List<String> hexColors = (colors != null && !colors.isEmpty()) ? colors : colorHexCodes;

            if (hexColors != null) {
                for (String hexColor : hexColors) {
                    try {
                        Color color = Color.web(hexColor);
                        colorList.add(color);
                    } catch (IllegalArgumentException e) {
                        // Skip invalid colors
                        System.err.println("Invalid color format: " + hexColor);
                    }
                }
            }
            return new ColorPalette(name, colorList);
        }
    }

    /**
     * Handles the "Quit" menu item click.
     * Exits the application.
     */
    @FXML
    protected void onQuitMenuItemClick() {
        Platform.exit();
    }

    /**
     * Handles the "Edit Palette" menu item click.
     * Opens a dialog to edit the currently selected color palette.
     */
    @FXML
    protected void onEditPaletteMenuItemClick() {
        // Get the currently selected tab
        Tab selectedTab = paletteTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            showAlert(Alert.AlertType.WARNING, "No Palette Selected", 
                    "No Palette Selected", 
                    "Please select a palette to edit.");
            return;
        }

        // Find the palette with the same name as the selected tab
        String paletteName = selectedTab.getText();
        ColorPalette selectedPalette = null;
        int selectedPaletteIndex = -1;

        for (int i = 0; i < colorPalettes.size(); i++) {
            if (colorPalettes.get(i).getName().equals(paletteName)) {
                selectedPalette = colorPalettes.get(i);
                selectedPaletteIndex = i;
                break;
            }
        }

        if (selectedPalette == null) {
            showAlert(Alert.AlertType.ERROR, "Palette Not Found", 
                    "Palette Not Found", 
                    "The selected palette could not be found.");
            return;
        }

        // Store the index for later use
        final int paletteIndex = selectedPaletteIndex;

        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-palette-dialog.fxml"));
            GridPane dialogContent = fxmlLoader.load();

            // Get the controller
            AddPaletteDialogController controller = fxmlLoader.getController();

            // Set the palette data in the controller
            controller.setPaletteName(selectedPalette.getName());
            controller.setNamedColors(selectedPalette.getNamedColors());

            // Create the dialog
            Dialog<Pair<String, List<Color>>> dialog = new Dialog<>();
            dialog.setTitle("Edit Color Palette");
            dialog.setHeaderText("Edit the color palette");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Set the content
            dialog.getDialogPane().setContent(dialogContent);

            // Request focus on the palette name field by default
            Platform.runLater(controller.getPaletteNameField()::requestFocus);

            // Convert the result to a palette when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return controller.getResultAsColors();
                }
                return null;
            });

            // Create final copies of variables for use in lambda
            final ColorPalette paletteCopy = selectedPalette;
            final Tab tabCopy = selectedTab;

            // Show the dialog and process the result
            Optional<Pair<String, List<Color>>> result = dialog.showAndWait();
            result.ifPresent(nameColors -> {
                String name = nameColors.getKey();
                List<Color> colors = nameColors.getValue();

                if (name != null && !name.trim().isEmpty() && !colors.isEmpty()) {
                    // Check if the name has changed and if it conflicts with an existing palette
                    if (!name.equals(paletteCopy.getName())) {
                        boolean nameExists = colorPalettes.stream()
                                .anyMatch(p -> p.getName().equals(name) && p != paletteCopy);

                        if (nameExists) {
                            showAlert(Alert.AlertType.ERROR, "Duplicate Name", 
                                    "Palette Name Already Exists", 
                                    "A palette with the name '" + name + "' already exists.");
                            return;
                        }
                    }

                    // Update the palette
                    paletteCopy.setName(name);

                    // Get the named colors from the controller
                    List<NamedColor> namedColors = controller.getColorList().getItems();

                    // Update the palette with the named colors
                    paletteCopy.setNamedColors(namedColors);

                    // Update the tab
                    tabCopy.setText(name);

                    // Recreate the tab content
                    paletteTabPane.getTabs().remove(tabCopy);
                    colorPalettes.remove(paletteIndex);
                    addPalette(paletteCopy);

                    showAlert(Alert.AlertType.INFORMATION, "Edit Successful", 
                            "Palette Updated", 
                            "Color palette '" + name + "' was successfully updated.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                            "Invalid Palette", 
                            "Palette must have a name and at least one color.");
                }
            });
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dialog Error", 
                    "Error Loading Dialog", 
                    "An error occurred while loading the dialog: " + e.getMessage());
        }
    }

    /**
     * Handles the "Import Palette" menu item click.
     * Opens a dialog with a text area to import a palette from text.
     */
    @FXML
    protected void onImportPaletteMenuItemClick() {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("import-palette-dialog.fxml"));
            VBox dialogContent = fxmlLoader.load();

            // Get the controller
            ImportPaletteDialogController controller = fxmlLoader.getController();

            // Create the dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Import Color Palette");
            dialog.setHeaderText("Import a color palette from text");

            // Set the button types
            ButtonType importButtonType = new ButtonType("Import", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);

            // Set the content
            dialog.getDialogPane().setContent(dialogContent);

            // Request focus on the text area by default
            Platform.runLater(controller.getPaletteTextArea()::requestFocus);

            // Convert the result to palette data when the import button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == importButtonType) {
                    return controller.getPaletteText();
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(paletteText -> {
                try {
                    // Parse the palette text
                    String[] lines = paletteText.trim().split("\\n");

                    if (lines.length < 2) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                                "Invalid Palette Format", 
                                "Palette must have a name and at least one color.");
                        return;
                    }

                    String name = lines[0].trim();
                    List<Color> colors = new ArrayList<>();

                    // Parse each color line
                    for (int i = 1; i < lines.length; i++) {
                        String colorHex = lines[i].trim();
                        if (colorHex.isEmpty()) {
                            continue; // Skip empty lines
                        }

                        try {
                            Color color = Color.web(colorHex);
                            colors.add(color);
                        } catch (IllegalArgumentException e) {
                            showAlert(Alert.AlertType.ERROR, "Invalid Color", 
                                    "Invalid Color Format", 
                                    "Color '" + colorHex + "' is not a valid hex color. Format should be #RRGGBB.");
                            return;
                        }
                    }

                    if (name.isEmpty() || colors.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                                "Invalid Palette", 
                                "Palette must have a name and at least one color.");
                        return;
                    }

                    // Check if a palette with this name already exists
                    boolean exists = colorPalettes.stream()
                            .anyMatch(p -> p.getName().equals(name));

                    if (exists) {
                        showAlert(Alert.AlertType.ERROR, "Duplicate Name", 
                                "Palette Name Already Exists", 
                                "A palette with the name '" + name + "' already exists.");
                        return;
                    }

                    // Create and add the palette
                    ColorPalette palette = new ColorPalette(name, colors);
                    addPalette(palette);

                    showAlert(Alert.AlertType.INFORMATION, "Import Successful", 
                            "Palette Imported", 
                            "Color palette '" + name + "' was successfully imported with " + colors.size() + " colors.");

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Import Error", 
                            "Error Importing Palette", 
                            "An error occurred while importing the palette: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dialog Error", 
                    "Error Loading Dialog", 
                    "An error occurred while loading the dialog: " + e.getMessage());
        }
    }
}
