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

% scale é a escala aplicada para se obter uma imagem 8x6
scale = 1/20;

% Inicializa um vetor de tamanho que guardará as imagens segmentadas
% [8*6,totalDataSet,400] = [mascara, total de imagens, subImagens de 
% uma imagem]
segImage = zeros((width*scale)*(heigth*scale)*3,totalDataSet,(int32(1/(scale*scale))));
segImage2 = zeros((width*scale)*(heigth*scale),totalDataSet,(int32(1/(scale*scale))));

% Definindo vetor de Inputs
ANNballI = zeros((width*scale)*(heigth*scale)*3,totalDataSet*(int32(1/(scale*scale))));

% Definindo vetor de labels
ANNballL = zeros((width*scale)*(heigth*scale),totalDataSet*(int32(1/(scale*scale))));

% Definindo vetor de validações
ANNvalidations = zeros(20,totalDataSet+1);


%Inicializa a pasta usada
folder='C:\Users\Gilmar Jeronimo\Desktop\Testes MATLAB\DataBase\Ball\';
type = 'A';

img=1;
index = 1;

for i = beginDB:lastDB
    % Abre uma imagem por vez
    picture = imread(strcat(folder,type,int2str(i),'.jpg'));
    picture2 = imread(strcat(folder,'R',int2str(i),'.jpg'));
    % Verifica se é colorida ou preto e branco
    channels = length(picture(1,1,:));
    channels2 = length(picture2(1,1,:));
    
    row = 0;
    col = 0;
    
    for frame = 1:(int32(1/(scale*scale)))
        
        %Inicializa uma máscara de 6*8
        kernel = zeros((width*scale),(heigth*scale),channels);
        kernel2 = zeros((width*scale),(heigth*scale),channels2);
        
        if col>=int32(1/scale)
            row = row + 1;
            col = 0;
        end   
        
        % a mascara/kernel será a imagem capturada [(1+(row*8)):(1+row)*8,(1+(col*6)):(col+1)*6]
        kernel = picture((1+(row*(width*scale))):((row+1)*(width*scale)),(1+(col*(heigth*scale))):((col+1)*(heigth*scale)),:);
        kernel2 = picture2((1+(row*(width*scale))):((row+1)*(width*scale)),(1+(col*(heigth*scale))):((col+1)*(heigth*scale)),:);
        
        % converte a subimagem imagem 2D colorida ou grayscale em um vetor 1D
        segImage(:,img,frame) = reshape(kernel,(width*scale)*(heigth*scale)*channels,1);
        segImage2(:,img,frame) = reshape(kernel2,(width*scale)*(heigth*scale)*channels2,1);
        col= col +1;
            
        % Salva o vetor 1D em ANN ball Inputs
        ANNballI(:,index) = segImage(:,img,frame);
        ANNballL(:,index) = segImage2(:,img,frame);
        index = index + 1;
    end
    img = img +1;
end

% Salva em um .mat os inputs e labels da rede neural para ser usado no java
save('ANNballI.mat','ANNballI');
save('ANNballL.mat','ANNballL');

%% Mostrando a imagem segmentada

% img = 2; 
% %index = (((img-1)*int32(1/(scale*scale)))+1);
% for i = 1:1/scale
%     for j = 1:1/scale
%         figure(1);
%         subplot('Position',[(j-1)*scale (1-(i*scale)) scale scale]),
%         imshow(uint8(reshape(segImage(:,img,((1/scale)*(i-1))+j),(width*scale),(heigth*scale),channels)));
%         
%         %title(int2str(data(index)))
%         %index = index +1;
%     end
% end

%% TREINANDO O FILTRO

for hiddenNeurons = 5
    netFilter = feedforwardnet(hiddenNeurons);
    netFilter.trainFcn = 'trainrp';
    netFilter.trainParam.max_fail = 100;
    netFilter.trainParam.time = 360;
    
    netFilter = train(netFilter,ANNballI(:,1:(trainDataSet*int32(1/(scale*scale)))),ANNballL(:,1:(trainDataSet)*(int32(1/(scale*scale)))));
    
 %% SALVANDO OS PESOS PARA O JAVA    
 
%     bR= [cell2mat(netFilter.b(1,:))' cell2mat(netFilter.b(2,:))'];
%     IWR = cell2mat(netFilter.IW(1,:));
%     LWR = cell2mat(netFilter.LW(2,1));
% 
%     WR_OPP = [bR(:,1) IWR(1,:)];
% 
%     for i = 2:hiddenNeurons
%         WR_OPP = [WR_OPP bR(:,i) IWR(i,:)];
%     end
% 
%     j = i;
%     for k = i+1:i+1
%         WR_OPP = [WR_OPP bR(:,k) LWR(k-j,:)];
%     end
%     
%     save('WR_OPP.mat','WR_OPP');
    
%% VALIDANDO FILTRO COM IMAGENS
    img = 55; 
    %index = (((img-1)*int32(1/(scale*scale)))+1);
    for i = 1:1/scale
        for j = 1:1/scale
            figure(1);
            %subplot('Position',[(j-1)*scale (1-(i*scale)) scale scale]),
            imshow(uint8(reshape(segImage(:,img,((1/scale)*(i-1))+j),(width*scale),(heigth*scale),channels)));
         
            figure(2);
            %subplot('Position',[(j-1)*scale (1-(i*scale)) scale scale]),
            imshow(uint8(reshape(netFilter(segImage(:,img,((1/scale)*(i-1))+j)),(width*scale),(heigth*scale),channels2)));
            %title(int2str(data(index)))
            %index = index +1;
        end
    end

%% VALIDANDO FILTRO COM DADOS
%     
%     ANNvalidations(hiddenNeurons+1,1) = hiddenNeurons;
%     
%     index = 1;
%     for i = 1:totalDataSet
%         ANNvalidations(1,i+1) = i;
%         ANNvalidations(hiddenNeurons+1,i+1) = 0;
%         for j = 1:(int32(1/(scale*scale)))
%             if(round(netFilter(ANNballI(:,index))) == ANNballL(index))
%                 ANNvalidations(hiddenNeurons+1,i+1) = ANNvalidations(hiddenNeurons+1,i+1) + (scale*scale);
%             end
%             index = index + 1;
%         end
%         disp("Verified Image: "+ type + num2str(i) + ".jpg");
%     end
end

save("ANNvalidationsBall"+type+".mat",'ANNvalidations');
