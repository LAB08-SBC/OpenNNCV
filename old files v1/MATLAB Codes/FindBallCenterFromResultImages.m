%% FIND THE CENTER OF THE BALL

clear
clc

heigth = 160;
width = 120;

quant = 322;

imagensLap = zeros(width,heigth,quant);
dados = zeros(quant,3);

for i = 1:quant
    imagensLap(:,:,i) = imread(strcat('.\LaplacianFilter\',int2str(i-1),'BC.jpg'));
    disp(strcat('.\LaplacianFilter\',int2str(i-1),'BC.jpg'));
      
    [yFinal,xFinal] = find(imagensLap(:,:,i),1,'last');
    [yInitial,xInitial] = find(imagensLap(:,:,i),1,'first');
    SizeX = xFinal-xInitial;
    SizeY = yFinal-yInitial;
    dados(i,:) = int32([(xInitial+(SizeX/2)) (yInitial+(SizeY/2)) SizeX]);
end

save('dados.mat','dados');