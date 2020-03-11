

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

public class MatrixOpenCV_Ext{

	/*
	 * Altera o valor de um pixel em um determinado canal
	 * matPoint(mat, new int{channel,row,col}, valor). 
	 */
	public static void matPoint(Mat matrix, int[] info, int value) {
		int channel = info[0];
		int row = info[1];
		int col = info[2];
		
		int size = matrix.channels();
		
		if (size == 1) 
			matrix.put(row, col, new int[] {value});
		else {
			if (size == 2) {
				if(channel == 0)
					matrix.put(row, col,new byte[] {(byte) value, (byte) matrix.get(row, col)[1]});
				else 
					matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) value});
			}
			else {
				if (size == 3) {
					if(channel == 0)
						matrix.put(row, col,new byte[] {(byte) value, (byte) matrix.get(row, col)[1],(byte) matrix.get(row, col)[2]});
					else {
						if(channel == 1) 
							matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) value,(byte) matrix.get(row, col)[2]});
						else	
							matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) matrix.get(row, col)[1],(byte) value});
					}
				}
				else {
					if (size == 4) {
						if(channel == 0)
							matrix.put(row, col,new byte[] {(byte) value, (byte) matrix.get(row, col)[1],(byte) matrix.get(row, col)[2],(byte) matrix.get(row, col)[3]});
						else {
							if(channel == 1) 
								matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) value,(byte) matrix.get(row, col)[2],(byte) matrix.get(row, col)[3]});
							else {
								if(channel == 2)
									matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) matrix.get(row, col)[1],(byte) value,(byte) matrix.get(row, col)[3]});
								else	
								matrix.put(row, col,new byte[] {(byte) matrix.get(row, col)[0],(byte) matrix.get(row, col)[1],(byte) matrix.get(row, col)[2],(byte) value});
							}
						}
					}
				}
			}
		}	
	}
	
	/*
	 * Altera os valores de uma linha de um determinado canal
	 * matRow(mat, new int{channel,row},valor)
	 */
	public static void matRow(Mat matrix, int[] info, int value) {
		int channel = info[0];
		int row = info[1];

		for(int i = 0;i<matrix.cols();i++)
			matPoint(matrix, new int[] {channel,row,i}, value);

	}
	
	/*
	 * Altera os valores de uma coluna de um determinado canal
	 * matCol(mat, new int{channel,col},valor)
	 */
	public static void matCol(Mat matrix, int[] info, int value) {
		int channel = info[0];
		int col = info[1];

		for(int i = 0;i<matrix.rows();i++)
			matPoint(matrix, new int[] {channel,i,col}, value);

	}
	
	/*
	 * Altera os valores de determinado canal
	 * matChannel(mat, channel,valor)
	 */
	public static void matChannel(Mat matrix, int channel, int value) {

		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		
		for(int i = 0;i<totalBytes;i+=3) {
			if(channel == 0)
				dadosMatriz[i] = (byte) value;
			else {
				if(channel == 1)
					dadosMatriz[i+1] = (byte) value;
				else
					dadosMatriz[i+2] = (byte) value;
			}
		}
		
		matrix.put(0, 0, dadosMatriz);

	}

	
	/*
	 * Altera os valores de todos os canais
	 * matAllChannels(mat,valor)
	 */
	public static void matAllChannels(Mat matrix, int value) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		
		for(int i = 0;i<totalBytes;i+=3) {
			dadosMatriz[i] = (byte) value;
			dadosMatriz[i+1] = (byte) value;
			dadosMatriz[i+2] = (byte) value;
		}
		
		matrix.put(0, 0, dadosMatriz);
	}

	/*
	 * Aplica brilho e contraste em um determinado canal
	 * matChannelBC(mat,channel,b,c)
	 */
	public static void matChannelBC(Mat matrix, int channel, double brightness, int contrast) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		double dadosMatriz[] = new double[totalBytes]; 
		byte transfer[] = new byte[totalBytes];
		
		matrix.get(0 , 0, transfer);
		
		for(int i = 0;i<totalBytes;i+=3) {
			if(channel == 0) {
				dadosMatriz[i] = brightness * (double) (transfer[i]&0xFF) + contrast;
				dadosMatriz[i+1] = (double) (transfer[i+1]&0xFF);
				dadosMatriz[i+2] = (double) (transfer[i+2]&0xFF);
			}
			else {
				if(channel == 1) {
					dadosMatriz[i+1] = brightness * (double) (transfer[i+1]&0xFF)+ contrast;
					dadosMatriz[i] = (double) (transfer[i]&0xFF);
					dadosMatriz[i+2] = (double) (transfer[i+2]&0xFF);
				}
				else {
					dadosMatriz[i+2] = brightness * (double) (transfer[i+2]&0xFF) + contrast;
					dadosMatriz[i] = (double) (transfer[i]&0xFF);
					dadosMatriz[i+1] = (double) (transfer[i+1]&0xFF);
				}
			}
		}
		
		matrix.put(0, 0, dadosMatriz);

	}
	
	/*
	 * Aplica brilho e contraste em uma matriz
	 * matBC(mat,b,c)
	 */
	public static void matBC(Mat matrix, double brightness, double contrast) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		double dadosMatriz[] = new double[totalBytes]; 
		byte transfer[] = new byte[totalBytes];
		
		matrix.get(0 , 0, transfer);
		
		for(int i = 0;i<totalBytes;i+=3) {
			dadosMatriz[i] = brightness * (double) (transfer[i]&0xFF) + contrast;
			dadosMatriz[i+1] = brightness * (double) (transfer[i+1]&0xFF)+ contrast;
			dadosMatriz[i+2] = brightness * (double) (transfer[i+2]&0xFF) + contrast;
		}
		
		matrix.put(0, 0, dadosMatriz);

	}

	/*
	 * Realiza a extração de valores em um determinado canal. Se o valor do pixel
	 * for abaixo do valor (value), ela será zerada.
	 * matChannelExtract(mat,channel,valor)
	 */
	public static void matChannelExtract(Mat matrix, int channel, int value) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		
		for(int i = 0;i<totalBytes;i+=3) {
			if (channel == 0) {
				if((int)(dadosMatriz[i]&0xFF)<value) 
					dadosMatriz[i] = 0;
				
				dadosMatriz[i+1] = 0;
				dadosMatriz[i+2] = 0;
			}
			else {
				if (channel == 1) {
					if((int)(dadosMatriz[i+1]&0xFF)<value) 
						dadosMatriz[i+1] = 0;
					
					dadosMatriz[i] = 0;
					dadosMatriz[i+2] = 0;
				}
				else {
					if((int)(dadosMatriz[i+2]&0xFF)<value) 
						dadosMatriz[i+2] = 0;
					
					dadosMatriz[i] = 0;
					dadosMatriz[i+1] = 0;
				}
					
			}
		}
		
		matrix.put(0, 0, dadosMatriz);
	}
	
	/*
	 * Realiza a extração de máximos valores de uma matriz. Se o valor do pixel for
	 * acima de value, ele será zerado.
	 * matExtractMax(mat,value). 
	 */
	public static void matExtractMax(Mat matrix, double value) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		
		for(int i = 0;i<totalBytes;i+=3) {
			if( ((int)(dadosMatriz[i]&0xFF) + (int)(dadosMatriz[i+1]&0xFF) + (int)(dadosMatriz[i+2]&0xFF))<(value*3)) { 
				dadosMatriz[i] = 0;
				dadosMatriz[i+1] = 0;
				dadosMatriz[i+2] = 0;
			}

		}
		
		matrix.put(0, 0, dadosMatriz);
	}
	
	/*
	 * Realiza a extração de mínimos valores de uma matriz. Se o valor do pixel for
	 * abaixo de value, ele será zerado.
	 * matExtractMin(mat,value). 
	 */
	public static void matExtractMin(Mat matrix, double value) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		
		for(int i = 0;i<totalBytes;i++) {
			if((int)(dadosMatriz[i]&0xFF)>value) 
				dadosMatriz[i] = 0;

		}
		
		matrix.put(0, 0, dadosMatriz);
	}
	
	/*
	 * Função para imprimir um pixel de uma matriz de forma intuitiva.
	 */
	public static void matPrintPixel(Mat matrix, int row, int col) {
		
		System.out.print("Pixel (" +row+","+col+"): [" );

		System.out.print((int) matrix.get(row, col)[0]);
		System.out.print(",");
		System.out.print((int) matrix.get(row, col)[1]);
		System.out.print(",");
		System.out.print((int) matrix.get(row, col)[2]);
		
		System.out.println("]");
	}
	
	/*
	 * Função para imprimir um canal de uma matriz de forma intuitiva.
	 */
	public static void matPrintChannel(Mat matrix, int channel) {

		System.out.println("Channel " + channel);
		
		System.out.println();
		
		for(int i = 0;i<matrix.rows();i++) {
			for(int j = 0;j<matrix.cols();j++) {
				if((int) matrix.get(i, j)[channel]<10)
					System.out.print("   " + (int) matrix.get(i, j)[channel]);
				else {
					if((int) matrix.get(i, j)[channel]<100)
						System.out.print("  " + (int) matrix.get(i, j)[channel]);
					else
						System.out.print(" " + (int) matrix.get(i, j)[channel]);
				}
			}
			System.out.println();
		}
		
		System.out.println();
	}
	
	/*
	 * Função para imprimir uma matriz de forma intuitiva.
	 */
	public static void matPrintAll(Mat matrix) {

		for(int k = 0;k < matrix.channels();k++) {
		
			System.out.println("Channel " + k);
			
			System.out.println();
			
			for(int i = 0;i<matrix.rows();i++) {
				for(int j = 0;j<matrix.cols();j++){
					if((int) matrix.get(i, j)[k]<10)
						System.out.print("   " + (int) matrix.get(i, j)[k]);
					else {
						if((int) matrix.get(i, j)[k]<100)
							System.out.print("  " + (int) matrix.get(i, j)[k]);
						else
							System.out.print(" " + (int) matrix.get(i, j)[k]);
					}
				}
				System.out.println();
			}
			
			System.out.println();
		}
	}
	
	
	public static Mat[] matSegmentation(Mat matrix, Size kernelSize){
		
		Mat[] transfer = new Mat[400];
		
		for (int i = 0; i < transfer.length; i++)
			transfer[i] = new Mat(kernelSize,CvType.CV_8UC3);
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes];
		byte dadosTransfer[] = new byte[totalBytes/(400)]; 
		
		matrix.get(0 , 0, dadosMatriz);
		

		for(int frame = 0;frame<400;frame++){
			
			int count = 0;
			
			for(int row= (int) ((frame/20)*(kernelSize.height));row<(int) (((frame/20)+1)*(kernelSize.height));row++){
				for (int col = (int) ((frame%20)*(kernelSize.width)); col < (int) (((frame%20)+1)*(kernelSize.width)); col++) {
					
					//System.out.println("row: "+row+", col: "+col);
					
					int nextFrameB = (int)((row*matrix.size().width*3)+(col*3));
					int nextFrameG = nextFrameB+1;
					int nextFrameR = nextFrameB+2;
					
					dadosTransfer[count] = dadosMatriz[nextFrameB];
					dadosTransfer[count+1] = dadosMatriz[nextFrameG];
					dadosTransfer[count+2] = dadosMatriz[nextFrameR];
					
					count+=3;
				}
			}
			transfer[frame].put(0, 0, dadosTransfer);
		}
		
		return transfer;	
	}
	
	/*
	 * Função para extrair uma submatriz de 3 canais de uma matriz, em uma determinada região da matriz
	 * principal. 
	 * startR: indica a linha do pixel de começo
	 * startC: indica a coluna do pixel do começo
	 * sizeR: largura da submatriz 
	 * sizeC: altura da submatriz
	 */
	public static Mat matRegion3(Mat matrix, int startR, int startC, int sizeR, int sizeC) {
		
		Mat transfer = new Mat(new Size(sizeC,sizeR),CvType.CV_8UC3);

		
		for(int k = 0;k < 3;k++) {
			for(int i = 0;i<sizeR;i++) {
				for(int j = 0;j<sizeC;j++)
					matPoint(transfer, new int[] {k,i,j}, (int) matrix.get(i+startR, j+startC)[k]);
			}
		}
		
		return transfer;
		
	}
	
	/*
	 * Função para extrair uma submatriz de 1 canal de uma matriz, em uma determinada região da matriz
	 * principal. 
	 * startR: indica a linha do pixel de começo
	 * startC: indica a coluna do pixel do começo
	 * sizeR: largura da submatriz 
	 * sizeC: altura da submatriz
	 */
	public static Mat matRegion1(Mat matrix, int startR, int startC, int sizeR, int sizeC) {
		
		/*Mat transfer = new Mat(new Size(sizeC,sizeR),CvType.CV_8UC3);
		
		for(int k = 0;k < 3;k++) {
			for(int i = 0;i<sizeR;i++) {
				for(int j = 0;j<sizeC;j++)
					matPoint(transfer, new int[] {k,i,j}, (int) matrix.get(i+startR, j+startC)[k]);
			}
		}*/
		
		return matGrayscale_Lightless(matRegion3(matrix, startR, startC, sizeR, sizeC));
		
	}
	
	/*
	 * Função que muda a cor de uma submatriz de uma matriz principal
	 * int[] valores: [BLUE,GREEN,RED]
	 */
	public static void matANNResults(Mat matrix, Size kernelSize, int[] valores, boolean[] results) {
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);
		

		for(int frame = 0;frame<400;frame++){
			if(results[frame]){
				for(int row= (int) ((frame/20)*(kernelSize.height));row<(int) (((frame/20)+1)*(kernelSize.height));row++){
					for (int col = (int) ((frame%20)*(kernelSize.width)); col < (int) (((frame%20)+1)*(kernelSize.width)); col++) {
						
						int nextFrameB = (int)((row*matrix.size().width*3)+(col*3));
						int nextFrameG = nextFrameB+1;
						int nextFrameR = nextFrameB+2;
						
						dadosMatriz[nextFrameB] = (byte) valores[0];
						dadosMatriz[nextFrameG] = (byte) valores[1];
						dadosMatriz[nextFrameR] = (byte) valores[2];
						
					}
				}
			}
		}
		
		matrix.put(0, 0, dadosMatriz);
		
	}
	
	/*
	 * Função que muda a cor de uma submatriz de uma matriz principal
	 * int[] valores: [BLUE,GREEN,RED]
	 * startR: indica a linha do pixel de começo
	 * startC: indica a coluna do pixel do começo
	 * sizeR: largura da submatriz 
	 * sizeC: altura da submatriz
	 */
	public static void matJointRegion3(Mat matrix, int[] valores, int startR, int startC, int sizeR, int sizeC) {
		
		/*int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte dadosMatriz[] = new byte[totalBytes]; 
		
		matrix.get(0 , 0, dadosMatriz);

		for(int i = startR*480;i<((startR+sizeR-1)*480);i+=480) {
			for(int j=startC*3;j<((startC+sizeC-1)*3);j+=3){
				dadosMatriz[j+i] = (byte) valor;
				dadosMatriz[j+1+i] = (byte) valor;
				dadosMatriz[j+2+i] = (byte) valor;
			}
		}
		
		matrix.put(0, 0, dadosMatriz);
		*/
		
		for(int k = 0;k < matrix.channels();k++) {
			for(int i = 0;i<sizeR;i++) {
				for(int j = 0;j<sizeC;j++) 
					matPoint(matrix, new int[] {k,startR+i,startC+j}, valores[k]);
			}
		}
	}
	
	// Grayscale https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
	/*
	 * Aplica o filtro Grayscale_Lightless em uma matriz, retornando uma mat de 1 canal
	 */
	public static Mat matGrayscale_Lightless(Mat matrix) {
		
		Mat gray = new Mat(matrix.size(),CvType.CV_8UC1);
		
		int totalBytes1 = (int) (gray.total()*gray.elemSize());
		byte dadosMatriz1[] = new byte[totalBytes1]; 
		
		int totalBytes3 = (int) (matrix.total()*matrix.elemSize());
		byte dadosMatriz3[] = new byte[totalBytes3]; 
		
		matrix.get(0 , 0, dadosMatriz3);
			
		for(int i = 0;i<totalBytes3;i+=3){
			int maior = (int) (dadosMatriz3[i]&0xFF);
			int menor = (int) (dadosMatriz3[i]&0xFF);
			
			for(int k = 1;k<3;k++ ) {

				if((int) (dadosMatriz3[i+k]&0xFF) < menor)
					menor = (int) (dadosMatriz3[i+k]&0xFF); 
					
				if((int) (dadosMatriz3[i+k]&0xFF) > maior)
					maior = (int) (dadosMatriz3[i+k]&0xFF);
					
			}
			dadosMatriz1[(i/3)] = 0;
			dadosMatriz1[(i/3)] = (byte) ((maior+menor)/2); 
		}
		
		gray.put(0, 0, dadosMatriz1);
		
		return gray;
	}
	
	/*
	 * Aplica o filtro Grayscale_Average em uma matriz, retornando uma mat de 1 canal
	 */
	public static Mat matGrayscale_Average(Mat matrix) {
		
		Mat gray = new Mat(matrix.size(),CvType.CV_8UC1);
		
		int totalBytes1 = (int) (gray.total()*gray.elemSize());
		byte dadosMatriz1[] = new byte[totalBytes1]; 
		
		int totalBytes3 = (int) (matrix.total()*matrix.elemSize());
		byte dadosMatriz3[] = new byte[totalBytes3]; 
		
		matrix.get(0 , 0, dadosMatriz3);
		
		for(int i = 0;i<totalBytes3;i+=3)
			dadosMatriz1[i/3] = (byte) (( (int) (dadosMatriz3[i]&0xFF) + (int) (dadosMatriz3[i+1]&0xFF) + (int) (dadosMatriz3[i+2]&0xFF))/3);
		
		gray.put(0, 0, dadosMatriz1);
		
		return gray;
	}
	
	/*
	 * Aplica o filtro Grayscale_Luminosity em uma matriz, retornando uma mat de 1 canal
	 */
	public static Mat matGrayscale_Luminosity(Mat matrix) {
		
		Mat gray = new Mat(matrix.size(),CvType.CV_8UC1);
		
		int totalBytes1 = (int) (gray.total()*gray.elemSize());
		byte dadosMatriz1[] = new byte[totalBytes1]; 
		
		int totalBytes3 = (int) (matrix.total()*matrix.elemSize());
		byte dadosMatriz3[] = new byte[totalBytes3]; 
		
		matrix.get(0 , 0, dadosMatriz3);
		
		for(int i = 0;i<totalBytes3;i+=3) 
			dadosMatriz1[i/3] = (byte) ((float) (0.07 * (int) (dadosMatriz3[i]&0xFF)) + (float) (0.72 * (int) (dadosMatriz3[i+1]&0xFF)) + (float) (0.21 * (int) (dadosMatriz3[i+2]&0xFF)) );
		
		gray.put(0, 0, dadosMatriz1);
		
		return gray;
	}
	
	/*
	 * Esta função transforma uma imagem 2d em um vetor unidimensional assim como
	 * realizado pelo MATLAB, para entrada coerente do vetor na RNA
	 */
	public static double[] reshape(Mat matrix, int widthScale){
		
		int totalBytes = (int) (matrix.total()*matrix.elemSize());
		
		byte matrixData[] = new byte[totalBytes]; 
		double[] output = new double[totalBytes];
		
		matrix.get(0 , 0, matrixData);
		
		int i = 0, k = 0;
		while(i<widthScale) {
			for(int j=i;j<totalBytes;k++, j+=widthScale)
				output[k] = (matrixData[j]&0xFF);

			i++;
		}
		return output;
	}

	// VideoViewer
	public static BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) 
				type = BufferedImage.TYPE_3BYTE_BGR;
			
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		
		return image;
	}
	
	/*
	 * Função que coloca a imagem desejada no vídeo criado pelo jFrame.
	 */
	public static void matVideo(JFrame frame, JLabel imageLabel, Mat matrix) {
		if( !matrix.empty() ){  
			Image tempImage= toBufferedImage(matrix);
			ImageIcon imageIcon = new ImageIcon(tempImage, "Captured video");
			imageLabel.setIcon(imageIcon);
			frame.pack();  //this will resize the window to fit the image
		}  
		else
			System.out.println(" -- Frame not captured -- Break!"); 
	}
}
