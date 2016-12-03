import socket
import sys
import random
import string
import ssl
import time
import threading
import os

# FUNCTIONS ---------------------------------------------------------------

def generateRandomString(): # Returns a new random string to generate a QRCode
	return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(20))
# END of generateRandomString()


def createTable(): # Creates a new table with an ID, QRCode and n_seats
	qrcodeString = generateRandomString() 
	tableID = random.randint(1,100)
	seats = random.randint(1,10)
	idSeats = [tableID, qrcodeString, seats] # [tableID, QRCodeString, n_seats]
	
	infoTable.append(idSeats)

	return tableID, qrcodeString, seats
# END of createTable()

def sendQRCodeSocket():	# Server send QRCode string to client
	port = 10000
	servicename = "SendQR"

	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind((hostname, port))
	print("<{}>:Port: {}".format(servicename, port))

	serversocket.listen(5)
	print("<{}>:Listenning".format(servicename))
	while 1:
		(tablesocket, address) = serversocket.accept()
		print("\n<{}>:Got a connection from <{}>".format(servicename, address))

		connstream = ssl.wrap_socket(tablesocket,
		                             server_side=True,
		                             certfile="restaurant/server.crt",
		                             keyfile="restaurant/server.key",
		                             ssl_version=ssl.PROTOCOL_TLSv1_2)
		
		
		
		tableID, qr, nseats = createTable()
		print("<{}>:New table <{}> <{}> <{}>".format(servicename, tableID, qr, nseats))
		print("<{}>:Sending QRCode <{}>".format(servicename, qr))
		
		connstream.send(str.encode(qr))
		
		print("Certificate Info")
		print(connstream.cipher())
		print("Certificate Info DONE")

		connstream.close()
		tablesocket.close()
# END of sendQRCodeSocket()


def connectionServerClient():
	port = 10001
	servicename = "connectionServerClient"
	
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind((hostname, port))
	print("<{}>:Port {}".format(servicename, port))
		
	serversocket.listen(5)
	print("<{}>:Listenning".format(servicename))
	while 1:
		(clientsocket, address) = serversocket.accept()
		print("\n<{}>:Got a connection from <{}>".format(servicename, address))
			
		secureServerClient = ssl.wrap_socket(clientsocket,
		                             server_side=True,
		                             certfile="restaurant/server.crt",
		                             keyfile="restaurant/server.key",
		                             ssl_version=ssl.PROTOCOL_TLSv1_2)
		
		service = secureServerClient.recv(32).decode("utf-8")
		print("Service Requested: <{}>".format(service))
		if(service == "ReceiveQR"):
			receiveQRCode(secureServerClient)
		elif(service == "ReceiveOrder"):
			receiveOrder(secureServerClient)
		elif(service == "ReceiveIDToPay"):
			calculatePrices(secureServerClient)
			
			
		secureServerClient.close()
		clientsocket.close()
			
# END of connectionServerClient()
		

def receiveQRCode(secureServerClient): # Server receives QRCode string from the Customer
	servicename = "ReceiveQR"	
		
	aux = secureServerClient.recv(24)
	data = aux.decode("utf-8")
	print("<{}>:Received <{}>".format(servicename, data))
	exists = False
	for i in infoTable:
		if i[1] == data :
			clientID = random.randint(1,1000)
			print("<{}>:QRCode received corresponds to table <{}> | <{}> ".format(servicename, i[0], data))
			print("<{}>:Customer <{}> is seated in table <{}> ".format(servicename, clientID, i[0]))
			clientsTable.update({clientID : i[0]})
			secureServerClient.send(str.encode("{}:{}".format(clientID, i[0])))			
			exists = True
	if not exists :
		print("<{}>:QRCode received DONT exists <{}> ".format(servicename, data))
		secureServerClient.send(str.encode(str(-1)))

		
# END of receiveQRCodeSocket()

def receiveOrder(secureServerClient): # Server receives order from the Customer
	servicename = "ReceiveOrder"	
		
	aux = secureServerClient.recv(2048)
	data = aux.decode("utf-8")
	id = data.split(':')
	clientID = int(id[0])
	datasplited = id[1].split(',')
	print("<{}>:Received order <{}> from customer <{}>".format(servicename, datasplited, clientID))
	aux = []
	for i in datasplited:
		aux += i.split(' ')
	orders = {}
	updateOrder = {}
	finalValueOfOrders = 0
	i = 0
	if (clientID in clientsTable):
		while i+1 < len(aux):
			if (clientsOrders.get(clientID, 'empty') == 'empty'):
				orders.update({aux[i] : aux[i+1]})
				
			else:
				updateOrder = clientsOrders.get(clientID)
				if(updateOrder.get(aux[i], 'empty') != 'empty'):
					finalValueOfOrders = int(updateOrder.get(aux[i])) + int(aux[i+1])
					updateOrder[aux[i]] = finalValueOfOrders
				else:
					updateOrder.update({aux[i] : aux[i+1]})
					
			i += 2
	if(not orders):
		orders = updateOrder
	
	clientsOrders.update({clientID : orders})		
	print("<{}>:Total order <{}>".format(servicename, clientsOrders))
					
