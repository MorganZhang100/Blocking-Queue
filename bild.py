#!/usr/bin/env python

# bootstrap by downloading bilder.py if not found
import urllib
import os

if not os.path.exists("bilder.py"):
	print "bootstrapping; downloading bilder.py"
	urllib.urlretrieve(
		"https://raw.githubusercontent.com/parrt/bild/master/src/python/bilder.py",
		"bilder.py")

# assumes bilder.py is in current directory
from bilder import *

def compile():
	javac("src", "out")
	javac("test", "out")

def test():
	require(compile)
	java("cs601.blkqueue.TestSynchronizedBlockingQueue", cp="out", vmargs=["-server"])
	java("cs601.blkqueue.TestBlockingQueue", cp="out", vmargs=["-server"])
	java("cs601.blkqueue.TestRingBuffer", cp="out", vmargs=["-server"])
	java("cs601.blkqueue.TestRingBufferGrading", cp="out", vmargs=["-server"])

def all():
	test()

processargs(globals())
