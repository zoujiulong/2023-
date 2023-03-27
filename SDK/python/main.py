#!/bin/bash
import sys
import math
import numpy as np
# 4个机器人，每个8个属性
robotState=[[0]*8]*4
workbenchStation=[[0]*6]*50
# 目标工作台
goal=[]
K=0
# 5ms检测一下
dt=0.005
robotR=0.45
evalParam = [0.045, 0.1 ,0.1, 3.0]
obstacle=[]
def norm(diff):
    return math.sqrt(diff[0]*diff[0]+diff[1]*diff[1])
def f(robotstate,u,robot_id):
    sys.stderr.write(str(robotstate[5])+'\n')
    F=np.array([[1,0,0,0,0],[0,1,0,0,0],[0,0,1,0,0],[0,0,0,0,0],[0,0,0,0,0]])
    B=np.array([[dt*robotstate[3],0],[dt*robotstate[4],0],[0,dt],[1,0],[0,1]])
    data=[]
    data.append(robotstate[6])
    data.append(robotstate[7])
    data.append(robotstate[5])
    data.append(norm([robotstate[3], robotstate[4]]))
    data.append(robotstate[2])
    for i in range(len(data)):
        sys.stderr.write(str(data[i])+'\n')
    update=(F.dot(data)+B.dot(u)).tolist()
    # for i in range(len(update)):
    #     robotState[robot_id][6]=update[0]
    #     robotState[robot_id][7]=update[1]
    #     robotState[robot_id][5]=update[2]
def read_util_ok():
    robotid=0
    work_id=0
    s=sys.stdin.readline()
    while s != "OK":
        parts=s.split()
        if(len(parts)==1 and parts[0].isdigit()):
            K=parts[0]
        if(len(parts)==10):
            # 工作台ID
            robotState[robotid][0]=int(parts[0])
            # 物品style
            robotState[robotid][1]=int(parts[1])
            # 角速度
            robotState[robotid][2]=float(parts[4])
            # Vx
            robotState[robotid][3]=float(parts[5])
            #Vy
            robotState[robotid][4]=float(parts[6])
            # 朝向
            robotState[robotid][5]=float(parts[7])
            # x
            robotState[robotid][6]=float(parts[8])
            # y
            robotState[robotid][7]=float(parts[9])
            sys.stderr.write(str(robotState[robotid][5]))
            robotid=robotid+1
        if len(parts)==6:
            workbenchStation[work_id][0] = int(parts[0])
            workbenchStation[work_id][1] = float(parts[1])
            workbenchStation[work_id][2] = float(parts[2])
            workbenchStation[work_id][3] = int(parts[3])
            workbenchStation[work_id][4] = int(parts[4])
            workbenchStation[work_id][5] = int(parts[5])
            work_id=work_id+1
        sys.stderr.write(s+'\n')
        s=input()
        pass
def finish():
    sys.stdout.write('OK\n')
    sys.stdout.flush()


if __name__ == '__main__':
    read_util_ok()
    finish()
    while True:
        line = sys.stdin.readline()
        if not line:
            break
        parts = line.split(' ')
        frame_id = int(parts[0])
        read_util_ok()
        sys.stdout.write('%d\n' % frame_id)
        line_speed, angle_speed = 3, 1.5
        for robot_id in range(1):
            sys.stderr.write(str(robotState[0][5]))
            f(robotState[robot_id],[norm([robotState[robot_id][3],robotState[robot_id][4]]),robotState[robot_id][2]],robot_id)
            sys.stderr.write(str(robotState[robot_id][5])+'\n')
            sys.stdout.write('forward %d %d\n' % (robot_id, line_speed))
            sys.stdout.write('rotate %d %f\n' % (robot_id, angle_speed))
        finish()
