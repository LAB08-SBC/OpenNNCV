# OpenNNCV

**Descrição:** A Biblioteca _Open Source Neural Net for Computer Vision_ (OpenNNCV) é uma biblioteca que utiliza o treinamento de uma rede neural no MATLAB e converte seus pesos neurais em uma rede neural MLP em Java sendo possível identificar objetos. Essa biblioteca conta com as bibliotecas OpenCV [1], MATIO [2] e Matriz.


**Objetivo:** Este repositório tem o objetivo de compartilhar os conhecimentos obtidos durante a iniciação científica [3], além de aprimorar o estudo da biblioteca OpenCV [1], possibilitando um uso simples e educacional desta plataforma.

## Arquitetura 


A arquitetura proposta da Figura 5.1 é dividida nos módulos de _hardware_ e _software_. O _hardware_ é composto pelo sistema de visão, jogador robótico e o projeto eletrônico. A _webcam_ Logitech c920 [4] (c) juntamente com o suporte _pan/tilt_ (d) com dois servos fazem parte do sistema de visão. Este sistema comunica com a Raspberry Pi [5] (e), através do OpenCV [1], que processa os dados da imagem e identifica todos os objetos presentes no campo. O _shield_ Raspi2Dynamixel (f) é responsável por controlar os servos motores da plataforma Bioloid ROBOTIS Premium [6] (g), que é o jogador robótico responsável pelos movimentos do humanoide em uma partida de futebol.

O _software_ está relacionado com a implementação da técnica de identificação dos objetos presentes no campo de futebol, como a bola, linhas, traves, adversários e companheiros de time. Além disso, pode-se observar o banco de imagens em (a) e o computador (b) que executa duas toolboxes do MATLAB [7] conhecidas como _Neural Network Toolbox_ e _Statistics and Machine Learning Toolbox_.

<p align="center">
<img src="https://user-images.githubusercontent.com/28567780/50059602-35ace900-0170-11e9-9894-4a167269b262.png" width="800">
</p>

Foi proposto uma arquitetura para o uso de técnicas de processamento de imagem e visão computacional, além da utilização da Raspberry Pi 3B para controlar os servos motores do humanoide de uma forma prática. Os detalhes do controle dos motores pode ser encontrado em [8].

O módulo de _software_ da Figura está relacionado com a implementação de técnicas, por exemplo, RNA’s e SVM’s, responsáveis pela identificação dos objetos presentes no campo. Pode-se observar que existe um banco de dados de imagens (a) e um computador (b) executando duas _toolboxes_ do MATLAB.

O banco de dados de (a) contêm imagens utilizadas na RNA e SVM, juntamente
com os _toolboxes_ (b) do MATLAB. Após o treinamento e validação da rede, um arquivo “.mat” (h) é gerado com os pesos de todos os neurônios artificiais. É importante destacar que as camadas de entrada, ocultas, saída e as funções de ativação também são consideradas no cálculo do peso.

Após ter gerado o arquivo “.mat”, as bibliotecas implementadas em Java e conhecidas como MATIO [2] e NeuralNet, são utilizadas para abrir o arquivo e convertê-los para pesos sinápticos de uma RNA em Java. Com a RNA convertida, é possível pegar as imagens obtidas pelo OpenCV [1] e ajustadas com uma biblioteca complementar em Java, nomeada Matriz, para obter resultados em tempo real da posição do objeto a ser identificado.

No treinamento das RNA’s considerou-se algumas configuração como, diferentes tipos de filtro no domínio do tempo e conversões aplicando transformada de Fourier discreta em 2D, para serem dados de entrada de treino de uma RNA. Além disso, foram executados testes com outras funções de ativação dos neurônios, como a gaussiana, e a aplicação de _layers_ de _Pooling_ utilizados em Redes Neurais Artificiais de Convolução [9]. 

Os principais resultados obtidos foram executar as RNA’s como uma aproximação do _pixel_ de centro e tamanho em _pixels_ da bola, como um filtro linear, no qual a saída da rede é uma imagem filtrada, e como um classificador de _frames_ da imagem, identificando se partes da imagem contém características do objeto procurado. Esse tipo de rede, de classificação, foi utilizado para marcações do campo, jogadores e gol.


## Orientações do Repositório

**_VideoOpenCV:_** Códigos em Java no qual se capturava as imagens e salva de acordo com suas propriedades e filtros aplicados. São os código disponíveis:
	
