package item23;

public class Circle extends Figure {
    final double radius;

    public Circle(double radius1) {
        this.radius = radius1;
    }


    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}
