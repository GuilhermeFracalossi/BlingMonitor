# BlingMonitor
**A camera surveillance system aimed at HTTP streams**

![Screenshot](src/main/resources/org/images/main-screen-print.jpg)

A free, open source Java software for monitoring cameras in a network. Supports any kind of cameras streams, such as HTTP, RTSP and RTMP. 

## Features
* **Grid-mode**: group all registered cameras to fit in a maximized screen
* **Slide-mode**: show each camera individually and slide it after a certain time
* Group up to 10 cameras in the visualization grid
* Auto scan: searches for HTTP cameras connected to the network, given an initial IP. Supports custom range of IP's and ports.
* Detects when a camera goes down, alerts and reconnects automatically once is up again
* Change brightness, contrast, hue and gama of each camera for better visualization
* Assign a custom name to each camera
* Save frames from any camera
* Change the size of the camera's grid
* Full screen mode
* Log system

## How to use
Just download the `BlingMonitor.jar` from the [Release](https://github.com/GuilhermeFracalossi/BlingMonitor/releases/) tab and run it with
a double click or `java -jar BlingMonitor.jar` from the terminal.

## Auto scan
***Atention!***

The camera's scan is basically a network mapping, so make sure that you are the administrator or have permissions to do such 
things in the network, since it can be considered a crime in some countries.

The scan searches for open ports in the range of ip:hosts that it's given (can be configured in the configuration screen).
Thereby, it can not detect a camera that it's streaming in a subdomain of some IP, like `/video`, or `/cam`, although it will
still recognize it as an open port and register the camera, but the address will end at the number of the port, not the subdomain included, 
thus not being able to reproduce the camera later in the visualization.

*Practical example:*

1. Camera it's up in the address `http://192.168.0.9:8081` -> the scan identifies the complete address
`http://192.168.0.9:8081` and the visualization **does work**.
2. Camera it's up in the address `http://192.168.2.10:8081/video` -> the scan identifies just part of the address
   `http://192.168.2.10:8081` and the visualization **does NOT work**.

## Requirements
* [VLC](https://www.videolan.org/vlc/index.html). On linux, recommend *not* using the snap version,
  but instead, the one that your package manager provides. `sudo apt-get install vlc`
* [Java](https://www.oracle.com/br/java/technologies/javase-downloads.html) version 13+ recommended.


## About
Both for Linux and Windows.

The GUI is made with recent versions of [JavaFX](https://openjfx.io/), 
while the visualization of the cameras is based on [VLCJ 4](https://github.com/caprica/vlcj).
Works really well together with [Motion](https://motion-project.github.io/) as the 
server for the cameras, since it's capable of scanning for HTTP cameras.

A minimum amount of GPU and CPU resources are necessary for the streams to run. Each stream of a camera is like 
an instance of VLC running, so if you are going to display 10 1080p cameras, it's the same
as opening VLC 10 times and streaming a camera on each instance.

If the device is too old or not compatible with this kind of streams, the software on this Java
version won't work, but we are working on a web-based system aimed at low profile devices. 

You can test if the software will be capable of showing you the camera's streams by simply 
opening `VLC -> Media -> Open Network Stream`, and checking if you can open a camera stream.



