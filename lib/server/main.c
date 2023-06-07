/*
** selectserver.c -- a cheezy multiperson chat server
** Source: beej.us/guide/bgnet/examples/selectserver.c
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <time.h>
#include <signal.h>
#include "main.h"

#include "arraylist.h"

#include "commands.h"
#include "client.h"

#define DEFAULT_PORT 9123   // port we're listening on

// get sockaddr, IPv4 or IPv6:

server *srv;
FILE *trace_file;

int process_message(server *server, int fd, char *message) {
    int i;
    int message_length = strlen(message);

    char *message_type;
    int argc = 0;

    if(message[message_length - 1] == '\n') {
        message[message_length - 1] = '\0';
    }

    if(message_length > 1) {
        if(message[message_length - 2] == 13) {
		    message[message_length - 2] = '\0';

	    }
    }

    message_length = strlen(message);
    if(message[message_length - 1] == '|') {
        message[message_length - 1] == '\0';
        message_length--;
    }

    if(message_length == 0) {
        return EXIT_SUCCESS;
    }


    for(i = 0; i < message_length; i++) {
        if(message[i] == '|') {
            argc++;
        }
    }

    char *argv[argc];

    if(argc == 0) {
        message_type = message;
    } else {
        message_type = strtok(message, "|");
    }

    for(i = 0; i < argc; i++) {
        argv[i] = strtok(NULL, "|");
    }

    fcmd handler = get_handler(message_type);

    if(handler) {
        if(handler(server, server->clients[fd], argc, argv)) {
            server->stat_fail_requests++;
        }
        server->clients[fd]->invalid_count = 0;
    } else {
        send_message(server->clients[fd], "error|neznamy prikaz\n");
        server->clients[fd]->invalid_count++;
        server->stat_unknown_commands++;
    }

    return EXIT_SUCCESS;
}

void intHandler(int dummy) {
    saveStats();
    fflush(trace_file);
    fclose(trace_file);
    if(srv) {
        server_free(&srv);
    }
}

void saveStats() {
    FILE *stats_file = fopen(STATS_FILE_NAME, "w");
    fprintf(stats_file, "Sent messages: %d\n", srv->stat_messages_out);
    fprintf(stats_file, "Transmitted bytes: %d\n", srv->stat_bytes_out);
    fprintf(stats_file, "Received messages: %d\n", srv->stat_messages_in);
    fprintf(stats_file, "Received bytes: %d\n", srv->stat_bytes_in);
    fprintf(stats_file, "Fail requests: %d\n", srv->stat_fail_requests);
    fprintf(stats_file, "Unknown commands count: %d\n", srv->stat_unknown_commands);
    fflush(stats_file);
    fclose(stats_file);
}

int main(int argc, char **argv) {
    srand(time(NULL));

    if(argc < 3) {
        printf("Use ./server -p <port> or ./server --port <port>\n");
        printf("<port> - integer from 1 to 65465\n");
        return EXIT_SUCCESS;
    }

    int port = 0;

    int i;
    for(i = 1; i < argc - 1; i++) {
        if(!strcmp(argv[i], "-p") || !strcmp(argv[i], "--port")) {
            int l = strlen(argv[i + 1]);
            int j;
            for(j = 0; j < l; j++) {
                if(argv[i + 1][j] < '0' || argv[i + 1][j] > '9') {
                    port = -1;
                }
            }

            if(port == -1) {
                port = 9123;
            } else {
                port = atoi(argv[i + 1]);
            }
        }
    }

    if(port < 1 || port > 65565) {
        printf("Use ./server -p <port> or ./server --port <port>");
        printf("<port> - integer from 1 to 65465");
        return EXIT_SUCCESS;
    }

    char buf_port[10];
    sprintf(buf_port, "%d", port);



    signal(SIGINT, intHandler);
    srv = server_init();
    trace_file = fopen(TRACE_LOG_FILE_NAME, "a+");

    if(!srv) {
        printf("Initialization failure");
        return EXIT_FAILURE;
    }

    server_listen(srv, buf_port);
}

int send_message(struct client *client, char *message) {

    if(client->name) {
        printf("Sending to client %s (%d) message: %s", client->name, client->fd, message);
    } else {
        printf("Sending to socket %d message: %s", client->fd, message);
    }
    int nbytes;
    nbytes = send(client->fd, message, strlen(message), 0);
    if(nbytes > 0) {
        srv->stat_messages_out++;
        srv->stat_bytes_out += nbytes;
    }
}


void trace(char *format,...) {
    va_list args;
    va_start(args, format);
    vprintf(format, args);
    va_end(args);
    printf("\n");

    va_start(args, format);
    vfprintf(trace_file, format, args);
    va_end(args);
    fprintf(trace_file, "\n");

    fflush(trace_file);

}