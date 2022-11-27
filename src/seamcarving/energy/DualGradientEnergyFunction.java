package seamcarving.energy;

import edu.princeton.cs.algs4.Picture;

import java.awt.*;

import static seamcarving.Utils.inBounds;

public class DualGradientEnergyFunction implements EnergyFunction {
    @Override
    public double apply(Picture picture, int x, int y) {
        /* Verify that x and y coordinates are within image bounds */
        if (!inBounds(x, y, picture.width(), picture.height())) {
            throw new IndexOutOfBoundsException("Coordinate (" + x + ", " + y + ") is not within the bounds of the image.");
        }

        double xDerivativeSquared;
        double yDerivativeSquared;

        if (x == 0 || x == picture.width() - 1) {
            int deltaX = x == 0 ? 1 : -1;
            Color color1 = picture.get(x, y);
            Color color2 = picture.get(x + deltaX, y);
            Color color3 = picture.get(x + 2 * deltaX, y);

            xDerivativeSquared = forwardDifference(color1, color2, color3);
        } else {
            Color color1 = picture.get(x - 1, y);
            Color color2 = picture.get(x + 1, y);

            xDerivativeSquared = centeredDifference(color1, color2);
        }

        if (y == 0 || y == picture.height() - 1) {
            int deltaY = y == 0 ? 1 : -1;
            Color color1 = picture.get(x, y);
            Color color2 = picture.get(x, y + deltaY);
            Color color3 = picture.get(x, y + 2 * deltaY);

            yDerivativeSquared = forwardDifference(color1, color2, color3);
        } else {
            Color color1 = picture.get(x, y - 1);
            Color color2 = picture.get(x, y + 1);

            yDerivativeSquared = centeredDifference(color1, color2);
        }

        return Math.sqrt(xDerivativeSquared + yDerivativeSquared);
    }

    private double forwardDifference(Color c1, Color c2, Color c3) {
        return Math.pow(4 * c2.getRed() - c3.getRed() - 3 * c1.getRed(), 2) + Math.pow(4 * c2.getGreen() - c3.getGreen() - 3 * c1.getGreen(), 2) + Math.pow(4 * c2.getBlue() - c3.getBlue() - 3 * c1.getBlue(), 2);
    }

    private double centeredDifference(Color c1, Color c2) {
        return Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2);
    }

}
