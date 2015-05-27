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
		for rede in redes:
			t.append(redes[rede][y][x])


		if not all(v == 0 for v in t):
			#bits = bitfield(x) + bitfield(y)
			#arr.append([t, bits])
			arr.append([t, [x/35.0,y/22.0]])

			#print str(redes[rede][y][x]) + ",",

		#print str(x) + ", " + str(y)


#for i in range(len(arr)):
	#print arr[i]



y_test = []
y_test_x_int = []
y_test_y_int = []

ds = SDS(12, 2)
for input, target in arr:
	y_test.append(target)
	y_test_y_int.append(target[1] * 22)
	y_test_x_int.append(target[0] * 35)
	ds.addSample(input, target)


net = pickle.load( open( 'model.pkl', 'rb' ))



#net = buildNetwork(12, 16, 12, bias = True, hiddenclass=TanhLayer)
'''
#Genetic algorithm
net = buildNetwork(12, 16, 2, bias = True)
ga = GA(ds.evaluateModuleMSE, net, minimize=True)
for i in range(10000):
    net = ga.learn(0)[0]
'''

'''
net = buildNetwork(12, 16, 2, bias = True)
trainer = BackpropTrainer( net, ds, learningrate=0.1, momentum=0.01) #learningrate=0.1, momentum=0.01
trainer.trainUntilConvergence( verbose = False, validationProportion = 0.15, maxEpochs = 1000, continueEpochs = 10 )
pickle.dump( net, open( 'model.pkl', 'wb' ))
'''

'''
net = buildNetwork(12, 16, 2, bias = True)
ga = CMAES(ds.evaluateModuleMSE, net, minimize=True)
for i in range(2000):
    net = ga.learn(0)[0]
'''




p = net.activateOnDataset( ds )
p_x_int = []
p_y_int = []
for i in range(len(p)):
	p_x_int.append(p[i][0] * 35)
	p_y_int.append(p[i][1] * 22)
	#print p[i].astype(int), y_test[i]


#for i in range(len(p)):
	#print p_y_int[i], y_test_y_int[i]


xy = net.activate(dBmArray2quality([0, 0, 0, -92, -68, -74, -50, -49, 0, 0, 0, 0])) # 25, 3
print 25, 3, xy[0] * 35, xy[1] * 22

xy = net.activate(dBmArray2quality([0, 0, 0, -92, -62, -78, -57, -57, 0, 0, 0, 0])) # 24, 3
print 24, 3, xy[0] * 35, xy[1] * 22

xy = net.activate(dBmArray2quality([0, 0, 0, -86, -30, 0, -61, -61, 0, 0, -105, -109])) # 18, 14
print 18, 14, xy[0] * 35, xy[1] * 22


mseX = MSE( y_test_x_int, p_x_int )
mseY = MSE( y_test_y_int, p_y_int )
mse = (mseX + mseY) / 2.0
rmse = sqrt( mse )

print "testing MSE:", mse
print "testing RMSE:", rmse
