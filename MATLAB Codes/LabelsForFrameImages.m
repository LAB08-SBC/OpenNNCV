%% Código para obtenção dos labels de todas as imagens segmentadas
clear
clc

% determinando o tamanho da imagem em largura e altura
heigth = 160;
width = 120;

% scale é a escala aplicada para se obter uma imagem 8x6
scale = 1/20;

% Escolhe o tipo para salvar a classificação 1 - Bola, 2 - Marcas, 3 - Gol
% e 4 - Oponente
dataType = 4;

if dataType == 1
    %% Para a BOLA
    beginDB = 59;
    endDB = 310;
    lastDB = 321;
    
    trainDataSet = (endDB-beginDB)+1;
    totalDataSet = (lastDB-beginDB)+1;
    
    %Cria um vetor para armazer as classificações
    dataType = 1;
    data = zeros((totalDataSet*int32(1/(scale*scale))),1); 
    type = '\Ball\R';
    
elseif dataType == 2
    %% Para Marcas
    beginDB = 1;
    endDB = 210;
    lastDB = 230;

    trainDataSet = (endDB-beginDB)+1;
    totalDataSet = (lastDB-beginDB)+1;

    %Cria um vetor para armazer as classificações
    data = zeros((totalDataSet*int32(1/(scale*scale))),1); 
    type = '\FieldMarks\R_M';

elseif dataType == 3
    %% Para Gol
    beginDB = 1;
    endDB = 51;
    lastDB = 61;

    trainDataSet = (endDB-beginDB)+1;
    totalDataSet = (lastDB-beginDB)+1;

    %Cria um vetor para armazer as classificações
    data = zeros((totalDataSet*int32(1/(scale*scale))),1); 
    type = '\Goal\R_G';

elseif dataType == 4 
    %% Para Adversário
    beginDB = 1;
    endDB = 53;
    lastDB = 63;

    trainDataSet = (endDB-beginDB)+1;
    totalDataSet = (lastDB-beginDB)+1;

    %Cria um vetor para armazer as classificações
    data = zeros((totalDataSet*int32(1/(scale*scale))),1); 
    type = '\Opponent\R_C';
end

%% Realizando a classificação 

% Inicializa um vetor de tamanho que guardará as imagens segmentadas
% [8*6,totalDataSet,400] = [mascara, total de imagens, subImagens de 
% uma imagem]
segImage = zeros((width*scale)*(heigth*scale),totalDataSet,int32(1/(scale*scale)));

%Inicializa a pasta usada
folder='C:\Users\Gilmar Jeronimo\Desktop\Testes MATLAB\DataBase';

img=1;
index=1;

for i = beginDB:lastDB
    %Abre uma imagem por vez
    picture = imread(strcat(folder,type,int2str(i),'.jpg'));
    
    row = 0;
    col = 0;
    
    for frame = 1:int32(1/(scale*scale))
        
        %Inicializa uma máscara de 6*8
        kernel = zeros((width*scale),(heigth*scale));
        
        if col>=int8(1/scale)
            row = row + 1;
            col = 0;
        end   
        
        % a mascara/kernel será a imagem capturada [(1+(row*8)):(1+row)*8,(1+(col*6)):(col+1)*6]
        kernel = picture((1+(row*(width*scale))):((row+1)*(width*scale)),(1+(col*(heigth*scale))):((col+1)*(heigth*scale)));
            
        % converte a subimagem imagem 2D em um vetor 1D
        segImage(:,img,frame) = reshape(kernel,(width*scale)*(heigth*scale),1);
        col = col + 1;
        
        %% Achando a porcentagem de branco de cada frame/kernel da foto
        
        percentage = 0;
        for k = 1:((width*scale)*(heigth*scale))
            if(segImage(k,img,frame) ~= 0)
                percentage = percentage + 1;
            end
        end
        percentage = percentage/((width*scale)*(heigth*scale));
        
        if(percentage >=0.40)
            data(index) = 1;
        end
        index = index + 1;
        
    end
    img = img +1;
end

%% Mostra a classificação realizada da imagem 55 do banco de dados
% img = 55; 
% index = (((img-1)*int32(1/(scale*scale)))+1);
% 
% for i = 1:400
%     subplot(1/scale,1/scale,i)
%     subimage = uint8(reshape(segImage(:,img,i),width*scale,heigth*scale));
%     imshow(subimage);
%     
%     title(int2str(data(index)))
%     
%     index = index + 1;
%     %disp(i)
% end

%% Salva a classificação
if dataType == 1
    save('.\Ball\Identification Throught Frames Classification\dataFramesBall.mat','data');
elseif dataType == 2
    save('.\Marks\dataFramesMarks.mat','data');
elseif dataType == 3
    save('.\Goal\dataFramesGoal.mat','data');
else
    save('.\Opponent\dataFramesOpponent.mat','data');
end