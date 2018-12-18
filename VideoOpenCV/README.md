# Biblioteca Matriz 

**Descrição:** Biblioteca Java para manipular e visualizar matrizes dadas pelo OpenCV.

##


## 1. Instalação na Raspberry:

Como instalar OpenCV 3.4.0 JAVA na Raspberry:

Digitar no terminal:

Passo 0 . 
	sudo apt-get purge openjdk-8-jre-headless
	sudo apt-get install openjdk-8-jre-headless
	sudo apt-get install openjdk-8-jre

Passo 1 . 
			
	sudo apt-get update && sudo apt-get install oracle-java7-jdk cmake ant
			
Passo 2 . 
			
	sudo apt-get install build-essential cmake pkg-config libpng12-0 libpng12-dev 
	libpng++-dev libpng3 libpnglite-dev zlib1g-dbg zlib1g zlib1g-dev pngtools  libtiff4 
	libtiffxx0c2 libtiff-tools libjpeg8 libjpeg8-dev libjpeg8-dbg libjpeg-progs libavcodec-dev   
	libavformat-dev libgstreamer0.10-0-dbg libgstreamer0.10-0 libgstreamer0.10-dev  libunicap2 
	libunicap2-dev libdc1394-22-dev libdc1394-22 libdc1394-utils swig libv4l-0 libv4l-dev
    
OBS: se aparecer qualquer problema digitar o seguinte:
		
    sudo rm /var/lib/apt/lists/* ; sudo rm /var/lib/apt/lists/partial/* ; sudo apt-get -f install ; sudo apt-get clean ; sudo apt-get update
    
Após execute o passo 2 novamente

Passo 3 .
			
	Abrir o arquivo em sudo leafpad ~/.bashrc
			
Passo 4 . 
		
	acrescentar no final do documento/:

		export ANT_HOME=/usr/share/ant/
		export PATH=${PATH}:${ANT_HOME}/bin
		export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/
		export PATH=$PATH:$JAVA_HOME/bin
				
Passo 5 .
			
	Salvar
			
Passo 6 . 
		
	sudo reboot

Para garantir execute os seguintes passos adicionais:
			
	sudo update-alternatives --config javac (escolha o que tem o java 1.8)
           
	sudo update-alternatives --config java (escolha o que tem o java 1.8)

No terminal:

Passo 7 .

	baixar o sources (zip) do release mais atual de https://opencv.org/releases.html
			
Passo 8 .
			
	colocar o zip na pasta pi
			
Passo 9 .
			
	unzip opencv.zip 
		
Passo 10 .
			
	cd opencv-3.4.0/
			
Passo 11 .
			
	mkdir build
			
Passo 12 .
		
	cd build
		
Passo 13 .
			
	cmake -D CMAKE_BUILD_TYPE=RELEASE -D WITH_OPENCL=OFF -D BUILD_PERF_TESTS=OFF -D BUILD_SHARED_LIBS=OFF -D JAVA_INCLUDE_PATH=$JAVA_HOME/include -D JAVA_AWT_LIBRARY=$JAVA_HOME/jre/lib/arm/libawt.so -D JAVA_JVM_LIBRARY=$JAVA_HOME/jre/lib/arm/server/libjvm.so -D CMAKE_INSTALL_PREFIX=/usr/local ..
			
Passo 14 .

	make
			
Passo 15 .
				
	sudo chmod -R 777 /usr/local/include
	sudo chmod -R 777 /usr/local/lib
	sudo chmod -R 777 /usr/local/share
	sudo chmod -R 777 /usr/local/bin
			
Passo 16 .
			
	make install


### 1.ECLIPSE

Para instalar o ECLIPSE:

	No terminal: 
	
		sudo apt-get install eclipse

Para colocar as bibliotecas no Eclipse:

* Abra o eclipse (sudo eclipse)
* em window -> preferences
* em java -> build path -> user libraries
* New...
* User Library name: OpenCV-3.1.0 (OK)
* Add External JARs...
* Procure por /home/pi/opencv-3.1.0/build/bin/opencv-310.jar
* em Native Library Location, clique em escolher diretório e adicione: /usr/local/share/OpenCV/java ou /home/pi/opencv-3.4.0/build/lib (o arquivo que puxará a lib é o libopencv_java340.so)
* OK!

Crie um novo projeto Java:

* Coloque o nome e clique em next>
* em Libraries clique em Add Library...
* User Library
* Selecione OpenCV-3.1.0
* Finish
* Crie a Classe e começe a brincadeira


## 4.Apoio

<img src="http://www.fc.unesp.br/Home/Cursos/Fisica/fisica-fapesp.png" width="200">
  
<img src = "http://proad.ufabc.edu.br/images/headers/logo_ufabc.png" width="100">

## 5.Referências 