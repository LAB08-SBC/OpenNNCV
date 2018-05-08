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

reduz = 1;

pasta = 'C:\Users\Gilmar Correia\Documents\Documentos\Projetos\GitHub\RNA-OpenCV\Licoes\LaplacianFilter\';
%pasta = '/Users/junior/Desktop/GitHub/RNA-OpenCV/Licoes/LaplacianFilter/';
tipo = 'BCG';

ANNimagensS = uint8(zeros((width/8)*(heigth/8),total));
ANNlabels = zeros(total,3);
ANNvalidations = zeros(trainPic,3);
ANNresult = zeros(total,4);

for i = inicio:321
    
    Pic = imread(strcat(pasta,tipo,int2str(i),'.jpg'));
    C = uint8(zeros(width/2,heigth/2));
    D = uint8(zeros(width/4,heigth/4));
    E = uint8(zeros(width/8,heigth/8));
    j=1;
    for k = 1:2:120 
        m = 1;
        for n = 1:2:160
           A = Pic(k:(1+k),n:(1+n)); 
           C(j,m) = uint8(max(A(:)));
           m = m +1;
        end
        j = j+1;
    end
    
    j = 1;
    for k = 1:2:60
        m = 1;
        for n = 1:2:80
           A = C(k:(1+k),n:(1+n)); 
           D(j,m) = uint8(max(A(:)));
           m = m +1;
        end
        j = j+1;
    end
    
    j = 1;
    for k = 1:2:30
        m = 1;
        for n = 1:2:40
           A = D(k:(1+k),n:(1+n)); 
           E(j,m) = uint8(max(A(:)));
           m = m +1;
        end
        j = j+1;
    end
    
    F = abs(fft2(E));
    ANNimagensS(:,(i-inicio+1)) = reshape(F,(width/8)*(heigth/8),1);
    
    ANNlabels((i-inicio+1),:) = dados(i+1,:);
end

ANNlabels = ANNlabels';

save('ANNlabels.mat','ANNlabels');
save('ANNimagensS.mat','ANNimagensS');

%% TREINANDO A REDE

for r = 1:35
    netX=feedforwardnet(r);
    netY=feedforwardnet(r);
    netSize=feedforwardnet(r);
    
    netX.trainParam.max_fail = 100;
    netY.trainParam.max_fail = 100;
    netSize.trainParam.max_fail = 100;
    
    netX=train(netX,double(ANNimagensS(:,1:trainPic)),ANNlabels(1,1:trainPic));
    netY=train(netX,double(ANNimagensS(:,1:trainPic)),ANNlabels(2,1:trainPic));
    netSize=train(netSize,double(ANNimagensS(:,1:trainPic)),ANNlabels(3,1:trainPic));

    %% VALIDA?A?O
    for j = 1:total
        ANNresult(j,:) = [((j-1)+inicio) sim(netX,double(ANNimagensS(:,j)))' sim(netY,double(ANNimagensS(:,j)))' sim(netSize,double(ANNimagensS(:,j)))'];
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