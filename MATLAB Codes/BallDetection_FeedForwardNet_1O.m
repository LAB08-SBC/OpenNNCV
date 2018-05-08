%% ABRINDO AS IMAGENS
clear
clc

load('dados.mat')

heigth = 160;
width = 120;

inicio = 59;
fim = 310;%321
trainPic = (fim-inicio)+1;

reduz = 1/8;

%pasta = 'C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Li??es\LaplacianFilter\';
pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';
tipo = 'BCGL';

ANNimagens = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNlabels = zeros(trainPic,3);
ANNvalidations = zeros(trainPic,3);
ANNresult = zeros(trainPic,4);

j=1;
for i = inicio:fim 
    ANNimagens(:,:,j) = imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz);
    ANNlabels(j,:) = dados(i+1,:);
    j = j+1;
end

ANNimagens = reshape(ANNimagens,width*reduz*heigth*reduz,trainPic);
ANNlabels = ANNlabels';

save('ANNlabels.mat','ANNlabels');
save('ANNimagens.mat','ANNimagens');
%% TREINANDO A REDE

for hiddenNeurons = 1:35
    
    netX=feedforwardnet(hiddenNeurons);
    netY=feedforwardnet(hiddenNeurons);
    netSize=feedforwardnet(hiddenNeurons);
    
    netX.trainParam.max_fail = 100;
    netY.trainParam.max_fail = 100;
    netSize.trainParam.max_fail = 100;
    
    netX=train(netX,ANNimagens,ANNlabels(1,:));
    netY=train(netY,ANNimagens,ANNlabels(2,:));
    netSize=train(netSize,ANNimagens,ANNlabels(3,:));

    %% VALIDA??O
    j=1;
    for i = inicio:fim
        ANNresult(j,:) = [i sim(netX,ANNimagens(:,j)) sim(netY,ANNimagens(:,j)) sim(netSize,ANNimagens(:,j))];
        j=j+1;
    end

    for i=(fim+1):321
        A = double(imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz));
        A = reshape(A,width*reduz*heigth*reduz,1);
        ANNresult(j,:) = [i sim(netX,A) sim(netY,A) sim(netSize,A)];
        j=j+1;
    end

    ANNvalidations(hiddenNeurons,1) = hiddenNeurons;
    for i = inicio:321

        if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,2)).^2)+ (((dados(i+1,2))-ANNresult(i-58,3)).^2) ) <= 10)
            ANNvalidations(hiddenNeurons,2) = ANNvalidations(hiddenNeurons,2) + 1;
        end
        
        if ((dados(i+1,3))-ANNresult(i-58,4) <= 10)
            ANNvalidations(hiddenNeurons,3) = ANNvalidations(hiddenNeurons,3) + 1;
        end
    end
end