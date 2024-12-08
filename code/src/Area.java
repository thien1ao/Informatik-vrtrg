package src;



public class Area {

    public static void main(String[] args) {

         Point a  = new Point(421, 312);
         Point b = new Point(134, 131);
         Point c = new Point(56,316);

        Area areaobj = new Area();
        double ABC = areaobj.area(a, b, c);
        double BCA = areaobj.area(b, c, a);
        double CAB = areaobj.area(c, b, a);
        
        System.out.println(ABC);
        }

        public double area(Point a, Point b, Point c) {
            return Math.abs((b.x - a.x)*(c.y - a.y) - (b.y -a.y)*(c.x-a.x));
        }

}

class Point {
    double x;
    double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
