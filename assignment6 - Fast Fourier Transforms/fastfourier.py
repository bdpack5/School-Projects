# -*- coding: utf-8 -*-
"""
Created on Sun Oct 31 12:52:34 2021

@author: bdpac
"""

import numpy as np
import math
import matplotlib.pyplot as plt
import datetime

#Fast Fourier Transforms! Utilizing mathematical witchcraft*, we prepare a polynomial of size 2^n
#to be multiplied with another polynomial of the same size. More witchcraft is needed to reverse the 
#process after the multiplication, producing the actual answer.
#*-complex number multiplication.
def fft(polynomial, omega,n): 
    #base case    
    if n == 1:
        return polynomial
    else :
    #split into even and odd
        Podd = np.array([polynomial[i] for i in range(1,n,2)])
        Peven = np.array([polynomial[i] for i in range(0,n,2)])
    #square values in X
    omega2 = np.array([omega[n]**2 for n in range(n//2)])
    #compute values for even/odd
    solEven = fft(Peven, omega2, n//2)
    solOdd = fft(Podd, omega2, n//2)
    #construct solution
    result1 = np.array([solEven[i] + omega[i]*solOdd[i] for i in range(0,n//2)])
    result2 = np.array([solEven[j] - omega[j]*solOdd[j] for j in range(0,n//2)])    
    return result1 + result2


#finds the nth root of unity, for use in the fft.

def unityRoots(n):
    omega = complex(math.cos(2*math.pi/n), math.sin(2*math.pi/n))
    return np.array([omega**i for i in range(n)])

#used for the inverse version of fft
def inverseUnityRoots(n):
    omega = complex(math.cos(2*math.pi/n), -1*math.sin(2*math.pi/n))
    return np.array([omega**i for i in range(n)])



def sampleSizeN(n):
    polynomial = np.array([i for i in range(n)])
    return fft(polynomial, unityRoots(n), n)


#plotting for n>=128 as far as I can.
def plotter(sizes):
    
    timeLine = []
    validSizes = []
    for n in sizes:
        print("calculating size ", n)
        startTime = datetime.datetime.now()
        sampleSizeN(n)
        endTime = datetime.datetime.now()
        time_diff = (endTime - startTime)
        elapsed = time_diff.total_seconds()*1000
        if elapsed > 0:
            timeLine.append(elapsed)
            validSizes.append(n)
        plt.plot(validSizes, timeLine)
        plt.yscale('log')
        plt.xscale('log')
        plt.ylabel("time in milliseconds")
        plt.xlabel("size")
        plt.rcParams["figure.figsize"] = [16,9]
        plt.show()
        
plotter([(128*(2**i)) for i in range(100)])
