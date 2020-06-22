minx=99
with open(r"D:\Github\KcodeRpcMonitor-master\2kcodeRpcMonitor.data",'r')as f:
    while True:
        l=len(f.readline())
        if l==0:
            break
        minx =min(minx,l)
print(minx)