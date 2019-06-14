clear 
clc

net = feedforwardnet(1);
net = train(net,[2 4 6],[4 8 12]);
b = sim(net,3);