#!/bin/bash
import sys
import math
workId=0
map=[[0]*6]*50
Robot=[[0]*5]*4
producer='1234567'
consumer='456789'
materialId=['12','13','23','456','7','1234567']
forwards=0.0
def read_util_ok():
    RobotId=0
    worksId=0
    s=''
    while s != "OK":
        s=sys.stdin.readline()
        pass
def finish():
    sys.stdout.write('OK\n')
    sys.stdout.flush()
def buyShortestRoot( x,  y) :
    min = sys.maxsize
    j = 0
    for i in range(len(map)):
        primState = bin(map[i][4]).replace('0b', '')
        primState=primState[::-1]
        if map[i][0] >= 1 and str(int(map[i][0])) in producer and map[i][5]==1:
            if math.sqrt(math.pow(x - map[i][1], 2) + math.pow(y - map[i][2], 2)) < min:
                min = math.sqrt(math.pow(x - map[i][1], 2) + math.pow(y - map[i][2], 2))
                j = i
    map[j][5]=0
    return j
def iscorrectDirect(firstX,firstY,workStationX,workStationY,theta) :
    forwards = math.atan((workStationY - firstY) / (workStationX - firstX))
    if ((workStationY - firstY) > 0 and (workStationX - firstX) < 0) :
        forwards = forwards + math.PI;
    elif ((workStationY - firstY) < 0 and (workStationX - firstX) < 0) :
        forwards = forwards - math.PI;
    if round(forwards,1)==round(theta,1):
        return True
    return False
def isReachWorkStation(firstX,firstY,workStationX,workStationY) :
    if (math.sqrt(math.pow(firstX - workStationX, 2) + math.pow(firstY - workStationY, 2)) < 0.4) :
        return True;
    return False;
def sellShortestRoot(x,y,type)->int:
    min = sys.maxsize
    j = 0
    for i in range(len(map)):
        primState=bin(map[i][4]).replace('0b','')
        primState=primState[::-1]
        if map[i][0] >= 1 and str(int(map[i][0])) in consumer and (len(primState)-1 < type or primState[int(type)] != '1') and (str(int(type))) in materialId[int(map[i][0])-4] :
            if math.sqrt(math.pow(x - map[i][1], 2) + math.pow(y - map[i][2], 2)) < min :
                min=math.sqrt(math.pow(x - map[i][1], 2) + math.pow(y - map[i][2], 2))
                j = i;
    map[j][5] = 0;
    return j;
if __name__ == '__main__':
    read_util_ok()
    finish()
    while True:
        line = sys.stdin.readline()
        sys.stderr.write(line)
        if not line:
            break
        parts = line.split(' ')
        frame_id = int(parts[0])
        line_speed,angle_speed=3,1.5
        sys.stdout.write('%d\n' % frame_id)
        read_util_ok()
        for robot_id in range(4):
            if (Robot[robot_id][1] == 0):
                id=buyShortestRoot(Robot[robot_id][3], Robot[robot_id][4]);
                if (iscorrectDirect(Robot[robot_id][3], Robot[robot_id][4], map[id][1], map[id][2], Robot[robot_id][2])) :
                    lineSpeed = 6;
                    angleSpeed = 0;
                if (Robot[robot_id][0] != -1 and isReachWorkStation(Robot[robot_id][3], Robot[robot_id][4], map[id][1], map[id][2])) :
                    lineSpeed = 0;
                    angleSpeed = 1.5;
                    sys.stdout.write("buy %d\n", robot_id);
            else:
                id=sellShortestRoot(Robot[robot_id][3], Robot[robot_id][4], Robot[robot_id][1]);
                if (iscorrectDirect(Robot[robot_id][3], Robot[robot_id][4], map[id][1], map[id][2], Robot[robot_id][2])) :
                    lineSpeed = 6;
                    angleSpeed = 0;
                if (Robot[robot_id][0] != -1 and isReachWorkStation(Robot[robot_id][3], Robot[robot_id][4], map[id][1], map[id][2])) :
                    lineSpeed = 0;
                    angleSpeed = 1.5;
                    sys.stdout.write("sell %d\n", robot_id);
            sys.stdout.write('forward %d %d\n' % (robot_id, line_speed))
            sys.stdout.write('rotate %d %f\n' % (robot_id, angle_speed))
        finish()



