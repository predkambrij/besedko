import sys, random

directory = "/home/lojze/hacks/code/bdpa/BesedkoBackend/src/"

words = [ x.strip() for x in file(directory+"google-10000-english_without_stopwords.txt","rb") if x.strip()]

sys.stdout.write(
                 #"concat"
                 words[random.randrange(0,min(len(words), int(sys.argv[1])))]
                 )