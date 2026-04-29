package mainFolder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

public class guibondbond extends JFrame{
	
	private JPanel panel;
	private JLabel label1;
	private JLabel label2;
	private Graphics graphics;
	Delaunay_Triangulation dt3 = new Delaunay_Triangulation();
	Triangle_dt[] triangleList1 = new Triangle_dt[1000];
	Triangle_dt[] triangleList2 = new Triangle_dt[1000];
	Triangle_dt[] triangleList3 = new Triangle_dt[1000];
	BufferedImage Image1 = null;
	BufferedImage Image2 = null;
	int counter = 0;
	int[] xCoords = new int[10000];
	int[] yCoords = new int[10000];
	Color[] colors = new Color[10000];
	int[] sourceCoords1X = new int[10000];
	int numberPoints = 43;
	int image1Xmin = 0;
	int image1Xmax = 0;
	int image1Ymin = 0;
	int image1Ymax = 0;
	int image2Xmin = 0;
	int image2Xmax = 0;
	int image2Ymin = 0;
	int image2Ymax = 0;

	public guibondbond(){
		
		getContentPane().setLayout(null);
		setSize(600, 500);
		getContentPane().setBackground(new Color(51, 102, 153));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final int[] image1PointX = new int[numberPoints];
		final int[] image1PointY = new int[numberPoints];
		final int[] image2PointX = new int[numberPoints];
		final int[] image2PointY = new int[numberPoints];
		
		image1PointX[0] = 314;
		image1PointX[1] = 209;
		image1PointX[2] = 173;
		image1PointX[3] = 358;
		image1PointX[4] = 169;
		image1PointX[5] = 363;
		image1PointX[6] = 181;
		image1PointX[7] = 360;
		image1PointX[8] = 217;
		image1PointX[9] = 320;
		image1PointX[10] = 268;
		image1PointX[11] = 264;
		image1PointX[12] = 250;
		image1PointX[13] = 276;
		image1PointX[14] = 219;
		image1PointX[15] = 307;
		image1PointX[16] = 179;
		image1PointX[17] = 348;
		image1PointX[18] = 219;
		image1PointX[19] = 306;
		image1PointX[20] = 266;
		image1PointX[21] = 247;
		image1PointX[22] = 280;
		image1PointX[23] = 266;
		image1PointX[24] = 230;
		image1PointX[25] = 301;
		image1PointX[26] = 265;
		image1PointX[27] = 245;
		image1PointX[28] = 287;
		image1PointX[29] = 174;
		image1PointX[30] = 149;
		image1PointX[31] = 142;
		image1PointX[32] = 370;
		image1PointX[33] = 395;
		image1PointX[34] = 400;
		image1PointX[35] = 149;
		image1PointX[36] = 214;
		image1PointX[37] = 296;
		image1PointX[38] = 372;
		image1PointX[39] = 40;
		image1PointX[40] = 512;
		image1PointX[41] = 40;
		image1PointX[42] = 512;
		
		image1PointY[0] = 108;
		image1PointY[1] = 116;
		image1PointY[2] = 177;
		image1PointY[3] = 169;
		image1PointY[4] = 236;
		image1PointY[5] = 220;
		image1PointY[6] = 317;
		image1PointY[7] = 312;
		image1PointY[8] = 369;
		image1PointY[9] = 368;
		image1PointY[10] = 384;
		image1PointY[11] = 218;
		image1PointY[12] = 215;
		image1PointY[13] = 207;
		image1PointY[14] = 200;
		image1PointY[15] = 190;
		image1PointY[16] = 216;
		image1PointY[17] = 213;
		image1PointY[18] = 220;
		image1PointY[19] = 215;
		image1PointY[20] = 285;
		image1PointY[21] = 284;
		image1PointY[22] = 283;
		image1PointY[23] = 317;
		image1PointY[24] = 324;
		image1PointY[25] = 320;
		image1PointY[26] = 338;
		image1PointY[27] = 337;
		image1PointY[28] = 336;
		image1PointY[29] = 315;
		image1PointY[30] = 277;
		image1PointY[31] = 232;
		image1PointY[32] = 302;
		image1PointY[33] = 269;
		image1PointY[34] = 217;
		image1PointY[35] = 152;
		image1PointY[36] = 87;
		image1PointY[37] = 83;
		image1PointY[38] = 136;
		image1PointY[39] = 40;
		image1PointY[40] = 40;
		image1PointY[41] = 512;
		image1PointY[42] = 512;
		
		/*image2PointX[0] = 989;
		image2PointX[1] = 867;
		image2PointX[2] = 810;
		image2PointX[3] = 1042;
		image2PointX[4] = 797;
		image2PointX[5] = 1052;
		image2PointX[6] = 810;
		image2PointX[7] = 1043;
		image2PointX[8] = 857;
		image2PointX[9] = 998;
		image2PointX[10] = 937;
		image2PointX[11] = 929;
		image2PointX[12] = 901;
		image2PointX[13] = 956;
		image2PointX[14] = 863;
		image2PointX[15] = 997;
		image2PointX[16] = 826;
		image2PointX[17] = 1030;
		image2PointX[18] = 874;
		image2PointX[19] = 987;
		image2PointX[20] = 929;
		image2PointX[21] = 892;
		image2PointX[22] = 962;
		image2PointX[23] = 927;
		image2PointX[24] = 863;
		image2PointX[25] = 993;
		image2PointX[26] = 930;
		image2PointX[27] = 899;
		image2PointX[28] = 958;
		image2PointX[29] = 789;
		image2PointX[30] = 763;
		image2PointX[31] = 765;
		image2PointX[32] = 1055;
		image2PointX[33] = 1079;
		image2PointX[34] = 1083;
		image2PointX[35] = 790;
		image2PointX[36] = 864;
		image2PointX[37] = 975;
		image2PointX[38] = 1063;
		image2PointX[39] = 440;
		image2PointX[40] = 1200;
		image2PointX[41] = 440;
		image2PointX[42] = 1200;
		
		image2PointY[0] = 70;
		image2PointY[1] = 65;
		image2PointY[2] = 135;
		image2PointY[3] = 140;
		image2PointY[4] = 201;
		image2PointY[5] = 191;
		image2PointY[6] = 289;
		image2PointY[7] = 299;
		image2PointY[8] = 392;
		image2PointY[9] = 389;
		image2PointY[10] = 429;
		image2PointY[11] = 200;
		image2PointY[12] = 178;
		image2PointY[13] = 175;
		image2PointY[14] = 165;
		image2PointY[15] = 162;
		image2PointY[16] = 187;
		image2PointY[17] = 184;
		image2PointY[18] = 196;
		image2PointY[19] = 194;
		image2PointY[20] = 282;
		image2PointY[21] = 272;
		image2PointY[22] = 273;
		image2PointY[23] = 299;
		image2PointY[24] = 313;
		image2PointY[25] = 313;
		image2PointY[26] = 354;
		image2PointY[27] = 346;
		image2PointY[28] = 345;
		image2PointY[29] = 286;
		image2PointY[30] = 237;
		image2PointY[31] = 176;
		image2PointY[32] = 283;
		image2PointY[33] = 241;
		image2PointY[34] = 177;
		image2PointY[35] = 87;
		image2PointY[36] = 39;
		image2PointY[37] = 40;
		image2PointY[38] = 86;
		image2PointY[39] = 40;
		image2PointY[40] = 40;
		image2PointY[41] = 452;
		image2PointY[42] = 452;*/
		
		for (int i = 0; i < 43; i++){
			image2PointX[i] = image1PointX[i]+390;
			image2PointY[i] = image1PointY[i];
		}
		
		try
		{
			Image picture = ImageIO.read(new File("C:/Users/Yohanan Ben-Gad/Documents/jamesbond.jpg"));
			panel = new JPanel();
			ImageIcon icon = new ImageIcon(picture);
			label1 = new JLabel(icon);
			label1.setBounds(10, 0, icon.getIconWidth(), icon.getIconHeight());
			getContentPane().add(label1);
			
			image1Xmin = 10;
			image1Xmax = 10 + icon.getIconWidth();
			image1Ymin = 0;
			image1Ymax = icon.getIconHeight();
		
			label1.addMouseListener(new MouseAdapter() { 
				public void mousePressed(MouseEvent me) { 
					graphics = getGraphics();
					graphics.fillOval(me.getX()+7, me.getY()+35, 6, 6);
					for (int k = 0; k < numberPoints; k++){
						graphics.fillOval(image1PointX[k], image1PointY[k], 6, 6);
					}
					System.out.println((me.getX()+7) + " " + (me.getY()+35));
					calculate();
				} 
			});
		
			File ImageFile1 = new File("C:/Users/Yohanan Ben-Gad/Documents/jamesbond.jpg");
			try {
				Image1 = ImageIO.read(ImageFile1);
				System.out.println(Image1.getHeight() + " " + Image1.getWidth());
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		
			Image picture2 = ImageIO.read(new File("C:/Users/Yohanan Ben-Gad/Documents/jamesbond.jpg"));
			panel = new JPanel();
			ImageIcon icon2 = new ImageIcon(picture2);
			label2 = new JLabel(icon2);
			label2.setBounds(400, 0, icon2.getIconWidth(), icon2.getIconHeight());
			getContentPane().add(label2);
			
			image2Xmin = 400;
			image2Xmax = 400 + icon2.getIconWidth();
			image2Ymin = 0;
			image2Ymax = icon2.getIconHeight();
			
			label2.addMouseListener(new MouseAdapter() { 
				public void mousePressed(MouseEvent ue) { 
					graphics = getGraphics();
					graphics.fillOval(ue.getX()+397, ue.getY()+35, 6, 6);
					for (int k = 0; k < numberPoints; k++){
						graphics.fillOval(image2PointX[k], image2PointY[k], 6, 6);
					}
					System.out.println((ue.getX()+397) + " " + (ue.getY()+35));
				} 
			});
			
			File ImageFile2 = new File("C:/Users/Yohanan Ben-Gad/Documents/jamesbond.jpg");
			try {
				Image2 = ImageIO.read(ImageFile2);
				System.out.println(Image2.getWidth() + " " + Image2.getHeight());
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			Delaunay_Triangulation dt1 = new Delaunay_Triangulation();
			Delaunay_Triangulation dt2 = new Delaunay_Triangulation();
			
			for (int i = 0; i < numberPoints; i++){
				dt1.insertPoint(new Point_dt(image1PointX[i], image1PointY[i]));
				dt2.insertPoint(new Point_dt(image2PointX[i], image2PointY[i]));
			}	
			
			DoublePair[] CoordsImage3 = new DoublePair[numberPoints];
			double[] image3PointX = new double[1000];
			double[] image3PointY = new double[1000];
			
			for (int i = 0; i < dt1.size(); i++){
				dt3.insertPoint(new Point_dt(image1PointX[i]*0.5 + image2PointX[i]*0.5, image1PointY[i]*0.5 + image2PointY[i]*0.5));
				CoordsImage3[i] = new DoublePair(image1PointX[i]*0.5 + image2PointX[i]*0.5, image1PointY[i]*0.5 + image2PointY[i]*0.5);
				image3PointX[i] = image1PointX[i]*0.5 + image2PointX[i]*0.5;
				image3PointY[i] = image1PointY[i]*0.5 + image2PointY[i]*0.5;
			}	
		
			Iterator<Triangle_dt> iterator3 = dt3.trianglesIterator();
			counter = 0;
		
			while (iterator3.hasNext()) {
				Triangle_dt curr3 = iterator3.next();
				if (!curr3.isHalfplane()) {
					triangleList3[counter] = curr3;
					counter++;
				}
			}	
			
			Point_dt pointHolderI1 = new Point_dt(0,0);
			Point_dt pointHolderI2 = new Point_dt(0,0);
			Point_dt pointHolderI3 = new Point_dt(0,0);
			Point_dt pointHolderII1 = new Point_dt(0,0);
			Point_dt pointHolderII2 = new Point_dt(0,0);
			Point_dt pointHolderII3 = new Point_dt(0,0);
			
			for (int i = 0; i < counter; i++){
				for (int j = 0; j < numberPoints; j++){
					if (triangleList3[i].p1().x() == image3PointX[j] && triangleList3[i].p1().y() == image3PointY[j]){
						pointHolderI1 = new Point_dt(image1PointX[j], image1PointY[j]);
						pointHolderII1 = new Point_dt(image2PointX[j], image2PointY[j]);
					}
					if (triangleList3[i].p2().x() == image3PointX[j] && triangleList3[i].p2().y() == image3PointY[j]){
						pointHolderI2 = new Point_dt(image1PointX[j], image1PointY[j]);
						pointHolderII2 = new Point_dt(image2PointX[j], image2PointY[j]);
					}
					if (triangleList3[i].p3().x() == image3PointX[j] && triangleList3[i].p3().y() == image3PointY[j]){
						pointHolderI3 = new Point_dt(image1PointX[j], image1PointY[j]);
						pointHolderII3 = new Point_dt(image2PointX[j], image2PointY[j]);
					}	
				}
				triangleList1[i] = new Triangle_dt (pointHolderI1, pointHolderI2, pointHolderI3);
				triangleList2[i] = new Triangle_dt (pointHolderII1, pointHolderII2, pointHolderII3);
			}
			
		}
		catch (IOException e)
		{
			String workingDir = System.getProperty("user.dir");
			System.out.println("Current working directory : " + workingDir);
			e.printStackTrace();
		}
	}
	
	public void calculate(){
		
		double Barry1 = 0;
		double Barry2 = 0;
		double Barry3 = 0;		
			
		double x1 = 0;
		double x2 = 0;
		double x3 = 0;
		double y1 = 0;
		double y2 = 0;
		double y3 = 0;
		
		int a1 = 0;
		int a2 = 0;
		int b1 = 0;
		int b2 = 0;
		
		int Xmin = image1Xmin/2+image2Xmin/2-1;
		int Xmax = image1Xmax/2+image2Xmax/2-1;
		int Ymin = image1Ymin/2+image2Ymin/2-1;
		int Ymax = image1Ymax/2+image2Ymax/2-1;
		
		System.out.println(Xmin + " " + Xmax + " " + Ymin + " " + Ymax);
					
		for (int a = Xmin; a < Xmax; a++){
			for (int b = Ymin; b < Ymax; b++){
				Point_dt point = new Point_dt(a,b);
				Triangle_dt aTriangle = dt3.find(point);
				if(dt3.find(point).p3() != null){
					for (int z = 0; z < counter; z++){
						if (dt3.find(point).p1().x() == triangleList3[z].p1().x() && dt3.find(point).p1().y() == triangleList3[z].p1().y() && 
								dt3.find(point).p2().x() == triangleList3[z].p2().x() && dt3.find(point).p2().y() == triangleList3[z].p2().y() &&
									dt3.find(point).p3().x() == triangleList3[z].p3().x() && dt3.find(point).p3().y() == triangleList3[z].p3().y()){
													
							x1 = aTriangle.p1().x();	
							x2 = aTriangle.p2().x();
							x3 = aTriangle.p3().x();
							y1 = aTriangle.p1().y();
							y2 = aTriangle.p2().y();
							y3 = aTriangle.p3().y();
						
							Barry1 = ((y2-y3)*(a-x3) + (x3-x2)*(b-y3))/((y2-y3)*(x1-x3)+(x3-x2)*(y1-y3));
							Barry2 = ((y3-y1)*(a-x3) + (x1-x3)*(b-y3))/((y2-y3)*(x1-x3)+(x3-x2)*(y1-y3));
							Barry3 = 1 - Barry1 - Barry2;
																			
							a1 = (int)(triangleList1[z].p1().x()*Barry1 + triangleList1[z].p2().x()*Barry2 + triangleList1[z].p3().x()*Barry3);
							b1 = (int)(triangleList1[z].p1().y()*Barry1 + triangleList1[z].p2().y()*Barry2 + triangleList1[z].p3().y()*Barry3);
							a2 = (int)(triangleList2[z].p1().x()*Barry1 + triangleList2[z].p2().x()*Barry2 + triangleList2[z].p3().x()*Barry3);
							b2 = (int)(triangleList2[z].p1().y()*Barry1 + triangleList2[z].p2().y()*Barry2 + triangleList2[z].p3().y()*Barry3);
						
							//System.out.println(a1-11 + " " + (b1-39));
							
							int clr1=  Image1.getRGB(a1-11, b1-39); 
							int clr2=  Image2.getRGB(a2-401, b2-39);
																		
							int  red1   = (clr1 & 0x00ff0000) >> 16;
							int  green1 = (clr1 & 0x0000ff00) >> 8;
							int  blue1  =  clr1 & 0x000000ff;
						
							int  red2   = (clr2 & 0x00ff0000) >> 16;
							int  green2 = (clr2 & 0x0000ff00) >> 8;
							int  blue2  =  clr2 & 0x000000ff;
						
							int red = (int)(red1*0.5 + red2*(1-0.5));
							int green = (int)(green1*0.5 + green2*(1-0.5));
							int blue = (int)(blue1*0.5 + blue2*(1-0.5));
							
							Color newColour = new Color(red, green, blue);
							
							graphics = getGraphics();
							paintComponent(a, b, graphics, newColour, a1, b1, a2, b2);
						}
					}
				}
			}
		}
	}
	
	public void paintComponent(int a, int b, Graphics graphicsy, Color color, int c, int d, int e, int f){
		graphicsy.setColor(color);
		graphics.drawRect(a, b, 1, 1);
		//graphics.drawRect(c, d, 1, 1);
		//graphics.drawRect(e, f, 1, 1);
	}
	
	class IntPair {
		final int x;
		final int y;
		IntPair(int x, int y){
			this.x=x;
			this.y=y;
		}
	}
	class DoublePair {
		final double x;
		final double y;
		DoublePair(double x, double y){
			this.x=x;
			this.y=y;
		}
	}
	class IntTriplet {
		final int x;
		final int y;
		final int z;
		IntTriplet(int x, int y, int z){
			this.x=x;
			this.y=y; 
			this.z=z;
		}
	}
}