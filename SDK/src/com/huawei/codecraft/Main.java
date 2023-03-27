package com.huawei.codecraft;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.text.DecimalFormat;

import static javafx.scene.input.KeyCode.R;

public class Main {
    private static int workId;
    private static final Scanner inStream = new Scanner(System.in);
    private static double[][] map = new double[50][8];
    private static double[][] Robot=new double[4][8];
    private static long currTime;
    private static double[][] pair=new double[4][2];
    private static String producer="1234567";
    private static String consumer="456789";
    private static String high="4567";
    private static int M=50;
    private static int[] workType=new int[9];
    private static String[] materialId={"12","13","23","456","7","1234567"};
    private static int[] lattice=new int[7];
    private static int[] total=new int[7];
    private static int[] workNum=new int[7];
    private static int[] cworkNum=new int[6];
    private static final PrintStream outStream = new PrintStream(new BufferedOutputStream(System.out));
    private static double forwards;
    private static int totalNum=0;
    private static int workbench;
    private static double dt=0.003;
    private static double[] modelParam={6,Math.PI,25.84,51.3,0.3,0.5};
    private static double[] evalParm={0.045, 0.1 ,0.1, 0.015};
    private static int[] buy=new int[7];
    private static int frameID;
    private static int num=1;
    private static String[][] prebuy=new String[4][3];
    private static int[] b=new int[4];
    public static void main(String[] args) {
        for(int i=0;i<4;i++){
            for(int j=0;j<3;j++){
                prebuy[i][j]="";
            }
        }
        schedule();
    }

