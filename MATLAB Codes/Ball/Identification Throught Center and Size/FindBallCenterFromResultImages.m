%% FIND THE CENTER OF THE BALL

clear
clc

% determinando o tamanho da imagem em largura e altura
heigth = 160;
width = 120;

% determinando a quantidade de imagens no banco de dados da bola
quant = 322;

% inicializando vetores de banco de dados da bola
ballDB = zeros(width,heigth,quant);

%inicializando o vetor de dados de X, Y e Size da Bola
dataXYS = zeros(quant,3);

% Determina em qual pasta se encontra as imagens capturadas e tratadas
folder='C:\Users\Gilmar Jeronimo\Desktop\Testes MATLAB\DataBase\Ball\R';

for i = 1:quant
    %Abre a imagem e passa para o vetor ballDB
    ballDB(:,:,i) = imread(strcat(folder,int2str(i-1),'.jpg'));
    %Imprime qual imagem abriu
    disp(strcat(folder,int2str(i-1),'.jpg'));
      
    %Retorna a posição do último pixel pintado
    [yFinal,xFinal] = find(ballDB(:,:,i),1,'last');
    %Retorna a posição do primeiro pixel pintado
    [yInitial,xInitial] = find(ballDB(:,:,i),1,'first');
    
    %Calcula-se o tamanho da bola na dimensão X e Y 
    SizeX = xFinal-xInitial;
    SizeY = yFinal-yInitial;
    
    %Atualiza o vetor de dados para apresentar o centro (X, Y) da bola e o
    %seu tamanho
    dataXYS(i,:) = int32([(xInitial+(SizeX/2)) (yInitial+(SizeY/2)) SizeX]);
end

%salva em um .mat
save('ballDataXYS.mat','dataXYS');