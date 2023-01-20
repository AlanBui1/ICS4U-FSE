from pygame import *
from glob import *

def val(fileName):
    return int(fileName[fileName.rfind("_")+1: fileName.rfind(".")])

stuff = []



pics = glob("*.png")
imgs = [image.load(p) for p in pics]
c=0
count = 0
for i in imgs:
    fileName = pics[c]
    if "_" in pics[c]:
        image.save(i, "FastSideAtk"+str(val(fileName))+".png")
        count += 1
##    image.save(i, pics[c].replace(".png",".jpg"))
        print(pics[c])
    c+=1

