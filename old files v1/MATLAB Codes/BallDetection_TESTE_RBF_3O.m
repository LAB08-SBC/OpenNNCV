%% ABRINDO AS IMAGENS
clear
clc

load('dados.mat')

heigth = 160;
width = 120;

inicio = 59;
fim = 316;
%channel = 1;
trainPic = (fim-inicio)+1;

reduz = 1/4;

pasta = '.\LaplacianFilter\';
tipo = 'BCG';

ANNimagens = zeros(width*reduz,heigth*reduz,1,trainPic);
ANNlabels = zeros(trainPic,3);
ANNvalidations = zeros(trainPic,3);
ANNresult = zeros(trainPic,4);

j=1;
for i = inicio:fim
    ANNimagens(:,:,:,j) = imresize(imread(strcat(pasta,tipo,int2str(i),'.jpg')),reduz);
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
      
    netR = newrb(ANNimagens,ANNlabels);
    
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