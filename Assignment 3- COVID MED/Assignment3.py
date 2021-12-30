# -*- coding: utf-8 -*-
"""
Created on Mon Oct  4 19:22:23 2021

@author: Blaine Pack
"""

"""A="_cat"
B="_Kit"
"""

from array import *
import fileinput
import sys, os
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import datetime

#Score table is referenced to find the 'cost' of a given mutation
              # A   T   C   G
SCORE_TABLE = [[1, -5, -5, -1], #A
               [-5, 1, -1, -5], #T
               [-5, -1, 1, -5], #C
               [-1, -5, -5, 1]] #G

#Matches the letter to it's index for the score table.
Gene_dict={
    "A":0,
    "T":1,
    "C":2,
    "G":3
    }




#Simply for readability of the main Function, score references the table above and returns the correct value
def score(a,b):
    return SCORE_TABLE[Gene_dict[a]][Gene_dict[b]]

OPEN_GAP=-5
CONT_GAP=-1


A=""
B=""
# the simple recursive version of minimum edit distance, as presented in class.
# Note that this is built for any strings, since it doesn't use the score table.
"""
def MED(n,m):
    if n==0:
        return m
    if m==0:
        return n
    return min(MED(n-1,m)+1, 
               MED(n,m-1)+1, 
               MED(n-1,m-1)+(A[n]!=B[m]))
"""

#The Dynamically programmed full version of Minimum Edit Distance, Using the score
#table above. Input is the size of the two strings A and B.
def MED_DP(n,m, trace=0):
    main_cache = np.zeros((n+1,m+1), dtype= np.int32())       #three caches are set up.
    A_cutter_cache = np.zeros((n+1,m+1), dtype= np.int32())   #These caches represent whether
    B_cutter_cache = np.zeros((n+1,m+1), dtype= np.int32())    #A cut has occurred in the sequence.
    
    
    
    for i in range(1,n+1):
        main_cache[i][0] = OPEN_GAP + A_cutter_cache[i-1][0];       #OPEN_GAP is the score assigned at the start of a sequence of cuts
        A_cutter_cache[i][0] = CONT_GAP + A_cutter_cache[i-1][0];   #CONT_GAP is the score for the remaining genes in the sequence.
        B_cutter_cache[i][0] = OPEN_GAP + A_cutter_cache[i-1][0]
    
    for j in range(1,m+1):
        main_cache[0][j] = OPEN_GAP + B_cutter_cache[0][j-1];
        B_cutter_cache[0][j] = CONT_GAP + B_cutter_cache[0][j-1];
        A_cutter_cache[0][j] = OPEN_GAP + B_cutter_cache[0][j-1]
        
    for i in range(1,n+1):
        for j in range(1,m+1):
            A_cutter_cache[i][j]=max(CONT_GAP + A_cutter_cache[i-1][j],         #Because of the nature of the score chart, we take max instead of min.
                                     OPEN_GAP + B_cutter_cache[i][j-1],         #While a cut has started in A, if we switch to cutting B we need to open cut again
                                     score(A[i],B[j]) + main_cache[i-1][j-1])
            B_cutter_cache[i][j] = max(OPEN_GAP + A_cutter_cache[i-1][j],
                                        CONT_GAP + B_cutter_cache[i][j-1],
                                        score(A[i],B[j]) + main_cache[i-1][j-1])
            main_cache[i][j] = max(OPEN_GAP + A_cutter_cache[i-1][j],
                                    OPEN_GAP + B_cutter_cache[i][j-1],
                                    score(A[i],B[j]) + main_cache[i-1][j-1])
    
    #Here begins the traceback logic
    i=n
    j=m
    current_Cache = 'main'
    Ascore=0
    Bscore=0
    Mscore=0
    for x in range(trace):
        if current_Cache == 'main':
            Ascore=OPEN_GAP + A_cutter_cache[i-1][j]
            Bscore=OPEN_GAP + B_cutter_cache[i][j-1]
            Mscore=score(A[i],B[j]) + main_cache[i-1][j-1]
        if current_Cache == 'Acut':
            Ascore=CONT_GAP + A_cutter_cache[i-1][j]
            Bscore=OPEN_GAP + B_cutter_cache[i][j-1]
            Mscore=score(A[i],B[j]) + main_cache[i-1][j-1]    
        if current_Cache == 'Bcut':
            Ascore=OPEN_GAP + A_cutter_cache[i-1][j]
            Bscore=CONT_GAP + B_cutter_cache[i][j-1]
            Mscore=score(A[i],B[j]) + main_cache[i-1][j-1] 
            
        if Mscore >= Ascore and Mscore >= Bscore:
            print(A[i] + '==' + B[j])
            i = i-1
            j = j-1
            current_Cache = 'main'
        elif Ascore >= Mscore and Ascore >= Bscore:
            print(A[i] + '==_')
            i=i-1
            current_Cache = 'Acut'
        elif Bscore>=Mscore and Bscore >= Ascore:
            print('_==' + B[j])
            j=j-1
            current_Cache = 'Bcut'
            
            
            
    return main_cache[n][m]
