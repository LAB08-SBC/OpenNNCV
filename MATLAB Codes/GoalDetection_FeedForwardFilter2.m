%% ABRINDO AS IMAGENS
clear
clc

load('dados4.mat')

heigth = 160;
width = 120;

inicio = 1;
fim = 51;%61
trainPic = (fim-inicio)+1;
total = (61-inicio)+1;

reduz = 20;
canais = 3;

Passar1 = zeros((width/reduz)*(heigth/reduz)*canais,total,(reduz*reduz));
ANNgolI = zeros((width/reduz)*(heigth/reduz)*canais,total*(reduz*reduz));
ANNgolL = dados4;
ANNvalidations = zeros(20,total+1);
pasta='C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';

imagem=1;
t = 1;


for i = inicio:61
    Pic1 = imread(strcat(pasta,'G_',int2str(i),'.jpg'));
    a = 0;
    b = 0;
    for parte = 1:(reduz*reduz)
        akk1 = zeros((width/reduz),(heigth/reduz),canais);
        if b<reduz
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)),:);
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz)*canais,1);
            b = b + 1;
        else
            a = a + 1;
            b = 0;
            akk1 = Pic1((1+(a*(width/reduz))):((a+1)*(width/reduz)),(1+(b*(heigth/reduz))):((b+1)*(heigth/reduz)),:);
            Passar1(:,imagem,parte) = reshape(akk1,(width/reduz)*(heigth/reduz)*canais,1);
            b= b +1;
        end   
        ANNgolI(:,t) = reshape(Passar1(:,imagem,parte),(width/reduz)*(heigth/reduz)*canais,1);
        t = t + 1;
    end
    imagem = imagem +1;
end
ANNgolL = ANNgolL';

save('ANNgolI.mat','ANNgolI');
save('ANNgolL.mat','ANNgolL');

% imagem = 2; 
% akk2 = uint8(reshape(ANNimagens(:,(((imagem-1)*(reduz*reduz))+1):(imagem*reduz*reduz)),width/reduz,heigth/reduz,(reduz*reduz)));
% t = 1;
% for i = 1:reduz
%     for j = 1:reduz
%         figure(1);
%         subplot('Position',[(j-1)*1/reduz (reduz-i)*1/reduz 1/reduz 1/reduz]),imshow(akk2(:,:,t));
%         t = t +1;
%     end
% end

%% TREINANDO O FILTRO

for r = 8
    netFilter = feedforwardnet(r);
    netFilter.trainParam.max_fail = 100;
    netFilter.trainParam.time = 360;
    
    netFilter = train(netFilter,ANNgolI(:,1:(trainPic*reduz*reduz)),ANNgolL(1:(trainPic)*(reduz*reduz)));
    
     %% SALVANDO OS PESOS PARA O JAVA    
 
    bR= [cell2mat(netFilter.b(1,:))' cell2mat(netFilter.b(2,:))'];
    IWR = cell2mat(netFilter.IW(1,:));
    LWR = cell2mat(netFilter.LW(2,1));

    WR_GOL = [bR(:,1) IWR(1,:)];

    for i = 2:r
        WR_GOL = [WR_GOL bR(:,i) IWR(i,:)];
    end

    j = i;
    for k = i+1:i+1
        WR_GOL = [WR_GOL bR(:,k) LWR(k-j,:)];
    end
    save('WR_GOL.mat','WR_GOL');
    
%% VALIDANDO FILTRO COM IMAGENS
    imagem = 13; 
    akk2 = uint8(reshape(ANNgolI(:,(((imagem-1)*(reduz*reduz))+1):(imagem*reduz*reduz)),width/reduz,heigth/reduz,canais,(reduz*reduz)));
    for i = 1:(reduz*reduz)
        figure(r);
        subplot(reduz,reduz,i),imshow(akk2(:,:,:,i));
        teste = string(round(sim(netFilter,double(reshape(akk2(:,:,:,i),(width/reduz)*(heigth/reduz)*canais,1)) ) ) );
        title(teste);
    end

%% VALIDANDO FILTRO COM DADOS
%     ANNvalidations(r+1,1) = r;
%     t = 1;
%     for i = 1:total
%         ANNvalidations(1,i+1) = i;
%         for j = 1:(reduz*reduz)
%             if(round(sim(netFilter,ANNimagens(:,t))) == ANNlabels(t))
%                 ANNvalidations(r+1,i+1) = ANNvalidations(r+1,i+1) + 1;
%             end
%             t = t + 1;
%         end
%         disp(i);
%         ANNvalidations(r+1,i+1) = (reduz*reduz) - ANNvalidations(r+1,i+1);
%     end
end
