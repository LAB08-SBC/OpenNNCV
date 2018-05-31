import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyEmotions extends Matriz{
	
	private static JFrame frame;
	private static JLabel imageLabel;
	public static int width = 160, height = 120;
	
	public static int reduz = 20;
	public static int widthR = width/reduz;
	public static int heightR = height/reduz;
	
	
	public static void initGUI() {
		frame = new JFrame("Camera Input Example");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(width,height);  
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);       
	}
	
	/*
	 * Esta função transforma uma imagem 2d em um vetor unidimensional assim como
	 * realizado pelo MATLAB, para entrada coerente do vetor na RNA
	 */
	public static double[] reshape(Mat matrix){
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		double[] resp = new double[totalBytes];
		
		matrix.get(0 , 0, dadosMatriz);
		
		int k = 0, i = 0;
		while(k<widthR) {
			for(int j=k;j<totalBytes;i++, j+=widthR)
				resp[i] = (dadosMatriz[j]&0xFF);

			k++;
		}
		return resp;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
	    
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat webcamMatImage = new Mat(); 
		initGUI();
		VideoCapture capture = new VideoCapture(0);
		
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,height);
		
		reduz = 8;
		widthR= width/reduz;
		heightR=height/reduz;
		NeuralNet netPos = new NeuralNet("WR_POS","ANNi1","ANNl1",(widthR*heightR),16,2);
		NeuralNet netS = new NeuralNet("WR_S","ANNi2","ANNl2",(widthR*heightR),29,1);
		
		//================================================================================
		
		/*
		 * Teste de resultado com a primeira imagem da base de treino
		 */
		
		MatFileReader mfr = new MatFileReader("testePicPos.mat");
		Map<String, MLArray> mlArrayRetrived = mfr.getContent();
		MLArray picPos = mlArrayRetrived.get("testePicPos");
		  
		mfr = new MatFileReader("testePicSize.mat");
		mlArrayRetrived = mfr.getContent();
		MLArray picSize = mlArrayRetrived.get("testePicSize");
		
		double[] fotoBCE_DE = ((MLDouble)picPos).getArray()[0];
		double[] fotoBCG_DFSD = ((MLDouble)picSize).getArray()[0];
		
		System.out.println("x: " + netPos.calculate(fotoBCE_DE)[0]);  //  128,999307706119	
		System.out.println("y: " + netPos.calculate(fotoBCE_DE)[1]);  //  95,0000240373988
		System.out.println("S: " + netS.calculate(fotoBCG_DFSD)[0]);  // 31,0000000000000
		
		//================================================================================
		
		reduz = 20;
		widthR= width/reduz;
		heightR=height/reduz;
		NeuralNet BOLA = new NeuralNet("WR_BOLA","ANNbolaI","ANNbolaL",(3*widthR*heightR),12,1);
		NeuralNet MARCAS = new NeuralNet("WR_MARCAS","ANNmarcasI","ANNmarcasL",(widthR*heightR),11,1);
		NeuralNet GOL = new NeuralNet("WR_GOL","ANNgolI","ANNgolL",(3*widthR*heightR),8,1);
		NeuralNet OP = new NeuralNet("WR_OP","ANNopI","ANNopL",(3*widthR*heightR),9,1);
		

		boolean cap = true;
		//int pos = 54;
		
		// Load from 'FCL' file
	    String fileName = "emotions.fcl";
	    FIS fisEMO = FIS.load(fileName, true);
	    String fileName2 = "brilho.fcl";
	    FIS fisB = FIS.load(fileName2, true);
	        
	    if (fisEMO == null || fisB == null) { // Error while loading
	    	if(fisEMO == null)
	    		System.err.println("Não carrega: '" + fileName + "'");
	    	else
	    		System.err.println("Não carrega: '" + fileName2 + "'");
	        return;
	    }
	   
	    FunctionBlock functionBlockEMO = fisEMO.getFunctionBlock(null);
	    JFuzzyChart.get().chart(functionBlockEMO);
	    
	    FunctionBlock functionBlockB = fisB.getFunctionBlock(null);
	    JFuzzyChart.get().chart(functionBlockB);

	    double teste = 0.0;
		if( capture.isOpened()) {
			while (cap){  
				capture.read(webcamMatImage);
				
				Mat imagemRNA1 = webcamMatImage.clone();
				Mat imagemRNA2 =  webcamMatImage.clone();
				Mat imagemBola =  webcamMatImage.clone();
				Mat imagemMarcas =  webcamMatImage.clone();
				Mat imagemGol =  webcamMatImage.clone();
				Mat imagemOP =  webcamMatImage.clone();
				Mat FuzzyIm = webcamMatImage.clone();
				
				Mat RNA1 = new Mat();
				Mat RNA2 = new Mat();
				
				
//=====================================================================================
//================================= RNA POS E SIZE ======================================================
//=====================================================================================
				
				/*
				 * tratando a primeira imagem
				 */
				/*
				
				reduz = 8;
				widthR= width/reduz;
				heightR=height/reduz;
		
				matBC(imagemRNA1,1.15,20);
				matExtractMin(imagemRNA1, 170);//170
				imagemRNA1 = matGrayscale_Average(imagemRNA1);
				matExtractMax(imagemRNA1, 50);// max 50
				//Imgproc.medianBlur(imagemRNA1, imagemRNA1, 3);
				Imgproc.resize(imagemRNA1,RNA1,new Size(widthR,heightR));
				double[] rna1 = reshape(RNA1);
				*/
				
				/*
				 * tratando a segunda imagem 
				 */
				/*
				matBC(imagemRNA2,1.1,20);
				imagemRNA2 = matGrayscale_Lightless(imagemRNA2);
				Mat floatI = new Mat(); 
				
				// convertendo para o espaço de Fourier
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
				
				Imgproc.resize(imagemRNA2,RNA2,new Size(widthR,heightR)); //Redução de 8 vezes
				double[] rna2 = reshape(RNA2);
				*/
				
				/*
				 * validando RNA da BOLA, é montado um círculo branco na imagem
				 */
				
				/*Point coord = new Point(netPos.calculate(rna1)[0],netPos.calculate(rna1)[1]);
				double radius =  netS.calculate(rna2)[0]*0.8;
				
				//System.out.println("x: " + coord.x + " y: " + coord.y + " S: " + radius);				   
				if(radius>0)
					Imgproc.circle(webcamMatImage, coord, (int) radius, new Scalar(255,255,255), 5);
				
				
				matVideo(frame, imageLabel,webcamMatImage);
				*/
//=====================================================================================
//===========================================================================================
//=====================================================================================
				
//=====================================================================================
//========================== RNA SEGMENTAÇÃO DE FRAMES DA BOLA ======================================================
//=====================================================================================
				
				/*
				 * Tratando os segmentos de imagens para Bola
				 */
				/*
				reduz = 20;
				widthR= width/reduz;
				heightR=height/reduz;
		
				matBC(imagemBola,1.15,10);
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(heightR,widthR), CvType.CV_8UC3);

				for(i = 0;i<((width*height)/(widthR*heightR));i++) {
					if(!(c<width)) {
						a += heightR;
						c = 0;
					}
			
					akk = matRegion3(imagemBola, a, c, heightR, widthR);
					double[] rnaBola = new double[(akk.channels()*heightR*widthR)];
					
					int cont = 0;
					for(int l = 0;l<akk.channels();l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaBola[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(BOLA.calculate(rnaBola)[0] >= 0.7)
						matJointRegion3(imagemBola, new int[] {0,0,255}, a, c, heightR, widthR);
					
					c+=widthR;
				}
				
				matVideo(frame, imageLabel,imagemBola);
				*/
//=====================================================================================
//=============================================================================================
//=====================================================================================
				
//=====================================================================================
//============================== RNA SEGMENTAÇÃO DE FRAMES DAS MARCAS ======================================================
//=====================================================================================
				
				/*
				 * Tratando os segmentos de imagens para MARCAS
				 */
				
				/*
				
				reduz = 20;
				widthR= width/reduz;
				heightR=height/reduz;
				
				matBC(imagemMarcas,1.15,10);
				Mat akkM = new Mat(new Size(heightR,widthR), CvType.CV_8UC1);
				int a = 0,c = 0, i;
				
				for(i = 0;i<((height*width)/(widthR*heightR));i++) {
					if(!(c<width)) {
						a += heightR;
						c = 0;
					}
					akkM = matRegion1(imagemMarcas, a, c, heightR, widthR);
					double[] rnaMarcas = new double[(widthR*heightR)];
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
						matJointRegion3(imagemMarcas, new int[] {0,0,255}, a, c, heightR, widthR);//a , c 
					
					c+=widthR;
				}
				
				matVideo(frame, imageLabel,imagemMarcas);
				*/
				
//=====================================================================================
//===============================================================================================================
//=====================================================================================
				
//=====================================================================================
//================================= RNA SEGMENTAÇÃO DE FRAMES DO GOL ======================================================
//=====================================================================================
				
				
				/*
				 * Tratando os segmentos de imagens para GOL
				 */
				
				/*
				
				reduz = 20;
				widthR= width/reduz;
				heightR=height/reduz;
				
				//matBC(imagemGol,1.2,5);
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(heightR,widthR), CvType.CV_8UC3);

				for(i = 0;i<((height*width)/(widthR*heightR));i++) {
					if(!(c<width)) {
						a += heightR;
						c = 0;
					}
			
					akk = matRegion3(imagemGol, a, c, heightR, widthR);
					double[] rnaGol = new double[(akk.channels()*widthR*heightR)];
					int cont = 0;
					for(int l = 0;l<akk.channels();l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaGol[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(GOL.calculate(rnaGol)[0] >= 0.9)
						matJointRegion3(imagemGol, new int[] {0,0,255}, a, c, heightR, widthR);
					
					c+=widthR;
				}
				matVideo(frame, imageLabel,imagemGol);
				*/
				
//=====================================================================================
//==============================================================================================================
//=====================================================================================
				
//=====================================================================================
//=========================== RNA SEGMENTAÇÃO DE FRAMES DO OPONENTE ======================================================
//=====================================================================================
				
				/*
				 * Tratando os segmentos de imagens para OPONENTE
				 */
				//matBC(imagemOP,1.1,0);
				/*
				
				reduz = 20;
				widthR= width/reduz;
				heightR=height/reduz;
				
				int i,a = 0,c = 0;
				Mat akk = new Mat(new Size(heightR,8), CvType.CV_8UC3);

				for(i = 0;i<((height*width)/(widthR*heightR));i++) {
					if(!(c<width)) {
						a += heightR;
						c = 0;
					}
			
					akk = matRegion3(imagemOP, a, c,heightR, widthR);
					double[] rnaOP = new double[(akk.channels()*heightR*widthR)];
					int cont = 0;
					for(int l = 0;l<akk.channels();l++) {
						for(int m=0;m<akk.cols();m++) {
							for(int n=0;n<akk.rows();n++) {
								rnaOP[cont] = (int) akk.get(n, m)[l];
								cont++;
							}
						}
					}

					if(OP.calculate(rnaOP)[0] <= 0.8)
						matJointRegion3(imagemOP, new int[] {0,0,255}, a, c, heightR, widthR);
					
					c+=widthR;
				}
				matVideo(frame, imageLabel,imagemOP);
				*/

//=====================================================================================
//==============================================================================================================
//=====================================================================================
					
//=====================================================================================
//=========================== FUZZY ======================================================
//=====================================================================================
				
				matGrayscale_Luminosity(FuzzyIm);
				matBC(FuzzyIm,teste,1.0);
				
				BigDecimal Luz = new BigDecimal("0");
				for(int i=0;i<FuzzyIm.cols();i++) {
					for(int j=0;j<FuzzyIm.rows();j++) {
						Luz = Luz.add(new BigDecimal(String.valueOf( (int) FuzzyIm.get(j, i)[0] )));
					}
				}
				Luz = new BigDecimal((Luz.doubleValue()/((double)255*width*height)) * 100);
				//System.out.printf("%.2f\n",Luz.doubleValue());
				
				
				
				
				functionBlockEMO.setVariable("luz", Luz.doubleValue());
			    fisEMO.evaluate();
			    
			    Variable medo = functionBlockEMO.getVariable("medo");
			    Variable confianca = functionBlockEMO.getVariable("confianca");

				functionBlockB.setVariable("medo", fisEMO.getVariable("medo").getValue());
				functionBlockB.setVariable("confianca", fisEMO.getVariable("confianca").getValue());
				
			    fisB.evaluate();
			    
			    Variable brilho = functionBlockB.getVariable("brilho");
			    
			    System.out.printf("Para L = %.2f ==> Medo = %.2f e Confianca = %.2f ==> Brilho = %.2f\n",
    		  			Luz.doubleValue(),
    		  			fisEMO.getVariable("medo").getValue(),
    		  			fisEMO.getVariable("confianca").getValue(),
    		  			fisB.getVariable("brilho").getValue());
			    
			    teste = fisB.getVariable("brilho").getValue();
			    		
				matVideo(frame,imageLabel,FuzzyIm);
				
				//Thread.sleep(5000);
//=====================================================================================
//==============================================================================================================
//=====================================================================================
									
				
				//Imgcodecs.imwrite("FINAL_"+pos+".jpg",imagemOP);
				//Thread.sleep(500);
				//pos++;
			}  
		}
		else{
			System.out.println("Couldn't open capture.");
		}

	}

}
