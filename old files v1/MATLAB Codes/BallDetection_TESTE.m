%% ABRINDO AS IMAGENS
clear
clc

load('dados.mat')

heigth = 160;
width = 120;

inicio = 59;
fim = 310;%321
trainPic = (fim-inicio)+1;
total = (321-inicio)+1;

reduz = 1/8;

pasta = '.\LaplacianFilter\';
tipo = 'BCG';

ANNimagensS = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNimagensF = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNlabels = zeros(trainPic,3);
ANNsim = zeros(2,trainPic);
ANNvalidations = zeros(trainPic,4);
ANNresult = zeros(total,6);

j=1;
for i = inicio:fim 
    ANNimagensS(:,:,j) = imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz);
    F=fft2(imread(strcat(pasta,tipo,int2str(i),'.jpg')));
    ANNimagensF(:,:,j) = imresize(fftshift(1+abs(F)),reduz);
    ANNlabels(j,:) = dados(i+1,:);
    j = j+1;
end

ANNimagensS = reshape(ANNimagensS,width*reduz*heigth*reduz,trainPic);
ANNimagensF = reshape(ANNimagensS,width*reduz*heigth*reduz,trainPic);
ANNlabels = ANNlabels';

save('ANNlabels.mat','ANNlabels');
save('ANNimagensS.mat','ANNimagensS');
save('ANNimagensF.mat','ANNimagensF');
%% TREINANDO A REDE
netPos=feedforwardnet(30);
netSize=feedforwardnet(31);

netPos.trainParam.max_fail = 100;
netSize.trainParam.max_fail = 100;

netPos=train(netPos,ANNimagensS,ANNlabels(1:2,:));
netSize=train(netSize,ANNimagensF,ANNlabels(3,:));

% load('netPos.mat')
% load('netSize.mat')

save('netPos','netPos');
save('netSize','netSize');

testePic = ANNimagensS(:,1)';
save('testePic.mat','testePic');

j=1;
for i = inicio:fim
    ANNsim(:,j) = sim(netPos,ANNimagensS(:,j));
    j=j+1;
end

for hiddenNeurons = 1:35
    netCor=feedforwardnet(hiddenNeurons);
    netCor.trainParam.min_grad = 0.001;
    netCor.trainParam.epochs = 12500;
    netCor.trainParam.max_fail = 100000;

    netCor=train(netCor,ANNsim,ANNlabels(1:2,:));

    %% VALIDAÇÃO
    j=1;
    for i = inicio:fim
        ANNresult(j,:) = [i sim(netPos,ANNimagensS(:,j))' sim(netCor,ANNsim(:,j))' sim(netSize,ANNimagensF(:,j))];
        j=j+1;
    end
    
    for i=(fim+1):321
        A = double(imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz));
        A = reshape(A,width*reduz*heigth*reduz,1);
        
        B = double(imresize(fftshift(1+abs(fft2(imread(strcat(pasta,tipo,int2str(i),'.jpg'))))),reduz));
        B = reshape(B,width*reduz*heigth*reduz,1);
        
        ANNresult(j,:) = [i sim(netPos,A)' sim(netCor,sim(netPos,A))' sim(netSize,B)];
        j=j+1;
    end
    
    ANNvalidations(hiddenNeurons,1) = hiddenNeurons;
    for i = inicio:321
        if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,2)).^2)+ (((dados(i+1,2))-ANNresult(i-58,3)).^2) ) <= 10)
            ANNvalidations(hiddenNeurons,2) = ANNvalidations(hiddenNeurons,2) + 1;
        end
        
        if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,4)).^2)+ (((dados(i+1,2))-ANNresult(i-58,5)).^2) ) <= 10)
            ANNvalidations(hiddenNeurons,3) = ANNvalidations(hiddenNeurons,3) + 1;
        end

        if ((dados(i+1,3))-ANNresult(i-58,6) <= 10)
            ANNvalidations(hiddenNeurons,4) = ANNvalidations(hiddenNeurons,4) + 1;
        end
    end
end