from socket import *
from cv2 import *
import time
cam = VideoCapture(0)
device = socket()
device.bind(('192.168.0.5', 27011))
device.listen(1)
while True:
    conn, addr = device.accept()
    host = conn.recv(1024)
    clientdata = host.split(':')
    if clientdata[1] == 'init':
        output = socket()
        ip = clientdata[0]
        msg = ''
        a, b = cam.read()
        for x in range(0, 480):
            for y in range(0, 640):
                for z in range(0, 3):
                    msg += chr(b[x][y][z])

        output.connect((ip, 27014))
        output.send(msg)
        num_sent = 1
        print 'Msgs sent: ' + str(num_sent)
        try:
           while True:
                a, b = cam.read()
                msg = ''
                for x in range(0, 480):
                    for y in range(0, 640):
                        for z in range(0, 3):
                            msg += chr(b[x][y][z])
                            
                output.send(msg)
                num_sent += 1
                print 'Msgs sent: ' + str(num_sent)
        except Exception:
            print 'connection closing'
            output.close()
            conn.close()
    elif clientdata[1] == 'test':
        print clientdata[0]
        try:
            conn.send('connection_established')
            time.sleep(3)
            conn.close()
        except:
            print 'error with test connection'
            if conn:
                conn.close()
cam.release()
device.close()
    
    
