package com.si.colorpalettefx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Controller for the Import Palette dialog.
 */
public class ImportPaletteDialogController {
    @FXML
    private VBox dialogContent;
    
    @FXML
    private TextArea paletteTextArea;
    
    @FXML
    private Label exampleLabel;
    
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // No initialization needed
    }
    
    /**
     * Gets the palette text from the dialog.
     * 
     * @return the palette text
     */
    public String getPaletteText() {
        return paletteTextArea.getText();
    }
    
    /**
     * Gets the root node of the dialog.
     * 
     * @return the root VBox
     */
    public VBox getDialogContent() {
        return dialogContent;
    }
    
    /**
     * Gets the palette text area.
     * 
     * @return the palette TextArea
     */
    public TextArea getPaletteTextArea() {
        return paletteTextArea;
    }
}