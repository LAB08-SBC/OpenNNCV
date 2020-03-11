package OpenRNAFC;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencvext.matrix.MatrixOpenCV_Ext;

public class OpenRNA extends MatrixOpenCV_Ext{
	
	private static JFrame frame;
	private static JLabel imageLabel;
	private static VideoCapture capture;
	private static int width = 160, height = 120;
	private static float scale = (float) (1.0/20.0);
	
	private static int imgNumber = 1;
	//private static String folder = "C:\\Users\\Gilmar Jeronimo\\Desktop\\Testes MATLAB\\OpenNNCV\\src\\OpenRNAFC\\";
	private static String folder = "/root/workspace/OpenNNCV/src/OpenRNAFC/";
	
	public static void main(String[] args){
	    
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		/* 
		 * A primeira webcam conectada tem valor 0 no initGUI, 
		 * a segunda tem valor 1, e assim por diante...
		 */
		initGUI(0); 
		
		// Define a matriz que vai ser mostrada na TELA.
		Mat webcamMatImage = new Mat();
		
		// Inicia o tempo do sistema.
		//long initial = System.nanoTime()/1000000;
		long initial = System.nanoTime();
		
		int widthScale = (int)(width * scale);
		int heightScale= (int)(height* scale);
		
		NeuralNet BALL = null, MARKS = null, GOAL = null, OPP = null;
		
		try {
			BALL = new NeuralNet(folder,"NNWeights/WR_BALL","NNInputs/ANNballI","NNLabels/ANNballL",(3*widthScale*heightScale),6,1);
			//MARKS = new NeuralNet(folder,"NNWeights/WR_MARK","NNInputs/ANNmarksI","NNLabels/ANNmarksL",(3*widthScale*heightScale),11,1);
			//GOAL = new NeuralNet(folder,"NNWeights/WR_GOAL","NNInputs/ANNgoalI","NNLabels/ANNgoalL",(3*widthScale*heightScale),8,1);
			//OPP = new NeuralNet(folder,"NNWeights/WR_OPP","NNInputs/ANNoppI","NNLabels/ANNoppL",(3*widthScale*heightScale),9,1);
		} catch(Exception e) {
			System.out.println(e);
		}
		System.out.println("nn load time: " + ((System.nanoTime()-initial)/1000000000.0));
		

		boolean webcamCapture = true;

		// Se achou a webcam conectada ir� mostrar na tela
		if( capture.isOpened()) {
			while (webcamCapture){  
				// A imagem capturada na webcam � salva em webcamMatImage
				capture.read(webcamMatImage);		
				initial = System.nanoTime();
				try {
					ballIdentification(BALL,webcamMatImage,widthScale,heightScale,new int[] {0,0,255});
					//marksIdentification(MARKS,webcamMatImage,widthScale,heightScale,new int[] {0,0,255});
					//goalIdentification(GOAL,webcamMatImage,widthScale,heightScale,new int[] {0,0,255});
					//opponentIdentification(OPP,webcamMatImage,widthScale,heightScale,new int[] {0,0,255});
				} catch(Exception e) {
					System.out.println(e);
				}
				
				System.out.println("image segmentation time: " + ((System.nanoTime()-initial)/1000000000.0));
				// Mostra a imagem
				matVideo(frame, imageLabel,webcamMatImage);
			}  
		}
		else{
			System.out.println("Couldn't open capture.");
		}
	}
	
	/*
	 * O c�digo para captura de tela em formato de v�deo e a classe ImageViewer 
	 * � baseado no c�digo de: https://
	 */
	public static void initGUI(int camNumber) {
		frame = new JFrame("Camera Input Example");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(width,height);  
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);    
		
