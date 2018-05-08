import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class VideoOpenCV extends Matriz{
	
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
	
	public static void main(String[] args) throws InterruptedException {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat webcamMatImage = new Mat(); 
		initGUI();

		boolean teste = true;
		
		int t = 1;
		
		VideoCapture capture = new VideoCapture(1);
		
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,heigth);
		
		long initial = System.nanoTime()/1000000;
		
		if( capture.isOpened()) {
			while (teste){  
				capture.read(webcamMatImage);
				
				Mat imagem = webcamMatImage.clone();
			
				matVideo(frame, imageLabel,imagem);
				
				long finals = System.nanoTime()/1000000;
				long time = finals - initial;
				
				if(time>=6000) {
					String pos = Integer.toString(t);
					String pasta = "./Adversario/";
					
					Imgcodecs.imwrite(pasta+"C_"+pos+".jpg",imagem);
					
					matBC(imagem,1.1,20);
					Imgcodecs.imwrite(pasta+"BC_C"+pos+".jpg",imagem);
					
					imagem = matGrayscale_Luminosity(imagem);
					Imgcodecs.imwrite(pasta+"BCG_C"+pos+".jpg",imagem);
					
					Imgproc.Laplacian(imagem,imagem, CvType.CV_8UC1, 3, (double) 3.5, (double) 1.5);
					Imgcodecs.imwrite(pasta+"BCGL_C"+pos+".jpg",imagem);
					
					t++;
					
					initial = System.nanoTime()/1000000;
					
					System.out.println(pos+".jpg");
				}
				
			}  
		}
		else{
			System.out.println("Couldn't open capture.");
		}
			
	}

}
