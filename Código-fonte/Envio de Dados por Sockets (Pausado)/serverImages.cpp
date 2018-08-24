

#include <iostream>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <unistd.h>

using namespace std;

int main(){


    int client1, client2, server;
    int portNum = 10001;
    char *ip = "10.42.0.1";
    bool isExit = false;
    int ok = 1;
    int type;

    struct sockaddr_in server_addr;
    socklen_t size;



    server = socket(AF_INET, SOCK_STREAM, 0);

    if (server < 0){
        cout << "\nError establishing socket..." << endl;
        exit(1);
    }


    cout << "\n=> Socket server has been created..." << endl;


    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr(ip);
    server_addr.sin_port = htons(portNum);




    if ((bind(server, (struct sockaddr*)&server_addr,sizeof(server_addr))) < 0)
    {
        cout << "=> Error binding connection, the socket has already been established..." << endl;
        return -1;
    }



    size = sizeof(server_addr);


    while (true){

        int client;
        client1 = -1;
        client2 = -1;


        cout << "=> Looking for clients..." << endl;

        do{
            listen(server, 1);


            client = accept(server,(struct sockaddr *)&server_addr,&size);


            // first check if it is valid or not
            if (client < 0)
                cout << "=> Error on accepting..." << endl;


            send(client, &ok, sizeof(int), 0);
            recv(client, &type, sizeof(int), 0);

            if(type == 1){
                client1 = client;
                cout << "=> Client 1 connected" << endl;
            }else if(type == 2){
                client2 = client;
                cout << "=> Client 2 connected" << endl;
            }


        }while(client1 == -1 || client2 == -1);

        send(client1, &ok,  sizeof(int), 0);
        send(client2, &ok,  sizeof(int), 0);







        int tamanhos[3];
        int tamanho;



        while(!isExit){

            int bytes;

            bytes = recv(client1, tamanhos,  sizeof(int)*3, 0);

            if(bytes < 0){
                isExit = true;
                break;
            }


            bytes = send(client2, tamanhos,  sizeof(int)*3, 0);

            if(bytes < 0){
                isExit = true;
                break;
            }

            tamanho = tamanhos[0];

            char *buffer = (char*) malloc (tamanho);
            if (buffer==NULL) exit (1);
            int ou;


            for(ou = 0;ou < tamanho;ou+=bytes){
                (bytes = recv(client1, buffer +ou, tamanho - ou, 0));

                if(bytes < 1 && ou < tamanho){
                    isExit = true;
                    break;
                }
            }


            bytes = send(client2, buffer, tamanho, 0);
            if(bytes < 0){
                isExit = true;
                break;
            }

            free(buffer);


        }

        cout << endl << "=>Disconnecting..." << endl << endl;
        isExit = false;
        close(client1);
        close(client2);


    }

    close(server);
    return 0;
}

