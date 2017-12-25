#Import the necessary methods from tweepy library
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream

#Variables that contains the user credentials to access Twitter API 
access_token = "57054526-jSqQQnhFB5BFCrknLtnae71nTyclZMy6DhYlvWCe4"
access_token_secret = "eYRmcHQXhIMeN3ruFOrJCqKb8hqakZC34Ye4wSp3lmP9K"
consumer_key = "epj1UaGCa8qczRFF22VHSBilt"
consumer_secret = "Pdoh1G8kiul6OL9KxKbfcod03a49JzQpiHOpvHnmV1qFpUQlbB"


#This is a basic listener that just prints received tweets to stdout.
class StdOutListener(StreamListener):

    def on_data(self, data):
        print(data)
        return True

    def on_error(self, status):
        print(status)


if __name__ == '__main__':

    #This handles Twitter authetification and the connection to Twitter Streaming API
    l = StdOutListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    stream = Stream(auth, l)

    #This line filter Twitter Streams to capture data by the keywords: 'python', 'javascript', 'ruby'
    #stream.filter(track=['python', 'javascript', 'ruby'])
    stream.filter(track=['dhoni', 'ms dhoni', 'The untold story', 'ms dhoni the untold story'])