    private static void schedule() {
        readUtilOK();
        outStream.println("OK");
        outStream.flush();
        int currMoney;
        currTime = System.currentTimeMillis();
        while (inStream.hasNextLine()) {
            String line = inStream.nextLine();
            String[] parts = line.split(" ");
            frameID = Integer.parseInt(parts[0]);
            currMoney = Integer.parseInt(parts[1]);
            readUtilOK();
            outStream.printf("%d\n", frameID);
            for(int i=0;i<4;i++){
                pair[i][0]=0;
                pair[i][1]=2;
            }
            for (int robotId = 0; robotId < 4; robotId++) {
                int coe=1;
                if(Robot[robotId][1]==0){
                    int id=buyShortestRoot(Robot[robotId][3],Robot[robotId][4],robotId);
                    b[robotId]=(int)map[id][0];
                    prebuy[robotId][2]=(int)map[id][0]+"";
                    coe=direct(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2]);
                    //double[] u=DynamicWindowApproach(Robot[robotId],6);
                    //pair[robotId][0]=u[0];
                    //pair[robotId][1]=u[1];
                    //pair[robotId] =(int)Math.ceil(DynamicWindowApproach(Robot[robotId],6)[0]);
                    //pair[robotId][1]=DynamicWindowApproach(Robot[robotId],6)[1];
                    //pair[robotId][0]=2;
                    //pair[robotId][1]=1.5;
                    if (iscorrectDirect(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2])) {
                        pair[robotId][0] = 6;
                        pair[robotId][1] = 0;
                        //System.err.println(Robot[robotId][5]+" "+Robot[robotId][6]);
                    }
                    if (Robot[robotId][0]!=-1&&isReachWorkStation(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2])) {
                        pair[robotId][0] = 0;
                        pair[robotId][1] = 1.5;
                        outStream.printf("buy %d\n",robotId);
                        prebuy[robotId][2]="";
                        map[id][7]--;
                        num++;
                        id=sellShortestRoot(Robot[robotId][3],Robot[robotId][4],Robot[robotId][1],robotId);
                        coe=direct(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2]);
                    }
                }else{
                    int id=sellShortestRoot(Robot[robotId][3],Robot[robotId][4],Robot[robotId][1],robotId);
//                    prebuy[robotId][1]=id+"";
                    coe=direct(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2]);
                    //pair[robotId][0] =(int)Math.ceil(DynamicWindowApproach(Robot[robotId],id)[0]);
                    //pair[robotId][1]=DynamicWindowApproach(Robot[robotId],id)[1];
                    if (iscorrectDirect(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2])) {
                        pair[robotId][0]= 6;
                        pair[robotId][1] = 0;
                    }
                    if (Robot[robotId][0]!=-1&&isReachWorkStation(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2])) {
                        pair[robotId][0]= 0;
                        pair[robotId][1] = 1.5;
                        outStream.printf("sell %d\n", robotId);
                        prebuy[robotId][1]="";
                        map[id][6]--;
                        id=buyShortestRoot(Robot[robotId][3],Robot[robotId][4],robotId);
                        coe=direct(Robot[robotId][3], Robot[robotId][4], map[id][1], map[id][2], Robot[robotId][2]);
                    }
                }
                pair[robotId][1]=coe*pair[robotId][1];
                outStream.printf("forward %d %f\n", robotId, pair[robotId][0]);
                outStream.printf("rotate %d %f\n", robotId, pair[robotId][1]);
            }
            for(int i=0;i<7;i++){
                lattice[i]=0;
            }
            outStream.print("OK\n");
            outStream.flush();
        }
    }

    private static boolean readUtilOK() {
        String line;
        int count = 0;
        int RobotId = 0;
        int forth = 0;
        int second = 0;
        int index = 0;
        int worksId = 0;
        while (inStream.hasNextLine()) {
            line = inStream.nextLine();

            String[] parts = line.split(" ");
            if(parts.length==1&&Character.isDigit(parts[0].charAt(0))){
                workbench=Integer.parseInt(parts[0]);
            }
            if (parts.length == 6) {
                map[worksId][0] = Double.parseDouble(parts[0]);
                map[worksId][1] = Double.parseDouble(parts[1]);
                map[worksId][2] = Double.parseDouble(parts[2]);
                map[worksId][3] = Double.parseDouble(parts[3]);
                map[worksId][4] = Double.parseDouble(parts[4]);
                map[worksId][5] = Double.parseDouble(parts[5]);
                map[worksId][6]=0;
                //map[worksId][7]=0;
                if(4<=map[worksId][0]&&map[worksId][0]<=9){
                    StringBuffer primState=new StringBuffer(Integer.toBinaryString((int)map[worksId][4]));
                    primState.reverse();
                    for(int i=0;i<primState.length();i++){
                        if(primState.charAt(i)=='1'){
                            lattice[i-1]++;
                        }
                    }
                }
                if(totalNum<workbench){
                    if((int)map[worksId][0]>=1&&(int)map[worksId][0]<=7){
                        workNum[(int)map[worksId][0]-1]++;
                    }

                    switch ((int)map[worksId][0]){
                        case 4: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            total[0]++;
                            total[1]++;
                            break;
                        }
                        case 5: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            total[0]++;
                            total[2]++;
                            break;
                        }
                        case 6: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            total[1]++;
                            total[2]++;
                            break;
                        }
                        case 7: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            total[3]++;
                            total[4]++;
                            total[5]++;
                            break;
                        }
                        case 8: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            total[6]++;
                            break;
                        }
                        case 9: {
                            cworkNum[(int)map[worksId][0]-4]++;
                            for(int i=0;i<7;i++){
                                total[i]++;
                            }
                            break;
                        }
                    }
                    totalNum++;
                }
                workType[(int)map[worksId][0]-1]++;
                worksId++;
            }
            if (parts.length == 10) {
                Robot[RobotId][0]=Integer.parseInt(parts[0]);
                if(Robot[RobotId][1]>0){
                    lattice[(int)Robot[RobotId][1]-1]++;
                }
                Robot[RobotId][1]=Integer.parseInt(parts[1]);
                Robot[RobotId][2]=Double.parseDouble(parts[7]);
                Robot[RobotId][3]=Double.parseDouble(parts[8]);
                Robot[RobotId][4]=Double.parseDouble(parts[9]);
                Robot[RobotId][5]=Double.parseDouble(parts[5]);
                Robot[RobotId][6]=Double.parseDouble(parts[6]);
                Robot[RobotId][7]=Double.parseDouble(parts[4]);
                RobotId++;
            }
            if ("OK".equals(line)) {
                return true;
            }
            // do something;
        }
        return false;
    }

    private static boolean isReachWorkStation(double firstX, double firstY, double workStationX, double workStationY) {
        if (Math.sqrt(Math.pow(firstX - workStationX, 2) + Math.pow(firstY - workStationY, 2)) < 0.4) {
            return true;
        }
        return false;
    }

    private static boolean iscorrectDirect(double firstX, double firstY, double workStationX, double workStationY, double theta) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        /*forwards = Math.atan((workStationY - firstY) / (workStationX - firstX));
        if ((workStationY - firstY) > 0 && (workStationX - firstX) < 0) {
            forwards = forwards + Math.PI;
        } else if ((workStationY - firstY) < 0 && (workStationX - firstX) < 0) {
            forwards = forwards - Math.PI;
        }*/
        if (decimalFormat.format(forwards).substring(0, 3).equals(decimalFormat.format(theta).substring(0, 3))) {
            return true;
        }
        return false;
    }
    private static int buyShortestRoot(double x, double y,int robotid) {
        double min = Integer.MAX_VALUE;
        int j = -1;
        double dist;
        int type=0;
        String conduct="";
        int flag=0;
        if(workNum[0]+workNum[1]+workNum[2]<4){
            for(int i=0;i<map.length;i++){
                if(consumer.contains((int)map[i][0]+"")){
                    if(map[i][5]==1){
                        flag=1;
                        return i;
                    }
                }
            }
        }
        int num=50;
        int t=0;
        if(workNum[3]==workNum[4]&&workNum[3]==workNum[5]){

        }else{
            for(int i=3;i<7;i++){
                if(workNum[i]<num){
                    num=workNum[i];
                    t=i;
                }
            }
        }
        for(int i=0;i<4;i++){
            if(i!=robotid&&Robot[i][1]!=0&&prebuy[i][2]!=""&&prebuy[robotid][2]==""){
                prebuy[robotid][2]+=prebuy[i][2]+" ";
            }
        }
        String[] s=prebuy[robotid][2].split(" ");
        for(int i=0;i< map.length;i++) {
            if(t!=0&&(int)map[i][0]==t+1&&map[i][5]==1) {
                if((j=typedist((int)map[i][0],x,y))!=-1){
                    return j;
                }
            }
        }

        for(int i=0;i<map.length;i++){
                if((int)map[i][0]==7){
                    if((j=typedist(7,x,y))!=-1){
                        return j;
                    }
                }else if((int)map[i][0]==6){
                    if((j=typedist(6,x,y))!=-1){
                        return j;
                    }
                }else if((int)map[i][0]==5){
                    if((j=typedist(5,x,y))!=-1){
                        return j;
                    }
                }else if((int)map[i][0]==4){
                    if((j=typedist(4,x,y))!=-1){
                        return j;
                    }
                }


            if(consumer.contains((int)map[i][0]+"")) {
                StringBuffer primState = new StringBuffer(Integer.toBinaryString((int) map[i][4]));
                primState.reverse();
                int k = primState.length();
                switch ((int)map[i][0]) {
                    case 7: {
                        if (k == 7) {
                            if (primState.charAt(4) == '0' && primState.charAt(5) == '1') {
                                conduct = "12";
                                break;
                            } else if (primState.charAt(4) == '1' && primState.charAt(5) == '0') {

                                conduct = "13";

                                break;
                            }
                        } else if (k == 6) {
                            if (primState.charAt(4) == '1' && primState.charAt(5) == '1') {
                                conduct = "23";
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (k == 4) {
                            if (primState.charAt(2) == '0') {
                                type = 2;
                            }
                            break;
                        } else if (k == 3) {
                            type = 3;
                        }
                        break;
                    }
                    case 5: {
                        if (k == 4) {
                            if (primState.charAt(1) == '0') {

                                type = 1;
                                break;
                            }

                        } else if (k == 2) {

                            type = 3;
                        }
                        break;
                    }
                    case 4: {

                        if (k == 3) {
                            if (primState.charAt(1) == '0') {

                                type = 1;
                            }
                            break;
                        } else if (k == 2) {
                            type = 2;
                            break;
                        }
                    }
                }
            }
            if(conduct!=""){
                break;
            }else if(type!=0){
                break;
            }

        }

        if(conduct!=""){
            for (int i = 0; i < map.length; i++) {
                if(producer.contains((int)map[i][0]+"")&&lattice[(int)map[i][0]-1]>workNum[(int)map[i][0]-1]+1){
                    continue;
                }
                if(map[i][0] >= 1&&producer.contains((int)map[i][0]+"")&&map[i][5]==1&&isMapHasWorkstation(i)&&lattice[(int)map[i][0]-1]<total[(int)map[i][0]-1]&&conduct.contains((int)map[i][0]+"")){
                    if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                        min = dist;
                        j = i;
                    }
                }
            }
        }
        if(!conduct.equals("")&&j!=-1){
            map[j][7]++;
            map[j][5]=0;
            return j;
        }
        for (int i = 0; i < map.length; i++) {
            //if(producer.contains((int)map[i][0]+"")&&lattice[(int)map[i][0]-1]>workNum[(int)map[i][0]-1]+1){
            //     continue;
            // }
            if(type!=0){
                if(map[i][0] >= 1&&producer.contains((int)map[i][0]+"")&&map[i][5]==1&&isMapHasWorkstation(i)&&lattice[(int)map[i][0]-1]<total[(int)map[i][0]-1]&&(int)map[i][0]==type){
                    if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                        min = dist;
                        j = i;
                    }
                }
            }
        }
        if(type!=0&&j!=-1){
            map[j][7]++;
            map[j][5]=0;
            return j;
        }

        for (int i = 0; i < map.length; i++) {
            if(producer.contains((int)map[i][0]+"")&&lattice[(int)map[i][0]-1]>workNum[(int)map[i][0]-1]+1){
                continue;
            }

                if(map[i][0] >= 1&&"123".contains((int)map[i][0]+"")&&map[i][5]==1&&isMapHasWorkstation(i)&&lattice[(int)map[i][0]-1]<total[(int)map[i][0]-1]) {
                    if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                        min = dist;
                        j = i;
                    }
                }


        }
        if(j==-1){
            for (int i = 0; i < map.length; i++) {
//                if(producer.contains((int)map[i][0]+"")&&lattice[(int)map[i][0]-1]>workNum[(int)map[i][0]-1]+1){
//                    continue;
//                }
                    if(map[i][0] >= 1&&producer.contains((int)map[i][0]+"")&&isMapHasWorkstation(i)&&lattice[(int)map[i][0]-1]<total[(int)map[i][0]-1]) {
                        if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                            min = dist;
                            j = i;
                        }
                    }
            }
        }

