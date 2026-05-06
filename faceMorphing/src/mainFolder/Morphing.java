package mainFolder;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import delaunay_triangulation.*;

public class Morphing extends JFrame implements ActionListener {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    private static final int    SPACE        = 300;
    private static final double OLD_WIDTH    = 1950.0;
    private static final double OLD_HEIGHT   = 1100.0;
    private static final int    NUM_STEPS    = 49;

    // -------------------------------------------------------------------------
    // Screen / scale
    // -------------------------------------------------------------------------
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double newWidth  = screenSize.getWidth();
    private final double newHeight = screenSize.getHeight();
    private final double width     = newWidth  / OLD_WIDTH;
    private final double height    = newHeight / OLD_HEIGHT;

    // -------------------------------------------------------------------------
    // Image-position constants (derived once)
    // -------------------------------------------------------------------------
    private final int firstImageXStart      = (int)(80.0  * width);
    private final int secondImageXStart     = (int)(1320.0 * width);
    private final int imagesYStart          = (int)(378.0  * height);
    private final int OriginalImageYcoordinate = (int)(340.0 * height);

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------
    private int     firstSelected  = 0, secondSelected = 0, meshSelected = 0;
    private int     numberStep     = 0;
    private int     save1X, save1Y, save2X, save2Y;
    private int     highestTraverse = 0;
    private int     counter        = 0;
    private int     numberPoints   = 53;
    private int     midPoint, XratioTemp, Yratio, Xratio;
    private int     positionAdOn   = (int)(10.0 * width);
    private int     numberCuts;
    private int     smallX, bigX, smallY, bigY;
    private int     lowerX, upperX, Xposition, Yposition;
    private int     Xmin, Xmax, Ymin, Ymax;

    private double  ratio         = 0.5;
    private double  ratioDivisour = 0;
    private double  image1Xmin, image1Xmax, image1Ymin, image1Ymax;
    private double  image2Xmin, image2Xmax, image2Ymin, image2Ymax;

    private double[] image3PointX = new double[1000];
    private double[] image3PointY = new double[1000];

    private int[] image1PointX = new int[SPACE];
    private int[] image1PointY = new int[SPACE];
    private int[] image2PointX = new int[SPACE];
    private int[] image2PointY = new int[SPACE];

    private boolean image1pressed = false, image2pressed = false;
    private boolean morphingTime  = false;
    private boolean firstOutput   = true;

    private boolean[] hasBeenTraversed = new boolean[SPACE];
    private boolean[] has1BeenModified = new boolean[SPACE];
    private boolean[] has2BeenModified = new boolean[SPACE];
    private boolean[] isTheLimit       = new boolean[50];

    private int[][] outputCoordinates = new int[10000][10000];

    // -------------------------------------------------------------------------
    // Colors
    // -------------------------------------------------------------------------
    private final Color alternateGreen  = new Color(83,  221, 89);
    private final Color alternatePurple = new Color(177, 88,  255);
    private final Color panelColor      = new Color(1,   1,   25);
    private final Color[] colors        = new Color[SPACE];

    // -------------------------------------------------------------------------
    // Triangulation
    // -------------------------------------------------------------------------
    private Triangle_dt[] triangleList1 = new Triangle_dt[SPACE];
    private Triangle_dt[] triangleList2 = new Triangle_dt[SPACE];
    private Triangle_dt[] triangleList3 = new Triangle_dt[SPACE];

    // -------------------------------------------------------------------------
    // Images
    // -------------------------------------------------------------------------
    private BufferedImage Image1, Image2, anImage, totalImage, imageOutput;
    private ImageIcon     icon1, icon2;
    private ImageIcon[]   faceImages = new ImageIcon[numberPoints];
    private Graphics      graphics;

    // -------------------------------------------------------------------------
    // GUI components
    // -------------------------------------------------------------------------
    private JPanel  outputPanel = new JPanel();
    private JFrame  displayFrame;
    private JLabel  imageLabel, instructions, label1, label2, lableOut = new JLabel();
    private JSlider slider;
    private JButton button1, button2, mash, next, previous;
    private JButton morphImages, filmStrip, saver;
    private JButton fiveTimes, tenTimes, twentyTimes, stripSaver;
    private JPanel  boarder;
    private JPanel  boarderImage1Vert1 = new JPanel(), boarderImage1Vert2 = new JPanel();
    private JPanel  boarderImage1Hori1 = new JPanel(), boarderImage1Hori2 = new JPanel();
    private JPanel  boarderImage2Vert1 = new JPanel(), boarderImage2Vert2 = new JPanel();
    private JPanel  boarderImage2Hori1 = new JPanel(), boarderImage2Hori2 = new JPanel();

