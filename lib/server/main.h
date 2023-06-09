#ifndef SERVER_MAIN_H
#define SERVER_MAIN_H

#include "server.h"

#define TRACE_LOG_FILE_NAME "trace.log"
#define STATS_FILE_NAME "stats.txt"

/**
 *  Function to close connection with socket
 * @param fd file descriptor number
 */
void close_connection(int fd);

/**
 * Function to evaluate messages
 * @param server server structure
 * @param fd file descriptor source number
 * @param message message
 * @return information about operation success
 */
int process_message(server *server, int fd, char *message);

/**
 * Function to save statistics data into file
 */
void saveStats();


#endif //SERVER_MAIN_H