//        if(map[j][0]==1){
//            prebuy[(robotid+1)%4][0]="23";
//        }else if(map[j][0]==2){
//            prebuy[(robotid+1)%4][0]="13";
//        }else{
//            prebuy[(robotid+1)%4][0]="12";
//        }
        map[j][7]++;

        //lattice[(int)map[j][0]-1]++;
        map[j][5]=0;
        return j;
    }
    private static int sellShortestRoot(double x, double y,double type,int robotid) {
        double min = Integer.MAX_VALUE;
        int j = 0;
        double dist;
        double min7=Integer.MAX_VALUE;
        double min6=Integer.MAX_VALUE;
        double min5=Integer.MAX_VALUE;
        double min4=Integer.MAX_VALUE;
        double min71=Integer.MAX_VALUE;
        double dist7;
        double dist71;
        double dist6;
        double dist5;
        double dist4;
        int o=-1;
        int l=-1;
        int m=-1;
        int n=-1;
        int q=-1;
//        if(workNum[0]+workNum[1]+workNum[2]<4){
//            if("456".contains((int)type+"")&&){
//
//            }
//        }
//        if(prebuy[robotid][1]!=""){
//            String[] s = prebuy[robotid][1].split(" ");
//            StringBuffer primState=new StringBuffer(Integer.toBinaryString((int)map[Integer.parseInt(s[0])][4]));
//            primState.reverse();
//            if(map[b[robotid]][0]>primState.length()-1||primState.charAt((int)map[b[robotid]][0])!=1){
//                return Integer.parseInt(s[0]);
//            }
//
//        }
//        for(int i=0;i<4;i++){
//            if(i!=robotid&&prebuy[i][1]!=""){
//                prebuy[robotid][1]+=prebuy[i][1]+" ";
//            }
//        }
        int num=50;
        int t=0;
        if(workNum[3]==workNum[4]&&workNum[3]==workNum[5]){

        }else{
            for(int i=3;i<7;i++){
                if(workNum[i]<num){
                    num=workNum[i];
                    t=i;
                }
            }
        }

        for(int i=0;i<map.length;i++) {
            if (consumer.contains((int) map[i][0] + "")) {
                StringBuffer primState = new StringBuffer(Integer.toBinaryString((int) map[i][4]));
                String[] s = prebuy[robotid][1].split(" ");
                primState.reverse();
                int k = primState.length();
                if ((int) map[i][0] == t+1) {
                    switch ((int) map[i][0]) {
                        case 7: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {

                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 7) {
                                        if ((int) type == 4 && primState.charAt(4) == '0' && primState.charAt(5) == '1') {
                                            return i;
//                                    o=i;
                                        } else if ((int) type == 5 && primState.charAt(4) == '1' && primState.charAt(5) == '0') {
                                            return i;
//                                    o=i;
                                        }
                                    } else if (k == 6) {
                                        if ((int) type == 6 && primState.charAt(4) == '1' && primState.charAt(5) == '1') {
                                            return i;
//                                    o=i;
                                        }
                                    }
                                }
                            }
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {

                                    if (k == 7) {
                                        if ((int) type == 4 || (int) type == 5 && primState.charAt(4) == '0' && primState.charAt(5) == '0') {
//                                    q=i;
                                            return i;
                                        }
                                    } else if (k == 6) {
                                        if ((int) type == 6 || (int) type == 4 && primState.charAt(4) == '0') {
//                                    q=i;
                                            return i;
                                        }
                                    } else if (k == 5) {
                                        if ((int) type == 5 || (int) type == 6) {
//                                    q=i;
                                            return i;
                                        }

                                    }
                                }
                            }
                        }

                        case 6: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 4) {
                                        if ((int) type == 2 && primState.charAt(2) == '0') {
                                            return i;
                                        }
                                    } else if ((int) type == 3 && k == 3) {
//                                l=i;
                                        return i;
                                    }
                                    if ((dist6 = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min6) {
                                        l = i;
                                        min6 = dist6;
                                    }
                                }
                            }
                        }
                        case 5: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 4) {
                                        if ((int) type == 1 && primState.charAt(1) == '0') {
//                                    m=i;
                                            return i;
                                        }
                                    } else if ((int) type == 3 && k == 2) {
//                                m=i;
                                        return i;
                                    }
                                    if ((dist5 = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min5) {
                                        m = i;
                                        min5 = dist5;
                                    }
                                }
                            }
                        }
                        case 4: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 3) {
                                        if ((int) type == 1 && primState.charAt(1) == '0') {
//                                    n=i;
                                            return i;
                                        }

                                    } else if ((int) type == 2 && k == 2) {
//                                n=i;
                                        return i;
                                    }else{
                                        if((int)type==1||(int)type==2){
                                            return i;
                                        }
                                    }

                                }
                            }
                        }
                    }
                } else {
                    switch ((int) map[i][0]) {
                        case 7: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {

                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 7) {
                                        if ((int) type == 4 && primState.charAt(4) == '0' && primState.charAt(5) == '1') {
                                            return i;
//                                    o=i;
                                        } else if ((int) type == 5 && primState.charAt(4) == '1' && primState.charAt(5) == '0') {
                                            return i;
//                                    o=i;
                                        }
                                    } else if (k == 6) {
                                        if ((int) type == 6 && primState.charAt(4) == '1' && primState.charAt(5) == '1') {
                                            return i;
//                                    o=i;
                                        }
                                    }
                                }
                            }
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {

                                    if (k == 7) {
                                        if ((int) type == 4 || (int) type == 5 && primState.charAt(4) == '0' && primState.charAt(5) == '0') {
//                                    q=i;
                                            return i;
                                        }
                                    } else if (k == 6) {
                                        if ((int) type == 6 || (int) type == 4 && primState.charAt(4) == '0') {
//                                    q=i;
                                            return i;
                                        }
                                    } else if (k == 5) {
                                        if ((int) type == 5 || (int) type == 6) {
//                                    q=i;
                                            return i;
                                        }

                                    }
                                }
                            }
                        }

                        case 6: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 4) {
                                        if ((int) type == 2 && primState.charAt(2) == '0') {
                                            return i;
                                        }
                                    } else if ((int) type == 3 && k == 3) {
//                                l=i;
                                        return i;
                                    }
                                    if ((dist6 = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min6) {
                                        l = i;
                                        min6 = dist6;
                                    }
                                }
                            }
                        }
                        case 5: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 4) {
                                        if ((int) type == 1 && primState.charAt(1) == '0') {
//                                    m=i;
                                            return i;
                                        }
                                    } else if ((int) type == 3 && k == 2) {
//                                m=i;
                                        return i;
                                    }
                                    if ((dist5 = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min5) {
                                        m = i;
                                        min5 = dist5;
                                    }
                                }
                            }
                        }
                        case 4: {
                            if (map[i][0] >= 1 && consumer.contains((int) map[i][0] + "") && (primState.length() - 1 < type || primState.charAt((int) type) != '1') && materialId[(int) map[i][0] - 4].contains((int) type + "") && map[i][6] == 0) {
                                int flag = 0;
                                for (int p = 0; p < s.length; i++) {
                                    if (s[p] == "") {

                                        break;
                                    }
                                    if (i != Integer.parseInt(s[p])) {
                                        continue;
                                    } else {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    if (k == 3) {
                                        if ((int) type == 1 && primState.charAt(1) == '0') {
//                                    n=i;
                                            return i;
                                        }

                                    } else if ((int) type == 2 && k == 2) {
//                                n=i;
                                        return i;
                                    }
                                    if ((dist4 = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min4) {
                                        n = i;
                                        min4 = dist4;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

//        if(o!=-1){
//            map[o][6]++;
//            return o;
//        }else if(l!=-1){
//            map[l][6]++;
//            return l;
//        }else if(m!=-1){
//            map[m][6]++;
//            return m;
//        }else if(n!=-1){
//            map[n][6]++;
//            return n;
//        }else if(q!=-1){
//            map[q][6]++;
//            return q;
//        }

        for (int i = 0; i < map.length; i++) {
            StringBuffer primState=new StringBuffer(Integer.toBinaryString((int)map[i][4]));
            primState.reverse();
            String[] s = prebuy[robotid][1].split(" ");
            if (map[i][0] >= 1&&consumer.contains((int)map[i][0]+"")&&(primState.length()-1<type||primState.charAt((int)type)!='1')&&materialId[(int)map[i][0]-4].contains((int)type+"")&&map[i][6]==0) {
                int flag = 0;
                for (int p = 0; p < s.length; i++) {
                    if (s[p] == "") {

                        break;
                    }
                    if (i != Integer.parseInt(s[p])) {
                        continue;
                    } else {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                        min = dist;
                        j = i;
                    }
                }
            }
        }

        map[j][6]++;
        return j;
    }
    private static boolean isMapHasWorkstation(int i){
        for(int j=0;j<6;j++){
            if(materialId[j].contains((int)map[i][0]+"")){
                if(workType[j+3]>0){
                    return true;
                }
            }
        }
        return false;
    }
    /*private static boolean isInSufficient(){
            if(w<0.77){
	n=w/0.7747;
	mod=w-0.7747*n;
            }
        0.77*
    }*/

    private static int  direct(double firstX, double firstY, double workStationX, double workStationY, double theta) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        forwards = Math.atan((workStationY - firstY) / (workStationX - firstX));
        if ((workStationY - firstY) > 0 && (workStationX - firstX) < 0) {
            forwards = forwards + Math.PI;
        } else if ((workStationY - firstY) < 0 && (workStationX - firstX) < 0) {
            forwards = forwards - Math.PI;
        }
        if(theta>0&&forwards>0){
            if(theta>forwards){
                return -1;
            }else{
                return 1;
            }
        }else if(theta>0&&forwards<0){
            if(theta-forwards<Math.PI){
                return -1;
            }else{
                return 1;
            }
        }else if(theta<0&&forwards>0){
            if(forwards-theta<Math.PI){
                return 1;
            }else{
                return -1;
            }
        }else{
            if(theta>forwards){
                return -1;
            }else{
                return 1;
            }
        }
    }
    private static double[] f(double[] cur,double vt,double ot){
        double[] calculate={dt*cur[5]+cur[3],dt*cur[6]+cur[4],cur[2]+dt*ot,cur[6],cur[7]};
        return calculate;
    }
    private static double v(double vx,double vy){
        return Math.sqrt(vx*vx+vy*vy);
    }
    private static double[] DynamicWindowApproach(double[] robotState,int goalId){
        double[] vWindow = CalcDynamicWindow(robotState);
        double[] model=Evaluation(robotState,vWindow,goalId);
        return model;
    }
    private static double[] CalcDynamicWindow(double[] currState){
        double[] window={Math.max(v(currState[5],currState[6])-dt*modelParam[2],0),Math.min(v(currState[5],currState[6])+dt*modelParam[2],6),Math.max(0,currState[7]-modelParam[3]*dt),Math.min(Math.PI,currState[7]+modelParam[3]*dt)};
        return window;
    }
    private static double[] Evaluation(double[] curState,double[] Vr,int goalId){
        double max=Integer.MIN_VALUE;
        double[] best=new double[2];
        double sumangle=0;
        double sumv=0;
        for(double vt=Vr[0];vt<=Vr[1];vt+=modelParam[4]){
            for(double ot=Vr[2];ot<=Vr[3];ot+=modelParam[5]){

                double[] simulation = simulation(curState, vt, ot, evalParm[3]);
                double angle = CalcHeadingEval(simulation, goalId);
//                if(angle*evalParm[0]+vt*evalParm[2]>max){
//                    max=angle*evalParm[0]+vt*evalParm[2];
//                    best[0]=vt;
//                    best[1]=ot;
//                }
                sumangle+=angle;
                sumv+=vt;
            }
        }
        for(double vt=Vr[0];vt<=Vr[1];vt+=modelParam[4]){
            for(double ot=Vr[2];ot<=Vr[3];ot+=modelParam[5]){
                double[] simulation = simulation(curState, vt, ot, evalParm[3]);
                double angle = CalcHeadingEval(simulation, goalId);
                if((sumangle!=0?angle/sumangle*evalParm[0]:angle*evalParm[0])+(sumv!=0?vt*evalParm[2]/sumv:vt*evalParm[2])>max){
                    max=angle*evalParm[0]+vt*evalParm[2];
                    best[0]=vt;
                    best[1]=ot;
                }
            }
        }
        return best;
    }
    private static double[] simulation(double[] currState,double vt ,double ot, double evaldt){
        double time=0;
        double[] f=new double[5];
        while(time<=evaldt){
            time=time+dt;
            f = f(currState, vt, ot);
        }
        return f;
    }
    private static int typedist(int type,double x,double y){
        double dist;
        double min=Integer.MAX_VALUE;
        int j=-1;
        for(int i=0;i<map.length;i++){
            if((int)map[i][0]==type&&map[i][5]==1&&isMapHasWorkstation(i)&&lattice[(int)map[i][0]-1]<total[(int)map[i][0]-1]){
                if ((dist = (Math.sqrt(Math.pow(x - map[i][1], 2) + Math.pow(y - map[i][2], 2)))) < min) {
                    min = dist;
                    j = i;

                }
            }
        }
        return j;
    }
    private static double CalcHeadingEval(double[] simulation,int goalId){
        double theta= simulation[2]/Math.PI*180;
        double forwards=Math.atan((map[goalId][2]-simulation[1])/(map[goalId][1]-simulation[0]));
        if ((map[goalId][2] - simulation[1]) > 0 && (map[goalId][1] - simulation[0]) < 0) {
            forwards = forwards + Math.PI;
        } else if ((map[goalId][2] - simulation[1]) < 0 && (map[goalId][1] - simulation[0]) < 0) {
            forwards = forwards - Math.PI;
        }
        forwards=forwards/Math.PI*180;
        double targettheta;
        if(forwards>theta){
            targettheta=forwards-theta;
        }else{
            targettheta=theta-forwards;
        }
        return 180-targettheta;
    }
}


