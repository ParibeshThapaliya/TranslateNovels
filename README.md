# TranslateNovels

useful for converting a chineese / japaneese novel to english/other language 


Jsoup is currently configured for https://www.biqubao.com/ right now cause I used it for emperors domination but 
you can easily change  it to work with any other websites, need the ccs selector for "next page" and for where the content is located 
I also used deep L translater to traanlate chineese to english, can easily replace with another translater as well 

steps go like   
      -extract the raws using j soup   
      -use selenium webdriver to translate   
      -extract the translated chapter   
      -convert it to a html   
      -use epublib to create the book   
