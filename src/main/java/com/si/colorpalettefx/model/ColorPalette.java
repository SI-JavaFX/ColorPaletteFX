package com.si.colorpalettefx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a color palette with a name and a list of colors.
 */
public class ColorPalette {
    private String name;
    @JsonIgnore
    private List<Color> colors;

    /**
     * Default constructor for Jackson deserialization
     */
    public ColorPalette() {
        this.colors = new ArrayList<>();
    }

    /**
     * Creates a new color palette with the given name and an empty list of colors.
     *
     * @param name the name of the palette
     */
    public ColorPalette(String name) {
        this.name = name;
        this.colors = new ArrayList<>();
    }

    /**
     * Creates a new color palette with the given name and list of colors.
     *
     * @param name the name of the palette
     * @param colors the list of colors in the palette
     */
    public ColorPalette(String name, List<Color> colors) {
        this.name = name;
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Returns the name of the palette.
     *
     * @return the name of the palette
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the palette.
     *
     * @param name the new name of the palette
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of colors in the palette.
     *
     * @return the list of colors in the palette
     */
    public List<Color> getColors() {
        return new ArrayList<>(colors);
    }

    /**
     * Sets the list of colors in the palette.
     *
     * @param colors the new list of colors in the palette
     */
    public void setColors(List<Color> colors) {
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Adds a color to the palette.
     *
     * @param color the color to add
     */
    public void addColor(Color color) {
        colors.add(color);
    }

    /**
     * Removes a color from the palette.
     *
     * @param color the color to remove
     * @return true if the color was removed, false otherwise
     */
    public boolean removeColor(Color color) {
        return colors.remove(color);
    }

    /**
     * Returns the number of colors in the palette.
     *
     * @return the number of colors in the palette
     */
    public int size() {
        return colors.size();
    }

    /**
     * Returns the list of color hex codes for JSON serialization.
     *
     * @return the list of color hex codes
     */
    @JsonProperty("colorHexCodes")
    public List<String> getColorHexCodes() {
        return colors.stream()
                .map(this::toHexString)
                .collect(Collectors.toList());
    }

    /**
     * Sets the colors from a list of hex codes for JSON deserialization.
     *
     * @param hexCodes the list of color hex codes
     */
    @JsonProperty("colorHexCodes")
    public void setColorHexCodes(List<String> hexCodes) {
        this.colors.clear();
        if (hexCodes != null) {
            hexCodes.forEach(hex -> this.colors.add(Color.web(hex)));
        }
    }

    /**
     * Converts a JavaFX Color to a hex string.
     *
     * @param color the color to convert
     * @return the hex string representation of the color
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