    private final String[] instructionWords = new String[SPACE];

    // =========================================================================
    // Entry point
    // =========================================================================
    public static void main(String[] args) {
        Morphing morph = new Morphing();
        morph.setVisible(true);
    }

    // =========================================================================
    // Constructor
    // =========================================================================
    public Morphing() {
        initInstructionWords();
        initColors();
        initFaceImages();
        initGUI();
    }

    // -------------------------------------------------------------------------
    // Initialisation helpers
    // -------------------------------------------------------------------------
    private void initInstructionWords() {
        String[] words = {
            "Upper left forehead",  "Upper right forehead",
            "Lower left forehead",  "Lower right forehead",
            "Left above ear",       "Right above ear",
            "Left below ear",       "Right below ear",
            "Upper left chin",      "Upper right chin",
            "Mid left chin",        "Mid right chin",
            "Bottom middle chin",   "Centre of left eye",
            "Centre of right eye",  "Upper left eye",
            "Upper right eye",      "Lower left eye",
            "Lower right eye",      "Right of left eye",
            "Left of right eye",    "Left of left eye",
            "Right of right eye",   "Inner left eyebrow",
            "Inner right eyebrow",  "Middle left eyebrow",
            "Middle right eyebrow", "Outer left eyebrow",
            "Outer right eyebrow",  "Centre of upper nose",
            "Center directly below nose", "Left directly below nose",
            "Right directly below nose",  "Left of lip",
            "Right of lip",         "Centre above lip",
            "Center below lip",     "Left of centre below lip",
            "Right of centre below lip",  "Bottom left ear",
            "Bottom right ear",     "Outer left ear",
            "Outer right ear",      "Upper left ear",
            "Upper right ear",      "Left mid-upper head",
            "Right mid-upper head", "Left upper head",
            "Right upper head"
        };
        System.arraycopy(words, 0, instructionWords, 0, words.length);
    }

    private void initColors() {
        Color[] palette = {
            Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.BLUE,
            Color.PINK, alternateGreen, Color.YELLOW, alternatePurple, Color.ORANGE
        };
        for (int i = 0; i < NUM_STEPS; i++) colors[i] = palette[i % palette.length];
    }

