package com.si.colorpalettefx;

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
    private ListView<Color> colorList;

    @FXML
    private Button addColorButton;

    @FXML
    private Button removeColorButton;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Set default color
        colorPicker.setValue(Color.RED);

        // Set up the cell factory for the color list
        colorList.setCellFactory(param -> new ListCell<Color>() {
            private final StackPane colorRect = new StackPane();

            {
                colorRect.setPrefSize(50, 20);
                setGraphic(colorRect);
            }

            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Set the color of the rectangle
                    colorRect.setStyle("-fx-background-color: #" + toHexString(item) + ";");
                    setGraphic(colorRect);

                    // Set the text to the hex code
                    setText("#" + toHexString(item));
                }
            }

            private String toHexString(Color color) {
                return String.format("%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
            }
        });
    }

    /**
     * Handles the "Add Color" button click.
     * Adds the selected color to the list.
     */
    @FXML
    protected void onAddColorButtonClick() {
        Color selectedColor = colorPicker.getValue();
        colorList.getItems().add(selectedColor);
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
        }
    }

    /**
     * Gets the palette name and colors from the dialog.
     * 
     * @return a pair containing the palette name and list of colors
     */
    public Pair<String, List<Color>> getResult() {
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
    public ListView<Color> getColorList() {
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
     * @param colors the list of colors to set
     */
    public void setColors(List<Color> colors) {
        colorList.getItems().clear();
        colorList.getItems().addAll(colors);
    }
}
