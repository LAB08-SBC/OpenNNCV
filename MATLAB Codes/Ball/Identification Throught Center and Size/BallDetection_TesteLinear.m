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

reduz = 8;

pasta='C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
tipo = 'BCG';


Passar1 = zeros((width/reduz)*(heigth/reduz),trainPic,(reduz*reduz));
Passar2 = zeros((width/reduz)*(heigth/reduz),trainPic,(reduz*reduz));
ANNimagensS = zeros((width/reduz)*(heigth/reduz),total,(reduz*reduz));

ANNlabels = zeros(total,3);
ANNvalidations = zeros(trainPic,4);
ANNresult = zeros(total,4,64);

j=1;
for i = inicio:321
    Pic1 = imread(strcat(pasta,tipo,int2str(i),'.jpg'));
    a = 0;
    b = 0;
    for parte = 1:(reduz*reduz)
        akk1 = zeros((width/reduz),(heigth/reduz));
        if b<reduz
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            ANNimagensS(:,j,parte) = reshape(akk1,(width/reduz)*(heigth/reduz),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            ANNimagensS(:,j,parte) = reshape(akk1,(width/reduz)*(heigth/reduz),1);
            b= b +1;
        end   
        %subplot(reduz,reduz,parte),imshow(akk1(:,:));
    end
    ANNlabels(j,:) = dados(i+1,:);
    j = j +1;
end


akk = uint8(reshape(ANNimagensS(:,252,:),width/reduz,heigth/reduz,64));
for i = 1:(reduz*reduz)
    subplot(reduz,reduz,i),imshow(akk(:,:,i));
end

ANNlabels = ANNlabels';

save('ANNlabels.mat','ANNlabels');
save('ANNimagensS.mat','ANNimagensS');

%% TREINANDO A REDE

for k=1:64
    netPos=feedforwardnet(3);
    netSize=feedforwardnet(3);
    
    netPos.trainParam.max_fail = 100;
    netSize.trainParam.max_fail = 100;
    
    netPos=train(netPos,ANNimagensS(:,1:trainPic,k),ANNlabels(1:2,1:trainPic));
    netSize=train(netSize,ANNimagensS(:,1:trainPic,k),ANNlabels(3,1:trainPic));

    for i = 1:total
        A = sim(netPos,ANNimagensS(:,i,k));
        ANNresult(i,:,k) = [i sim(netPos,ANNimagensS(:,i,k))' sim(netSize,ANNimagensS(:,i,k))]; 
    end
    
    disp(k);
end

netCor = feedforwardnet(8);
netCor.trainParam.max_fail = 10000;
Teste1 = reshape(ANNresult(:,2,:),total,64)';
netCor= train(netCor,Teste1(:,1:trainPic),ANNlabels(1,1:trainPic));

Co = zeros(trainPic,4);

for j = 1:total
    C(j,1:2) = [(inicio+j)-1 sim(netCor,Teste1(:,j))];
end

netCor2 = feedforwardnet(8);
netCor2.trainParam.max_fail = 10000;
Teste2 = reshape(ANNresult(:,3,:),total,64)';
netCor2= train(netCor2,Teste2(:,1:trainPic),ANNlabels(2,1:trainPic));

for j = 1:total
    C(j,3) = sim(netCor2,Teste2(:,j));
end

netCor3 = feedforwardnet(8);
netCor3.trainParam.max_fail = 10000;
Teste3 = reshape(ANNresult(:,4,:),total,64)';
netCor3= train(netCor3,Teste3(:,1:trainPic),ANNlabels(3,1:trainPic));

for j = 1:total
    C(j,4) = sim(netCor3,Teste3(:,j));
end

% for hiddenNeurons = 1:35
%     netCor=feedforwardnet(hiddenNeurons);
%     netCor.trainParam.min_grad = 0.001;
%     netCor.trainParam.epochs = 12500;
%     netCor.trainParam.max_fail = 100000;
% 
%     netCor=train(netCor,ANNsim,ANNlabels(1:2,:));
% 
%     %% VALIDAÇÃO
%     j=1;
%     for i = inicio:fim
%         ANNresult(j,:) = [i sim(netPos,ANNimagensS(:,j))' sim(netCor,ANNsim(:,j))' sim(netSize,ANNimagensF(:,j))];
%         j=j+1;
%     end
%     
%     for i=(fim+1):321
%         A = double(imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz));
%         A = reshape(A,width*reduz*heigth*reduz,1);
%         
%         B = double(imresize(fftshift(1+abs(fft2(imread(strcat(pasta,tipo,int2str(i),'.jpg'))))),reduz));
%         B = reshape(B,width*reduz*heigth*reduz,1);
%         
%         ANNresult(j,:) = [i sim(netPos,A)' sim(netCor,sim(netPos,A))' sim(netSize,B)];
%         j=j+1;
%     end
%     
%     ANNvalidations(hiddenNeurons,1) = hiddenNeurons;
%     for i = inicio:321
%         if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,2)).^2)+ (((dados(i+1,2))-ANNresult(i-58,3)).^2) ) <= 10)
%             ANNvalidations(hiddenNeurons,2) = ANNvalidations(hiddenNeurons,2) + 1;
%         end
%         
%         if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,4)).^2)+ (((dados(i+1,2))-ANNresult(i-58,5)).^2) ) <= 10)
%             ANNvalidations(hiddenNeurons,3) = ANNvalidations(hiddenNeurons,3) + 1;
%         end
% 
%         if ((dados(i+1,3))-ANNresult(i-58,6) <= 10)
%             ANNvalidations(hiddenNeurons,4) = ANNvalidations(hiddenNeurons,4) + 1;
%         end
%     end
% end