package item23;

import java.util.Arrays;
import java.util.Collections;

public class Rectangle extends Figure {
    final double length;
    final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }


    @Override
    double area() {
        return length * width;
    }

}
