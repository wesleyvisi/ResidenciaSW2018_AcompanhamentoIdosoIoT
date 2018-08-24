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




using namespace cv;
using namespace std;


int keyboard;





int main(int argc, char *argv[])
{


    int client;
    int portNum = 10001; // NOTE that the port number is same for both client and server
    bool isExit = false;
    char* ip = "10.42.0.1";
    int i, type = 2;

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
        cout << "=> Connection confirmed, you are good to go..." << endl << endl;
        send(client, &type,4, 0);
        recv(client, &i, sizeof(int), 0);
    }









    while( (char)keyboard != 'q' && (char)keyboard != 27 )
    {





        int frameSizes[3];



        recv(client, frameSizes, sizeof(int)*3, 0);






        int bytes = 0;
        int ou;
        int frameSize = frameSizes[0];
        int width = frameSizes[1];
        int height = frameSizes[2];


        char *buffer = (char*) malloc(frameSize);



        for ( ou = 0; ou < frameSize; ou += bytes)
        {
            if ((bytes = recv(client, buffer +ou, frameSize  - ou, 0)) == -1)
            {
                //quit("recv failed", 1);
            }
        }





        Mat frame(Size(width,height),16,buffer);
        resize(frame,frame,Size(640,480));

        free(buffer);


        imshow("tela2", frame);









        keyboard = waitKey( 30 );
    }


    cout << "\n=> Connection terminated.\nGoodbye...\n";

    close(client);
    return 0;

}



