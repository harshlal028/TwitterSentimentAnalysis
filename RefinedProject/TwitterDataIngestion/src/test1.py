#Import the necessary methods from tweepy library
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream

#Variables that contains the user credentials to access Twitter API 
access_token = "787194016555229184-9s2XgGRE0CkvHpnHhRfh5MmbvcwQaMO"
access_token_secret = "Vj6jSJ1ljrNMSVqmhN1lCZUTkf23llWBqLLd4gtmssJT8"
consumer_key = "GrOPSAI3MfA551X9RCU9fNHOT"
consumer_secret = "8YKkR7UrA7Co2zwSYzzUfLDiNRDCgclPpmfKmFDzcDrGDr0IZk"


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