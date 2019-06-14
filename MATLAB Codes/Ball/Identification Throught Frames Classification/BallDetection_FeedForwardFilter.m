%% ABRINDO AS IMAGENS
clear
clc

% determinando o tamanho da imagem em largura e altura
heigth = 160;
width = 120;

% Escolhe em qual imagem começa o banco de dados e em qual termina
beginDB = 59;
endDB = 310;
lastDB = 321; %última imagem, ou seja, usarei 11 para treino

%Define a quantidade de figuras para treinar
trainPic = (endDB-beginDB)+1;
%Define o tamanho total da base de dados
total = (lastDB-beginDB)+1;

%aplica a redução nas imagens
scale = 1/10;

%
Passar1 = zeros((width*scale)*(heigth*scale),trainPic,(scale*scale));
Passar2 = zeros((width*scale)*(heigth*scale),trainPic,(scale*scale));
ANNimagens1 = zeros((width*scale)*(heigth*scale),trainPic*(scale*scale));
ANNimagens2 = zeros((width*scale)*(heigth*scale),trainPic*(scale*scale));
ANNvalidations = zeros((width*scale)*(heigth*scale),total-trainPic,(scale*scale));

pasta='C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';

imagem=1;
t = 1;
for i = beginDB:310
    Pic1 = imread(strcat(pasta,'BCG',int2str(i),'.jpg'));
    Pic2 = rgb2gray(imread(strcat(pasta,'R',int2str(i),'.jpg')));
    a = 0;
    b = 0;
    for parte = 1:(scale*scale)
        akk1 = zeros((width*scale),(heigth*scale));
        akk2 = zeros((width*scale),(heigth*scale));
        if b<scale
            akk1 = Pic1((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            akk2 = Pic2((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            Passar1(:,imagem,parte) = reshape(akk1,(width*scale)*(heigth*scale),1);
            Passar2(:,imagem,parte) = reshape(akk2,(width*scale)*(heigth*scale),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            akk2 = Pic2((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            Passar1(:,imagem,parte) = reshape(akk1,(width*scale)*(heigth*scale),1);
            Passar2(:,imagem,parte) = reshape(akk2,(width*scale)*(heigth*scale),1);
            b= b +1;
        end   
        ANNimagens1(:,t) = reshape(Passar1(:,imagem,parte),(width*scale)*(heigth*scale),1);
        ANNimagens2(:,t) = reshape(Passar2(:,imagem,parte),(width*scale)*(heigth*scale),1);
        t = t + 1;
        %subplot(reduz,reduz,parte),imshow(akk1(:,:));
    end
    imagem = imagem +1;
end

imagem = 2; 
akk2 = uint8(reshape(ANNimagens1(:,(((imagem-1)*(scale*scale))+1):(imagem*scale*scale)),width*scale,heigth*scale,(scale*scale)));
t = 1;
for i = 1:scale
    for j = 1:scale
        figure(1);
        subplot('Position',[(j-1)*1*scale (scale-i)*1*scale 1*scale 1*scale]),imshow(akk2(:,:,t));
        t = t +1;
    end
end

akk3 = uint8(reshape(ANNimagens2(:,(((imagem-1)*(scale*scale))+1):(imagem*scale*scale)),width*scale,heigth*scale,(scale*scale)));
t = 1;
for i = 1:scale
    for j = 1:scale
        figure(2);
        subplot('Position',[(j-1)*1*scale (scale-i)*1*scale 1*scale 1*scale]),imshow(akk3(:,:,t));
        t = t +1;
    end
end


%% TREINANDO O FILTRO

for r = 5
    netFilter = feedforwardnet(r);
    netFilter.trainParam.max_fail = 100;

    netFilter = train(netFilter,ANNimagens1,ANNimagens2);
end

imagem = 1;
for i = (trainPic+1):total
    Pic1 = imread(strcat(pasta,'BCG',int2str(i),'.jpg'));
    a = 0;
    b = 0;
    for parte = 1:(scale*scale)
        akk1 = zeros((width*scale),(heigth*scale));
       if b<scale
            akk1 = Pic1((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            Passar1(:,imagem,parte) = reshape(akk1,(width*scale)*(heigth*scale),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width*scale))):((a+1)*(width*scale)),(1+(b*(heigth*scale))):((b+1)*(heigth*scale)));
            Passar1(:,imagem,parte) = reshape(akk1,(width*scale)*(heigth*scale),1);
            b= b +1;
        end   
        ANNvalidations = reshape(Passar1(:,imagem,:),(width*scale)*(heigth*scale),(scale*scale));
        %subplot(reduz,reduz,parte),imshow(akk1(:,:));
    end
    imagem = imagem +1;
end

akk = uint8(reshape(ANNvalidations(:,1:(scale*scale)),width*scale,heigth*scale,(scale*scale)));
t = 1;
for i = 1:scale
    for imagem = 1:scale
        figure(3);
        subplot('Position',[(imagem-1)*1*scale (scale-i)*1*scale 1*scale 1*scale]),imshow(akk(:,:,t));
        t = t +1;
    end
end

t = 1;
for i = 1:scale
    for imagem = 1:scale
        ANNresp = sim(netFilter,ANNvalidations(:,t));
        akk = uint8(reshape(ANNresp,(width*scale),(heigth*scale)));
        figure(4);
        subplot('Position',[(imagem-1)*1*scale (scale-i)*1*scale 1*scale 1*scale]),imshow(akk);
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
