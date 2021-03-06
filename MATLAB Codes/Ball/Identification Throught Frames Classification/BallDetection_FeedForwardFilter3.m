%% ABRINDO AS IMAGENS
clear
clc

load('dataFramesBall.mat')

% determinando o tamanho da imagem em largura e altura
heigth = 160;
width = 120;

beginDB = 59;
endDB = 310;
lastDB = 321;
trainDataSet = (endDB-beginDB)+1;
totalDataSet = (lastDB-beginDB)+1;

% scale � a escala aplicada para se obter uma imagem 8x6
scale = 1/20;

% Inicializa um vetor de tamanho que guardar� as imagens segmentadas
% [8*6,totalDataSet,400] = [mascara, total de imagens, subImagens de 
% uma imagem]
segImage = zeros((width*scale)*(heigth*scale),totalDataSet,(int32(1/(scale*scale))));
%segImage = zeros((width*scale)*(heigth*scale)*3,totalDataSet,(int32(1/(scale*scale))));

% Definindo vetor de Inputs
ANNballI = zeros((width*scale)*(heigth*scale),totalDataSet*(int32(1/(scale*scale))));
%ANNballI = zeros((width*scale)*(heigth*scale)*3,totalDataSet*(int32(1/(scale*scale))));

% Definindo vetor de labels
ANNballL = data;

% Definindo vetor de valida��es
ANNvalidations = zeros(20,totalDataSet+1);


%Inicializa a pasta usada
folder='C:\Users\Gilmar Jeronimo\Documents\Documentos\Projetos\GitHub\LAB GIT\OpenNNCV\DataBase\Ball\';
%folder='Users/junior/Desktop/Testes MATLAB/DataBase/Ball/';
%folder='E:\Testes MATLAB\DataBase\Ball\';
type = 'BCG';

img=1;
index = 1;

for i = beginDB:lastDB
    % Abre uma imagem por vez
    picture = imread(strcat(folder,type,int2str(i),'.jpg'));
    
    % Verifica se � colorida ou preto e branco
    channels = length(picture(1,1,:));
    
    row = 0;
    col = 0;
    
    for frame = 1:(int32(1/(scale*scale)))
        
        %Inicializa uma m�scara de 6*8
        kernel = zeros((width*scale),(heigth*scale),channels);
        
        if col>=int32(1/scale)
            row = row + 1;
            col = 0;
        end   
        
        % a mascara/kernel ser� a imagem capturada [(1+(row*8)):(1+row)*8,(1+(col*6)):(col+1)*6]
        kernel = picture((1+(row*(width*scale))):((row+1)*(width*scale)),(1+(col*(heigth*scale))):((col+1)*(heigth*scale)),:);
        
        % converte a subimagem imagem 2D colorida ou grayscale em um vetor 1D
        segImage(:,img,frame) = reshape(kernel,(width*scale)*(heigth*scale)*channels,1);
        col= col +1;
            
        % Salva o vetor 1D em ANN ball Inputs
        ANNballI(:,index) = segImage(:,img,frame);
        
        index = index + 1;
    end
    img = img +1;
end

ANNballL = ANNballL';

% Salva em um .mat os inputs e labels da rede neural para ser usado no java
save('ANNballI.mat','ANNballI');
save('ANNballL.mat','ANNballL');

%% Mostrando a imagem segmentada

 img = 134; 
 index = (((img-1)*int32(1/(scale*scale)))+1);
 for i = 1:1/scale
     for j = 1:1/scale
         figure(1);
             graph = subplot('Position',[((j-1)*scale) (1-(i*scale)) 0.045 0.045]);
             %graph = subplot('Position',[((j-1)*scale) (1-(i*scale)) scale scale]);
             imshow(uint8(reshape(segImage(:,img,((1/scale)*(i-1))+j),(width*scale),(heigth*scale),channels)));
             text(graph,4,3,1,int2str(data(index)))
             index = index +1;
     end
 end

%% TREINANDO O FILTRO

for hiddenNeurons = 6%1:12
    %netFilter = feedforwardnet(hiddenNeurons);
    netFilter = newff(hiddenNeurons,ANNballI(:,1:(trainDataSet*int32(1/(scale*scale))))',ANNballL(1:(trainDataSet)*(int32(1/(scale*scale))))');
    netFilter.trainFcn = 'trainrp';
    netFilter.trainParam.max_fail = 100;
    netFilter.trainParam.time = 360;
    
    netFilter = train(netFilter,ANNballI(:,1:(trainDataSet*int32(1/(scale*scale)))),ANNballL(1:(trainDataSet)*(int32(1/(scale*scale)))));
    
 %% SALVANDO OS PESOS PARA O JAVA    
 
    bR= [cell2mat(netFilter.b(1,:))' cell2mat(netFilter.b(2,:))'];
    IWR = cell2mat(netFilter.IW(1,:));
    LWR = cell2mat(netFilter.LW(2,1));

    WR_BALL = [bR(:,1) IWR(1,:)];

    for i = 2:hiddenNeurons
        WR_BALL = [WR_BALL bR(:,i) IWR(i,:)];
    end

    j = i;
    for k = i+1:i+1
        WR_BALL = [WR_BALL bR(:,k) LWR(k-j,:)];
    end
    
    save('WR_BALL.mat','WR_BALL');
    
%% VALIDANDO FILTRO COM IMAGENS
%     img = 55; 
%     akk2 = uint8(reshape(ANNballI(:,(((img-1)*(int32(1/(scale*scale))))+1):(img*int32(1/(scale*scale)))),width*scale,heigth*scale,3,(int32(1/(scale*scale)))));
%     for i = 1:(int32(1/(scale*scale)))
%         figure(hiddenNeurons);
%         subplot(scale,scale,i),imshow(akk2(:,:,:,i));
%         teste = string(round(sim(netFilter,double(reshape(akk2(:,:,:,i),(width*scale)*(heigth*scale)*3,1)) ) ) );
%         title(teste);
%     end

%% VALIDANDO FILTRO COM DADOS
    
%     ANNvalidations(hiddenNeurons+1,1) = hiddenNeurons;
%     
%     index = 1;
%     for i = 1:totalDataSet
%         ANNvalidations(1,i+1) = i;
%         ANNvalidations(hiddenNeurons+1,i+1) = 0;
%         tic
%         for j = 1:(int32(1/(scale*scale)))
%             if(round(netFilter(ANNballI(:,index))) == ANNballL(index))
%                 ANNvalidations(hiddenNeurons+1,i+1) = ANNvalidations(hiddenNeurons+1,i+1) + (scale*scale);
%             end
%             index = index + 1;
%         end
%         disp(string('Verified Image: ')+ type + num2str(i) + string('.jpg - ')+num2str(toc));
%     end
end

%save(string('ANNvalidationsMarks')+type+string('.mat'),'ANNvalidations');
