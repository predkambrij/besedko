
directory = "/home/lojze/hacks/code/bdpa/BesedkoBackend/src/"
stopwords = [ x.strip() for x in file(directory+"english_stop_words.txt","rb") if x.strip()]
englishwords = [ x.strip() for x in file(directory+"google-10000-english.txt","rb") if x.strip() and len(x.strip())>3]

#print repr(stopwords)
#print repr(englishwords)




without_stopwords = [x for x in englishwords if not x in stopwords]
file(directory+"google-10000-english_without_stopwords.txt","wb").write("\n".join(without_stopwords))
#print repr(without_stopwords)
