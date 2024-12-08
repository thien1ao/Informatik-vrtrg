
package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class Main {
    static double[] x = new double[1]; // Heading (yaw)
    static double[] y = new double[1]; // Pitch (elevation)

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // Panel to display render results
        JPanel renderPanel = new JPanel() {
            ArrayList<Triangle> tris = new ArrayList<>();

            {
                // Add triangles to the list
                tris.add(new Triangle(new Vertex(100, 100, 100),
                                      new Vertex(-100, -100, 100),
                                      new Vertex(-100, 100, -100),
                                      Color.WHITE));
                tris.add(new Triangle(new Vertex(100, 100, 100),
                                      new Vertex(-100, -100, 100),
                                      new Vertex(100, -100, -100),
                                      Color.RED));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                                      new Vertex(100, -100, -100),
                                      new Vertex(100, 100, 100),
                                      Color.GREEN));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                                      new Vertex(100, -100, -100),
                                      new Vertex(-100, -100, 100),
                                      Color.BLUE));
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Create the heading and pitch transformation matrices
                double heading = Math.toRadians(x[0]);
                double pitch = Math.toRadians(y[0]);

                Matrix3 headingMatrix = Matrix3.createHeadingMatrix(heading);
                Matrix3 pitchMatrix = Matrix3.createPitchMatrix(pitch);
                
                g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.WHITE);
                // Apply transformation and render triangles
                for (Triangle t : tris) {
                    // Apply the rotations to each vertex
                    Vertex v1 = pitchMatrix.multiply(headingMatrix.multiply(t.v1));
                    Vertex v2 = pitchMatrix.multiply(headingMatrix.multiply(t.v2));
                    Vertex v3 = pitchMatrix.multiply(headingMatrix.multiply(t.v3));

                    // Simple orthographic projection (ignoring Z-axis)
                    int[] xPoints = {(int) v1.x, (int) v2.x, (int) v3.x};
                    int[] yPoints = {(int) v1.y, (int) v2.y, (int) v3.y};

                    g2.setColor(t.color);  // Set the triangle color
                    g2.fillPolygon(xPoints, yPoints, 3);  // Draw the triangle
                }
            }
        };

        // Add the renderPanel to the pane
        pane.add(renderPanel, BorderLayout.CENTER);

        // Add MouseMotionListener for dragging the mouse to change rotation
        renderPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double yi = 180.0 / renderPanel.getHeight();  // Scaling factor for y-axis (pitch)
                double xi = 180.0 / renderPanel.getWidth();   // Scaling factor for x-axis (heading)

                // Update the x and y rotation values based on mouse position
                x[0] = e.getX() * xi;
                y[0] = -(e.getY() * yi);  // Negative for flipping y-axis

                // Repaint the panel to apply the new rotation values
                renderPanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Optionally, you can add this for real-time tracking of mouse movement
            }
        });

        // Setup the frame and display the window
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

// Vertex class for 3D points
class Vertex {
    double x, y, z;

    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

// Triangle class to represent a 3D triangle with three vertices and a color
class Triangle {
    Vertex v1, v2, v3;
    Color color;

    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}

// Matrix3 class for 3D rotation matrices
class Matrix3 {
    double[] values;

    Matrix3(double[] values) {
        if (values.length != 9) throw new IllegalArgumentException("Matrix must have 9 values.");
        this.values = values;
    }

    // Multiply this matrix by a vertex (which is a 3D vector)
    public Vertex multiply(Vertex v) {
        double x = values[0] * v.x + values[1] * v.y + values[2] * v.z;
        double y = values[3] * v.x + values[4] * v.y + values[5] * v.z;
        double z = values[6] * v.x + values[7] * v.y + values[8] * v.z;
        return new Vertex(x, y, z);
    }

    // Static method to create a Heading (Yaw) rotation matrix
    public static Matrix3 createHeadingMatrix(double angle) {
        return new Matrix3(new double[]{
                Math.cos(angle), 0, -Math.sin(angle),
                0, 1, 0,
                Math.sin(angle), 0, Math.cos(angle)
        });
    }

    // Static method to create a Pitch (Elevation) rotation matrix
    public static Matrix3 createPitchMatrix(double angle) {
        return new Matrix3(new double[]{
                1, 0, 0,
                0, Math.cos(angle), Math.sin(angle),
                0, -Math.sin(angle), Math.cos(angle)
        });
    }
}