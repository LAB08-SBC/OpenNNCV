package OpenRNAFC;

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
	int inputLayer = 0;                 // quantidade de dados de input
	int neurons = 0;                    // quantidade de neurônios na camada oculta
	int outputLayer = 0;                // quantidade de saídas desejadas
	double [][]pixels,labels;           // vetor de valores de cada pixel e label das RNA's
	boolean part1 = true, part2 = true; // variável de controle de transição de estados
	String folder, weightsMatName, inputsMatName, labelsMatName;            // nomes dos arquivos, em pesos, inputs e labels .mat
	
	
	/* 
	 * Constroí o Objeto NeuralNet passando os nomes dos arquivos de pesos sinápticos,
	 * arquivos de inputs, arquivos de labels, número de inputs, neurônios na camada 
	 * oculta, e número de saídas.
	 */
	public NeuralNet(String folder, String weightsMatName, String inputsMatName, String labelsMatName, int IL, int N, int OL) throws FileNotFoundException, IOException{
		setFolder(folder);
		setWeightsMatName(weightsMatName);
		setInputsMatName(inputsMatName);
		setLabelsMatName(labelsMatName);
		setInputLayer(IL);
		setNeurons(N);
		setOutputLayer(OL);
		setWeights(matlabWeights());
	}
	
	/*
	 * =============================== DEFININDO GETTERS E SETTERS =========================================
	 */
	
	private void setWeights(double[] weights) {
		this.weights = weights;
	}
	
	private void setInputLayer(int inputLayer) {
		this.inputLayer = inputLayer;
	}
	
	private void setNeurons(int neurons) {
		this.neurons = neurons;
	}
	
	private void setOutputLayer(int outputLayer) {
		this.outputLayer = outputLayer;
	}
	
	private void setFolder(String folder) {
		this.folder = folder;
	}
	
	private void setWeightsMatName(String weightsMatName) {
		this.weightsMatName = weightsMatName;
	}
	
	private void setInputsMatName(String inputsMatName) {
		this.inputsMatName = inputsMatName;
	}
	
	private void setLabelsMatName(String labelsMatName) {
		this.labelsMatName = labelsMatName;
	}
	
	private void setPixels(double[][] pixels) {
		this.pixels = pixels;
	}
	
	private void setLabels(double[][] labels) {
		this.labels = labels;
	}
	
	public double[] getWeights() {
		return this.weights;
	}
	
	public int getInputLayer() {
		return this.inputLayer;
	}
	
	public int getNeurons() {
		return this.neurons;
	}
	
	public int getOutputLayer() {
		return this.outputLayer;
	}
	
	public String getFolder() {
		return this.folder;
	}
	
	public String getWeightsMatName() {
		return this.weightsMatName;
	}
	
	public String getInputsMatName() {
		return this.inputsMatName;
	}
	
	public String getLabelsMatName() {
		return this.labelsMatName;
	}

	public double[][] getPixels(){
		return this.pixels;
	}
	
	public double[][] getLabels(){
		return this.labels;
	}
	/*
	 * =====================================================================================================
	 */
	
	/*
	 * ============================================ METHODS ================================================
	 */
	
	/*
	 * Retorna o mínimo valor de um vetor
	 */
	public double min(double[] array) {
		double min = array[0];
		
		for(int m=0;m<array.length && min!=0;m++) {
			if(array[m]<min)
				min = array[m];
		}

		return min;
	}
	
	/*
	 * Retorna o máximo valor de um vetor
	 */
	public double max(double[] array) {
		double max = array[0];
		
		for(int n=0;n<array.length && max!=255;n++) {
			if(array[n]>max)
				max = array[n];
		}
			
		return max;
	}
	
	/*
	 * Retorna um vetor sendo a posição [0] o máximo e [1] o mínimo de um vetor teste
	 */
	public double[] maxmin(double[] array) throws InterruptedException{
		
		double[] norm = new double[2];
		norm[0] = array[0]; // max
		norm[1] = array[0]; // min
		
		for(int n=0;n<array.length;n++) {
			if(array[n]>norm[0])
				norm[0] = array[n];
			if(array[n]<norm[1])
				norm[1] = array[n];
		}
		
		return norm;
	}

	/*
	 * Converte o vetor de pesos sinápticos do MATLAB para um vetor double w
	 * O mesmo para a imagens e labels.
	 * 
	 * pix e lab são matrizes no qual a primeira linha representa o dado de todos os 
	 * primeiros pixels e labels das imagens de treino, sendo cada coluna uma imagem
	 * da base de dados. i.e. pix[largura*comprimento][quant de imagens treinadas]
	 */
	public double[] matlabWeights(){
		
		double[] w = null; 
		
		try{
			MatFileReader mfr1 = new MatFileReader(getFolder()+getWeightsMatName()+".mat");
			Map<String, MLArray> mlArrayRetrived = mfr1.getContent();
			MLArray W = mlArrayRetrived.get(getWeightsMatName().substring(getWeightsMatName().indexOf("/")+1));
			w = ((MLDouble)W).getArray()[0];
		} catch(Exception e){
			System.out.println("Arquivo 1 não abriu");
		}
		
		
		try{
			MatFileReader mfr2 = new MatFileReader(getFolder()+getInputsMatName()+".mat");
			Map<String, MLArray> mlArrayRetrived2 = mfr2.getContent();
			MLArray Img = mlArrayRetrived2.get(getInputsMatName().substring(getInputsMatName().indexOf("/")+1));

			setPixels(((MLDouble)Img).getArray());
		} catch(Exception e){
			System.out.println("Arquivo 2 não abriu");
		}

		try{
			MatFileReader mfr3 = new MatFileReader(getFolder()+getLabelsMatName()+".mat");
			Map<String, MLArray> mlArrayRetrived3 = mfr3.getContent();
			MLArray Lab = mlArrayRetrived3.get(getLabelsMatName().substring(getLabelsMatName().indexOf("/")+1));
	
			setLabels(((MLDouble)Lab).getArray());
		} catch(Exception e){
			System.out.println("Arquivo 3 não abriu");
		}
		
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
	public void normalizeInput(double[] image) throws FileNotFoundException, IOException, InterruptedException {
		
		double max,min;
		
		double[][] pixels = getPixels(); 
		
		for(int i = 0; i<image.length;i++) {
			if(part1)
				Arrays.sort(pixels[i]);
			
			min = pixels[i][0];
			max = pixels[i][pixels[i].length-1];
			
			image[i] = (2*(image[i]-min)/(max-min))-1;
		}
		
		part1 = false;
	}
	
	/*
	 * Esta função normaliza a saída da RNA, colocando seus valores entre entre o resultado dos labels.
	 * 
	 * A normalização reversa é realizada pela fórmula para cada saída:
	 * 
	 *             (valor+1)*(max - min)   
	 * normOut=    ---------------------   + min
	 *                      2
	 */
	public void normalizeOutput(double[] resultOL) throws FileNotFoundException, IOException, InterruptedException {
		
		double max,min;
		
		double[][]labels = getLabels();
		
		for(int i = 0; i<getOutputLayer();i++) {
			if(part2)
				Arrays.sort(labels[i]);
			
			min = labels[i][0];
			max = labels[i][labels[i].length-1];
			
			resultOL[i] = ((resultOL[i]+1)*(max-min)/2)+min;
		}
		
		part2 = false;
	}
	
	/*
	 * calcula a saída da RNA, similar ao sim do MATLAB.
	 */
	public double[] calculate(double[] matrix) throws FileNotFoundException, IOException, InterruptedException {
		
		double[] image = matrix.clone();   
		normalizeInput(image);    // A imagem de entrada é normalizada
	
		double[] resultHL = new double[getNeurons()];
		
		int j = 0;
		
		double[] weights = getWeights();
		
		/* 
		 * Esse primeiro for realiza o cálculo das saída da camada oculta, pegando
		 * os resultados de cada nerônio no resultHL.
		 */
		for(int i =0;i<getNeurons();i++) {
			resultHL[i]=0;
			
			for(int cont = 0;cont<getInputLayer()+1;j++,cont++) {
				if(cont != 0)
					resultHL[i] += image[cont-1]*weights[j]; //pesos multiplicados pelas entrada
				else
					resultHL[i] += weights[j];                //soma do bias
			}
			
			resultHL[i] = (2/(1+Math.exp(-2*resultHL[i])))-1; //função de ativação tansig
		}
		
		double[] resultOL = new double[getOutputLayer()];
		
		/*
		 * Esse segundo for calcula a saída do layer de output
		 */
		for(int i =0;i<getOutputLayer();i++) {
			resultOL[i]=0;
			
			for(int cont = 0;cont<getNeurons()+1;j++,cont++) {
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
