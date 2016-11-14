# AndroidApp
An app designed within an Andriod course. A prototype home security system

Description:				The app developed is a basic prototype for a home security system.
					The app interfaces with a camera via a basic protocol using sockets
					over TCP/IP, and displays this information within an ImageView
					to the user. Additionally, user information (name, device names/IPs/etc)
					are stored to identify and interface with the user and devices.
					Using this app requires transmission of image data corresponding to
					a 640*480 image, with RGB values stored as one byte each of the data
					transmitted: (640*480) * 3bytes. The device has a simple protocol
					running as a daemon on the device with the following format:
					
```
(Client) ip:method
	 ip 		- IP is the IPv4 address of the device.
	 
	 method		- Method is either test/init.
			- test connects to a socket on port 27011, returning the phrase connection_established.
			- init connects to a socket on port 27014, returning device data in the above 640x480x3 format.
```
													
Lastly, explicit bindings for both the device's connections and the app's connections must be input to ensure they communicate properly. The app's bindings are located within the ConnectionManager object, and the device's are within the bind method of the included code.
					
Third party libraries:		The prototype device uses OpenCV for image processing, and is included for completeness; however, any device conforming to the above protocol should be sufficient.
	OpenCV website: http://opencv.org/
	
Bugs:				a.	The thread started by the run method on the ConnectionManager object has
					indeterminate behaviour, once the connection has ended. It is intended to end
					following the exit of a loop corresponding with the life of the connection. The
					bug can be replicated by:
						1.	selecting a device from the AppMenu activity and starting the ShowVideo activity	
						2.	pressing back to return to the AppMenu
						3.	selecting a device from the AppMenu activity and starting the ShowVideo activity
					The thread which maintained the initial connection remains active, and throws an exception
					despite the use of a mutex to wait while the thread is neither terminated or new.
