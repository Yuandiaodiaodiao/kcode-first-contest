import os
import requests
import json
import schedule
import time
import re
last_score = 0
want_best_score = 1160382


def git_push():
    with open("README.md", encoding="utf-8", mode="a") as data:
        data.write("//test")
    data.close()

    os.system("git status")
    os.system("git commit -a -m \"flush score\" ")
    os.system("git push")
    print("finish!!!")

# schedule.every(1).minutes.do(flush_score)
# schedule.every(10).minutes.do(git_push)
git_push()
schedule.every(5).minutes.do(git_push)
while True:
    schedule.run_pending()
    time.sleep(1)
