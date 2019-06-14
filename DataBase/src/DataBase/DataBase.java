package DataBase;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencvext.matrix.MatrixOpenCV_Ext;

public class DataBase extends MatrixOpenCV_Ext{
	
	public static int marcas[] = new int[]{81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,150,151,152,153,154,155,156,157,158,159,160,162,163,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,222,223,224,225,226,227,228,229,230,231,232,242,244,245,248,249,250,259,260,261,262,263,264,265,266,267,304,305,306,307,308,309,310,311,312,313,314};

	public static void main(String[] args) throws InterruptedException {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//int qtdBall = 321;
		//int qtdGoal = 60;
		int qtdOpponent = 63;
		//int qtdMarks = 230;
		
		for (int img = 0; img <= qtdOpponent; img++){
			String folder = "C:\\Users\\Gilmar Jeronimo\\Desktop\\Testes MATLAB\\DataBase\\Opponent";
			//changeName(folder,img,"BCE","BCE_Max");
			toGrayscale(folder,img,"R_C");
		}
	}
	
	/*
	 * Dado uma pasta do tipo "C:\\Users\\Users\\Desktop\\DataBase\\Ball", o número da imagem
	 * o seu nome atual e o seu novoNome, altera o nome da imagem selecionada.
	 * 
	 * Ex: changeName("C:\\Users\\Users\\Desktop\\DataBase\\Ball",img,"BCE","BCE_Max");
	 */
	public static void changeName(String folder, int number, String currentName, String newName) {
		String name = folder+"\\"+currentName;
		name  = name.concat(Integer.toString(number) + ".jpg");
		System.out.println(name);
		
		Mat image = Imgcodecs.imread(name);
		
		Imgcodecs.imwrite((folder+"\\"+newName).concat(Integer.toString(number) + ".jpg"),image);
	}
	
	/*
	 * Dado uma pasta do tipo "C:\\Users\\Users\\Desktop\\DataBase\\Ball", o número da imagem
	 * o seu nome atual, converte a imagem para grayscale.
	 * 
	 * Ex: toGrayscale("C:\\Users\\Users\\Desktop\\DataBase\\Ball",img,"R");
	 */
	public static void toGrayscale(String folder, int number, String currentName) {
		String name = folder+"\\"+currentName;
		name  = name.concat(Integer.toString(number) + ".jpg");
		System.out.println(name);
		
		Mat image = Imgcodecs.imread(name);
		
		Imgcodecs.imwrite(name,matGrayscale_Luminosity(image));
	}
	
	/*
	 * desatualizado
	 */
	public static void renameMarks() {
		for(int i=0,j=102;i<marcas.length;i++,j++) {
			for(int k=0;k<4;k++) {
				String nome = null,nome2 = null;
				
				switch(k) {
					case 0:{ 
						nome = "A";
						nome2 = "M_";
						break;
					}
					case 1:{
						nome = "BC";
						nome2 = "BC_M";
						break;
					}
					case 2:{
						nome = "BCG";
						nome2 = "BCG_M";
						break;
					}
					case 3:{
						nome = "BCGL";
						nome2 = "BCGL_M";
						break;
					}
				}
				
				nome  = nome.concat(Integer.toString(marcas[i]) + ".jpg");
				System.out.println(nome);
				
				Mat imagem = Imgcodecs.imread(nome);
				
				nome2  = nome2.concat(Integer.toString(j) + ".jpg");
				
				Imgcodecs.imwrite(nome2, imagem);
			}			
		}	
	}

	/*
	 * desatualizado
	 */
	public static void test() {
		for(int t = 1; t<=230;t++){  	
			String nome = "R_M";
				
			nome  = nome.concat(Integer.toString(t) + ".jpg");
				
			System.out.println(nome);
				
			Mat imagem = Imgcodecs.imread(nome);
					
			Imgcodecs.imwrite(nome,matGrayscale_Average(imagem));
			
			//matExtractMax(imagem, 100);
			//imagem = matGrayscale_Luminosity(imagem);
			//matExtractMin(imagem, 200);
			
			//String nome2 = "BCE";
			//nome2 = nome2.concat(Integer.toString(t) + ".jpg");
			Imgcodecs.imwrite(nome,imagem);
			
		} 
	}
}

