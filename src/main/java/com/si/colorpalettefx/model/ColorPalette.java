package com.si.colorpalettefx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a color palette with a name and a list of colors.
 */
public class ColorPalette {
    private String name;
    @JsonIgnore
    private List<NamedColor> namedColors;

    /**
     * Represents a color with a name.
     */
    public static class NamedColor {
        private Color color;
        private String name;

        /**
         * Default constructor for Jackson deserialization
         */
        public NamedColor() {
        }

        /**
         * Creates a new named color with the given color and name.
         *
         * @param color the color
         * @param name the name of the color
         */
        public NamedColor(Color color, String name) {
            this.color = color;
            this.name = name;
        }

        /**
         * Returns the color.
         *
         * @return the color
         */
        public Color getColor() {
            return color;
        }

        /**
         * Sets the color.
         *
         * @param color the new color
         */
        public void setColor(Color color) {
            this.color = color;
        }

        /**
         * Returns the name of the color.
         *
         * @return the name of the color
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the color.
         *
         * @param name the new name of the color
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Default constructor for Jackson deserialization
     */
    public ColorPalette() {
        this.namedColors = new ArrayList<>();
    }

    /**
     * Creates a new color palette with the given name and an empty list of colors.
     *
     * @param name the name of the palette
     */
    public ColorPalette(String name) {
        this.name = name;
        this.namedColors = new ArrayList<>();
    }

    /**
     * Creates a new color palette with the given name and list of colors.
     *
     * @param name the name of the palette
     * @param colors the list of colors in the palette
     */
    public ColorPalette(String name, List<Color> colors) {
        this.name = name;
        this.namedColors = new ArrayList<>();
        for (Color color : colors) {
            // Default the color name to its hex code
            String colorName = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
            this.namedColors.add(new NamedColor(color, colorName));
        }
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
        return namedColors.stream()
                .map(NamedColor::getColor)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of named colors in the palette.
     *
     * @return the list of named colors in the palette
     */
    public List<NamedColor> getNamedColors() {
        return new ArrayList<>(namedColors);
    }

    /**
     * Sets the list of colors in the palette.
     *
     * @param colors the new list of colors in the palette
     */
    public void setColors(List<Color> colors) {
        this.namedColors.clear();
        for (Color color : colors) {
            // Default the color name to its hex code
            String colorName = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
            this.namedColors.add(new NamedColor(color, colorName));
        }
    }

    /**
     * Sets the list of named colors in the palette.
     *
     * @param namedColors the new list of named colors in the palette
     */
    public void setNamedColors(List<NamedColor> namedColors) {
        this.namedColors.clear();
        this.namedColors.addAll(namedColors);
    }

    /**
     * Adds a color to the palette.
     *
     * @param color the color to add
     */
    public void addColor(Color color) {
        // Default the color name to its hex code
        String colorName = String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
        namedColors.add(new NamedColor(color, colorName));
    }

    /**
     * Adds a named color to the palette.
     *
     * @param color the color to add
     * @param name the name of the color
     */
    public void addColor(Color color, String name) {
        namedColors.add(new NamedColor(color, name));
    }

    /**
     * Removes a color from the palette.
     *
     * @param color the color to remove
     * @return true if the color was removed, false otherwise
     */
    public boolean removeColor(Color color) {
        return namedColors.removeIf(nc -> nc.getColor().equals(color));
    }

    /**
     * Returns the number of colors in the palette.
     *
     * @return the number of colors in the palette
     */
    public int size() {
        return namedColors.size();
    }

    /**
     * Returns the list of color data for JSON serialization.
     *
     * @return the list of color data
     */
    @JsonProperty("colors")
    public List<Map<String, String>> getColorData() {
        return namedColors.stream()
                .map(nc -> {
                    Map<String, String> colorData = new HashMap<>();
                    colorData.put("name", nc.getName());
                    colorData.put("hex", toHexString(nc.getColor()));
                    return colorData;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sets the colors from a list of color data for JSON deserialization.
     *
     * @param colorData the list of color data
     */
    @JsonProperty("colors")
    public void setColorData(List<Map<String, String>> colorData) {
        this.namedColors.clear();
        if (colorData != null) {
            colorData.forEach(data -> {
                String hex = data.get("hex");
                String name = data.get("name");
                if (hex != null) {
                    Color color = Color.web(hex);
                    // If name is null, use hex as the name
                    this.namedColors.add(new NamedColor(color, name != null ? name : hex));
                }
            });
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
