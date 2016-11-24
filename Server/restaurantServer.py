import socket
import sys
import random
import string
import ssl
import thread
import time
import threading
import os

# FUNCTIONS ---------------------------------------------------------------

def createQRCodeString(): # Returns a new random string to generate a QRCode
	return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(20))
# END of createQRCodeString()


def createTable(): # Creates a new table with an ID, QRCode and n_seats
	qrcodeString = createQRCodeString() 
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
	print"<{}>:Port: {}".format(servicename, port)
	
	serversocket.listen(5)
	print "<{}>:Listenning".format(servicename)
	while 1:
		(clientsocket, address) = serversocket.accept()
		print "\n<{}>:Got a connection from <{}>".format(servicename, address)
		
		connstream = ssl.wrap_socket(clientsocket,
                             server_side=True,
                             certfile="server.crt",
                             keyfile="server.key",
		             ssl_version=ssl.PROTOCOL_TLSv1)		

		tableID, qr, nseats = createTable()
		print "<{}>:New table <{}> <{}> <{}>".format(servicename, tableID, qr, nseats)
		print "<{}>:Sending QRCode <{}>".format(servicename, qr)
		connstream.send(qr)
	
		connstream.close()
		clientsocket.close()
# END of sendQRCodeSocket()

def receiveQRCodeSocket(): # Server receives QRCode string from the Customer
	port = 10001
	servicename = "ReceiveQR"
	global clientID
	
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind((hostname, port))
	print "<{}>:Port {}".format(servicename, port)
	
	serversocket.listen(5)
	print "<{}>:Listenning".format(servicename)
	while 1:
		(clientsocket, address) = serversocket.accept()
		print "\n<{}>:Got a connection from <{}>".format(servicename, address)
		
		data = clientsocket.recv(24)
		print "<{}>:Received <{}>".format(servicename, data)
		exists = False
		for i in infoTable:
			if i[1] == data :
				print "<{}>:QRCode received corresponds to table <{}> | <{}> ".format(servicename, i[0], data)
				print "<{}>:Customer <{}> is seated in table <{}> ".format(servicename, clientID, i[0])
				clientsTable.update({clientID : i[0]})
				clientsocket.send("{}:{}".format(clientID, i[0]))			
				clientID = random.randint(1,1000)
				exists = True
		if not exists :
			print "<{}>:QRCode received DONT exists <{}> ".format(servicename, data)
			clientsocket.send(str(-1))

		clientsocket.close()	
# END of receiveQRCodeSocket()

def receiveOrderSocket(): # Server receives order from the Customer
	port = 10002
	servicename = "ReceiveOrder"
	serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serversocket.bind((hostname, port))
	print "<{}>: Port: {}".format(servicename, port)

	serversocket.listen(5)
	print "<{}>:Listenning".format(servicename)
	while 1:
		(clientsocket, address) = serversocket.accept()
		print "\n<{}>:Got a connection from <{}>".format(servicename, address)
	
		#ssl.wrap_socket(sock, keyfile=None, certfile=None, server_side=True, cert_reqs=CERT_NONE, 
		#ssl_version=ssl.PROTOCOL_, ca_certs=None, do_handshake_on_connect=True, suppress_ragged_eofs=True, ciphers=None)
		
		data = clientsocket.recv(2048)
		customerID = -1
		print "<{}>:Received order <{}> from customer <{}>".format(servicename, data, customerID)
		datasplited = data.split(',')
		aux = []
		for i in datasplited:
			aux += i.split(' ')
		orders = {}
        customerID = aux[0]
		i = 1
		while i+1 < len(aux):
			orders.update({aux[i] : aux[i+1]})
			i += 2
		clientsocket.close()	
# END of receiveOrderSocket()

class myThread (threading.Thread):
	def __init__(self, threadID, name, counter):
		threading.Thread.__init__(self)
		self.threadID = threadID
		self.name = name
		self.counter = counter
	def stop(self):
		self._stop.set()	
	def run(self):
		print "Starting " + self.name
		if(self.name=="SendQR"):
			sendQRCodeSocket()
		if(self.name=="ReceiveQR"):
			receiveQRCodeSocket()
		if(self.name=="ReceiveOrder"):
			receiveOrderSocket()		
		print "Exiting " + self.name
# END of myThread

# MAIN PROGRAM -----------------------------------------------------------------------

clientID = 1

infoTable = [] 		# info about each table [tableID, QRCode, n_seats]
clientsTable = {} 	# info where the client is seated {clientID : tableID}
clientsOrders = {}	# Orders that each customer ordered {clientID : {order : quantity}}

burgers = {"b'Perfect": 7.5, "b'Toque": 7.0, "b'Happy": 7.5, "b'Cool": 6.5, "b'Smart": 6.0, "b'Spicy": 6.5}
drinks = {"Water": 1.5, "Coke": 1.5, "Lemonade": 1.5, "Wine": 1.0, "Beer": 1.0 }
deserts = {"b'Brownie": 3.0, "b'Cheese": 3.0}

hostname = ''

certificatefile = "restaurant.cer"	# Certifate file name
keyfile = "key.pem"		# Public Key file name

BUFFER = 1024

print("SmartRestaurant Server")

try:
	thread1 = myThread(1, "SendQR", 1)
	thread2 = myThread(1, "ReceiveQR", 1)
	thread3 = myThread(1, "ReceiveOrder", 1)
	thread1.start()
	thread2.start()
	thread3.start()
	raw_input()
	print "Closing!"
	os._exit(1)
except:
	print "Error!"
	

# END PROGRAM -----------------------------------------------------------------------