		capture = new VideoCapture(camNumber);
		
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,height);	
	}

	public static void ballIdentification(NeuralNet BALL, Mat image, int widthScale, int heightScale, int[] frameColor) throws InterruptedException, FileNotFoundException, IOException {	
		
		matBC(image,1.25,10);
		imageSegmentation(BALL,image, heightScale,widthScale, image.channels(), (float)(0.6), true, frameColor);
	}
	
	public static void ballIdentification(NeuralNet BALL, Mat image, int widthScale, int heightScale, int[] frameColor, int delaySave) throws InterruptedException, FileNotFoundException, IOException {	
		ballIdentification(BALL,image,widthScale,heightScale,frameColor);
		
		Imgcodecs.imwrite("FINAL_BALL"+imgNumber+".jpg",image);
		Thread.sleep(delaySave);
		imgNumber++;
	}
	
	public static void marksIdentification(NeuralNet MARKS, Mat image, int widthScale, int heightScale, int[] frameColor) throws InterruptedException, FileNotFoundException, IOException {
		matBC(image,1.15,10);
		imageSegmentation(MARKS,image, heightScale,widthScale, image.channels(), (float)(0.5),true, frameColor);
	
	}

	public void marksIdentification(NeuralNet MARKS, Mat image, int widthScale, int heightScale, int[] frameColor, int delaySave) throws InterruptedException, FileNotFoundException, IOException {
		marksIdentification(MARKS,image,widthScale,heightScale,frameColor);
		
		Imgcodecs.imwrite("FINAL_MARKS"+imgNumber+".jpg",image);
		Thread.sleep(delaySave);
		imgNumber++;
	}
	
	public static void goalIdentification(NeuralNet GOAL, Mat image, int widthScale, int heightScale, int[] frameColor) throws InterruptedException, FileNotFoundException, IOException {
		//matBC(image,1.2,5);
		imageSegmentation(GOAL,image, heightScale,widthScale, image.channels(), (float)(0.9),true, frameColor);

	}

	public void goalIdentification(NeuralNet GOAL, Mat image, int widthScale, int heightScale, int[] frameColor, int delaySave) throws InterruptedException, FileNotFoundException, IOException {
		
		goalIdentification(GOAL,image,widthScale,heightScale,frameColor);
		
		Imgcodecs.imwrite("FINAL_GOAL"+imgNumber+".jpg",image);
		Thread.sleep(delaySave);
		imgNumber++;
	}
	
	public static void opponentIdentification(NeuralNet OPP, Mat image, int widthScale, int heightScale, int[] frameColor) throws InterruptedException, FileNotFoundException, IOException {
		//matBC(image,1.1,0);
		imageSegmentation(OPP,image, heightScale,widthScale, image.channels(), (float)(0.5), true, frameColor);
		//imageSegmentation(OPP,image, heightScale,widthScale, image.channels(), (float)(0.8), false, frameColor);
	}
	
	public void opponentIdentification(NeuralNet OPP, Mat image, int widthScale, int heightScale, int[] frameColor, int delaySave) throws InterruptedException, FileNotFoundException, IOException {
		opponentIdentification(OPP,image,widthScale,heightScale,frameColor);
		
		Imgcodecs.imwrite("FINAL_OPPONENT"+imgNumber+".jpg",image);
		Thread.sleep(delaySave);
		imgNumber++;
	}
	
	public static void imageSegmentation(NeuralNet ANN, Mat image, int heightScale, int widthScale, int channels, float value, boolean geq,int[] frameColor) throws FileNotFoundException, IOException, InterruptedException {
		
		
		Mat[] frames = matSegmentation(image,new Size(widthScale,heightScale));
		
		boolean[] results = new boolean[(int) (1/(scale*scale)) + 1];
		
		for (boolean b : results)
			b = false;
		
		
		for(int frameNumber = 0, row = 0, col = 0;frameNumber<=(1/(scale*scale));frameNumber++) {
			
			Mat frame = frames[frameNumber];
			
			double[] inputVector = new double[(frame.channels()*heightScale*widthScale)];
			
			int counter = 0;
			
			for(int l = 0;l<frame.channels();l++) {
				for(int m=0;m<frame.cols();m++) {
					for(int n=0;n<frame.rows();n++) {
						inputVector[counter] = (int) frame.get(n, m)[l];
						counter++;
					}
				}
			}

			if (geq) {
				if(ANN.calculate(inputVector)[0] >= value) 
					results[frameNumber] = true;
			}
			else {
				if(ANN.calculate(inputVector)[0] <= value) 
					results[frameNumber] = true;
			}
			
		}
		
		matANNResults(image, new Size(widthScale,heightScale), frameColor, results);
		
	}
	/*
	public static void imageSegmentation(NeuralNet ANN, Mat image, int heightScale, int widthScale, int channels, float value, boolean geq,int[] frameColor) throws FileNotFoundException, IOException, InterruptedException {
		Mat frame = new Mat();
		
		if (channels == 1) 
			frame = new Mat(new Size(heightScale,widthScale), CvType.CV_8UC1);
		else if(channels == 3) 
			frame = new Mat(new Size(heightScale,widthScale), CvType.CV_8UC3);
		
		for(int frameNumber = 0, row = 0, col = 0;frameNumber<=(1/(scale*scale));frameNumber++) {
			
			if(!(col<width)) {
				row += heightScale;
				col = 0;
			}
	
			if (channels == 1)
				frame = matRegion1(image, row, col, heightScale, widthScale);
			else if (channels == 3)	
				frame = matRegion3(image, row, col, heightScale, widthScale);
			
			double[] ann = new double[(frame.channels()*heightScale*widthScale)];
			
			int counter = 0;
			
			for(int l = 0;l<frame.channels();l++) {
				for(int m=0;m<frame.cols();m++) {
					for(int n=0;n<frame.rows();n++) {
						ann[counter] = (int) frame.get(n, m)[l];
						counter++;
					}
				}
			}

			if (geq) {
				if(ANN.calculate(ann)[0] >= value) 
					matJointRegion3(image, frameColor, row, col, heightScale, widthScale);
			}
			else {
				if(ANN.calculate(ann)[0] <= value) 
					matJointRegion3(image, frameColor, row, col, heightScale, widthScale);
			}
			
			col+=widthScale;
		}
		
	}*/
	
}
