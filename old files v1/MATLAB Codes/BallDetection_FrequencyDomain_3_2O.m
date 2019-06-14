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

pasta = 'C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';
tipo = 'BCG';

ANNimagens = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNlabels = zeros(trainPic,3);
ANNvalidations = zeros(trainPic,3);
ANNresult = zeros(trainPic,4);

j=1;
for i = inicio:fim 
    F=fft2(imread(strcat(pasta,tipo,int2str(i),'.jpg')));
    ANNimagens(:,:,j) = imresize(abs(F),reduz);
    ANNlabels(j,:) = dados(i+1,:);
    j = j+1;
end

ANNimagens = reshape(ANNimagens,width*reduz*heigth*reduz,trainPic);
ANNlabels = ANNlabels';
ANNl2 = ANNlabels(3,:);
ANNi2 = ANNimagens;
save('ANNl2.mat','ANNl2');
save('ANNi2.mat','ANNi2');
%% TREINANDO A REDE

for hiddenNeurons = 29
    
    netPos=feedforwardnet(hiddenNeurons);
    netSize=feedforwardnet(hiddenNeurons);
    
    netPos.trainParam.max_fail = 100;
    netSize.trainParam.max_fail = 100;
    
    netPos=train(netPos,ANNimagens,ANNlabels(1:2,:));
    netSize=train(netSize,ANNimagens,ANNlabels(3,:));
    
    testePicSize = ANNimagens(:,1)';
    save('testePicSize.mat','testePicSize');
    
      %% SALVANDO OS PESOS PARA O JAVA

    bR= [cell2mat(netSize.b(1,:))' cell2mat(netSize.b(2,:))'];
    IWR = cell2mat(netSize.IW(1,:));
    LWR = cell2mat(netSize.LW(2,1));

    WR_S = [bR(:,1) IWR(1,:)];

    for i = 2:hiddenNeurons
        WR_S = [WR_S bR(:,i) IWR(i,:)];
    end

    j = i;
    for k = i+1:i+1
        WR_S = [WR_S bR(:,k) LWR(k-j,:)];
    end
    save('WR_S.mat','WR_S');


    %% VALIDA??O
    j=1;
    for i = inicio:fim
        ANNresult(j,:) = [i sim(netPos,ANNimagens(:,j))' sim(netSize,ANNimagens(:,j))];
        j=j+1;
    end

    for i=(fim+1):321
        A = double(imresize(abs(fft2(imread(strcat(pasta,tipo,int2str(i),'.jpg')))),reduz));
        A = reshape(A,width*reduz*heigth*reduz,1);
        ANNresult(j,:) = [i sim(netPos,A)' sim(netSize,A)];
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