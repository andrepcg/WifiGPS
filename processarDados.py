import re
import neuralNetwork as NN
from pybrain.tools.shortcuts import buildNetwork
from pybrain.supervised.trainers import BackpropTrainer
from pybrain.datasets.supervised import SupervisedDataSet as SDS
from sklearn.metrics import mean_squared_error as MSE
from math import sqrt
import cPickle as pickle
from pybrain.optimization.populationbased.ga import GA
import numpy
from pybrain.structure import SoftmaxLayer
from pybrain.structure import TanhLayer
from pybrain.structure import SigmoidLayer
from pybrain.optimization import HillClimber, CMAES
from pybrain.rl.environments.cartpole.balancetask import BalanceTask
from pybrain.utilities import fListToString
from pybrain.rl.agents import OptimizationAgent
from pybrain.rl.experiments import EpisodicExperiment

f = open("dados_exportados.txt", "r")

lines = f.readlines()

redes = {}

def dBm2quality(dbm):
	if dbm <= -100 or dbm == 0:
		return 0.0
	elif dbm >= -50:
		return 1.0
	else:
		return float(2.0 * (dbm + 100))/ 100.0

def dBmArray2quality(arr):
	for i in range(len(arr)):
		arr[i] = dBm2quality(arr[i])

	return arr


i = 0
while i < len(lines):
	line = lines[i]

	if re.match( r'(([a-f0-9]{2}:)+[a-f0-9]{2})', line):
		nomeRede = line.strip()
		i = i + 1
		x = int(lines[i].split(" ")[0])
		y = int(lines[i].split(" ")[1])
		redes[nomeRede] = [[0 for xx in range(x)] for yy in range(y)]

		#print len(redes[nomeRede][0])

		i = i + 1
		for yy in range(y):
			numeros = lines[i].split(" ")

			for xx in range(x):
				redes[nomeRede][yy][xx] = dBm2quality(int(numeros[xx]))

			i = i + 1

		#print redes[nomeRede]

	else:
		i = i + 1

'''
for rede in redes:
	print rede + ",",

print "x, y"
'''

arr = []

for y in range(22):
	for x in range(35):

		t = []
		#for rede in redes:
			#t.append(redes[rede][y][x])

		t.append(redes["a4:b1:e9:44:08:ad"][y][x])
		t.append(redes["0c:47:3d:09:df:c8"][y][x])
		t.append(redes["0c:47:3d:09:df:c9"][y][x])
		t.append(redes["00:26:5b:16:bf:08"][y][x])
		t.append(redes["00:26:5b:16:bf:09"][y][x])
		t.append(redes["a4:b1:e9:ed:46:74"][y][x])
		t.append(redes["08:76:ff:87:fe:7e"][y][x])
		t.append(redes["00:26:5b:1c:b4:99"][y][x])
		t.append(redes["00:1f:9f:ff:37:36"][y][x])
		t.append(redes["9c:97:26:9b:94:47"][y][x])
		t.append(redes["64:70:02:b2:6c:2a"][y][x])


		if not all(v == 0 for v in t):
			#bits = bitfield(x) + bitfield(y)
			#arr.append([t, bits])
			arr.append([t, [x/35.0,y/22.0]])

'''
			for i in range(len(t)):
				print str(t[i]) + " ",

			print ""
			print str(x / 35.0) + " " + str(y / 22.0)
'''

			#print str(redes[rede][y][x]) + ",",

		#print str(x) + ", " + str(y)


#for i in range(len(arr)):
	#print arr[i]



y_test = []
y_test_x_int = []
y_test_y_int = []

ds = SDS(len(redes), 2)
for input, target in arr:
	y_test.append(target)
	y_test_y_int.append(target[1] * 22)
	y_test_x_int.append(target[0] * 35)
	ds.addSample(input, target)


net = pickle.load( open( 'model.pkl', 'rb' ))



#net = buildNetwork(12, 16, 12, bias = True, hiddenclass=TanhLayer)

'''
#Geneic algorithm
net = buildNetwork(len(redes), 16, 2, bias = True)
ga = GA(ds.evaluateModuleMSE, net, minimize=True)
for i in range(1000):
    net = ga.learn(0)[0]


net = buildNetwork(len(redes), 16, 2, bias = True)
ga = HillClimber(ds.evaluateModuleMSE, net, minimize=True)
for i in range(6000):
    net = ga.learn(0)[0]
'''




net = buildNetwork(len(redes), 16, 2, bias = True)
trainer = BackpropTrainer( net, ds, learningrate=0.0095, momentum=0.001) #learningrate=0.1, momentum=0.01
trainer.trainUntilConvergence( verbose = False, maxEpochs=2500)


pickle.dump( net, open( 'model.pkl', 'wb' ))


p = net.activateOnDataset( ds )
p_f = []

for i in range(len(p)):
	p_f.append([int(p[i][0] * 35), int(p[i][1] * 22)])


xy = net.activate([0.18, 0.0, 0.0, 0.82, 0.82, 0.0, 0.0, 0.0, 0.0, 0.62, 0.34]) # 25, 3
print 20, 3, xy[0] * 35, xy[1] * 22


y_test[:] = [[i[0] * 35, i[1] * 22] for i in y_test] 

#for i in range(len(p)):
	#print y_test[i][0], y_test[i][1], " -> ", p_f[i][0], p_f[i][1]


mse = MSE(y_test, p_f)
rmse = sqrt( mse )

print "testing MSE:", mse
print "testing RMSE:", rmse
