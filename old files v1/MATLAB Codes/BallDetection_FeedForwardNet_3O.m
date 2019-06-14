%% ABRINDO AS IMAGENS
clear
clc

load('dados.mat')

heigth = 160;
width = 120;

inicio = 59;
fim = 310;
%channel = 1;
trainPic = (fim-inicio)+1;

reduz = 1/8;

pasta = 'C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
tipo = 'BCG';

ANNimagens = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNlabels = zeros(trainPic,3);
ANNvalidations = zeros(trainPic,3);
ANNresult = zeros(trainPic,4);

j=1;
for i = inicio:fim
    ANNimagens(:,:,j) = imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz);
    %teste = imresize(imread(strcat(pasta,int2str(i),tipo)),reduz);
    %imagens(:,:,j) = teste(:,:,channel);
    ANNlabels(j,:) = dados(i+1,:);
    j = j+1;
end

ANNimagens = reshape(ANNimagens,width*reduz*heigth*reduz,trainPic);
%imagens = reshape(reshape(imagens,width*reduz*heigth*reduz,3,trainPic),3*width*reduz*heigth*reduz,trainPic);
ANNlabels = ANNlabels';
%labels = labels*reduz;

save('ANNlabels.mat','ANNlabels');
save('ANNimagens.mat','ANNimagens');
%% TREINANDO A REDE

for r = 1
    netR=feedforwardnet(r);
    %netR.layers{1}.transferFcn = 'radbas';
    netR.trainParam.max_fail = 100;

    %load('netR.mat')

    netR=train(netR,ANNimagens,ANNlabels);

    %save('netR','netR');

    %testePic = ANNimagens(:,1)';
    %save('testePic.mat','testePic');

    %% SALVANDO OS PESOS PARA O JAVA

%     bR= [cell2mat(netR.b(1,:))' cell2mat(netR.b(2,:))'];
%     IWR = cell2mat(netR.IW(1,:));
%     LWR = cell2mat(netR.LW(2,1));
% 
%     WR = [bR(:,1) IWR(1,:)];
% 
%     for i = 2:r
%         WR = [WR bR(:,i) IWR(i,:)];
%     end
% 
%     j = i;
%     for k = i+1:i+3
%         WR = [WR bR(:,k) LWR(k-j,:)];
%     end
%     save('WR.mat','WR');
    %% VALIDAÇÃO
    j=1;
    for i = inicio:fim
        ANNresult(j,:) = [i sim(netR,ANNimagens(:,j))'];
        j=j+1;
    end

    for i=(fim+1):321
        A = double(imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz));
        %A = reshape(A(:,:,channel),width*reduz*heigth*reduz,1);
        A = reshape(A,width*reduz*heigth*reduz,1);
        %A = reshape(reshape(A,width*reduz*heigth*reduz,3),width*reduz*heigth*reduz*3,1);
        ANNresult(j,:) = [i sim(netR,A)'];
        j=j+1;
    end

    ANNvalidations(r,1) = r;
    for i = inicio:321

        if ( sqrt( (((dados(i+1,1))-ANNresult(i-58,2)).^2)+ (((dados(i+1,2))-ANNresult(i-58,3)).^2) ) <= 10)
            ANNvalidations(r,2) = ANNvalidations(r,2) + 1;
        end
        
        if ((dados(i+1,3))-ANNresult(i-58,4) <= 10)
            ANNvalidations(r,3) = ANNvalidations(r,3) + 1;
        end
    end
end