* VideoOpenCV.java: Coloca na tela a imagem capturada por uma _webcam_ conectado ao computador ou ao microcontrolador, e organiza por pasta e tipo as fotos capturadas. As _%-##.jpg_ as imagens originais, _BC-%##.jpg_ as imagens aplicadas brilho e contraste, _BCG-%##.jpg_ as imagens com brilho, contraste e filtro _grayscale_ e _BCGL-%##.jpg_ as imagens com brilho, contraste, filtro _grayscale_ e filtro laplaciano. As imagens que aparecem com R seriam os resultados desenvolvidos manualmente para realizar o treinamento das redes neurais. Os termos % podem representar C - Adversários, M - Marcações, G - Gol, enquanto ## são os números das imagens. 

* Matriz.java: Biblioteca desenvolvida para manipular e visualizar mais facilmente imagens tratadas e coletadas pelo OpenCV, mais detalhes em [10]. 

* ImageViewer.java: Código adaptado de [11] para plotar vídeo na tela utilizando OpenCV.

**_LaplacianFilter:_** Contém todas as imagens capturadas utilizadas, os códigos dentro dessa pasta realizam alguma operação com as imagens, seja aplicar novos filtros, converter tamanho das matrizes, ou somente renomear as imagens. 
	
**_MATLAB Codes:_** Nessa pasta se encontram os códigos utilizados para treinar as redes neurais. Mais informações em [12].

**_OpenRNA:_** Contém a biblioteca NeuralNet desenvolvida para conversão das redes neurais do MATLAB em redes neurais Java, além do código de identificação em tempo real em OpenRNA.java. Contém também as instruções de instalação do OpenCV. Mais informações em [13]
	
## Contato

Gilmar Correia Jeronimo

e-mail: gilmarjeronimo@uol.com.br

Paulo Consoni

e-mail: paulo.consoni4000@gmail.com

## Apoio

<img src="http://www.fc.unesp.br/Home/Cursos/Fisica/fisica-fapesp.png" width="200">
  
<img src = "http://proad.ufabc.edu.br/images/headers/logo_ufabc.png" width="100">

## Referências 

[1] OpenCV. **OpenCV**. Disponível em: <https://opencv.org/>. Acesso em 16 de Dezembro de 2018.

[2] tbeu.**MATLAB MAT File I/O Library**. Disponível em: <https://github.com/tbeu/matio>. Acesso em 16 de Dezembro de 2018.

[3] JERONIMO, G. C; BOTELHO, W. T. **Implementação de Técnica de Processamento de Imagens para a Categoria Kid Size da RoboCup com Validação Real na Plataforma Bioloid ROBOTIS Premium**. FAPESP, UFABC, 2016.

[4] LOGITECH©. **Specifications**. In: Logitech. Disponível em: <http://support.logitech.com/en_us/product/hd-pro-webcam-c920/specs>. Acesso em 20 de Setembro de 2017: Logitech, 2017.

[5] FOUNDATION, R. P ©. **Raspberry Pi**. Disponível em: <https://www.raspberrypi.org/learning/hardware-guide/components/raspberry-pi/>. Acesso em 12 de Novembro de 2016: RaspberryPi Foundation, 2016.

[6] BIOLOID. **Robotis - Bioloid Premium Kit**. Disponível em: <http://en.robotis.com/index/product.php?cate_code=121010>. Acesso em 24 de Maio de 2016: [s.n.], 2016.

[7] GUIDE, MATLAB User’s. **The mathworks**. Inc., Natick, MA, v. 5, p. 333, 1998.

[8] JERONIMO, G. C; CONSONI, P.; BOTELHO, W. T. **BioloidCodes**. Disponível em: <https://github.com/LAB08-SBC/BioloidCodes>. Acesso em 16 de Dezembro de 2018.

[9] KRIZHEVSKY, A.; SUTSKEVER, I.; HINTON, G. E. Imagenet classification with deep
convolutional neural networks. In: **Advances in neural information processing systems**. [S.l.: s.n.], 2012. p. 1097–1105. 

[10] JERONIMO, G. C; CONSONI, P.; BOTELHO, W. T. **VideoOpenCV**. Disponível em: <https://github.com/LAB08-SBC/OpenNNCV/tree/master/VideoOpenCV>. Acesso em 16 de Dezembro de 2018.

[11] BAGGIO, D. L. **OpenCV 3.0 Computer Vision with Java**. Packt Publishing Ltd, 2015.

[12] JERONIMO, G. C; CONSONI, P.; BOTELHO, W. T. **MATLAB Codes**. Disponível em: <https://github.com/LAB08-SBC/OpenNNCV/tree/master/MATLAB%20Codes>. Acesso em 16 de Dezembro de 2018.

[13] JERONIMO, G. C; CONSONI, P.; BOTELHO, W. T. **OpenRNA**. Disponível em: <https://github.com/LAB08-SBC/OpenNNCV/tree/master/OpenRNA>. Acesso em 16 de Dezembro de 2018.