# END of receiveOrderSocket()

def calculatePrices(secureServerClient):
	servicename = "ReceiveIDToPay"
		
	aux = secureServerClient.recv(128)
	data = aux.decode("utf-8")
	print("Client id:<{}>".format(data))
	clientID = int(data)
	orders = {}
	f = {}
	valueToPay = 0
	
	if((clientID in clientsOrders) and (clientsOrders.get(clientID,'empty') != 'empty')):
		orders = clientsOrders.get(clientID)
	for key in orders.keys():
		f.update({key : food[key]*float(orders[key])})
	for key in f:
		valueToPay += f[key]
	
	randomClientID = generateRandomString()
	dataToSend = str(f) + " . " + str(valueToPay) + " . " + randomClientID
	print("Data to send:<{}>".format(dataToSend))
	
	clientsPayment.update({clientID : valueToPay})
	clientIDRandomIDValueToPay.update({clientID : [randomClientID, valueToPay]})
	 
	secureServerClient.send(str.encode(dataToSend))
		
	#sendRandomClientIDValueToPay(clientID)
# END of calculatePrices()
	

def sendRandomClientIDValueToPay(clientID):
	port = 10002
	servicename = "sendClientIDValueToPay"
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	
	connstream = ssl.wrap_socket(serversocket,
	                             certfile="pay_dal/pay_dal.crt",
	                             keyfile="pay_dal/pay_dal.key",
	                             ssl_version=ssl.PROTOCOL_TLSv1_2)	
	
	connstream.connect((hostname, port))
	print("<{}>: Port: {}".format(servicename, port))
	connstream.send(str.encode("sendClientIDValueToPay"))
	
	randomIDValueToPay = clientIDRandomIDValueToPay[clientID]
	
	dataToSend = randomIDValueToPay[0] + " : " + str(randomIDValueToPay[1])
	 
	
	connstream.send(str.encode(dataToSend))
	print("Data Sent: <{}>".format(dataToSend))
	
	connstream.close()
	serversocket.close()	

# END of sendClientIDValueToPay()		
		
class myThread (threading.Thread):
	def __init__(self, threadID, name, counter):
		threading.Thread.__init__(self)
		self.threadID = threadID
		self.name = name
		self.counter = counter
	def stop(self):
		self._stop.set()	
	def run(self):
		print("Starting " + self.name)
		if(self.name=="SendQR"):
			sendQRCodeSocket()
		if(self.name=="connectionServerClient"):
			connectionServerClient()
		print("Exiting " + self.name)
# END of myThread

# MAIN PROGRAM -----------------------------------------------------------------------

infoTable = [] 		# info about each table [tableID, QRCode, n_seats]
clientsTable = {} 	# info where the client is seated {clientID : tableID}
clientsOrders = {}	# Orders that each customer ordered {clientID : {order : quantity}}
clientsPayment = {}	# Total value that a customer have to pay {clientID : value}
clientIDRandomIDValueToPay = {}	# {clientID : [ randomID , valueToPay ]}

food = {"bPerfect": 7.5, "bToque": 7.0, "bHappy": 7.5, "bCool": 6.5, "bSmart": 6.0, "bSpicy": 6.5,
        "water": 1.5, "coke": 1.5, "lemonade": 1.5, "wine": 1.0, "beer": 1.0,"bBrownie": 3.0, "bCheese": 3.0}

hostname = ''

certificatefile = "restaurant.cer"	# Certifate file name
keyfile = "key.pem"		# Public Key file name

BUFFER = 1024

print("SmartRestaurant Server")

try:
	thread1 = myThread(1, "SendQR", 1)
	thread2 = myThread(1, "connectionServerClient", 1)
	thread3 = myThread(1, "ReceiveIDToPay", 1)
	thread1.start()
	thread2.start()
	thread3.start()
	input("Press key for close")
	print("Closing!")
	os._exit(1)
except:
	print("Error!")
	

# END PROGRAM -----------------------------------------------------------------------
