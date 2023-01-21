from pygame import *
from glob import *

pics = glob("*.png")
imgs = [image.load(p) for p in pics]
c=0
for i in imgs:

    if "Fixed" in pics[c]:
        image.save(i, pics[c].replace("Fixed", ""))
##    image.save(i, pics[c].replace(".png",".jpg"))
        print(pics[c])
    c+=1
    
