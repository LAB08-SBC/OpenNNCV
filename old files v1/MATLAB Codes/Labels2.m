%% FIND THE CENTER OF THE BALL

clear
clc

heigth = 160;
width = 120;
reduz = 20;

%% Para Bola
% inicio = 59;
% fim = 310;%321
% trainPic = (fim-inicio)+1;
% total = (fimMesmo-inicio)+1;
% dados6 = zeros((total*(reduz*reduz)),1);
% tipo = 'R';

%% Para Marcas
% inicio = 1;
% fim = 210;
% fimMesmo = 230;
% trainPic = (fim-inicio)+1;
% total = (fimMesmo-inicio)+1;
% dados3 = zeros((total*(reduz*reduz)),1);
% tipo = 'R_M';

%% Para Gol
% inicio = 1;
% fim = 51;
% fimMesmo = 61;
% trainPic = (fim-inicio)+1;
% total = (fimMesmo-inicio)+1;
% dados4 = zeros((total*(reduz*reduz)),1);
% tipo = 'R_G';

%% Para Adversário
inicio = 1;
fim = 53;
fimMesmo = 63;
trainPic = (fim-inicio)+1;
total = (fimMesmo-inicio)+1;
dados5 = zeros((total*(reduz*reduz)),1);
tipo = 'R_C';


Passar = zeros((width/reduz)*(heigth/reduz),total,(reduz*reduz));
ANNimagens = zeros((width/reduz)*(heigth/reduz),total*(reduz*reduz));
pasta='C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';
%pasta= 'C:\Users\Lab08\Documents\Gilmar\RNA-OpenCV\Licoes\LaplacianFilter\';
imagem=1;
t=1;

for i = inicio:fimMesmo
    Pic = rgb2gray(imread(strcat(pasta,tipo,int2str(i),'.jpg')));
    a = 0;
    b = 0;
    for parte = 1:(reduz*reduz)
        akk = zeros((width/reduz),(heigth/reduz));
        if b<reduz
            akk = Pic((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar(:,imagem,parte) = reshape(akk,(width/reduz)*(heigth/reduz),1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk = Pic((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)));
            Passar(:,imagem,parte) = reshape(akk,(width/reduz)*(heigth/reduz),1);
            b= b +1;
        end   
        ANNimagens(:,t) = reshape(Passar(:,imagem,parte),(width/reduz)*(heigth/reduz),1);

        %% Achando a porcentagem de branco de cada frame da foto
        percentage = 0;
        for k = 1:((width/reduz)*(heigth/reduz))
            if(ANNimagens(k,t) ~= 0)
                percentage = percentage + 1;
            end
        end
        percentage = percentage/((width/reduz)*(heigth/reduz));
        
        if(percentage >=0.40)
            dados5(t) = 1;
        end
        t = t + 1;
        
    end
    imagem = imagem +1;
end

imagem = 55; 
akk2 = uint8(reshape(ANNimagens(:,(((imagem-1)*(reduz*reduz))+1):(imagem*reduz*reduz)),width/reduz,heigth/reduz,(reduz*reduz)));
t = (((imagem-1)*(reduz*reduz))+1);
for i = 1:(reduz*reduz)
    subplot(reduz,reduz,i),imshow(akk2(:,:,i));
    teste = int2str(dados5(t));
    title(teste);
    t = t + 1;
end

%save('dados3.mat','dados3');
%save('dados4.mat','dados4');
%save('dados5.mat','dados5');
%save('dados6.mat','dados6');