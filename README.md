# TranslateNovels

useful for converting a chineese/korean / japaneese novel to english/other language 


Jsoup is currently configured for https://www.biqubao.com/ and https://ncode.syosetu.com right now but 
you can easily change  it to work with any other websites, need the ccs selector for "next page" and for where the content is located 
I also used deep L translater to traanlate chineese to english, can easily replace with another translater as well 
you might get temp banned i tried to fix it but its not perfect
steps go like   
      -extract the raws using j soup   
      -use selenium webdriver to translate   
      -extract the translated chapter   
      -convert it to a html   
      -use epublib to create the book   
