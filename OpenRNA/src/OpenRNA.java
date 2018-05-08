import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class OpenRNA extends Matriz{
	
	private static JFrame frame;
	private static JLabel imageLabel;
	public static int width = 160, heigth = 120;
	
	public static void initGUI() {
		frame = new JFrame("Camera Input Example");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(width,heigth);  
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);       
	}
	
	public static double[] reshape(Mat matrix){
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		double[] resp = new double[totalBytes];
		
		matrix.get(0 , 0, dadosMatriz);
		
		int k = 0, i = 0;
		while(k<40) {
			for(int j=k;j<totalBytes;i++, j+=40)
				resp[i] = (dadosMatriz[j]&0xFF);

			k++;
		}
		return resp;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
	    
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat webcamMatImage = new Mat(); 
		initGUI();
		VideoCapture capture = new VideoCapture(1);
		
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,heigth);
		
		NeuralNet netPos = new NeuralNet("WR_POS","ANNi1","ANNl1",300,16,2);
		NeuralNet netS = new NeuralNet("WR_S","ANNi2","ANNl2",300,29,1);
		
		MatFileReader mfr = new MatFileReader("testePicPos.mat");
		Map<String, MLArray> mlArrayRetrived = mfr.getContent();
		MLArray picPos = mlArrayRetrived.get("testePicPos");
		  
		mfr = new MatFileReader("testePicSize.mat");
		mlArrayRetrived = mfr.getContent();
		MLArray picSize = mlArrayRetrived.get("testePicSize");
		
		double[] fotoBCE_DE = ((MLDouble)picPos).getArray()[0];
		double[] fotoBCG_DFSD = ((MLDouble)picSize).getArray()[0];
		
		System.out.println("x: " + netPos.calculate(fotoBCE_DE)[0]);  //  128,999307706119	
		System.out.println("y: " + netPos.calculate(fotoBCE_DE)[1]); //  95,0000240373988
		System.out.println("S: " + netS.calculate(fotoBCG_DFSD)[0]);  // 31,0000000000000
		
		NeuralNet BOLA = new NeuralNet("WR_BOLA","ANNbolaI","ANNbolaL",144,7,1);
		NeuralNet MARCAS = new NeuralNet("WR_MARCAS","ANNmarcasI","ANNmarcasL",48,11,1);
		NeuralNet GOL = new NeuralNet("WR_GOL","ANNgolI","ANNgolL",144,8,1);
		NeuralNet OP = new NeuralNet("WR_OP","ANNopI","ANNopL",144,9,1);
		

		boolean cap = true;
		int pos = 54;
		
		if( capture.isOpened()) {
			while (cap){  
				capture.read(webcamMatImage);
				
				Mat imagemRNA1 = webcamMatImage.clone();
				Mat imagemRNA2 =  webcamMatImage.clone();
				Mat imagemBola =  webcamMatImage.clone();
				Mat imagemMarcas =  webcamMatImage.clone();
				Mat imagemGol =  webcamMatImage.clone();
				Mat imagemOP =  webcamMatImage.clone();
				
				Mat RNA1 = new Mat();
				Mat RNA2 = new Mat();
				
				/*
				 * tratando a primeira imagem
				 */
				/*
				matBC(imagemRNA1,1.15,20);
				matExtractMin(imagemRNA1, 170);//170
				imagemRNA1 = matGrayscale_Average(imagemRNA1);
				matExtractMax(imagemRNA1, 50);// max 50
				//Imgproc.medianBlur(imagemRNA1, imagemRNA1, 3);
				Imgproc.resize(imagemRNA1,RNA1,new Size(20,15));
				double[] rna1 = reshape(RNA1);
				*/
				
				/*
				 * tratando a segunda imagem 
				 */
				/*
				matBC(imagemRNA2,1.1,20);
				imagemRNA2 = matGrayscale_Lightless(imagemRNA2);
				Mat floatI = new Mat(); 
				imagemRNA2.convertTo(floatI, CvType.CV_32FC1);
				List<Mat> matList = new ArrayList<Mat>();
				matList.add(floatI);
				Mat zeroMat = Mat.zeros(floatI.size(), CvType.CV_32F);
				matList.add(zeroMat);
				Mat complexImage = new Mat();
				Core.merge(matList, complexImage);
				Core.dft(complexImage,complexImage);
				List<Mat> splitted = new ArrayList<Mat>();
				Core.split(complexImage,splitted);
				Mat magnitude = new Mat();
				Core.magnitude(splitted.get(0), splitted.get(1), magnitude);
				Core.add(Mat.ones(magnitude.size(), CvType.CV_32F), magnitude,
						magnitude);
				Core.log(magnitude, magnitude);
				magnitude.convertTo(magnitude, CvType.CV_8UC1);
				Core.normalize(magnitude, magnitude,0,255, Core.NORM_MINMAX, CvType.
						CV_8UC1);
				imagemRNA2 = magnitude.clone();
				Imgproc.resize(imagemRNA2,RNA2,new Size(20,15));
				double[] rna2 = reshape(RNA2);
				*/
				
				/*
				 * Tratando os segmentos de imagens para Bola
				 */
				/*matBC(imagemBola,1.15,10);
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(6,8), CvType.CV_8UC3);

				for(i = 0;i<400;i++) {
					if(!(c<160)) {
						a += 6;
						c = 0;
					}
			
					akk = matRegion3(imagemBola, a, c, 6, 8);
					double[] rnaBola = new double[144];
					int cont = 0;
					for(int l = 0;l<3;l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaBola[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(BOLA.calculate(rnaBola)[0] >= 0.7)
						matJointRegion3(imagemBola, new int[] {0,0,255}, a, c, 6, 8);
					
					c+=8;
				}
				*/
				
				/*
				 * Tratando os segmentos de imagens para MARCAS
				 */
				/*matBC(imagemMarcas,1.15,10);
				Mat akkM = new Mat(new Size(6,8), CvType.CV_8UC1);
				int a = 0,c = 0, i;
				
				for(i = 0;i<400;i++) {
					if(!(c<160)) {
						a += 6;
						c = 0;
					}
					akkM = matRegion1(imagemMarcas, a, c, 6, 8);
					double[] rnaMarcas = new double[48];
					int cont = 0;
					for(int l = 0;l<akkM.channels();l++) {
						for(int m=0;m<akkM.cols();m++) {
							for(int n=0;n<akkM.rows();n++) {
								rnaMarcas[cont] = (int) akkM.get(n, m)[l];
								cont++;
							}
						}
					}
					
					if(MARCAS.calculate(rnaMarcas)[0] >= 0.5)
						matJointRegion3(imagemMarcas, new int[] {0,0,255}, a, c, 6, 8);
					
					c+=8;
				}
				*/
				
				/*
				 * Tratando os segmentos de imagens para GOL
				 */
				/*//matBC(imagemGol,1.2,5);
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(6,8), CvType.CV_8UC3);

				for(i = 0;i<400;i++) {
					if(!(c<160)) {
						a += 6;
						c = 0;
					}
			
					akk = matRegion3(imagemGol, a, c, 6, 8);
					double[] rnaGol = new double[144];
					int cont = 0;
					for(int l = 0;l<3;l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaGol[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(GOL.calculate(rnaGol)[0] >= 0.9)
						matJointRegion3(imagemGol, new int[] {0,0,255}, a, c, 6, 8);
					
					c+=8;
				}*/
				
				
				/*
				 * Tratando os segmentos de imagens para GOL
				 */
				//matBC(imagemOP,1.1,0);
				/*
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(6,8), CvType.CV_8UC3);

				for(i = 0;i<400;i++) {
					if(!(c<160)) {
						a += 6;
						c = 0;
					}
			
					akk = matRegion3(imagemOP, a, c, 6, 8);
					double[] rnaOP = new double[144];
					int cont = 0;
					for(int l = 0;l<3;l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaOP[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(OP.calculate(rnaOP)[0] <= 0.8)
						matJointRegion3(imagemOP, new int[] {0,0,255}, a, c, 6, 8);
					
					c+=8;
				}
				*/
				
				/*
				 * validando RNA
				 */
				//Point coord = new Point(netPos.calculate(rna1)[0],netPos.calculate(rna1)[1]);
				//double radius =  netS.calculate(rna2)[0]*0.8;
				
				//System.out.println("x: " + coord.x + " y: " + coord.y + " S: " + radius);				   
				//if(radius>0)
					//Imgproc.circle(webcamMatImage, coord, (int) radius, new Scalar(255,255,255), 5);
				
				matVideo(frame, imageLabel,imagemOP);
				
				//Imgcodecs.imwrite("FINAL_"+pos+".jpg",imagemOP);
				
				//Thread.sleep(5000);
				//pos++;
			}  
		}
		else{
			System.out.println("Couldn't open capture.");
		}

	}

}
