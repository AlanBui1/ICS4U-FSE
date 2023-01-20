from pygame import *
from glob import *

pics = glob("*.png")
imgs = [image.load(p) for p in pics]
c=0
count =0
for i in imgs:

    if "_" in pics[c]:
        image.save(i, "ChargeDownAtk"+str(count)+".png")
        count += 1
##    image.save(i, pics[c].replace(".png",".jpg"))
        print(pics[c])
    c+=1
    
