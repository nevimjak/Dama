cmake_minimum_required(VERSION 3.0)
project(server C)

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pg")
SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -pg")
SET(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -pg")

set(CMAKE_C_STANDARD 99)

add_executable(server main.c client.h hashmap.h hashmap.c commands.c commands.h client.c game.c game.h server.c server.h arraylist.c arraylist.h main.h)
target_link_libraries(server m)
