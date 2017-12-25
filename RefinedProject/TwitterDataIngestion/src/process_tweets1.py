import json
import re
import csv

tweets_data_path = './tweets - Copy.txt'

tweets_data = []
tweets_file = open(tweets_data_path, "r")
for line in tweets_file:
    try:
        tweet = json.loads(line)
        tweets_data.append(tweet)
    except:
        continue

#print(len(tweets_data))

final_tweet = []

for line in tweets_data:
    temp_line = line['text'].encode("utf-8",'replace').decode('utf-8','replace')
    temp_line = re.sub(r"http\S+", "", temp_line)
    temp_line = re.sub(r"@\S+", "", temp_line)
    temp_line = re.sub(r"\n\S+", "", temp_line)
    temp_line = ''.join(e for e in temp_line if e.isalnum() or e.isspace())
    temp_line = temp_line.strip()
    if(temp_line == ''):
        continue
    else:
        temp_tweet = ['NA',temp_line]
        final_tweet.append(temp_tweet)
#print(final_tweet)

# for line1,line2 in final_tweet:
#     print(line2)

with open("../res/output.csv","w",encoding='utf-8',newline='') as f:
    writer = csv.writer(f)
    writer.writerows(final_tweet)

print("Hello")