    private void initFaceImages() {
        for (int i = 0; i < NUM_STEPS; i++) {
            faceImages[i] = new ImageIcon(
                new ImageIcon(getClass().getResource("/faceDirections/HumanFace" + (i + 1) + ".jpg"))
                    .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)
            );
        }
    }

    private void initGUI() {
        getContentPane().setLayout(null);
        setSize((int) newWidth, (int) newHeight);
        getContentPane().setBackground(panelColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addButtons();
        addFaceSketch();
        addSlider();
        addInstructionsLabel();
        addBorders();
        outputPanel.setBounds((int)(725*width), (int)(340*height), (int)(500*width), (int)(600*height));
        getContentPane().add(outputPanel);
    }

    private void addButtons() {
        button1 = makeButton("Select Image", (int)(firstImageXStart  - 5*width), (int)(300*height), (int)(150*width), (int)(30*height), 12, "Blue");
        button2 = makeButton("Select Image", (int)(secondImageXStart - 5*width), (int)(300*height), (int)(150*width), (int)(30*height), 12, "Blue");

        mash        = makeButton("Begin!",        (int)(firstImageXStart),  (int)(60*height),  (int)(557*width), (int)(200*height), 40, "Blue");
        next        = makeButton("Next",          (int)(secondImageXStart), (int)(60*height),  (int)(557*width), (int)(200*height), 40, "Blue");
        previous    = makeButton("Previous",      (int)(firstImageXStart),  (int)(60*height),  (int)(557*width), (int)(200*height), 40, "Blue");
        morphImages = makeButton("Morph Images",  (int)(secondImageXStart), (int)(60*height),  (int)(557*width), (int)(200*height), 40, "Red");
        saver       = makeButton("Save",          (int)(firstImageXStart), (int)(60*height), (int)(500*width), (int)(120*height), 12, "Blue");
        filmStrip   = makeButton("Film Strip",    (int)(firstImageXStart+500), (int)(60*height), (int)(500*width), (int)(120*height), 12, "Blue");
        
        mash.setVisible(false);
        next.setVisible(false);
        previous.setVisible(false);
        morphImages.setVisible(false);
        filmStrip.setVisible(false);
        saver.setVisible(false);
    }

    private JButton makeButton(String label, int x, int y, int w, int h, int textSize, String color) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, w, h);
        btn.addActionListener(this);
        btn.setFont(new Font("Arial", Font.PLAIN, textSize));
        if (color.equals("Red")) {
        	btn.setBackground(Color.RED); 
        }
        getContentPane().add(btn);
        return btn;
    }

    private void addFaceSketch() {
        ImageIcon faceSketch = new ImageIcon(
            new ImageIcon(getClass().getResource("humanFaceexample.jpg"))
                .getImage().getScaledInstance((int)(150*width), (int)(150*height), Image.SCALE_SMOOTH)
        );
        imageLabel = new JLabel(faceSketch);
        imageLabel.setBounds((int)(915*width), (int)(80*height), (int)(120*width), (int)(150*height));
        getContentPane().add(imageLabel);
    }

    private void addSlider() {
        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setBounds((int)(firstImageXStart), (int)(250*height), (int)(1100*width), (int)(40*height));
        slider.addChangeListener(e -> ratio = ((JSlider) e.getSource()).getValue() / 100.0);
        slider.setVisible(false);
        getContentPane().add(slider);
    }

    private void addInstructionsLabel() {
        instructions = new JLabel();
        instructions.setBounds((int)(835*width), (int)(250*height), (int)(280*width), (int)(20*height));
        getContentPane().add(instructions);

        boarder = new JPanel();
        boarder.setBounds((int)(835*width), (int)(250*height), (int)(280*width), (int)(20*height));
        getContentPane().add(boarder);
    }

    private void addBorders() {
        int x1 = firstImageXStart, x2 = secondImageXStart, y = OriginalImageYcoordinate;
        addImageBorder(boarderImage1Vert1, boarderImage1Vert2, boarderImage1Hori1, boarderImage1Hori2, x1, y);
        addImageBorder(boarderImage2Vert1, boarderImage2Vert2, boarderImage2Hori1, boarderImage2Hori2, x2, y);
    }

    private void addImageBorder(JPanel v1, JPanel v2, JPanel h1, JPanel h2, int x, int y) {
        v1.setBounds((int)(x - 5*width), y, (int)(5*width), (int)(600*height));
        v2.setBounds((int)(x + 557*width), y, (int)(5*width), (int)(600*height));
        h1.setBounds((int)(x - 5*width), (int)(y - 5*height+2), (int)(510*height), (int)(5*width));
        h2.setBounds((int)(x - 5*width), (int)(y + 600*height), (int)(510*height), (int)(5*width));
        getContentPane().add(v1);
        getContentPane().add(v2);
        getContentPane().add(h1);
        getContentPane().add(h2);
    }

    // =========================================================================
    // Action dispatch
    // =========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == button1)    selectMesh(1);
        else if (src == button2)    selectMesh(2);
        else if (src == mash)       reformulateGUI();
        else if (src == next)       next();
        else if (src == previous)   previous();
        else if (src == morphImages)morphImages();
        else if (src == saver)      save(imageOutput);
        else if (src == filmStrip)  createFilmStripPanel();
        else if (src == fiveTimes || src == tenTimes || src == twentyTimes) createStrip(src);
        else if (src == stripSaver) save(totalImage);
    }

    // =========================================================================
    // Image selection
    // =========================================================================
    public void selectMesh(int boxNum) {
        if (meshSelected != 0) return;

        File file = promptFileChooser();
        if (file == null || !isValidJpeg(file)) return;

        try {
            Image picture = ImageIO.read(file);
            ImageIcon icon = scaleIfNeeded(picture);
            picture = icon.getImage();

            JLabel label = createAndPlaceLabel(boxNum, icon);
            updateImageState(boxNum, icon, toBufferedImage(picture));

            int otherSelected = (boxNum == 1) ? secondSelected : firstSelected;
            if (otherSelected == 1) mash.setVisible(true);

            addClickListener(label, boxNum, icon);
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File promptFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        return chooser.getSelectedFile();
    }

    private boolean isValidJpeg(File file) {
        try {
            String type = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            return "image/jpeg".equals(type) || "image/jpg".equals(type);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private ImageIcon scaleIfNeeded(Image picture) {
        ImageIcon icon = new ImageIcon(picture);

        int maxW = (int)(557 * width);
        int maxH = (int)(600 * height);

        int origW = icon.getIconWidth();
        int origH = icon.getIconHeight();

        if (origW <= maxW && origH <= maxH) {
            return icon;
        }
        double scale = Math.min((double) maxW / origW,
                                (double) maxH / origH);

        int sw = (int)(origW * scale);
        int sh = (int)(origH * scale);

        return new ImageIcon(
            picture.getScaledInstance(sw, sh, Image.SCALE_SMOOTH)
        );
    }

    private JLabel createAndPlaceLabel(int boxNum, ImageIcon icon) {
        JLabel existing = (boxNum == 1) ? label1 : label2;
        if (existing != null) {
            getContentPane().remove(existing);
            getContentPane().revalidate();
            getContentPane().repaint();
        }
        int xStart = (boxNum == 1) ? firstImageXStart : secondImageXStart;
        JLabel label = new JLabel(icon);
        label.setBounds(xStart, OriginalImageYcoordinate, icon.getIconWidth(), icon.getIconHeight());
        getContentPane().add(label);
        if (boxNum == 1) label1 = label; else label2 = label;
        return label;
    }

    private void updateImageState(int boxNum, ImageIcon icon, BufferedImage img) {
        int xStart = (boxNum == 1) ? firstImageXStart : secondImageXStart;
        double xMin = xStart, xMax = xStart + icon.getIconWidth();
        double yMin = imagesYStart, yMax = imagesYStart + icon.getIconHeight();
        if (boxNum == 1) {
            image1Xmin = xMin; image1Xmax = xMax; image1Ymin = yMin; image1Ymax = yMax;
            Image1 = img; icon1 = icon; firstSelected = 1;
        } else {
            image2Xmin = xMin; image2Xmax = xMax; image2Ymin = yMin; image2Ymax = yMax;
            Image2 = img; icon2 = icon; secondSelected = 1;
        }
    }

    private void addClickListener(JLabel label, int boxNum, ImageIcon selectedIcon) {
        int xStart = (boxNum == 1) ? firstImageXStart : secondImageXStart;
        label.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (meshSelected != 1) return;
                boolean inBounds =
                    me.getX() + xStart > xStart + (int)(5*width) &&
                    me.getX() + xStart < xStart - (int)(5*width) + selectedIcon.getIconWidth() &&
                    me.getY() + imagesYStart - (int)(3*height) > imagesYStart + (int)(7*height) &&
                    me.getY() + imagesYStart - (int)(3*height) < imagesYStart - (int)(8*height) + selectedIcon.getIconHeight();
                if (!inBounds) return;

                graphics = getGraphics();
                int sx = me.getX() + xStart + 5;
                int sy = me.getY() + imagesYStart - (int)(3*height) + 5;
                if (boxNum == 1) { save1X = sx; save1Y = sy; has1BeenModified[numberStep] = true; image1pressed = true; }
                else             { save2X = sx; save2Y = sy; has2BeenModified[numberStep] = true; image2pressed = true; }
                paintComponent(graphics);

                if (image1pressed && image2pressed) {
                    if (numberStep != 48) next.setVisible(true);
                    else                  morphImages.setVisible(true);
                }
            }
        });
    }

    // =========================================================================
    // Navigation
    // =========================================================================
    public void reformulateGUI() {
        updateImageLabel(0);
        button1.setVisible(false);
        button2.setVisible(false);
        mash.setVisible(false);
        meshSelected = 1;
        numberStep = 0;
        instructions.setText(instructionWords[0]);
    }

    public void next() {
        if (numberStep < 48) updateImageLabel(numberStep + 1);
        previous.setVisible(true);
        saveCurrentPoints();
        if (!hasBeenTraversed[numberStep]) { next.setVisible(false); highestTraverse = numberStep + 1; }
        if (isTheLimit[numberStep]) next.setVisible(false);
        hasBeenTraversed[numberStep] = true;
        advanceStep(1);
    }

    public void previous() {
        updateImageLabel(numberStep - 1);
        previous.setVisible(true);
        next.setVisible(true);
        saveCurrentPoints();
        isTheLimit[numberStep] = !hasBeenTraversed[numberStep + 1];
        advanceStep(-1);
        if (numberStep == 0) previous.setVisible(false);
    }

    private void updateImageLabel(int index) {
        getContentPane().remove(imageLabel);
        imageLabel = new JLabel(faceImages[index]);
        imageLabel.setBounds((int)(915*width), (int)(80*height), (int)(120*width), (int)(150*height));
        getContentPane().add(imageLabel);
    }

    private void saveCurrentPoints() {
        if (has1BeenModified[numberStep]) { image1PointX[numberStep] = save1X; image1PointY[numberStep] = save1Y; }
        if (has2BeenModified[numberStep]) { image2PointX[numberStep] = save2X; image2PointY[numberStep] = save2Y; }
        has1BeenModified[numberStep] = false;
        has2BeenModified[numberStep] = false;
    }

    private void advanceStep(int direction) {
        numberStep += direction;
        instructions.setText(instructionWords[numberStep]);
        image1pressed = false;
        image2pressed = false;
        save1X = save1Y = save2X = save2Y = 0;
    }

    // =========================================================================
    // Morphing
    // =========================================================================
    public void morphImages() {
        previous.setVisible(false);
        filmStrip.setVisible(true);
        instructions.setVisible(false);
        boarder.setVisible(false);
        imageLabel.setVisible(false);
        
        numberPoints = 52;
        morphingTime = true;

        saveCurrentPoints();
        setupCornerPoints();

        int[] b = computeBounds();
        int xMin = b[0], xMax = b[1], yMin = b[2], yMax = b[3];
        midPoint = (int)(975*width) - ((xMax - xMin) / 2 + (int)(image1Xmin*0.5 + image2Xmin*0.5 + 1));
        imageOutput = new BufferedImage(xMax - xMin, yMax - yMin, BufferedImage.TYPE_INT_ARGB);

        Delaunay_Triangulation[] dts = buildTriangulations();
        counter = collectTriangles(dts[2]);
        buildInputTriangleLists(image3PointX, image3PointY);
        renderToImage(imageOutput, xMin, xMax, yMin, yMax, dts[2], firstImageXStart, secondImageXStart);
        displayOutputImage();
    }

    private Delaunay_Triangulation[] buildTriangulations() {
        Delaunay_Triangulation dt1 = new Delaunay_Triangulation();
        Delaunay_Triangulation dt2 = new Delaunay_Triangulation();
        Delaunay_Triangulation dt3 = new Delaunay_Triangulation();
        for (int i = 0; i < numberPoints + 1; i++) {
            dt1.insertPoint(new Point_dt(image1PointX[i], image1PointY[i]));
            dt2.insertPoint(new Point_dt(image2PointX[i], image2PointY[i]));
        }
        for (int i = 0; i < dt1.size(); i++) {
            double x = image1PointX[i] * (1 - ratio) + image2PointX[i] * ratio;
            double y = image1PointY[i] * (1 - ratio) + image2PointY[i] * ratio;
            dt3.insertPoint(new Point_dt(x, y));
            image3PointX[i] = x;
            image3PointY[i] = y;
        }
        return new Delaunay_Triangulation[]{ dt1, dt2, dt3 };
    }

    private void renderToImage(BufferedImage target, int xMin, int xMax, int yMin, int yMax,
                                Delaunay_Triangulation dt3, int offsetX1, int offsetX2) {
        for (int a = xMin; a < xMax; a++) {
            for (int b = yMin; b < yMax; b++) {
                Triangle_dt tri = dt3.find(new Point_dt(a, b));
                if (tri == null || tri.p3() == null) continue;
                int z = findTriangleIndex(tri);
                if (z < 0) continue;
                double[] bary = computeBarycentric(tri, a, b);
                int[] s1 = mapPoint(triangleList1[z], bary);
                int[] s2 = mapPoint(triangleList2[z], bary);
                int c1 = Image1.getRGB(s1[0] - offsetX1, s1[1] - imagesYStart);
                int c2 = Image2.getRGB(s2[0] - offsetX2, s2[1] - imagesYStart);
                target.setRGB(a - xMin, b - yMin, interpolateColor(c1, c2).getRGB());
            }
        }
    }

    // =========================================================================
    // Film strip
    // =========================================================================
    private void createFilmStripPanel() {
        displayFrame = new JFrame();
        displayFrame.setLayout(null);
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        displayFrame.setSize((int) newWidth, (int) newHeight);
        displayFrame.setVisible(true);

        fiveTimes    = makeStripButton("20% Intervals", (int)(10*width),   displayFrame);
        tenTimes     = makeStripButton("10% Intervals", (int)(300*width),  displayFrame);
        twentyTimes  = makeStripButton("5% Intervals",  (int)(590*width),  displayFrame);
        stripSaver   = makeStripButton("Save",          (int)(1500*width), displayFrame);
    }

    private JButton makeStripButton(String label, int x, JFrame frame) {
        JButton btn = new JButton(label);
        btn.setBounds(x, (int)(10*height), (int)(200*width), (int)(50*height));
        btn.addActionListener(this);
        frame.add(btn);
        return btn;
    }

    private void createStrip(Object numberTimes) {
        numberPoints = 52;
        morphingTime = true;
        filmStrip.setVisible(true);
        morphImages.setVisible(true);
        slider.setVisible(true);
        previous.setVisible(false);

        clearBackground();
        configureStripSettings(numberTimes);

        double xSmall = (int)(image1Xmin*0.5 + image2Xmin*0.5 + 1);
        double xBig   = (int)(image1Xmax*0.5 + image2Xmax*0.5 - 1);
        double ySmall = (int)(image1Ymin*0.5 + image2Ymin*0.5 + 1);
        double yBig   = (int)(image1Ymax*0.5 + image2Ymax*0.5 - 1);
        Yratio = (int)(XratioTemp * (yBig - ySmall) / (xBig - xSmall));

        computeImageExtents();
        positionAdOn = 0;
        for (int o = 0; o < numberCuts; o++) {
            ratio = o / ratioDivisour;
            renderStripFrame(o);
        }
    }

    private void clearBackground() {
        Graphics g = displayFrame.getGraphics();
        Color bg = displayFrame.getBackground();
        for (int i = 0; i < newWidth; i++)
            for (int j = 0; j < newHeight; j++)
                paintComponent(i, j, 0, g, bg);
    }

    private void configureStripSettings(Object src) {
        if      (src == fiveTimes)   { XratioTemp = (int)(310*width); numberCuts =  6; ratioDivisour =  5; }
        else if (src == tenTimes)    { XratioTemp = (int)(170*width); numberCuts = 11; ratioDivisour = 10; }
        else                         { XratioTemp = (int)(89*width);  numberCuts = 21; ratioDivisour = 20; }
    }

    private void computeImageExtents() {
        smallX = (image1Xmax - image1Xmin > image2Xmax - image2Xmin) ? (int)image1Xmin : (int)image2Xmin;
        bigX   = (image1Xmax - image1Xmin > image2Xmax - image2Xmin) ? (int)image1Xmax : (int)image2Xmax;
        smallY = (image1Ymax - image1Ymin > image2Ymax - image2Ymin) ? (int)image1Ymin : (int)image2Ymin;
        bigY   = (image1Ymax - image1Ymin > image2Ymax - image2Ymin) ? (int)image1Ymax : (int)image2Ymax;
        lowerX = (int)((image1Xmax + image2Xmin) / 2 - (bigX - smallX) / 2);
        upperX = (int)((image1Xmax + image2Xmin) / 2 + (bigX - smallX) / 2);
        Xposition = (int)(image1Xmin*0.5 + image2Xmin*0.5 + 1);
        Yposition = (int)(image1Ymin*0.5 + image2Ymin*0.5 + 1);
    }

    private void renderStripFrame(int o) {
        setupCornerPoints();
        int[] b = computeBounds();
        int xMin = b[0], xMax = b[1], yMin = b[2], yMax = b[3];
        midPoint = (int)(975*width) - ((xMax - xMin) / 2 + Xposition);

        anImage = new BufferedImage(xMax - xMin, yMax - yMin, BufferedImage.TYPE_INT_ARGB);
        Delaunay_Triangulation[] dts = buildTriangulations();
        counter = collectTriangles(dts[2]);
        buildInputTriangleLists(image3PointX, image3PointY);
        renderToImage(anImage, xMin, xMax, yMin, yMax, dts[2], firstImageXStart, secondImageXStart);

        Xratio = (int)(Yratio * (double)(xMax - xMin) / (yMax - yMin));
        BufferedImage scaled = resize(anImage, Xratio, Yratio);

        if (o == 0) totalImage = new BufferedImage((int)newWidth, Yratio + 1, BufferedImage.TYPE_INT_ARGB);

        Graphics g = displayFrame.getGraphics();
        for (int i = 0; i < scaled.getWidth(); i++) {
            for (int j = 0; j < scaled.getHeight(); j++) {
                int rgb = scaled.getRGB(i, j);
                Color c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
                paintComponent(i, j, positionAdOn, g, c);
                totalImage.setRGB(i + positionAdOn, j, c.getRGB());
            }
        }
        positionAdOn += Xratio;
    }

    // =========================================================================
    // Triangulation helpers
    // =========================================================================
    private void setupCornerPoints() {
        image1PointX[49] = (int)image1Xmin+1; image1PointX[50] = (int)image1Xmax-1;
        image1PointX[51] = (int)image1Xmin+1; image1PointX[52] = (int)image1Xmax-1;
        image1PointY[49] = (int)image1Ymin+1; image1PointY[50] = (int)image1Ymin+1;
        image1PointY[51] = (int)image1Ymax-1; image1PointY[52] = (int)image1Ymax-1;
        image2PointX[49] = (int)image2Xmin+1; image2PointX[50] = (int)image2Xmax-1;
        image2PointX[51] = (int)image2Xmin+1; image2PointX[52] = (int)image2Xmax-1;
        image2PointY[49] = (int)image2Ymin+1; image2PointY[50] = (int)image2Ymin+1;
        image2PointY[51] = (int)image2Ymax-1; image2PointY[52] = (int)image2Ymax-1;
    }

    private int[] computeBounds() {
        return new int[]{
            (int)(image1Xmin*(1-ratio) + image2Xmin*ratio + 1),
            (int)(image1Xmax*(1-ratio) + image2Xmax*ratio - 1),
            (int)(image1Ymin*(1-ratio) + image2Ymin*ratio + 1),
            (int)(image1Ymax*(1-ratio) + image2Ymax*ratio - 1)
        };
    }

    private int collectTriangles(Delaunay_Triangulation dt) {
        Iterator<Triangle_dt> it = dt.trianglesIterator();
        int count = 0;
        while (it.hasNext()) {
            Triangle_dt t = it.next();
            if (!t.isHalfplane()) triangleList3[count++] = t;
        }
        return count;
    }

    private void buildInputTriangleLists(double[] px3, double[] py3) {
        for (int i = 0; i < counter; i++) {
            Point_dt[] p1 = new Point_dt[3], p2 = new Point_dt[3];
            for (int j = 0; j <= numberPoints; j++) {
                Triangle_dt t = triangleList3[i];
                if (matches(t.p1(), px3[j], py3[j])) { p1[0] = img1pt(j); p2[0] = img2pt(j); }
                if (matches(t.p2(), px3[j], py3[j])) { p1[1] = img1pt(j); p2[1] = img2pt(j); }
                if (matches(t.p3(), px3[j], py3[j])) { p1[2] = img1pt(j); p2[2] = img2pt(j); }
            }
            triangleList1[i] = new Triangle_dt(p1[0], p1[1], p1[2]);
            triangleList2[i] = new Triangle_dt(p2[0], p2[1], p2[2]);
        }
    }

    private Point_dt img1pt(int j) { return new Point_dt(image1PointX[j], image1PointY[j]); }
    private Point_dt img2pt(int j) { return new Point_dt(image2PointX[j], image2PointY[j]); }
    private boolean  matches(Point_dt p, double x, double y) { return p.x() == x && p.y() == y; }

    private int findTriangleIndex(Triangle_dt tri) {
        for (int z = 0; z < counter; z++) {
            Triangle_dt t = triangleList3[z];
            if (matches(tri.p1(), t.p1().x(), t.p1().y()) &&
                matches(tri.p2(), t.p2().x(), t.p2().y()) &&
                matches(tri.p3(), t.p3().x(), t.p3().y())) return z;
        }
        return -1;
    }

    private double[] computeBarycentric(Triangle_dt t, double a, double b) {
        double x1 = t.p1().x(), x2 = t.p2().x(), x3 = t.p3().x();
        double y1 = t.p1().y(), y2 = t.p2().y(), y3 = t.p3().y();
        double denom = (y2-y3)*(x1-x3) + (x3-x2)*(y1-y3);
        double b1 = ((y2-y3)*(a-x3) + (x3-x2)*(b-y3)) / denom;
        double b2 = ((y3-y1)*(a-x3) + (x1-x3)*(b-y3)) / denom;
        return new double[]{ b1, b2, 1-b1-b2 };
    }

    private int[] mapPoint(Triangle_dt t, double[] bary) {
        return new int[]{
            (int)(t.p1().x()*bary[0] + t.p2().x()*bary[1] + t.p3().x()*bary[2]),
            (int)(t.p1().y()*bary[0] + t.p2().y()*bary[1] + t.p3().y()*bary[2])
        };
    }

    private Color interpolateColor(int c1, int c2) {
        int r1 = (c1>>16)&0xff, g1 = (c1>>8)&0xff, b1 = c1&0xff;
        int r2 = (c2>>16)&0xff, g2 = (c2>>8)&0xff, b2 = c2&0xff;
        return new Color(
            (int)(r1*(1-ratio) + r2*ratio),
            (int)(g1*(1-ratio) + g2*ratio),
            (int)(b1*(1-ratio) + b2*ratio)
        );
    }

    // =========================================================================
    // Output display & saving
    // =========================================================================
    private void displayOutputImage() {
        if (!firstOutput) { outputPanel.remove(lableOut); outputPanel.revalidate(); outputPanel.repaint(); }
        firstOutput = false;
        lableOut = new JLabel(new ImageIcon(imageOutput));
        outputPanel.add(lableOut);
        slider.setVisible(true);
        saver.setVisible(true);
    }

    private void save(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");

        // Optional: set default file name
        fileChooser.setSelectedFile(new File("saved.png"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure the file has .png extension
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                ImageIO.write(image, "png", fileToSave);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =========================================================================
    // Painting
    // =========================================================================
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        for (int i = 0; i < highestTraverse; i++) {
            if (i != numberStep) {
                g.setColor(colors[i]);
                g.fillOval(image1PointX[i], image1PointY[i], 6, 6);
                g.fillOval(image2PointX[i], image2PointY[i], 6, 6);
            }
        }
        g.setColor(colors[numberStep]);
        g.fillOval(has1BeenModified[numberStep] ? save1X : image1PointX[numberStep],
                   has1BeenModified[numberStep] ? save1Y : image1PointY[numberStep], 6, 6);
        g.fillOval(has2BeenModified[numberStep] ? save2X : image2PointX[numberStep],
                   has2BeenModified[numberStep] ? save2Y : image2PointY[numberStep], 6, 6);

        if (morphingTime) {
            for (int i = 0; i < outputCoordinates.length; i++) {
                for (int j = 0; j < outputCoordinates[i].length; j++) {
                    int rgb = outputCoordinates[i][j];
                    g.setColor(new Color((rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff));
                    g.fillRect(i, j, 1, 1);
                }
            }
        }
        g.dispose();
    }

    public void paintComponent(int a, int b, Graphics g, Color color, int XMIN, int YMIN, int posX, int posY, int pointMid) {
        g.setColor(color);
        g.drawRect(a + posX - XMIN + pointMid, b + posY - YMIN + 10, 1, 1);
    }

    public void paintComponent(int a, int b, int posX, Graphics g, Color color) {
        g.setColor(color);
        g.drawRect(a + posX, (int)(150*height) + b, 1, 1);
    }

    public void paintComponent(int a, int b, int posX, int posY, Graphics g, Color color) {
        g.setColor(color);
        g.drawRect(a + posX, (int)(150*height) + b, 10, 10);
    }

    // =========================================================================
    // Static utilities
    // =========================================================================
    public static BufferedImage resize(BufferedImage img, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        return out;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;
        BufferedImage out = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return out;
    }

    // =========================================================================
    // Inner classes
    // =========================================================================
    static class DoublePair {
        final double x, y;
        DoublePair(double x, double y) { this.x = x; this.y = y; }
    }
}