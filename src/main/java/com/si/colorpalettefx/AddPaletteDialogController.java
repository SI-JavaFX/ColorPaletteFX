package com.si.colorpalettefx;

import com.si.colorpalettefx.model.ColorPalette;
import com.si.colorpalettefx.model.ColorPalette.NamedColor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Add/Edit Palette dialog.
 */
public class AddPaletteDialogController {
    @FXML
    private GridPane dialogContent;

    @FXML
    private TextField paletteName;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField colorName;

    @FXML
    private ListView<NamedColor> colorList;

    @FXML
    private Button addColorButton;

    @FXML
    private Button removeColorButton;

    // Track the currently selected color index for editing
    private int selectedColorIndex = -1;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Set default color
        colorPicker.setValue(Color.RED);

        // Set up the cell factory for the color list
        colorList.setCellFactory(param -> new ListCell<NamedColor>() {
            private final StackPane colorRect = new StackPane();

            {
                colorRect.setPrefSize(50, 20);
                setGraphic(colorRect);
            }

            @Override
            protected void updateItem(NamedColor item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Set the color of the rectangle
                    colorRect.setStyle("-fx-background-color: #" + toHexString(item.getColor()) + ";");
                    setGraphic(colorRect);

                    // Set the text to the color name
                    setText(item.getName());
                }
            }

            private String toHexString(Color color) {
                return String.format("%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
            }
        });

        // Add a selection listener to the color list
        colorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update the color picker and name field with the selected color's values
                colorPicker.setValue(newValue.getColor());
                colorName.setText(newValue.getName());

                // Update the button text and store the selected index
                selectedColorIndex = colorList.getSelectionModel().getSelectedIndex();
                addColorButton.setText("Update Color");
            } else {
                // Reset to add mode if nothing is selected
                selectedColorIndex = -1;
                addColorButton.setText("Add Color");
            }
        });
    }

    /**
     * Handles the "Add Color" or "Update Color" button click.
     * Adds a new color to the list or updates an existing one.
     */
    @FXML
    protected void onAddColorButtonClick() {
        Color selectedColor = colorPicker.getValue();
        String name = colorName.getText().trim();

        // If no name is provided, use the hex code as the name
        if (name.isEmpty()) {
            name = String.format("#%02X%02X%02X",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));
        }

        if (selectedColorIndex >= 0) {
            // Update existing color
            NamedColor namedColor = colorList.getItems().get(selectedColorIndex);
            namedColor.setColor(selectedColor);
            namedColor.setName(name);

            // Refresh the list view
            colorList.refresh();

            // Reset selection and button text
            colorList.getSelectionModel().clearSelection();
            selectedColorIndex = -1;
            addColorButton.setText("Add Color");
        } else {
            // Add new color
            colorList.getItems().add(new NamedColor(selectedColor, name));
        }

        // Clear the color name field for the next color
        colorName.clear();
    }

    /**
     * Handles the "Remove Selected" button click.
     * Removes the selected color from the list.
     */
    @FXML
    protected void onRemoveColorButtonClick() {
        int selectedIndex = colorList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            colorList.getItems().remove(selectedIndex);

            // Reset selection state and button text
            selectedColorIndex = -1;
            addColorButton.setText("Add Color");
            colorName.clear();
        }
    }

    /**
     * Gets the palette name and colors from the dialog.
     * 
     * @return a pair containing the palette name and list of colors
     */
    public Pair<String, List<NamedColor>> getResult() {
        return new Pair<>(paletteName.getText(), new ArrayList<>(colorList.getItems()));
    }

    /**
     * Gets the palette name and colors from the dialog, converting NamedColor to Color.
     * 
     * @return a pair containing the palette name and list of colors
     */
    public Pair<String, List<Color>> getResultAsColors() {
        List<Color> colors = colorList.getItems().stream()
                .map(NamedColor::getColor)
                .collect(Collectors.toList());
        return new Pair<>(paletteName.getText(), colors);
    }

    /**
     * Gets the palette name and named colors from the dialog.
     * 
     * @return a pair containing the palette name and list of named colors
     */
    public Pair<String, List<NamedColor>> getResultAsNamedColors() {
        return new Pair<>(paletteName.getText(), new ArrayList<>(colorList.getItems()));
    }

    /**
     * Gets the root node of the dialog.
     * 
     * @return the root GridPane
     */
    public GridPane getDialogContent() {
        return dialogContent;
    }

    /**
     * Gets the palette name field.
     * 
     * @return the palette name TextField
     */
    public TextField getPaletteNameField() {
        return paletteName;
    }

    /**
     * Gets the color list.
     * 
     * @return the color ListView
     */
    public ListView<NamedColor> getColorList() {
        return colorList;
    }

    /**
     * Sets the palette name.
     * 
     * @param name the palette name to set
     */
    public void setPaletteName(String name) {
        paletteName.setText(name);
    }

    /**
     * Sets the colors in the color list.
     * 
     * @param namedColors the list of named colors to set
     */
    public void setNamedColors(List<NamedColor> namedColors) {
        colorList.getItems().clear();
        colorList.getItems().addAll(namedColors);
    }

    /**
     * Sets the colors in the color list.
     * 
     * @param colors the list of colors to set
     */
    public void setColors(List<Color> colors) {
        colorList.getItems().clear();
        for (Color color : colors) {
            // Default the color name to its hex code
            String colorName = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
            colorList.getItems().add(new NamedColor(color, colorName));
        }
    }
}
