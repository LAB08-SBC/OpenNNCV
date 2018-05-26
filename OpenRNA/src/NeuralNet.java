import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class NeuralNet {
	
	double[] weights;
	int InputLayer = 0;
	int Neurons = 0;
	int OutputLayer = 0;
	double [][]pix,lab;
	boolean part1 = true, part2 = true;
	String name, ANNi, ANNl;
	
	public NeuralNet(String nome, String ANNimagens, String ANNlabels, int IL, int N, int OL) throws FileNotFoundException, IOException{
		weights = matlabWeights(nome,ANNimagens,ANNlabels);
		InputLayer = IL;
		Neurons = N;
		OutputLayer = OL;
		name = nome;
		ANNi = ANNimagens;
		ANNl = ANNlabels;
	}
	
	public double min(double[] teste) {
		double min = teste[0];
		
		for(int m=0;m<teste.length && min!=0;m++) {
			if(teste[m]<min)
				min = teste[m];
		}

		return min;
	}
	
	public double max(double[] teste) {
		double max = teste[0];
		
		for(int n=0;n<teste.length && max!=255;n++) {
			if(teste[n]>max)
				max = teste[n];
		}
			
		return max;
	}
	
	public double[] maxmin(double[] teste) throws InterruptedException{
		
		double[] norma = new double[2];
		norma[0] = teste[0]; // max
		norma[1] = teste[0]; // min
		
		for(int n=0;n<teste.length;n++) {
			if(teste[n]>norma[0])
				norma[0] = teste[n];
			if(teste[n]<norma[1])
				norma[1] = teste[n];
		}
		
		return norma;
	}

	public double[] matlabWeights(String nome, String ANNimagens, String ANNlabels) throws FileNotFoundException, IOException {
		
		MatFileReader mfr = new MatFileReader(nome+".mat");
		Map<String, MLArray> mlArrayRetrived = mfr.getContent();
		MLArray W = mlArrayRetrived.get(nome);
		
		double[] w = ((MLDouble)W).getArray()[0];
		
		MatFileReader mfr2 = new MatFileReader(ANNimagens+".mat");
		Map<String, MLArray> mlArrayRetrived2 = mfr2.getContent();
		MLArray Img = mlArrayRetrived2.get(ANNimagens);
		
		pix = ((MLDouble)Img).getArray();

		
		MatFileReader mfr3 = new MatFileReader(ANNlabels+".mat");
		Map<String, MLArray> mlArrayRetrived3 = mfr3.getContent();
		MLArray Lab = mlArrayRetrived3.get(ANNlabels);
		
		lab = ((MLDouble)Lab).getArray();
		
		return w;
	}
	
	public void normalizeInput(double[] imagem) throws FileNotFoundException, IOException, InterruptedException {
		
		double max,min;
		
		for(int i = 0; i<imagem.length;i++) {
			if(part1)
				Arrays.sort(pix[i]);
			
			min = pix[i][0];
			max = pix[i][pix[i].length-1];
			
			imagem[i] = (2*(imagem[i]-min)/(max-min))-1;
		}
		
		part1 = false;
	}
	
	public void normalizeOutput(double[] resultOL) throws FileNotFoundException, IOException, InterruptedException {
		
		double max,min;
		for(int i = 0; i<OutputLayer;i++) {
			if(part2)
				Arrays.sort(lab[i]);
			
			min = lab[i][0];
			max = lab[i][lab[i].length-1];
			
			resultOL[i] = ((resultOL[i]+1)*(max-min)/2)+min;
		}
		
		part2 = false;
	}
	
	public double[] calculate(double[] matrix) throws FileNotFoundException, IOException, InterruptedException {
		
		double[] imagem = matrix.clone();
		normalizeInput(imagem);
	
		double[] resultHL = new double[Neurons];
		
		int j = 0;
		
		for(int i =0;i<Neurons;i++) {
			resultHL[i]=0;
			
			for(int cont = 0;cont<InputLayer+1;j++,cont++) {
				if(cont != 0)
					resultHL[i] += imagem[cont-1]*weights[j];
				else
					resultHL[i] += weights[j];
			}
			
			resultHL[i] = (2/(1+Math.exp(-2*resultHL[i])))-1;
		}
		
		
		
		double[] resultOL = new double[OutputLayer];
		
		for(int i =0;i<OutputLayer;i++) {
			resultOL[i]=0;
			
			for(int cont = 0;cont<Neurons+1;j++,cont++) {
				if(cont != 0)
					resultOL[i] += resultHL[cont-1]*weights[j];
				else
					resultOL[i] += weights[j];
			}
		}
		
		normalizeOutput(resultOL);
		
		return resultOL;
	}
	
}