Genome_Names = ["" for i in range(15)]
Genome_Array = ["_" for i in range(0,15)]
count=0
# code borrowed from stackoverflow for getting the file from same directory as the module
__location__ = os.path.realpath(
    os.path.join(os.getcwd(), os.path.dirname(__file__)))
file = open(os.path.join(__location__, 'sequences.txt'))

writefile = open(os.path.join(__location__, 'results.txt'), 'w')
sys.stdout = writefile
#Fill an array with the different genome strings
for line in file:

    if line[0]==">":        #skip the identifiers for the strands
        #print(line)
        count= count+1
        Genome_Names[count]=line
    else:
        Genome_Array[count]= Genome_Array[count] + line.strip('\nN')


score_Array = [[0  for i in range(15)]for j in range(15)]
for i in range(1,14):
    A=Genome_Array[i].strip('N')
    for j in range(i+1,15):
        B=Genome_Array[j].strip('N')
        print(Genome_Names[i] + " vs. " + Genome_Names[j])
        myscore=MED_DP(1000,1000, 100)
        print("scored " + str(myscore))
        score_Array[i][j]=myscore
        score_Array[j][i]=myscore



[print(*line) for line in score_Array]

def showTime(function, sizes, init = None, fit = 'exponential'):  
    # times a given function and displays the time as a function of problem size  
    #function takes a single integer argument  
    #runs the function with an input values taken from input array sizes  
    #generates a graph of run time as a function of problem size  
    # init, if provided, is a function that is called once before function is called  
    # fit may be 'exponential' then the time as a function of problem size is assumed  
    #     to of the form time = c * a^n and the function solves for c and a  
    #     where a is the base of the exponential function and c is a multiplicative factor  
    #     sizes should be arithmeticly increasing (such as [10, 11, 12, 13, 14, 15])  
    # fit my be 'polynomial' then the time as a function of problem size is assumed  
    #     to of the form time = c * n ^ b and the function solves for c and b   
    #     where b is the power of n (the degree of the polynomial) and c is a multiplicative factor  
    #     sizes should be geometrically increasing (such as [64, 128, 256, 512, 1024])    
    timeLine = []    
    validSizes = []    
    for n in sizes:
        startTime = datetime.datetime.now()
        if not init == None:
            init()
        function(n,n)
        endTime = datetime.datetime.now()
        time_diff = (endTime - startTime)
        elapsed = time_diff.total_seconds() * 1000
        if elapsed > 0: #sometimes the function is too fast and we get 0 time
            timeLine.append(elapsed)
            validSizes.append(n)
            ##Generating the plot between time taken by each function call with n as variable and n
        plt.plot(validSizes, timeLine, 'g')
        plt.xlabel("n")
        if fit == 'exponential':
            plt.yscale('log')
        if fit == 'polynomial':
            plt.yscale('log')
            plt.xscale('log')
        plt.ylabel("time in milliseconds")
        plt.rcParams["figure.figsize"] = [16,9]
        plt.show()
        if fit == 'exponential': #fit a straight line to n and log time
            slope, intercept, _, _, _ = stats.linregress([validSizes], [np.log(t) for t in timeLine])
            print("time = %.6f %.3f ^ n" % (np.exp(intercept), np.exp(slope)))
        elif fit == 'polynomial': # fit a straight line to log n and log time
            slope, intercept, _, _, _ = stats.linregress([np.log(v) for v in validSizes], [np.log(t) for t in timeLine])
                    
            print("time = %.6f n ^ %.3f" % (np.exp(intercept), slope))


showTime(MED_DP, [2**i for i in range(8,13)])