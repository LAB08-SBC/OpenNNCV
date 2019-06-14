import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;


/*
 * Biblioteca NeuralNet
 * 
 * Descrição: Biblioteca utilizada para converter uma rede neural feed forward do 
 * MATLAB para Java, usá-se os pesos sinápticos, as entradas e saídas, para normali-
 * zação dos dados.
 * 
 * Desenvolvido por:
 *   Gilmar Correia Jeronimo
 *   Universidade Federal do ABC 
 *   Apoio: FAPESP
 */

public class NeuralNet {
	
	double[] weights;                   // vetor de pesos sinápticos
	int InputLayer = 0;                 // quantidade de dados de input
	int Neurons = 0;                    // quantidade de neurônios na camada oculta
	int OutputLayer = 0;                // quantidade de saídas desejadas
	double [][]pix,lab;                 // vetor de valores de cada pixel e label das RNA's
	boolean part1 = true, part2 = true; // variável de controle de transição de estados
	String name, ANNi, ANNl;            // nomes dos arquivos, em pesos, inputs e labels .mat
	
	
	/* Constroí o Objeto NeuralNet passando os nomes dos arquivos de pesos sinápticos,
	 * arquivos de inputs, arquivos de labels, número de inputs, neurônios na camada 
	 * oculta, e número de saídas.
	 */
	public NeuralNet(String nome, String ANNimagens, String ANNlabels, int IL, int N, int OL) throws FileNotFoundException, IOException{
		weights = matlabWeights(nome,ANNimagens,ANNlabels);
		InputLayer = IL;
		Neurons = N;
		OutputLayer = OL;
		name = nome;
		ANNi = ANNimagens;
		ANNl = ANNlabels;
	}
	
	/*
	 * Retorna o mínimo valor de um vetor
	 */
	public double min(double[] teste) {
		double min = teste[0];
		
		for(int m=0;m<teste.length && min!=0;m++) {
			if(teste[m]<min)
				min = teste[m];
		}

		return min;
	}
	
	/*
	 * Retorna o máximo valor de um vetor
	 */
	public double max(double[] teste) {
		double max = teste[0];
		
		for(int n=0;n<teste.length && max!=255;n++) {
			if(teste[n]>max)
				max = teste[n];
		}
			
		return max;
	}
	
	/*
	 * Retorna um vetor sendo a posição [0] o máximo e [1] o mínimo de um vetor teste
	 */
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

	/*
	 * Converte o vetor de pesos sinápticos do MATLAB para um vetor double w
	 * O mesmo para a imagens e labels.
	 * 
	 * pix e lab são matrizes no qual a primeira linha representa o dado de todos os 
	 * primeiros pixels e labels das imagens de treino, sendo cada coluna uma imagem
	 * da base de dados. i.e. pix[largura*comprimento][quant de imagens treinadas]
	 */
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
	
	/*
	 * Esta função normaliza a entrada de cada imagem, colocando seus valores entre 0 e 1.
	 * A imagem como vetor unidimensional entra nessa função e é realizado uma ordenação
	 * no vetor pix para cada pixel da imagem. Ao ordenar, o mínimo valor de todos o 
	 * banco de dados para o i-pixel analisado estará na posição 0, enquanto o máximo para
	 * o i-pixel estará na última posição.
	 * 
	 * A normalização é realizada pela fórmula para cada pixel de entrada:
	 * 
	 *           2*(valor-min)   
	 * normInp=  -------------   - 1
	 *            (max - min)
	 */
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
	
	/*
	 * Esta função normaliza a saída da RNA, colocando seus valores entre entre o resultado dos labels.
	 * 
	 * A normalização reversa é realizada pela fórmula para cada saída:
	 * 
	 *             (valor+1)*(max - min)   
	 * normOut=   ----------------------   + min
	 *                      2
	 */
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
	
	/*
	 * calcula a saída da RNA, similar ao sim do MATLAB.
	 */
	public double[] calculate(double[] matrix) throws FileNotFoundException, IOException, InterruptedException {
		
		double[] imagem = matrix.clone();   
		normalizeInput(imagem);    // A imagem de entrada é normalizada
	
		double[] resultHL = new double[Neurons];
		
		int j = 0;
		
		
		/* 
		 * Esse primeiro for realiza o cálculo das saída da camada oculta, pegando
		 * os resultados de cada nerônio no resultHL.
		 */
		for(int i =0;i<Neurons;i++) {
			resultHL[i]=0;
			
			for(int cont = 0;cont<InputLayer+1;j++,cont++) {
				if(cont != 0)
					resultHL[i] += imagem[cont-1]*weights[j]; //pesos multiplicados pelas entrada
				else
					resultHL[i] += weights[j];                //soma do bias
			}
			
			resultHL[i] = (2/(1+Math.exp(-2*resultHL[i])))-1; //função de ativação tansig
		}
		
		double[] resultOL = new double[OutputLayer];
		
		/*
		 * Esse segundo for calcula a saída do layer de output
		 */
		for(int i =0;i<OutputLayer;i++) {
			resultOL[i]=0;
			
			for(int cont = 0;cont<Neurons+1;j++,cont++) {
				if(cont != 0)
					resultOL[i] += resultHL[cont-1]*weights[j]; // pesos multiplicados pelo resultado dos neurônios ocultos
				else
					resultOL[i] += weights[j];                  // soma do bias
			}
		}
		
		normalizeOutput(resultOL); // normalização da saída
		
		return resultOL;
	}
	
}
