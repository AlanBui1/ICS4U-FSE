from pygame import *
from glob import *

def val(fname):
    return fname[fname.rfind("_")+1: fname.rfind(".")]

pics = glob("*.png")
imgs = [image.load(p) for p in pics]
c=0
for i in imgs:

    if "_" in pics[c]:
        image.save(i, "ChargeDownAtk"+val(pics[c])+".png")
##    image.save(i, pics[c].replace(".png",".jpg"))
        print(pics[c])
    c+=1
    
