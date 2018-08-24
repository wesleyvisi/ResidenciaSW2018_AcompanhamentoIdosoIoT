
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui.hpp"
#include "opencv2/video.hpp"

#include <stdio.h>
#include <stdlib.h>
#include <iostream>

#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>

#include <fstream>


//`pkg-config --cflags opencv` `pkg-config --libs opencv`




using namespace cv;
using namespace std;

Mat frame;

int keyboard;









int main(int argc, char *argv[])
{


    int client;
    int portNum = 10001;
    char* ip = "10.42.0.1";
    int i, type = 1;
    int width = 320, height = 240;


    vector<uchar> jpgbytes;


    struct sockaddr_in server_addr;

    client = socket(AF_INET, SOCK_STREAM, 0);

    if (client < 0)
    {
        cout << "\nError establishing socket..." << endl;
        exit(1);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr(ip);
    server_addr.sin_port = htons(portNum);

    if (connect(client,(struct sockaddr *)&server_addr, sizeof(server_addr)) == 0)
        cout << "=> Connection to the server port number: " << portNum << endl;

    cout << "=> Awaiting confirmation from the server..." << endl; //line 40
    recv(client, &i, sizeof(int), 0);
    if(i == 1)
    {
        cout << "=> Connection confirmed, you are good to go..."<< endl<< endl;
        send(client, &type,4, 0);
        recv(client, &i, sizeof(int), 0);
    }











    char* videoFilename = "0";

    VideoCapture capture;

    int deviceID = 0;
    int apiID = cv::CAP_ANY;



    //capture.open(deviceID+apiID);
    capture.open("rtsp://10.42.0.94:554/user=admin&password=admin&channel=1&stream=1.sdp?");
    if(!capture.isOpened())
    {
        cerr << "Unable to open video file: "<<videoFilename<<endl;
    }



    while( (char)keyboard != 'q' && (char)keyboard != 27 )
    {

        if(!capture.read(frame))
        {
            cerr << "Unable to read next frame." << endl;
            cerr << "Exiting..." << endl;
            exit(EXIT_FAILURE);
        }







        resize(frame,frame,Size(width,height));

        imshow("Frame", frame);




        int frameSizes[3];
        int frameSize = frame.total() * frame.elemSize();

        frameSizes[0] = frameSize;
        frameSizes[1] = width;
        frameSizes[2] = height;





        send(client, frameSizes,sizeof(int)*3, 0);




        int bytes = 0;
        int ou;
        for(ou = 0; ou < frameSize; ou += bytes)
        {
            bytes = send(client, frame.data+ou,frameSize - ou, 0);


        }




        keyboard = waitKey( 30 );
    }

    capture.release();

    cout << "\n=> Connection terminated.\nGoodbye...\n";

    close(client);
    return 0;

}


