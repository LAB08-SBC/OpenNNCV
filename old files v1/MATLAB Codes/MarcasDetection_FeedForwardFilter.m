%% ABRINDO AS IMAGENS
clear
clc

heigth = 160;
width = 120;

inicio = 1;
fim = 210;%230
trainPic = (fim-inicio)+1;
total = (230-inicio)+1;
canais = 1;

reduz = 10; %40, 20, 10, 8, 4, 2


Passar1 = zeros((width/reduz)*(heigth/reduz),trainPic,(reduz*reduz),canais);
Passar2 = zeros((width/reduz)*(heigth/reduz),trainPic,(reduz*reduz));
ANNimagens1 = zeros((width/reduz)*(heigth/reduz),trainPic*(reduz*reduz));
ANNimagens2 = zeros((width/reduz)*(heigth/reduz),trainPic*(reduz*reduz));
ANNvalidations = zeros((width/reduz)*(heigth/reduz),total-trainPic,(reduz*reduz));

pasta='C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';

imagem=1;
t = 1;
for i = inicio:fim
    Pic1 = imread(strcat(pasta,'BCG_M',int2str(i),'.jpg'));
    Pic2 = imread(strcat(pasta,'R_M',int2str(i),'.jpg'));
    a = 0;
    b = 0;
    for parte = 1:(reduz*reduz)
        akk1 = zeros((width/reduz),(heigth/reduz));
        akk2 = zeros((width/reduz),(heigth/reduz));
        if b<reduz
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            akk2 = Pic2((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz)*canais,1);
            Passar2(:,imagem,parte) = reshape(akk2,(width/reduz)*(heigth/reduz),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            akk2 = Pic2((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz)*canais,1);
            Passar2(:,imagem,parte) = reshape(akk2,(width/reduz)*(heigth/reduz),1);
            b= b +1;
        end   
        ANNimagens1(:,t) = reshape(Passar1(:,imagem,parte),(width/reduz)*(heigth/reduz)*canais,1);
        ANNimagens2(:,t) = reshape(Passar2(:,imagem,parte),(width/reduz)*(heigth/reduz),1);
        t = t + 1;
        %subplot(reduz,reduz,parte),imshow(akk1(:,:));
    end
    imagem = imagem +1;
end

imagem = 2; 
akk2 = uint8(reshape(ANNimagens1(:,(((imagem-1)*(reduz*reduz))+1):(imagem*reduz*reduz)),width/reduz,heigth/reduz,(reduz*reduz)));
t = 1;
for i = 1:reduz
    for j = 1:reduz
        figure(1);
        subplot('Position',[(j-1)*1/reduz (reduz-i)*1/reduz 1/reduz 1/reduz]),imshow(akk2(:,:,t));
        t = t +1;
    end
end

akk3 = uint8(reshape(ANNimagens2(:,(((imagem-1)*(reduz*reduz))+1):(imagem*reduz*reduz)),width/reduz,heigth/reduz,(reduz*reduz)));
t = 1;
for i = 1:reduz
    for j = 1:reduz
        figure(2);
        subplot('Position',[(j-1)*1/reduz (reduz-i)*1/reduz 1/reduz 1/reduz]),imshow(akk3(:,:,t));
        t = t +1;
    end
end


%% TREINANDO O FILTRO

for r = 3
    netFilter = feedforwardnet(r);
    netFilter.trainParam.max_fail = 100;

    netFilter = train(netFilter,ANNimagens1,ANNimagens2);
end

imagem = 1;
for i = (trainPic+1):total
    Pic1 = imread(strcat(pasta,'BCG_M',int2str(i),'.jpg'));
    a = 0;
    b = 0;
    for parte = 1:(reduz*reduz)
        akk1 = zeros((width/reduz),(heigth/reduz));
       if b<reduz
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz),1);
            b= b +1;
        end   
        ANNvalidations = reshape(Passar1(:,imagem,:),(width/reduz)*(heigth/reduz),(reduz*reduz));
        %subplot(reduz,reduz,parte),imshow(akk1(:,:));
    end
    imagem = imagem +1;
end

akk = uint8(reshape(ANNvalidations(:,1:(reduz*reduz)),width/reduz,heigth/reduz,(reduz*reduz)));
t = 1;
for i = 1:reduz
    for imagem = 1:reduz
        figure(3);
        subplot('Position',[(imagem-1)*1/reduz (reduz-i)*1/reduz 1/reduz 1/reduz]),imshow(akk(:,:,t));
        t = t +1;
    end
end

t = 1;
for i = 1:reduz
    for imagem = 1:reduz
        ANNresp = sim(netFilter,ANNvalidations(:,t));
        akk = uint8(reshape(ANNresp,(width/reduz),(heigth/reduz)));
        figure(4);
        subplot('Position',[(imagem-1)*1/reduz (reduz-i)*1/reduz 1/reduz 1/reduz]),imshow(akk);
        t = t+1;
    end
end

% ANNimagens1 = reshape(ANNimagens1,300,total*parte,1);
%     A = double(imresize(imread(strcat('.\VideoOpenCV\',int2str(fim+1),'BCG.jpg')),reduz));
%     A = reshape(A,width/reduz*heigth/reduz,1);
% 
%     Resp = sim(netFilter,A);
%     Resp = reshape(Resp,width/reduz,heigth/reduz);
%     %Resp = imresize(A,1/reduz);
% 
%     imwrite(imresize(uint32(Resp),1/reduz),strcat('hahaha',int2str(r),'.jpg'));
