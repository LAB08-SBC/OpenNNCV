import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class NeuralNet {
	
	double[] weights;
	int InputLayer = 0;
	int Neurons = 0;
	int OutputLayer = 0;
	double maxOut, minOut;
	double [][]pix,lab;
	boolean cont2;
	int cont1 = 0;
	double []max,min;
	double []maxL,minL;
	String name, ANNi, ANNl;
	
	public NeuralNet(String nome, String ANNimagens, String ANNlabels, int IL, int N, int OL) throws FileNotFoundException, IOException{
		weights = matlabWeights(nome,ANNimagens,ANNlabels);
		InputLayer = IL;
		Neurons = N;
		OutputLayer = OL;
		name = nome;
		ANNi = ANNimagens;
		ANNl = ANNlabels;
		maxOut = 0;
		minOut = 0;
		cont1 = 0;
		cont2 = true;
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
	
	public void normalizeInput(double[] imagem) throws FileNotFoundException, IOException {
	
		for(int i = 0; i<imagem.length;i++) {
			
			if(cont1!=2) {
				max = new double[imagem.length];
				min = new double[imagem.length];
				max[i] = max(pix[i]);
				min[i] = min(pix[i]);
			}
			
			imagem[i] = (2*(imagem[i]-min[i])/(max[i]-min[i]))-1;
		}
		
		//cont1++;
	}
	
	public void normalizeOutput(double[] resultOL) throws FileNotFoundException, IOException {
		
		for(int i = 0; i<OutputLayer;i++) {
			if(cont2) {
				maxL = new double[OutputLayer];
				minL = new double[OutputLayer];
				maxOut = max(lab[i]);
				maxL[i] = max(lab[i]);
				minOut = min(lab[i]);
				minL[i] = min(lab[i]);	
			}
			resultOL[i] = ((resultOL[i]+1)*(maxL[i]-minL[i])/2)+minL[i];
		}
		
		//cont2 = false;
	}
	
	public double[] calculate(double[] matrix) throws FileNotFoundException, IOException {
		
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
